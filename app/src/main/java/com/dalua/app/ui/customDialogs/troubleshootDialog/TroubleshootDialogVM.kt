package com.dalua.app.ui.customDialogs.troubleshootDialog

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dalua.app.R
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.Device
import com.dalua.app.models.DeviceAddedPlace
import com.dalua.app.models.SingleAquarium
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class TroubleshootDialogVM @Inject constructor(val repository: RemoteDataRepository) : ViewModel() {

    val title: MutableLiveData<String> = MutableLiveData("Troubleshoot")
    val description: MutableLiveData<String> =
        MutableLiveData("Connection lost with the device. Bluetooth connection is required to fix it. Make sure that you are near to the device. Would you like to continue?")
    val stateCode: MutableLiveData<Int> = MutableLiveData(1234)
    val buttonText: MutableLiveData<String> = MutableLiveData("Continue")
    val wifi: MutableLiveData<Boolean> = MutableLiveData(false)
    val ble: MutableLiveData<Boolean> = MutableLiveData(false)
    val aws: MutableLiveData<Boolean> = MutableLiveData(false)

    val ble_1: MutableLiveData<Boolean> = MutableLiveData(false)
    val wifi_1: MutableLiveData<Boolean> = MutableLiveData(false)
    val aws_1: MutableLiveData<Boolean> = MutableLiveData(false)

    val success: MutableLiveData<Boolean> = MutableLiveData(false)
    val connectivity: MutableLiveData<Int> = MutableLiveData(R.drawable.ic_error_connection_24)
    val progress: MutableLiveData<Boolean> = MutableLiveData(false)
    val bleConnectivity: MutableLiveData<Boolean> = MutableLiveData(false)
    var backendAddedDevice: Device? = null
    var deviceToConnected: DeviceAddedPlace? = null
    var groupId: Int? = null
    var macAddress = ""
    var finishActivityData: MutableLiveData<Boolean> = MutableLiveData()
    var unregisterTheReciver: Boolean = false
    var apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    var aquariumClicked: SingleAquarium? = null
    val bleDeviceCode: MutableLiveData<Int> = MutableLiveData(0)
    val deviceImage: MutableLiveData<Int> = MutableLiveData(R.drawable.ic_phone)
    val lightImage: MutableLiveData<Int> = MutableLiveData(R.drawable.light_img)
    val wifiConnectivity: MutableLiveData<Boolean> = MutableLiveData(false)
    val awsConnectivity: MutableLiveData<Boolean> = MutableLiveData(false)
    var startPairing: MutableLiveData<Int> = MutableLiveData()
    var inflatedDevices: MutableList<DeviceAddedPlace> = mutableListOf()


    fun backPressed(view: View) {
        finishActivityData.value = true
    }

}