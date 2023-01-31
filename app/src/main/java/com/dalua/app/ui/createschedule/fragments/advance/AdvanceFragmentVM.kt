package com.dalua.app.ui.createschedule.fragments.advance

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.AdvanceTimeModel
import com.dalua.app.models.AdvanceValuesModel
import com.dalua.app.models.GeoLocationResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class AdvanceFragmentVM @Inject constructor(val repository: RemoteDataRepository) : ViewModel() {

    var waterType: MutableLiveData<String> = MutableLiveData()
    var nextButtonText: MutableLiveData<String> = MutableLiveData()
    var geoLocationList: GeoLocationResponse = GeoLocationResponse()
    val changeActivity: MutableLiveData<Boolean> = MutableLiveData()
    val showDialog: MutableLiveData<Boolean> = MutableLiveData()
    val endTimeDialog: MutableLiveData<Boolean> = MutableLiveData()
    val startTimeDialog: MutableLiveData<Boolean> = MutableLiveData()
    val button1Clicked: MutableLiveData<Boolean> = MutableLiveData(true)
    val button2Clicked: MutableLiveData<Boolean> = MutableLiveData()
    val button3Clicked: MutableLiveData<Boolean> = MutableLiveData()
    val button4Clicked: MutableLiveData<Boolean> = MutableLiveData()
    val button5Clicked: MutableLiveData<Boolean> = MutableLiveData()
    val button6Clicked: MutableLiveData<Boolean> = MutableLiveData()
    val finalStepButton: MutableLiveData<Boolean> = MutableLiveData(true)
    var stayOnEasyFragment: Boolean = true

    var animationStarted: MutableLiveData<Boolean> = MutableLiveData(false)
    var isDialogNameShow: Boolean = true

    val startTime1: MutableLiveData<Boolean> = MutableLiveData(false)
    val startTime2: MutableLiveData<Boolean> = MutableLiveData(false)
    val startTime3: MutableLiveData<Boolean> = MutableLiveData(false)
    val startTime4: MutableLiveData<Boolean> = MutableLiveData(false)
    val startTime5: MutableLiveData<Boolean> = MutableLiveData(false)
    val startTime6: MutableLiveData<Boolean> = MutableLiveData(false)

    val endTime1: MutableLiveData<Boolean> = MutableLiveData(false)
    val endTime2: MutableLiveData<Boolean> = MutableLiveData(false)
    val endTime3: MutableLiveData<Boolean> = MutableLiveData(false)
    val endTime4: MutableLiveData<Boolean> = MutableLiveData(false)
    val endTime5: MutableLiveData<Boolean> = MutableLiveData(false)
    val endTime6: MutableLiveData<Boolean> = MutableLiveData(false)

    val showMessage: MutableLiveData<String> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    var saveAndUpload: Int = 0

    val singleSchedule: MutableLiveData<SingleSchedule> = MutableLiveData()
    val button7Clicked: MutableLiveData<Boolean> = MutableLiveData()
    val stepButtonClicked: MutableLiveData<Boolean> = MutableLiveData(true)
    val gradualButtonClicked: MutableLiveData<Boolean> = MutableLiveData()
    val listOfTimeValues: MutableList<AdvanceTimeModel> = mutableListOf()
    val listOfValueAndStep: MutableList<AdvanceValuesModel> = mutableListOf()
    val geoLocationEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val quickPlay: MutableLiveData<Boolean> = MutableLiveData()

    val cardBackGround: MutableLiveData<Boolean> = MutableLiveData(false)
    val moonLightEnable: MutableLiveData<Boolean> = MutableLiveData(false)

    var selectedTab = -1


    fun showDialog() {
        saveAndUpload = 0
        showDialog.value = true
    }

    fun quickPlay() {
        quickPlay.value = true
    }

    fun saveAndUpload() {
        saveAndUpload = 1
        showDialog.value = true
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

    fun onMoonlightCswitch(switch: Boolean) {
        Log.d("AdvancedFragment", "onMoonlightCswitch: model: $switch")
        moonLightEnable.value = switch
    }

    fun button1Clicked() {
        disableTimeLineBackGround()
        button1Clicked.value = true
        nextButtonText.value = "Next"
    }

    fun button2Clicked() {

        if (startTime1.value == true) {
            if (endTime1.value == true) {
                disableTimeLineBackGround()
                button2Clicked.value = true
                nextButtonText.value = "Next"
            } else showMessage.value = "Please Enter End Time for Step 1"

        } else showMessage.value = "Please Enter Start Time for Step 1"

    }

    fun button3Clicked() {

        if (startTime2.value == true) {
            if (endTime2.value == true) {
                disableTimeLineBackGround()
                button3Clicked.value = true
                nextButtonText.value = "Next"
            } else showMessage.value = "Please Enter End Time for Step 2"

        } else showMessage.value = "Please Enter Start Time for Step 2"
    }

    fun button4Clicked() {

        if (startTime3.value == true) {
            if (endTime3.value == true) {
                disableTimeLineBackGround()
                button4Clicked.value = true
                nextButtonText.value = "Next"
            } else showMessage.value = "Please Enter End Time for Step 3"

        } else showMessage.value = "Please Enter Start Time for Step 3"

    }

    fun button5Clicked() {

        if (startTime4.value == true) {
            if (endTime4.value == true) {

                disableTimeLineBackGround()
                button5Clicked.value = true
                nextButtonText.value = "Next"
            } else showMessage.value = "Please Enter End Time for Step 4"

        } else showMessage.value = "Please Enter Start Time for Step 4"
    }

    fun button6Clicked() {
        if (startTime5.value == true) {
            if (endTime5.value == true) {
                disableTimeLineBackGround()
                button6Clicked.value = true
                nextButtonText.value = "Preview"
            } else showMessage.value = "Please Enter End Time for Step 5"

        } else showMessage.value = "Please Enter Start Time for Step 5"
    }

    fun button7Clicked() {

        if (startTime6.value == true) {
            if (endTime6.value == true) {
                disableTimeLineBackGround()
                finalStepButton.value = false
                button7Clicked.value = true
                nextButtonText.value = "Next"
            } else showMessage.value = "Please Enter End Time for Step 6"

        } else showMessage.value = "Please Enter Start Time for Step 6"

    }

    fun startTimeClicked() {
        startTimeDialog.value = true
    }

    fun endTimeClicked() {
        endTimeDialog.value = true
    }

    fun setStartValues() {
        changeActivity.value = true
    }

    fun setEndValues() {
        changeActivity.value = false
    }

    private fun disableTimeLineBackGround() {
        button1Clicked.value = false
        button2Clicked.value = false
        button4Clicked.value = false
        button3Clicked.value = false
        button5Clicked.value = false
        button6Clicked.value = false
        button7Clicked.value = false
    }

    private fun disableStepGradualBackGround() {
        stepButtonClicked.value = false
        gradualButtonClicked.value = false
    }

    fun stepButtonClicked() {
        disableStepGradualBackGround()
        stepButtonClicked.value = true
    }

    fun gradualButtonClicked() {
        disableStepGradualBackGround()
        gradualButtonClicked.value = true
    }

    fun updateScheduleApi(
        name: String,
        public: Int,
        hashmap: HashMap<String, String>,
        id: Int,
        geo_location: Int,
        geo_location_id: Int?,
        enabled: Int,
        mode: Int,
        moon_light: String,
        slot_0_start_time: String,
        slot_0_value_a: String,
        slot_0_value_b: String,
        slot_0_value_c: String,
        slot_0_gradual_or_step: String,
        slot_1_start_time: String,
        slot_1_value_a: String,
        slot_1_value_b: String,
        slot_1_value_c: String,
        slot_1_gradual_or_step: String,
        slot_2_start_time: String,
        slot_2_value_a: String,
        slot_2_value_b: String,
        slot_2_value_c: String,
        slot_2_gradual_or_step: String,
        slot_3_start_time: String,
        slot_3_value_a: String,
        slot_3_value_b: String,
        slot_3_value_c: String,
        slot_3_gradual_or_step: String,
        slot_4_start_time: String,
        slot_4_value_a: String,
        slot_4_value_b: String,
        slot_4_value_c: String,
        slot_4_gradual_or_step: String,
        slot_5_start_time: String,
        slot_5_value_a: String,
        slot_5_value_b: String,
        slot_5_value_c: String,
        slot_5_gradual_or_step: String
    ) {

        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.updateSchedule(
                name,
                public,
                hashmap,
                id,
                geo_location,
                geo_location_id,
                enabled,
                mode,
                moon_light,
                slot_0_start_time,
                slot_0_value_a,
                slot_0_value_b,
                slot_0_value_c,
                slot_0_gradual_or_step,
                slot_1_start_time,
                slot_1_value_a,
                slot_1_value_b,
                slot_1_value_c,
                slot_1_gradual_or_step,
                slot_2_start_time,
                slot_2_value_a,
                slot_2_value_b,
                slot_2_value_c,
                slot_2_gradual_or_step,
                slot_3_start_time,
                slot_3_value_a,
                slot_3_value_b,
                slot_3_value_c,
                slot_3_gradual_or_step,
                slot_4_start_time,
                slot_4_value_a,
                slot_4_value_b,
                slot_4_value_c,
                slot_4_gradual_or_step,
                slot_5_start_time,
                slot_5_value_a,
                slot_5_value_b,
                slot_5_value_c,
                slot_5_gradual_or_step
            ).collect {
                apiResponse.value = it
            }
        }
    }


    fun createScheduleApi(
        name: String,
        public: Int,
        id: HashMap<String, String>,
        geo_location: Int,
        geo_location_id: Int?,
        enabled: Int,
        mode: Int,
        moon_light: String,
        slot_0_start_time: String,
        slot_0_value_a: String,
        slot_0_value_b: String,
        slot_0_value_c: String,
        slot_0_gradual_or_step: String,
        slot_1_start_time: String,
        slot_1_value_a: String,
        slot_1_value_b: String,
        slot_1_value_c: String,
        slot_1_gradual_or_step: String,
        slot_2_start_time: String,
        slot_2_value_a: String,
        slot_2_value_b: String,
        slot_2_value_c: String,
        slot_2_gradual_or_step: String,
        slot_3_start_time: String,
        slot_3_value_a: String,
        slot_3_value_b: String,
        slot_3_value_c: String,
        slot_3_gradual_or_step: String,
        slot_4_start_time: String,
        slot_4_value_a: String,
        slot_4_value_b: String,
        slot_4_value_c: String,
        slot_4_gradual_or_step: String,
        slot_5_start_time: String,
        slot_5_value_a: String,
        slot_5_value_b: String,
        slot_5_value_c: String,
        slot_5_gradual_or_step: String
    ) {

        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.createSchedule(
                name,
                public,
                id,
                geo_location,
                geo_location_id,
                enabled,
                mode,
                moon_light,
                slot_0_start_time,
                slot_0_value_a,
                slot_0_value_b,
                slot_0_value_c,
                slot_0_gradual_or_step,
                slot_1_start_time,
                slot_1_value_a,
                slot_1_value_b,
                slot_1_value_c,
                slot_1_gradual_or_step,
                slot_2_start_time,
                slot_2_value_a,
                slot_2_value_b,
                slot_2_value_c,
                slot_2_gradual_or_step,
                slot_3_start_time,
                slot_3_value_a,
                slot_3_value_b,
                slot_3_value_c,
                slot_3_gradual_or_step,
                slot_4_start_time,
                slot_4_value_a,
                slot_4_value_b,
                slot_4_value_c,
                slot_4_gradual_or_step,
                slot_5_start_time,
                slot_5_value_a,
                slot_5_value_b,
                slot_5_value_c,
                slot_5_gradual_or_step
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