package com.dokiwa.dokidoki.center.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Septenary on 2019/1/6.
 */
public class AppUtil {

    public static boolean isMainProcess(Context context) {
        String processName = AppUtil.getProcessName(context);
        return AppUtil.isMainProcess(context, processName);
    }

    public static boolean isMainProcess(Context context, final String processName) {
        return processName != null && processName.equals(context.getPackageName());
    }

    public static String getProcessName(final Context context) {

        int pid = android.os.Process.myPid();
        final List<ActivityManager.RunningAppProcessInfo> appProcessInfoList =
                getRunningProcess(context);

        if (appProcessInfoList == null) {
            return null;
        }

        for (int i = 0; i < appProcessInfoList.size(); i++) {
            final ActivityManager.RunningAppProcessInfo info = appProcessInfoList.get(i);
            if (info != null && info.pid == pid) {
                return info.processName;
            }
        }

        return null;
    }

    public static void killProcess(final Context context, final String processName) {
        final List<ActivityManager.RunningAppProcessInfo> appProcessInfoList =
                getRunningProcess(context);
        if (appProcessInfoList == null) {
            return;
        }

        for (int i = 0; i < appProcessInfoList.size(); i++) {
            final ActivityManager.RunningAppProcessInfo info = appProcessInfoList.get(i);
            if (info != null && info.processName != null && info.processName.equals(processName)) {
                android.os.Process.killProcess(info.pid);
                return;
            }
        }
    }

    public static List<ActivityManager.RunningAppProcessInfo> getRunningProcess(
            final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.
                getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager == null) {
            return null;
        }

        return activityManager.getRunningAppProcesses();
    }

    public static void restartApp(Context context) {
        try {
            Class mainClass = Class.forName(context.getPackageName() + ".LaunchActivity");
            Intent dataIntent = new Intent(context, mainClass);
            dataIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent intent;
            intent = PendingIntent.getActivity(context.getApplicationContext(), 0, dataIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            /*Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP*/
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (mgr != null) {
                mgr.set(AlarmManager.RTC, System.currentTimeMillis(), intent);
            }
            killApp();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void killApp() {
        System.exit(-1);
    }

    public static boolean isSupportAbi() {
        List<String> abiList = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (Build.CPU_ABI != null) {
                abiList.add(Build.CPU_ABI);
            }
            if (Build.CPU_ABI2 != null) {
                abiList.add(Build.CPU_ABI2);
            }
        } else {
            Collections.addAll(abiList, Build.SUPPORTED_ABIS);
        }

        boolean isSupport = false;

        for (String abi : abiList) {
            if (abi.equals("armeabi-v7a")) {
                isSupport = true;
            }
        }
        return isSupport;
    }

    public static String getSignature(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            StringBuilder builder = new StringBuilder();
            for (Signature signature : signatures) {
                builder.append(signature.toCharsString());
            }
            return builder.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
