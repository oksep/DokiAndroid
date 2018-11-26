package com.dokiwa.dokidoki.center.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.dokiwa.dokidoki.center.Log;
import com.dokiwa.dokidoki.center.ext.StringExtKt;

import java.util.UUID;


/**
 * Created by adam on 9/23/14.
 */
public class ContextHelper {

    private static final String SUBSYSTEM_CATEGORY = "ContextHelper";

    /**
     * 获取操作系统版本
     *
     * @return
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取设备名
     **/
    public static String getModel() {
        return Build.MODEL;
    }


    private static int sVerCode = 0;

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        if (sVerCode != 0) {
            return sVerCode;
        }

        try {
            final String packageName = context.getPackageName();
            sVerCode = context.getPackageManager()
                    .getPackageInfo(packageName, 0)
                    .versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sVerCode;
    }


    private static String sVerName = "";

    /**
     * //获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {

        if (!TextUtils.isEmpty(sVerName)) {
            return sVerName;
        }
        try {
            sVerName =
                    context.getPackageManager()
                            .getPackageInfo(context.getPackageName(), 0)
                            .versionName;
        } catch (Exception e) {
            Log.INSTANCE.e(SUBSYSTEM_CATEGORY, "get version name exception", e);
        }
        return sVerName;

    }


    private static String sDeviceId = "";

    /**
     * 获取设备 ID
     *
     * @return
     */
    public static String getDeviceId(Context context) {
        if (!TextUtils.isEmpty(sDeviceId)) {
            return sDeviceId;
        }

        sDeviceId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (TextUtils.isEmpty(sDeviceId)) {
            Log.INSTANCE.w(SUBSYSTEM_CATEGORY, "dz[getDeviceId is null]", null);
        }

        return sDeviceId;
    }


    /**
     * Get device name, manufacturer + model
     *
     * @return device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return StringExtKt.capitalize(model);
        } else {
            return StringExtKt.capitalize(manufacturer) + " " + model;
        }
    }


    /**
     * 虚拟DeviceId
     *
     * @param context
     * @return
     */
    private static String getVirtualdeviceid(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceId = pref.getString("engzo.prefer.virtualdeviceid", "");
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString().replace("-", "");
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("engzo.prefer.virtualdeviceid", deviceId);
            editor.commit();
        }
        return deviceId;
    }

    public static String getSdeviceId(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String sDeviceId = pref.getString("engzo.prefer.sdeviceid", "");
        if (TextUtils.isEmpty(sDeviceId)) {
            sDeviceId = getDeviceId(context);
            if (!TextUtils.isEmpty(sDeviceId) && !"0".equals(sDeviceId)) {
                pref.edit().putString("engzo.prefer.sdeviceid", sDeviceId).apply();
            }
        }
        return sDeviceId;
    }


//    /**
//     * 是否存在SDCard
//     *
//     * @return
//     */
//    public static boolean isExistSDCard() {
//
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//            return true;
//        else
//            return false;
//    }

    /**
     * 获取屏幕密度
     **/
    public static float getScaledDensity(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.scaledDensity;
    }


    public static boolean isEmulator() {

        try {
            if (Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk")
                    || getSystemProperty("ro.product.model").equals("sdk")
                    || getSystemProperty("ro.product.name").equals("sdk")) {

                return true;
            }
        } catch (Exception e) {
            Log.INSTANCE.e(SUBSYSTEM_CATEGORY, "get isEmulater exception", e);
        }

        return false;

    }

    private static String getSystemProperty(String propertyName) throws Exception {
        Class clazz = Class.forName("android.os.SystemProperties");
        return (String) clazz.getMethod("get", new Class[]{String.class}).invoke(clazz,
                new Object[]{propertyName});
    }

    public static Uri res2Uri(Context context, int resId) {
        return Uri.parse("android.resource://"
                + context.getApplicationContext().getPackageName()
                + "/" + resId);
    }
}
