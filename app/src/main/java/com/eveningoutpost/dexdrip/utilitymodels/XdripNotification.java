package com.eveningoutpost.dexdrip.utilitymodels;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;

/*
 * Created by jwoglom on 5/17/2018
 *
 * Wrapper for android.app.Notification.Builder that adds the necessary notification
 * channel ID if enabled. Identical functionality-wise to XdripNotificationCompat.
 */

public class XdripNotification {

    @TargetApi(Build.VERSION_CODES.O)
    public static Notification build(final Notification.Builder builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Android O+ requires a valid channelId for notifications, and foreground services
            // are especially strict: a missing/invalid channelId can crash the app with:
            // RemoteServiceException$CannotPostForegroundServiceNotificationException.
            //
            // For safety we never set the channelId to null on Android O+.
            try {
                // If channel handling is enabled, we try to dynamically choose a channel.
                if (Pref.getBooleanDefaultFalse("use_notification_channels")) {
                    final String id = NotificationChannels.getChan(builder).getId();
                    if (id != null) {
                        builder.setChannelId(id);
                    }
                }
            } catch (Exception e) {
                // Keep existing channelId if dynamic channel selection fails.
            }

            // Final fallback: ensure we always have *some* channel ID.
            try {
                final Notification temp = builder.build();
                if (temp.getChannelId() == null) {
                    builder.setChannelId(NotificationChannels.ONGOING_CHANNEL);
                }
            } catch (Exception e) {
                builder.setChannelId(NotificationChannels.ONGOING_CHANNEL);
            }

            return builder.build();
        }

        // Standard pre-oreo behavior
        return builder.build();
    }
}
