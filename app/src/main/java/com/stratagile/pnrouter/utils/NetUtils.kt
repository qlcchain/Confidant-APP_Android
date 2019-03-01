package com.stratagile.pnrouter.utils

import android.content.Context
import java.math.BigDecimal
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v4.content.ContextCompat.getSystemService



object NetUtils {
    fun parseSize(size : Long) : String{
        if (size < 1024 * 1024) {
            return "" + size / 1024 + " KB"
        }
        if (size <1024 * 1024 * 1024) {
            return "" + (size / 1024.0 / 1024.0).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB"
        }
        if (size < 1024 * 1024 * 1024 * 1024) {
            return "" + (size / 1024.0 / 1024.0 / 1024.0).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB"
        }
        return ""
    }

    /**
     * 判断网络情况
     * @param context 上下文
     * @return false 表示没有网络 true 表示有网络
     */
    fun isNetworkAvalible(context: Context): Boolean {
        // 获得网络状态管理器
        val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager == null) {
            return false
        } else {
            // 建立网络数组
            val net_info = connectivityManager!!.getAllNetworkInfo()

            if (net_info != null) {
                for (i in net_info!!.indices) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info!![i].getState() === NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }
}