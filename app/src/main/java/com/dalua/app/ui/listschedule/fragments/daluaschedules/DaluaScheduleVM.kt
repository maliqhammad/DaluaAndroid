package com.dalua.app.ui.listschedule.fragments.daluaschedules

import android.util.Log
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
class DaluaScheduleVM @Inject constructor(val repository: RemoteDataRepository) : ViewModel() {

    val apiResponses: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val previewClicked: MutableLiveData<SingleSchedule> = MutableLiveData()
    val geoLocation: MutableLiveData<GeoLocationResponse> = MutableLiveData()
    val waterType: MutableLiveData<String> = MutableLiveData()
    val configuration: MutableLiveData<Configuration> = MutableLiveData()
    val selectedSchedule: MutableLiveData<SingleSchedule> = MutableLiveData()

    init {
//        loadFirstPage()

    }

    fun previewClicked(singleSchedule: SingleSchedule) {
        Log.d("TAG", "previewClicked: ")
        IsEditOrPreviewOrCreate = "preview"
        previewClicked.value = singleSchedule
    }


    fun loadFirstPage() {
        val path = "dalua" + "?page=" + 1
        apiResponses.value = Resource.Loading()
        viewModelScope.launch {
            repository.getDaluaSchedules(1).collect {
                apiResponses.value = it
            }
        }
    }

    fun loadNextPage(page: Int) {
        val path = "dalua?page=$page"
        viewModelScope.launch {
            repository.getDaluaSchedules(page).collect {
                apiResponses.value = it
            }
        }
    }

}