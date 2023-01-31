package com.dalua.app.ui.customDialogs.devicesListDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dalua.app.models.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DevicesListDialogVM @Inject constructor() : ViewModel() {

    var title: MutableLiveData<String> = MutableLiveData()
    var button: MutableLiveData<String> = MutableLiveData()
    var progressBar: MutableLiveData<Boolean> = MutableLiveData()
    var error: MutableLiveData<Boolean> = MutableLiveData(false)
    var devicesList: MutableLiveData<List<Device>> = MutableLiveData()
    var isDevicesConnected: MutableLiveData<Boolean> = MutableLiveData(false)
    var success: MutableLiveData<Boolean> = MutableLiveData(false)
    var isReceiveACK: MutableLiveData<Boolean> = MutableLiveData(false)
    var device: MutableLiveData<Device> = MutableLiveData()


    fun onDiagnoseClick(device: Device) {
        this.device.value = device
    }
}