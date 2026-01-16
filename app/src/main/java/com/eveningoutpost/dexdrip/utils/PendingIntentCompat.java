package com.eveningoutpost.dexdrip.utils;

import android.app.PendingIntent;
import android.os.Build;

/**
 * Utility class for creating PendingIntents with proper flags for Android 12+ compatibility
 * 
 * Android 12 (API 31) and above requires that PendingIntents specify either FLAG_IMMUTABLE
 * or FLAG_MUTABLE. This class provides helper methods to ensure compatibility across all
 * Android versions.
 * 
 * @author xDrip Contributors
 */
public class PendingIntentCompat {

    /**
     * Gets appropriate PendingIntent flags for Android 12+ compatibility.
     * Use this when you need to combine with other flags like FLAG_UPDATE_CURRENT.
     * 
     * @param mutable Whether the PendingIntent should be mutable (e.g., for inline replies or bubbles)
     * @return FLAG_IMMUTABLE or FLAG_MUTABLE for API 23+, or 0 for older versions
     */
    public static int getFlags(boolean mutable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mutable ? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_IMMUTABLE;
        }
        return 0;
    }

    /**
     * Gets immutable PendingIntent flags. This is the recommended default.
     * 
     * @return FLAG_IMMUTABLE for API 23+, or 0 for older versions
     */
    public static int getImmutableFlags() {
        return getFlags(false);
    }

    /**
     * Gets mutable PendingIntent flags. Only use when the PendingIntent needs to be
     * modified after creation (e.g., inline replies, notification bubbles).
     * 
     * @return FLAG_MUTABLE for API 23+, or 0 for older versions
     */
    public static int getMutableFlags() {
        return getFlags(true);
    }

    /**
     * Combines base flags with immutability flag for Android 12+ compatibility.
     * Common usage: combineFlags(PendingIntent.FLAG_UPDATE_CURRENT, false)
     * 
     * @param baseFlags The base flags (e.g., FLAG_UPDATE_CURRENT, FLAG_ONE_SHOT)
     * @param mutable Whether the PendingIntent should be mutable
     * @return Combined flags suitable for the current Android version
     */
    public static int combineFlags(int baseFlags, boolean mutable) {
        return baseFlags | getFlags(mutable);
    }
}
