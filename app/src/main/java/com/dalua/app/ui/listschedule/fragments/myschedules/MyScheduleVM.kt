package com.dalua.app.ui.listschedule.fragments.myschedules

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.Configuration
import com.dalua.app.models.GeoLocationResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.utils.AppConstants.Companion.IsEditOrPreviewOrCreate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class MyScheduleVM @Inject constructor(val repository: RemoteDataRepository) :
    ViewModel() {

    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val previewClicked: MutableLiveData<SingleSchedule> = MutableLiveData()
    val geoLocation: MutableLiveData<GeoLocationResponse> = MutableLiveData()
    val waterType: MutableLiveData<String> = MutableLiveData()
    val configuration: MutableLiveData<Configuration> = MutableLiveData()
    val selectedSchedule: MutableLiveData<SingleSchedule> = MutableLiveData()
    lateinit var view: View
    val isEnable: MutableLiveData<Boolean> = MutableLiveData(false)

    fun previewClicked(singleSchedule: SingleSchedule) {
        IsEditOrPreviewOrCreate = "preview"
        previewClicked.value = singleSchedule
    }

    fun showScheduleMenu(singleSchedule: SingleSchedule, view: View, isEnable: Boolean) {
        this.view = view
        this.isEnable.value = isEnable
        selectedSchedule.value = singleSchedule
    }

    fun deleteSchedule(singleSchedule: SingleSchedule) {
        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.deleteSchedule(singleSchedule.id).collect {
                apiResponse.value = it
            }

        }
    }

    fun renameScheduleApi(
        scheduleId: Int,
        name: String
    ) {
        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.renameSchedule(scheduleId, name).collect {
                apiResponse.value = it
            }
        }
    }


    fun loadFirstPage(deviceId: Int?, groupId: Int?) {
        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.getMySchedules(deviceId, groupId, page = 1).collect {
                apiResponse.value = it
            }
        }
    }

    fun loadNextPage(deviceId: Int?, groupId: Int?, page: Int) {
        viewModelScope.launch {
            repository.getMySchedules(deviceId, groupId, page).collect {
                apiResponse.value = it
            }
        }
    }

}