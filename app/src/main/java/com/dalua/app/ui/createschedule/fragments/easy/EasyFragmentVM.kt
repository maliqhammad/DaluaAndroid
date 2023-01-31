package com.dalua.app.ui.createschedule.fragments.easy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.GeoLocationResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class EasyFragmentVM @Inject constructor(val repository: RemoteDataRepository) : ViewModel() {

    var geoLocationList: GeoLocationResponse = GeoLocationResponse()
    var animationStarted: MutableLiveData<Boolean> = MutableLiveData(false)
    val showDialog: MutableLiveData<Boolean> = MutableLiveData()
    var saveAndUpload: Int = 0
    var stayOnEasyFragment: Boolean = true
    val endTimeDialog: MutableLiveData<Boolean> = MutableLiveData()
    val startTimeDialog: MutableLiveData<Boolean> = MutableLiveData()
    val rampTimePopUp: MutableLiveData<Boolean> = MutableLiveData()
    val sunSetEnable: MutableLiveData<Boolean> = MutableLiveData(true)
    val rampTimeEnable: MutableLiveData<Boolean> = MutableLiveData(true)
    val singleSchedule: MutableLiveData<SingleSchedule> = MutableLiveData()
    val sunSet: MutableLiveData<String> = MutableLiveData("17:00")
    val sunRise: MutableLiveData<String> = MutableLiveData("08:00")
    val ramp: MutableLiveData<String> = MutableLiveData("4:00")
    val rampStart: MutableLiveData<String> = MutableLiveData("12:00")
    val rampEnd: MutableLiveData<String> = MutableLiveData("21:00")
    val geoLocationEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val quickPlay: MutableLiveData<Boolean> = MutableLiveData()
    var isDialogNameShow: Boolean = true
    val changeControl: MutableLiveData<String> = MutableLiveData()


    fun changeControl() {

        if (changeControl.value.contentEquals("instant"))
            changeControl.value = "master"
        else
            changeControl.value = "instant"

    }

    fun showDialog() {
        saveAndUpload = 0
        showDialog.value = true
    }

    fun saveAndUpload() {
        saveAndUpload = 1
        showDialog.value = true
    }

    fun quickPlay() {
        quickPlay.value = true
    }

    fun startTimeClicked() {
        startTimeDialog.value = true
    }

    fun endTimeClicked() {
        endTimeDialog.value = true
    }

    fun rampTimeClicked() {
        rampTimePopUp.value = true
    }

    fun onCheckedChanged() {
        geoLocationEnabled.value = true
    }

    fun onCheckedChangedforswitch(switch: Boolean) {
        if (switch) {
            if (!stayOnEasyFragment) {
                geoLocationEnabled.value = true
                stayOnEasyFragment = true
            }

        } else {
            stayOnEasyFragment = false
            geoLocationEnabled.value = false
        }
    }

    fun updateScheduleApi(
        name: String,
        moon_light: String,
        hashmap: HashMap<String, String>,
        public: Int,
        id: Int,
        geo_location: Int,
        geo_location_id: Int?,
        enabled: Int,
        mode: Int,
        sunrise: String,
        sunset: String,
        value_a: String,
        value_b: String,
        value_c: String,
        ramp_time: String,
    ) {
        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.updateEasySchedule(
                name,
                moon_light,
                hashmap,
                public,
                id,
                geo_location,
                geo_location_id,
                enabled,
                mode,
                sunrise,
                sunset,
                value_a,
                value_b,
                value_c,
                ramp_time,
            ).collect {
                apiResponse.value = it
            }
        }
    }

    fun createScheduleApi(
        name: String,
        moon_light: String,
        public: Int,
        device_or_group_id: HashMap<String, String>,
        geo_location: Int,
        geo_location_id: Int?,
        enabled: Int,
        mode: Int,
        sunrise: String,
        sunset: String,
        value_a: String,
        value_b: String,
        value_c: String,
        ramp_time: String,
    ) {
        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.createEasySchedule(
                name,
                moon_light,
                public,
                device_or_group_id,
                geo_location,
                geo_location_id,
                enabled,
                mode,
                sunrise,
                sunset,
                value_a,
                value_b,
                value_c,
                ramp_time,
            ).collect {
                apiResponse.value = it
            }
        }
    }

    fun checkAckMacAddress(device: List<String>) {
        viewModelScope.launch {
            repository.checkAckMacAddress(
                device
            ).collect {
                apiResponse.value = it
            }
        }
    }

}