package com.dalua.app.ui.wifiactivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShowWifiConnectionVM @Inject constructor() : ViewModel() {
    val timerTime: MutableLiveData<String> = MutableLiveData("00:00")
    val enableNow: MutableLiveData<Boolean> = MutableLiveData(false)
    val backPress: MutableLiveData<Boolean> = MutableLiveData(false)

    fun backPressed() {
        backPress.value = true
    }

}