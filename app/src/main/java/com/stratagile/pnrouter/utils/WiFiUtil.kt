package com.stratagile.pnrouter.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.stratagile.pnrouter.constant.ConstantValue

/**
 * Created by zl on 2018/9/7.
 */

object WiFiUtil {

    /**
     * 返回网关地址
     */
    fun  getGateWay(context: Context): String {

        return ConstantValue.currentRouterIp
        /*val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val di = wm.dhcpInfo
        val getewayIpL = di.gateway
        return Formatter.formatIpAddress(getewayIpL)*/
    }
}
