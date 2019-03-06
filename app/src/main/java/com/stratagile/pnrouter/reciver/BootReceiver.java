package com.stratagile.pnrouter.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.socks.library.KLog;
import com.stratagile.pnrouter.data.service.BackGroundService;

public class BootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i("ppm", "收到了静态广播");
    if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      Log.i("","ppm 收到了手机启动完成的广播");
      Intent messageRetrievalService = new Intent(context, BackGroundService.class);
      context.startService(messageRetrievalService);
    }
  }

}
