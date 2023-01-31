package com.dalua.app.ui.createschedule

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.*
import com.dalua.app.models.schedulemodel.SingleSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class CreateScheduleActivityVM @Inject constructor(private val repository: RemoteDataRepository) :
    ViewModel() {

    val TAG = "CreateScheduleActivityVM"
    var killActivity: Boolean = true
    var launchFrom: MutableLiveData<String> = MutableLiveData()
    var scheduleType: MutableLiveData<Int> = MutableLiveData()
    val croller1Progress: MutableLiveData<Int> = MutableLiveData(0)
    val croller2Progress: MutableLiveData<Int> = MutableLiveData(0)
    val croller3Progress: MutableLiveData<Int> = MutableLiveData(0)
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val apiResponse2: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val saveClicked: MutableLiveData<Boolean> = MutableLiveData()
    val schedule: MutableLiveData<SingleSchedule> = MutableLiveData()
    val waterType: MutableLiveData<String> = MutableLiveData()
    val configuration: MutableLiveData<Configuration> = MutableLiveData()
    val device: MutableLiveData<Device> = MutableLiveData()
    val progress: MutableLiveData<Boolean> = MutableLiveData(false)
    val group: MutableLiveData<AquariumGroup> = MutableLiveData()
    val sendDataToBleNow: MutableLiveData<Boolean> = MutableLiveData()
    val saveText: MutableLiveData<String> = MutableLiveData()
    val scheduleNameText: MutableLiveData<String> = MutableLiveData("New Schedule")
    val backPressed: MutableLiveData<Boolean> = MutableLiveData()
    val resendSchedule: MutableLiveData<Boolean> = MutableLiveData(true)
    val isAllGroupDevicesConnect: MutableLiveData<Boolean> = MutableLiveData(false)
    val saveVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val tabVisibility: MutableLiveData<Boolean> = MutableLiveData()
    var isAwsConnected: MutableLiveData<Boolean> = MutableLiveData(false)
    var mqttManager: AWSIotMqttManager? = null
    val socketResponseModel: MutableLiveData<SocketACKResponseModel> = MutableLiveData()
    val aquarium: MutableLiveData<SingleAquarium> = MutableLiveData()
    val isDialogOpen: MutableLiveData<Boolean> = MutableLiveData(false)

    fun saveScheduleClicked() {
        saveClicked.value = true
    }

    fun onBackPressed() {
        backPressed.value = true
    }

    fun reSendSchedule(id: Int?, d_g: String) {

        Log.d(TAG, "reSendSchedule: CreateScheduleActivity: ")
        Log.d(TAG, "CreateScheduleActivity: $id : $d_g")

        val map: HashMap<String, String> = HashMap()
        map[d_g] = id.toString()
        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.reUploadSchedule(map).collect {
                apiResponse.value = it
            }
        }

    }

    fun reSendSchedule2(id: Int?, d_g: String) {

        Log.d(TAG, "reSendSchedule: CreateScheduleActivity: ")
        Log.d(TAG, "CreateScheduleActivity: $id : $d_g")

        val map: HashMap<String, String> = HashMap()
        map[d_g] = id.toString()
        apiResponse2.value = Resource.Loading()
        viewModelScope.launch {
            repository.reUploadSchedule(map).collect {
                apiResponse2.value = it
            }
        }

    }


}