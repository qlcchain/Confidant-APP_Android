package com.stratagile.pnrouter.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.stratagile.pnrouter.application.AppConfig
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

    fun isWifiConnect() : Boolean{
        var connManager  = AppConfig.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return mWifi.isConnected()
    }
    //判断移动数据是否打开
    fun isMobile() : Boolean {
        var connManager = AppConfig.instance.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true
        }
        return false
    }
}

