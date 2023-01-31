package com.dalua.app.ui.devicedetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.Configuration
import com.dalua.app.models.Device
import com.dalua.app.models.SingleAquarium
import com.dalua.app.models.schedulemodel.SingleSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class DeviceDetailsVM @Inject constructor(private val repository: RemoteDataRepository) :
    ViewModel() {

    val finishActivityData: MutableLiveData<Boolean> = MutableLiveData()
    val valueA: MutableLiveData<String> = MutableLiveData()
    val valueB: MutableLiveData<String> = MutableLiveData()
    val valueC: MutableLiveData<String> = MutableLiveData()
    val onScheduleControlClicked: MutableLiveData<Boolean> = MutableLiveData()
    val aquariumType: MutableLiveData<Int> = MutableLiveData()
    val device: MutableLiveData<Device> = MutableLiveData()
    val configuration: MutableLiveData<Configuration> = MutableLiveData()
    val schedule: MutableLiveData<SingleSchedule> = MutableLiveData()
    var aquariumClicked: SingleAquarium? = null
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val resetDevice: MutableLiveData<Boolean> = MutableLiveData()
    val showMenuItem: MutableLiveData<Boolean> = MutableLiveData()
    val showMasterDialogInfo: MutableLiveData<Boolean> = MutableLiveData()
    val showInstantDialogInfo: MutableLiveData<Boolean> = MutableLiveData()
    val instantControlClick: MutableLiveData<Boolean> = MutableLiveData()


    fun getDeviceDetails(id: Int) {

        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.getDeviceDetails(id.toString()).collect {
                apiResponse.value = it
            }
        }

    }

    fun reSendSchedule(id: Int, d_g: String) {


        val map: HashMap<String, String> = HashMap()
        map[d_g] = id.toString()

        viewModelScope.launch {
            repository.reUploadSchedule(map).collect {
                apiResponse.value = it
            }
        }

    }

    fun onMenuObjectClicked() {
        showMenuItem.value = true
    }

    fun backPressed() {
        finishActivityData.value = true
    }

    fun showBottomSheet() {
        instantControlClick.value = true
    }

    fun showMasterControlInfoDialog() {
        showMasterDialogInfo.value = true
    }

    fun showInstantControlInfoDialog() {
        showInstantDialogInfo.value = true
    }


    fun deleteDeviceApi() {

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.deleteDeviceApi(device.value?.id.toString()).collect {
                apiResponse.value = it
            }
        }

    }

    fun updateDeviceWaterType(device: Device, waterType: String) {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.updateDeviceApi(
                id = device.id.toString(),
                name = device.name,
                group_id = "",
                water_type = waterType,
                aquarium_id = aquariumClicked!!.id.toString(),
                ip_address = "",
                timezone = TimeZone.getDefault().id
            ).collect {
                apiResponse.value = it
            }
        }
    }

    fun onScheduleControlClicked() {
        onScheduleControlClicked.value = true
    }


    fun updateDeviceApi(name: String, id: String) {

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.updateDeviceApiName(
                name = name,
                id = device.value!!.id.toString(),
                aquarium_id = aquariumClicked!!.id.toString(),
                ip_address = device.value!!.ipAddress,
                timezone = id
            ).collect {
                apiResponse.value = it
            }
        }
    }


}