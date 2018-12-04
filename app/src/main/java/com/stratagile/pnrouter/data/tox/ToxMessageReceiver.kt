package com.stratagile.pnrouter.data.tox

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import events.ToxMessageEvent
import events.ToxStatusEvent
import interfaceScala.InterfaceScaleUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ToxMessageReceiver(){


    init {
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxMessageEvent: ToxMessageEvent) {

       var aa = "";
    }
}