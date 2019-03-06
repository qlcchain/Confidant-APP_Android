package com.stratagile.pnrouter.data.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.ui.activity.main.MainActivity;
import com.stratagile.pnrouter.utils.OkHttpUtils;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Notification.PRIORITY_MAX;
import static android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT;
import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;


/**
 * Created by zl on 2019/2/27
 */

public class BackGroundService extends Service {
    NotificationCompat.Builder notification;
    private Context mContext;
    private MediaPlayer bgmediaPlayer;
    private boolean isrun = true;

    public BackGroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        //新增---------------------------------------------
        String CHANNEL_ONE_ID = "com.primedu.cn";
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //1.通知栏占用，不清楚的看官网或者音乐类APP的效果
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(mContext)
                    .setChannelId(CHANNEL_ONE_ID)
                    .setSmallIcon(R.drawable.mipush_small_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(),R.mipmap.ic_launcher))
                    .setWhen(System.currentTimeMillis())
                    .setTicker(AppConfig.instance.getString(R.string.app_name))
                    .setContentTitle(AppConfig.instance.getString(R.string.app_name))
                    .setContentText(AppConfig.instance.getString(R.string.MessageRetrievalService_background_connection_enabled))
                    .setOngoing(false)
                    .setPriority(PRIORITY_DEFAULT)
                    .setAutoCancel(false)
                    .setWhen(0)
                    .setSound(null)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(Notification.VISIBILITY_PRIVATE);
        }else{
           notification = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.mipush_small_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(),R.mipmap.ic_launcher))
                    .setTicker(AppConfig.instance.getString(R.string.app_name))
                    .setContentTitle(AppConfig.instance.getString(R.string.app_name))
                    .setContentText(AppConfig.instance.getString(R.string.MessageRetrievalService_background_connection_enabled))
                    .setOngoing(false)
                    .setPriority(PRIORITY_DEFAULT)
                    .setWhen(0)
                    .setAutoCancel(false)
                    .setSound(null);
        }
        /*使用startForeground,如果id为0，那么notification将不会显示*/
        startForeground(313399, notification.build());
        KLog.i("ppm 注册小米推送。。");
        MiPushClient.registerPush(this, AppConfig.instance.getMI_PUSH_APP_ID(), AppConfig.instance.getMI_PUSH_APP_KEY());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        isrun = false;
        stopForeground(true);
        if (bgmediaPlayer != null) {
            bgmediaPlayer.release();
        }
        stopSelf();
        super.onDestroy();
    }

}
