package com.eveningoutpost.dexdrip.utilitymodels;

import android.app.Service;
import android.content.Context;
import android.os.Build;

import com.eveningoutpost.dexdrip.Home;
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

    final private Service mService;
    final private Context mContext;
    final private boolean run_service_in_foreground;
    //final private Handler mHandler;


    public static boolean shouldRunCollectorInForeground() {
        // Force foreground with Oreo and above
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Home.get_follower())
                || Pref.getBoolean("run_service_in_foreground", true);
    }

    public ForegroundServiceStarter(Context context, Service service) {
        mContext = context;
        mService = service;
        //mHandler = new Handler(Looper.getMainLooper());

        run_service_in_foreground = shouldRunCollectorInForeground();
    }


    /**
     * Start the service in foreground mode with proper Android 15+ (API 35+) handling.
     * 
     * Android 15+ requires additional foreground service type permissions to be declared
     * in the manifest. Services using connectedDevice type need FOREGROUND_SERVICE_CONNECTED_DEVICE
     * and related Bluetooth permissions.
     */
    public void start() {
        if (mService == null) {
            Log.e(TAG, "SERVICE IS NULL - CANNOT START!");
            return;
        }
        if (run_service_in_foreground) {
            Log.d(TAG, "should be moving to foreground");
            // mHandler.post(new Runnable() {
            //     @Override
            //     public void run() {
            // TODO use constants
            final long end = System.currentTimeMillis() + (60000 * 5);
            final long start = end - (60000 * 60 * 3) - (60000 * 10);
            foregroundStatus();
            Log.d(TAG, "CALLING START FOREGROUND: " + mService.getClass().getSimpleName());
            
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    /*
                      When is a foreground service not a foreground service?
                      When it's started from the background of course!
                      On Android 10+, even though the user explicitly grants permissions,
                      we still have to request to use them on a foreground service,
                      but only when it isn't re-started with the app open.
                      
                      Android 15+ (API 35) requires that foreground service type permissions
                      are declared in the manifest and properly handled during service startup.
                     */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        // Android 15+: Use FOREGROUND_SERVICE_TYPE_MANIFEST if available
                        try {
                            mService.startForeground(
                                ongoingNotificationId,
                                new Notifications().createOngoingNotification(
                                    new BgGraphBuilder(mContext, start, end), mContext),
                                FOREGROUND_SERVICE_TYPE_MANIFEST
                            );
                            Log.d(TAG, "Successfully started foreground service with type manifest for " + 
                                    mService.getClass().getSimpleName());
                        } catch (SecurityException e) {
                            // Fallback: Missing required permissions in manifest
                            Log.e(TAG, "SecurityException starting foreground service (check manifest permissions): " +
                                    mService.getClass().getSimpleName() + " - " + e.getMessage());
                            // Try without the service type flag
                            try {
                                mService.startForeground(
                                    ongoingNotificationId,
                                    new Notifications().createOngoingNotification(
                                        new BgGraphBuilder(mContext, start, end), mContext)
                                );
                                Log.w(TAG, "Started foreground service without type flag (degraded mode) for " +
                                        mService.getClass().getSimpleName());
                            } catch (Exception fallbackException) {
                                Log.e(TAG, "Failed to start foreground service even in fallback mode: " +
                                        fallbackException.getMessage());
                            }
                        }
                    } else {
                        // Android 10-14: Original behavior
                        try {
                            mService.startForeground(
                                ongoingNotificationId,
                                new Notifications().createOngoingNotification(
                                    new BgGraphBuilder(mContext, start, end), mContext),
                                FOREGROUND_SERVICE_TYPE_MANIFEST
                            );
                            Log.d(TAG, "Successfully started foreground service for " + 
                                    mService.getClass().getSimpleName());
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Got exception trying to use Android 10+ service starting for " +
                                    mService.getClass().getSimpleName() + " " + e);
                            mService.startForeground(
                                ongoingNotificationId,
                                new Notifications().createOngoingNotification(
                                    new BgGraphBuilder(mContext, start, end), mContext)
                            );
                        }
                    }
                } else {
                    // Android 9 and below
                    mService.startForeground(
                        ongoingNotificationId,
                        new Notifications().createOngoingNotification(
                            new BgGraphBuilder(mContext, start, end), mContext)
                    );
                    Log.d(TAG, "Started foreground service (Android < 10) for " + 
                            mService.getClass().getSimpleName());
                }
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error starting foreground service: " + e.getMessage());
            }

            //     }
            // });
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
