package com.dalua.app.ui.groupdetails

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class GroupDetailsVM @Inject constructor(private val repository: RemoteDataRepository) :
    ViewModel() {

    val callMenu: MutableLiveData<Boolean> = MutableLiveData()
    val goBackActivity: MutableLiveData<Boolean> = MutableLiveData()
    val goToNextActivity: MutableLiveData<Boolean> = MutableLiveData()
    val onScheduleControlClicked: MutableLiveData<Boolean> = MutableLiveData()
    val isAllDevicesActive: MutableLiveData<Boolean> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val deviceList: MutableLiveData<MutableList<Device>> = MutableLiveData(mutableListOf())
    val group: MutableLiveData<AquariumGroup> = MutableLiveData()
    val aquariumType: MutableLiveData<Int> = MutableLiveData()
    val device: MutableLiveData<Device> = MutableLiveData()
    var deviceUngroup: Device? = null
    val valueA: MutableLiveData<String> = MutableLiveData()
    val valueB: MutableLiveData<String> = MutableLiveData()
    val valueC: MutableLiveData<String> = MutableLiveData()
    val changeTopicOfTheDevice: MutableLiveData<Boolean> = MutableLiveData()
    val ungroupDevice: MutableLiveData<Device> = MutableLiveData()
    val deviceClick: MutableLiveData<Device> = MutableLiveData()
    var aquariumClicked: SingleAquarium? = null
    var viewDeviveInGroup: View? = null
    val instantControlClick: MutableLiveData<Boolean> = MutableLiveData()


    fun onAddDeviceClicked() {
        goToNextActivity.value = true
    }

    fun backPressed() {
        goBackActivity.value = true
    }

    fun showInstantControl() {
        instantControlClick.value = true
    }

    fun onScheduleControlClicked() {
        onScheduleControlClicked.value = true
    }

    fun showDeviceDeleteMenu(singleDevice: Device, view: View) {
        viewDeviveInGroup = view
        ungroupDevice.value = singleDevice

    }

    fun onDeviceClick(device: Device) {
        deviceClick.value = device

    }

    fun onMenuObjectClicked() {
        callMenu.value = true
    }

    fun deleteGroupApi() {

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.deleteGroupApi(group.value?.id.toString()).collect {
                apiResponse.value = it
            }
        }
    }

    fun getGroupDetailApi() {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.getGroupDetailApi(group.value?.id.toString()).collect {
                apiResponse.value = it
            }
        }
    }

    fun updateGroupApi(name: String, timezone: String) {

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.updateGroupApiName(
                name,
                group.value?.id.toString(),
                group.value!!.aquariumId.toString(),
                timezone = timezone
            ).collect {
                apiResponse.value = it
            }
        }
    }

    fun deleteDeviceApi(singleDevice: Device) {

        apiResponse.value = Resource.Loading()
        device.value = singleDevice
        viewModelScope.launch {
            repository.deleteDeviceApi(singleDevice.id.toString()).collect {
                apiResponse.value = it
            }
        }

    }

    fun updateDeviceApi(
        name: String,
        id: String,
        aquarium_id: String,
        group_id: String,
        ip_address: String? = null,
        timezone: String,
    ) {


        viewModelScope.launch {

            apiResponse.value = Resource.Loading()
            repository.updateDeviceApi(
                name = name,
                id = id,
                aquarium_id = aquarium_id,
                group_id = group_id,
                timezone = timezone
            ).collect {
                apiResponse.value = it
            }

        }
    }


    fun deleteDeviceFromGroupApi(
        singleDevice: Device, timezone: String
    ) {
        viewModelScope.launch {

            apiResponse.value = Resource.Loading()
            repository.unGroupDeviceApi(
                singleDevice.name,
                singleDevice.id.toString(),
                aquarium_id = aquariumClicked?.id.toString(),
                timezone = timezone
            ).collect {
                apiResponse.value = it
            }

        }
    }



    fun loadDevicesFirstPage() {
        Log.d("AquariumDetailsVM", "loadDevicesFirstPage: ")
        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.getUserDevice(aquariumClicked!!.id, group.value!!.id, pageNumber = 1).collect {
                apiResponse.value = it
            }
        }
    }

    fun loadDevicesNextPage(page: Int) {
        Log.d("AquariumDetailsVM", "loadDevicesNextPage: ")
        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.getUserDevice(aquariumClicked!!.id, group.value!!.id, pageNumber = page).collect {
                apiResponse.value = it
            }
        }
    }

}