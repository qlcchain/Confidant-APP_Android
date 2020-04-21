package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FireBaseUtils {
    //url = https://github.com/huzhipeng111/Confidant-APP_Android.git
    public static String eventLogin = "login";
    public static String eventStartApp = "startApp";
    public static String eventRegiester = "regiester";
    public static String eventTest = "test";
    public static void logEvent(Context context, String event) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, event);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, event);
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
    }
}
