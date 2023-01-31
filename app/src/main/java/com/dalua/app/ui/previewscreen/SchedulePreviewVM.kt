package com.dalua.app.ui.previewscreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.*
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class SchedulePreviewVM @Inject constructor(val repository: RemoteDataRepository) : ViewModel() {

    val name: MutableLiveData<String> = MutableLiveData()
    val configuration: MutableLiveData<Configuration> = MutableLiveData()
    val device: MutableLiveData<Device> = MutableLiveData()
    val group: MutableLiveData<AquariumGroup> = MutableLiveData()
    val schedule: MutableLiveData<SingleSchedule> = MutableLiveData()
    val weatherImage: MutableLiveData<Int> = MutableLiveData()
    val temperature: MutableLiveData<String> = MutableLiveData()
    val rain: MutableLiveData<String> = MutableLiveData()
    val location: MutableLiveData<Boolean> = MutableLiveData(false)
    val aValue: MutableLiveData<String> = MutableLiveData()
    val bValue: MutableLiveData<String> = MutableLiveData()
    val cValue: MutableLiveData<String> = MutableLiveData()
    val sunSet: MutableLiveData<String> = MutableLiveData("17:00")
    val sunRise: MutableLiveData<String> = MutableLiveData("08:00")
    val ramp: MutableLiveData<String> = MutableLiveData("4:00")
    val rampStart: MutableLiveData<String> = MutableLiveData("12:00")
    val rampEnd: MutableLiveData<String> = MutableLiveData("21:00")
    val backPressed: MutableLiveData<Boolean> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val moonLightEnable: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEnableLocation: MutableLiveData<Boolean> = MutableLiveData(false)

    val listOfTimeValues: MutableList<AdvanceTimeModel> = mutableListOf()
    val listOfValueAndStep: MutableList<AdvanceValuesModel> = mutableListOf()
    val aquarium: MutableLiveData<SingleAquarium> = MutableLiveData()

    fun onBackPressed() {
        backPressed.value = true
    }

    fun getMyScheduleApi(deviceId: Int?, groupId: Int?) {
        Log.d("TAG", "getMyScheduleApi: $deviceId")
        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.getPreviewSchedule(
                "listing",
                deviceId,
                groupId,
                AppConstants.ApiTypes.GetMySchedules.name
            )
                .collect {
                    apiResponse.value = it
                }

        }
    }
}