package com.dalua.app.ui.taptocomplete

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.AquariumGroup
import com.dalua.app.models.Device
import com.dalua.app.models.DeviceAddedPlace
import com.dalua.app.models.SingleAquarium
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class TabToConnectVM @Inject constructor(private val repository: RemoteDataRepository) :
    ViewModel() {


    val isBluetoothConnected: MutableLiveData<Boolean> = MutableLiveData(false)
    var disconnectBluetooth: MutableLiveData<Boolean> = MutableLiveData(false)
    var backendAddedDevice: Device? = null
    var deviceToConnected: DeviceAddedPlace? = null
    var groupId: Int? = null
    var macAddress = ""
    var finishActivityData: MutableLiveData<Boolean> = MutableLiveData()
    var unregisterTheReciver: Boolean = false
    var groupOfTheDevice: AquariumGroup? = null
    var apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    var aquariumClicked: SingleAquarium? = null

    fun backPressed() {
        finishActivityData.value = true
    }

    fun updateDeviceApi(device: Device, timezone: String) {

        viewModelScope.launch {

            repository.updateDeviceApiName(
                device.name,
                device.id.toString(),
                group_id = groupId,
                aquarium_id = aquariumClicked!!.id.toString(),
                ip_address = device.ipAddress,
                timezone = timezone
            ).collect {

            }
        }


    }

    fun updateDeviceStatusApi(
        device: Device,
        completed: Int,
        wifi: String,
        water_type: String
    ) {

        viewModelScope.launch {
            repository.updateDeviceStatusApi(
                device.id.toString(),
                completed,
                wifi,
                water_type,
                device.ipAddress
            ).collect {
                apiResponse.value = it
            }
        }


    }

    fun deleteDeviceApi(id: String) {

        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.deleteDeviceApi(id).collect {
                apiResponse.value = it
            }
        }

    }

}