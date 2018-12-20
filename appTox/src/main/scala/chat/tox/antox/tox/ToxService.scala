
package chat.tox.antox.tox

import android.app.{Notification, Service}
import android.content.Intent
import android.os.{Build, IBinder}
import android.preference.PreferenceManager
import chat.tox.antox.av.CallService
import chat.tox.antox.callbacks.{AntoxOnSelfConnectionStatusCallback, ToxCallbackListener, ToxavCallbackListener}
import chat.tox.antox.utils.AntoxLog
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.impl.jni.ToxJniLog
import rx.lang.scala.schedulers.AndroidMainThreadScheduler
import rx.lang.scala.{Observable, Subscription}

import scala.concurrent.duration._
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ToxService extends Service {

  private var serviceThread: Thread = _

  private var keepRunning: Boolean = true

  private val connectionCheckInterval = 10000 //in ms

  private val reconnectionIntervalSeconds = 1

  private var reconnectionCount = 1;

  private var callService: CallService = _

  override def onCreate() {
    if (!ToxSingleton.isInited) {
      ToxSingleton.initTox(getApplicationContext)
      AntoxLog.debug("Initting ToxSingleton")
    }
    /* if (Build.VERSION.SDK_INT >= 26) startForeground(Integer.MAX_VALUE-1, new Notification)*/
    //这个id不要和应用内的其他同志id一样，不行就写 int.maxValue()        //context.startForeground(SERVICE_ID, builder.getNotification());
    reconnectionCount = 1
    keepRunning = true
    val thisService = this

    val start = new Runnable() {

      override def run() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext)

        callService = new CallService(thisService)
        callService.start()

        val toxCallbackListener = new ToxCallbackListener(thisService)
        val toxAvCallbackListener = new ToxavCallbackListener(thisService)

        var reconnection: Subscription = null

        val connectionSubscription = AntoxOnSelfConnectionStatusCallback.connectionStatusSubject
          .observeOn(AndroidMainThreadScheduler())
          .distinctUntilChanged
          .subscribe(toxConnection => {
            if (toxConnection != ToxConnection.NONE) {
              if (reconnection != null && !reconnection.isUnsubscribed) {
                reconnection.unsubscribe()
              }
              EventBus.getDefault().post(new ToxStatusEvent(0))
              AntoxLog.debug("Tox connected. Stopping reconnection")
            } else {
              reconnection = Observable
                .interval((reconnectionIntervalSeconds * reconnectionCount) seconds)
                .subscribe(x => {
                  AntoxLog.debug("Reconnecting")
                  Observable[Boolean](_ => ToxSingleton.bootstrap(getApplicationContext)).subscribe()
                })
              EventBus.getDefault().post(new ToxStatusEvent(1))
              AntoxLog.debug(s"Tox disconnected. Scheduled reconnection every " + (reconnectionIntervalSeconds * reconnectionCount) + " seconds")
              reconnectionCount = reconnectionCount + 1;
            }
          })

        var ticks = 0
        while (keepRunning) {
          if (!ToxSingleton.isToxConnected(preferences, thisService)) {
            try {
              Thread.sleep(connectionCheckInterval)
            } catch {
              case e: Exception =>
            }
          } else {
            try {
              ToxSingleton.tox.iterate(toxCallbackListener)
              ToxSingleton.toxAv.iterate(toxAvCallbackListener)

              if (ticks % 100 == 0) {
                println(ToxJniLog().entries.filter(_.name == "tox4j_video_receive_frame_cb").map(_.elapsedNanos).toList.map(nanos => s" elapsed nanos video cb: $nanos").mkString("\n"))
              }

              Thread.sleep(Math.min(ToxSingleton.interval, ToxSingleton.toxAv.interval))
              ticks += 1
            } catch {
              case e: Exception =>
                e.printStackTrace()
            }
          }
        }

        connectionSubscription.unsubscribe()
      }
    }

    serviceThread = new Thread(start)
    serviceThread.start()
  }

  override def onBind(intent: Intent): IBinder = null

  override def onStartCommand(intent: Intent, flags: Int, id: Int): Int = Service.START_STICKY

  override def onDestroy() {
    super.onDestroy()
    keepRunning = false

    serviceThread.interrupt()
    serviceThread.join()

    callService.destroy()

    ToxSingleton.save()
    ToxSingleton.isInited = false
    ToxSingleton.tox.close()
    AntoxLog.debug("onDestroy() called for Tox service")
  }
}
