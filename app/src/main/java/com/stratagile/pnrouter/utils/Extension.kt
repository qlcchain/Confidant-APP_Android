package com.stratagile.pnrouter.utils


fun <T : Any>MutableList<T>.MutableListToArrayList() : ArrayList<T> {
    val list = ArrayList<T>()
    forEach {
        list.add(it)
    }
    return list
}