package com.stratagile.pnrouter.data.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.socks.library.KLog;
import com.stratagile.pnrouter.constant.ConstantValue;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        KLog.i("google fcm token :" + s);
        ConstantValue.INSTANCE.setFcmToken(s);
    }
}
