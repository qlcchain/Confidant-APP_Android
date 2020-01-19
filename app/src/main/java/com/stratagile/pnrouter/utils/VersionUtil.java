package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.socks.library.KLog;

/**
 * Created by Anroid on 2017/3/1.
 */

public class VersionUtil {
    /**
     * 返回当前程序版本名 int类型
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = -1;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }
    /**
     * 返回当前程序版本名 int类型
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
    /**
     * 品牌索引
     */
    public static int getDeviceBrand() {
        int type = 0;
        switch (android.os.Build.BRAND.toLowerCase())
        {
            case "xiaomi":
                type = 2;
                break;
            case "huawei":
                type = 3;
                break;
            case "honor":
                type = 3;
                break;
            case "zhongxing":
                type = 4;
                break;
            case "oppo":
                type = 5;
                break;
            case "vivo":
                type = 6;
                break;
            case "meizhu":
                type = 7;
                break;
            case "onejia":
                type = 8;
                break;
            default:
                type = 0;
                break;

        }
        return type;
    }

}
