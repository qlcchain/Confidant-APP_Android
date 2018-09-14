package com.stratagile.pnrouter.ui.activity.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var toAddUserId = MutableLiveData<String>()

    var freindChange = MutableLiveData<Long>()
}