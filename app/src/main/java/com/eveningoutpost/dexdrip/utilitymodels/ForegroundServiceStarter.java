package com.eveningoutpost.dexdrip.utilitymodels;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.R;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError;
import com.eveningoutpost.dexdrip.models.UserError.Log;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST;
import static com.eveningoutpost.dexdrip.utilitymodels.Notifications.ongoingNotificationId;

/**
 * Created by Emma Black on 12/25/14.
 * Updated for Android 15+ (API 35+) foreground service compatibility.
 */
public class ForegroundServiceStarter {

    private static final String TAG = "FOREGROUND";

    private final Service mService;
    private final Context mContext;
    private final boolean run_service_in_foreground;

    public static boolean shouldRunCollectorInForeground() {
        // Force foreground with Oreo and above.
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Home.get_follower())
                || Pref.getBoolean("run_service_in_foreground", true);
    }

    public ForegroundServiceStarter(final Context context, final Service service) {
        mContext = context;
        mService = service;
        run_service_in_foreground = shouldRunCollectorInForeground();
    }

    /**
     * Android O+ strictly requires a valid Notification channel for foreground services.
     * Some ROMs/Android versions (notably MIUI) crash the app if startForeground() is called with
     * an invalid notification, producing:
     * RemoteServiceException$CannotPostForegroundServiceNotificationException: Bad notification for startForeground
     */
    @TargetApi(Build.VERSION_CODES.O)
    private static void ensureOngoingChannelExists(final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        try {
            final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;

            final NotificationChannel existing = nm.getNotificationChannel(NotificationChannels.ONGOING_CHANNEL);
            if (existing != null) return;

            final NotificationChannel channel = new NotificationChannel(
                    NotificationChannels.ONGOING_CHANNEL,
                    NotificationChannels.getString(NotificationChannels.ONGOING_CHANNEL),
                    NotificationManager.IMPORTANCE_LOW
            );

            // Collector ongoing notification should be silent.
            channel.setSound(null, null);
            channel.enableVibration(false);
            channel.enableLights(false);

            nm.createNotificationChannel(channel);
        } catch (Exception e) {
            // Best effort only.
            Log.e(TAG, "Failed to ensure ongoing notification channel exists: " + e);
        }
    }

    private Notification buildFallbackForegroundNotification() {
        // Minimal notification used for the actual startForeground() call.
        //
        // IMPORTANT: Even if we can build the full xDrip ongoing notification, some Android builds
        // may still reject it when posted as a foreground-service notification (RemoteViews, bitmaps,
        // channel edge cases). So we start with a minimal, known-safe notification and then update it.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ensureOngoingChannelExists(mContext);
        }

        final Intent intent = new Intent(mContext, Home.class);
        final int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        final PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, flags);

        return new NotificationCompat.Builder(mContext, NotificationChannels.ONGOING_CHANNEL)
                .setSmallIcon(R.drawable.ic_action_communication_invert_colors_on)
                .setContentTitle("xDrip")
                .setContentText("Data collection service is running.")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(pi)
                .build();
    }

    private Notification buildRichOngoingNotification(final long start, final long end) {
        // This is the existing “full” notification (graph etc). It can be updated after the service
        // is already in the foreground.
        return new Notifications().createOngoingNotification(new BgGraphBuilder(mContext, start, end), mContext);
    }

    private void updateToRichNotification(final long start, final long end) {
        // Update in the background; if this fails, the service is still safe because the minimal
        // notification remains.
        Inevitable.task("update-rich-ongoing-notification", 1500, () -> {
            try {
                final Notification rich = buildRichOngoingNotification(start, end);
                NotificationManagerCompat.from(mContext).notify(ongoingNotificationId, rich);
            } catch (Exception e) {
                Log.e(TAG, "Failed to post rich ongoing notification (keeping fallback): " + e);
            }
        });
    }

    /**
     * Start the service in foreground mode with proper Android 15+ (API 35+) handling.
     */
    public void start() {
        if (mService == null) {
            Log.e(TAG, "SERVICE IS NULL - CANNOT START!");
            return;
        }

        if (!run_service_in_foreground) return;

        Log.d(TAG, "should be moving to foreground");

        // TODO use constants
        final long end = System.currentTimeMillis() + (60000 * 5);
        final long start = end - (60000 * 60 * 3) - (60000 * 10);

        foregroundStatus();
        Log.d(TAG, "CALLING START FOREGROUND: " + mService.getClass().getSimpleName());

        // Always use the safe notification for startForeground().
        final Notification safeNotification = buildFallbackForegroundNotification();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    // Android 15+: Use FOREGROUND_SERVICE_TYPE_MANIFEST if available.
                    try {
                        mService.startForeground(
                                ongoingNotificationId,
                                safeNotification,
                                FOREGROUND_SERVICE_TYPE_MANIFEST
                        );
                        Log.d(TAG, "Successfully started foreground service with type manifest for "
                                + mService.getClass().getSimpleName());
                    } catch (SecurityException e) {
                        // Fallback: Missing required permissions in manifest.
                        Log.e(TAG, "SecurityException starting foreground service (check manifest permissions): "
                                + mService.getClass().getSimpleName() + " - " + e.getMessage());
                        try {
                            mService.startForeground(ongoingNotificationId, safeNotification);
                            Log.w(TAG, "Started foreground service without type flag (degraded mode) for "
                                    + mService.getClass().getSimpleName());
                        } catch (Exception fallbackException) {
                            Log.e(TAG, "Failed to start foreground service even in fallback mode: "
                                    + fallbackException.getMessage());
                            return;
                        }
                    }
                } else {
                    // Android 10-14.
                    try {
                        mService.startForeground(
                                ongoingNotificationId,
                                safeNotification,
                                FOREGROUND_SERVICE_TYPE_MANIFEST
                        );
                        Log.d(TAG, "Successfully started foreground service for "
                                + mService.getClass().getSimpleName());
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "Got exception trying to use Android 10+ service starting for "
                                + mService.getClass().getSimpleName() + " " + e);
                        mService.startForeground(ongoingNotificationId, safeNotification);
                    }
                }
            } else {
                // Android 9 and below.
                mService.startForeground(ongoingNotificationId, safeNotification);
                Log.d(TAG, "Started foreground service (Android < 10) for "
                        + mService.getClass().getSimpleName());
            }

            // If we got here, startForeground() succeeded. Now try to update the notification.
            updateToRichNotification(start, end);

        } catch (Exception e) {
            // If this throws, Android will likely kill the process. Log anyway for crash reports.
            Log.e(TAG, "Unexpected error starting foreground service: " + e.getMessage());
        }
    }

    /**
     * Stop the foreground service.
     */
    public void stop() {
        if (run_service_in_foreground) {
            Log.d(TAG, "should be moving out of foreground");
            try {
                mService.stopForeground(true);
            } catch (Exception e) {
                Log.e(TAG, "Error stopping foreground service: " + e.getMessage());
            }
        }
    }

    /**
     * Log the current foreground status of the service.
     */
    protected void foregroundStatus() {
        Inevitable.task("foreground-status", 2000, () ->
                UserError.Log.d("XFOREGROUND",
                        mService.getClass().getSimpleName() +
                                (JoH.isServiceRunningInForeground(mService.getClass()) ?
                                        " is running in foreground" :
                                        " is not running in foreground"
                                )
                )
        );
    }
}
