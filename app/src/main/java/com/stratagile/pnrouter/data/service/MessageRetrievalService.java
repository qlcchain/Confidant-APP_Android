package com.stratagile.pnrouter.data.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.socks.library.KLog;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.data.web.SignalServiceMessagePipe;
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageRetrievalService extends Service implements InjectableType, RequirementListener {

    private static final String TAG = MessageRetrievalService.class.getSimpleName();

    public static final  String ACTION_ACTIVITY_STARTED  = "ACTIVITY_STARTED";
    public static final  String ACTION_ACTIVITY_FINISHED = "ACTIVITY_FINISHED";
    public static final  String ACTION_PUSH_RECEIVED     = "PUSH_RECEIVED";
    public static final  String ACTION_INITIALIZE        = "INITIALIZE";
    public static final  int    FOREGROUND_ID            = 313399;

    private static final long   REQUEST_TIMEOUT_MINUTES  = 1;

//    private NetworkRequirement         networkRequirement;
//    private NetworkRequirementProvider networkRequirementProvider;

    public PNRouterServiceMessageReceiver receiver;

    private int                    activeActivities = 0;
    private List<Intent> pushPending      = new LinkedList<>();
    private MessageRetrievalThread retrievalThread  = null;

    public static SignalServiceMessagePipe pipe = null;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           // startForeground(Integer.MAX_VALUE,new Notification()); //这个id不要和应用内的其他同志id一样，不行就写 int.maxValue()        //context.startForeground(SERVICE_ID, builder.getNotification());
        }
        receiver = AppConfig.instance.getPNRouterServiceMessageReceiver();

//        networkRequirement         = new NetworkRequirement(this);
//        networkRequirementProvider = new NetworkRequirementProvider(this);
//
//        networkRequirementProvider.setListener(this);

        retrievalThread = new MessageRetrievalThread();
        retrievalThread.start();

        setForegroundIfNecessary();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;

        if      (ACTION_ACTIVITY_STARTED.equals(intent.getAction()))  incrementActive();
        else if (ACTION_ACTIVITY_FINISHED.equals(intent.getAction())) decrementActive();
        else if (ACTION_PUSH_RECEIVED.equals(intent.getAction()))     incrementPushReceived(intent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (retrievalThread != null) {
            retrievalThread.stopThread();
        }

        sendBroadcast(new Intent("org.thoughtcrime.securesms.RESTART"));
    }

    @Override
    public void onRequirementStatusChanged() {
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setForegroundIfNecessary() {
//        if (TextSecurePreferences.isGcmDisabled(this)) {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannels.OTHER);
//            builder.setContentTitle(getString(R.string.MessageRetrievalService_signal));
//            builder.setContentText(getString(R.string.MessageRetrievalService_background_connection_enabled));
//            builder.setPriority(NotificationCompat.PRIORITY_MIN);
//            builder.setWhen(0);
//            builder.setSmallIcon(R.drawable.ic_signal_grey_24dp);
//            startForeground(FOREGROUND_ID, builder.build());
//        }
    }

    private synchronized void incrementActive() {
        activeActivities++;
        KLog.d("Active Count: " + activeActivities);
        notifyAll();
    }

    private synchronized void decrementActive() {
        activeActivities--;
        KLog.d("Active Count: " + activeActivities);
        notifyAll();
    }

    private synchronized void incrementPushReceived(Intent intent) {
        pushPending.add(intent);
        notifyAll();
    }

    private synchronized void decrementPushReceived() {
        if (!pushPending.isEmpty()) {
            Intent intent = pushPending.remove(0);
//            GcmBroadcastReceiver.completeWakefulIntent(intent);
            notifyAll();
        }
    }

    private synchronized boolean isConnectionNecessary() {
//        boolean isGcmDisabled = TextSecurePreferences.isGcmDisabled(this);

//        Log.d(TAG, String.format("Network requirement: %s, active activities: %s, push pending: %s, gcm disabled: %b",
//                networkRequirement.isPresent(), activeActivities, pushPending.size(), isGcmDisabled));

//        return TextSecurePreferences.isPushRegistered(this)                       &&
//                TextSecurePreferences.isWebsocketRegistered(this)                  &&
//                (activeActivities > 0 || !pushPending.isEmpty() || isGcmDisabled)  &&
//                networkRequirement.isPresent();
        return true;
    }

    private synchronized void waitForConnectionNecessary() {
        try {
            while (!isConnectionNecessary()) wait();
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
    }

    private void shutdown(SignalServiceMessagePipe pipe) {
        try {
            pipe.shutdown();
        } catch (Throwable t) {
            KLog.w( t);
        }
    }

    public static void registerActivityStarted(Context activity) {
        Intent intent = new Intent(activity, MessageRetrievalService.class);
        intent.setAction(MessageRetrievalService.ACTION_ACTIVITY_STARTED);
        activity.startService(intent);
    }

    public static void registerActivityStopped(Context activity) {
        Intent intent = new Intent(activity, MessageRetrievalService.class);
        intent.setAction(MessageRetrievalService.ACTION_ACTIVITY_FINISHED);
        activity.startService(intent);
    }
    public static void stopThisService(Context activity) {
        Intent intent = new Intent(activity, MessageRetrievalService.class);
        activity.stopService(intent);
    }
    public static @Nullable
    SignalServiceMessagePipe getPipe() {
        return pipe;
    }

    private class MessageRetrievalThread extends Thread implements Thread.UncaughtExceptionHandler {

        private AtomicBoolean stopThread = new AtomicBoolean(false);

        MessageRetrievalThread() {
            super("MessageRetrievalService");
            setUncaughtExceptionHandler(this);
        }

        @Override
        public void run() {
            while (!stopThread.get()) {
                KLog.i("Waiting for websocket state change....");
                waitForConnectionNecessary();

                KLog.i("Making websocket connection....");
                pipe = receiver.createMessagePipe();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                KLog.i( "Looping...");
            }

            KLog.i("Exiting...");
        }

        private void stopThread() {
            stopThread.set(true);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            KLog.w("*** Uncaught exception!");
            KLog.w(e);
        }
    }
}
