package com.stratagile.tox.toxcallback

enum class ToxConnection {
    NONE,
    TCP,
    UDP;
    companion object {
        fun parseToxConnect(int: Int) : ToxConnection{
            when (int) {
                0 -> return ToxConnection.NONE
                1 -> return ToxConnection.TCP
                2 -> return ToxConnection.UDP
            }
            return ToxConnection.NONE
        }
    }
}