package com.stratagile.pnrouter.reciver;

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;
import com.socks.library.KLog;

public class HuaweiPushReceiver extends PushReceiver {
    @Override
    public void onToken(Context context, String token, Bundle extras) {

        //开发者自行实现Token保存逻辑。
        KLog.i("华为推送的token为：" + token);

    }
    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {

        //开发者自行实现透传消息处理。
        return true;
    }
}
