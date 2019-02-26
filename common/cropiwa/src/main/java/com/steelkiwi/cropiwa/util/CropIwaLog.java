package com.steelkiwi.cropiwa.util;

import android.util.Log;

/**
 * Created by yarolegovich on 06.02.2017.
 * https://github.com/yarolegovich
 */
public class CropIwaLog {

    private static final String LOG_TAG = CropIwaLog.class.getSimpleName();

    private static boolean isLogOn = true;

    public static void d(String formatStr, Object... args) {
        if (isLogOn) {
            Log.d(LOG_TAG, String.format(formatStr, args));
        }
    }

    public static void e(String message, Throwable e) {
        if (isLogOn) {
            Log.e(LOG_TAG, message, e);
        }
    }

    public static void setEnabled(boolean enabled) {
        isLogOn = enabled;
    }

}
