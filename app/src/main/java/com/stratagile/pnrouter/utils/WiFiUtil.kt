package com.stratagile.pnrouter.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import android.support.v4.content.ContextCompat.getSystemService



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
    fun isNetworkConnected(): Boolean {
        if (AppConfig.instance != null) {
            val mConnectivityManager = AppConfig.instance
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }
}

