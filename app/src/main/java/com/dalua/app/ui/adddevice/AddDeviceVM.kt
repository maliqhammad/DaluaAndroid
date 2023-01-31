package com.dalua.app.ui.adddevice

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddDeviceVM @Inject constructor(private val repository: RemoteDataRepository) :
    ViewModel() {


    val isBluetoothConnected: MutableLiveData<Boolean> = MutableLiveData(false)
    var backEndAddedDevice: Device? = null
    var deviceToConnected: DeviceAddedPlace? = null
    var groupId: Int? = null
    var macAddress = ""
    var finishActivityData: MutableLiveData<Boolean> = MutableLiveData()
    var disconnectBluetooth: MutableLiveData<Boolean> = MutableLiveData(false)
    var unregisterTheReceiver: Boolean = false
    var groupOfTheDevice: AquariumGroup? = null
    var inflatedDevices: MutableList<DeviceAddedPlace> = mutableListOf()
    var apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    var aquariumClicked: SingleAquarium? = null
    var deviceClicked: MutableLiveData<Int> = MutableLiveData()

    fun onDeviceClicked(view_position: Int) {
        deviceClicked.value = view_position
    }

    fun backPressed() {
        finishActivityData.value = true
    }

    //    you have to get the mac address of ble device here

    fun createDeviceApi(name: String, deviceName: String, timezone: String) {

        val macAddress = deviceToConnected!!.bleDevice.name.split("-")

        this.macAddress = macAddress[macAddress.size - 1].uppercase(Locale.ROOT)
        Log.d("TAG", "createDeviceApi: " + this.macAddress)
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.createDeviceApi(
                name = name,
                product = deviceName,
                aquarium_id = aquariumClicked!!.id.toString(),
                group_id = groupId,
                mac_address = this@AddDeviceVM.macAddress,
                timezone = timezone
            ).collect {
                apiResponse.value = it
            }
        }

    }

    fun updateDeviceApi(device: Device, timezone: String) {

        viewModelScope.launch {

            repository.updateDeviceApiName(
                name = device.name,
                id = device.id.toString(),
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
                id = device.id.toString(),
                completed = completed,
                wifi = wifi,
                water_type = water_type,
                ipAddress = device.ipAddress
            ).collect {
                apiResponse.value = it
            }
        }


    }

    fun checkMacAddress(device: List<String>) {

        viewModelScope.launch {
            repository.checkMacAddress(
                device
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