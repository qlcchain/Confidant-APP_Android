package com.stratagile.pnrouter.data.web

interface ConnectivityListener {
    fun onConnected()
    fun onConnecting()
    fun onDisconnected()
    fun onAuthenticationFailure()
}