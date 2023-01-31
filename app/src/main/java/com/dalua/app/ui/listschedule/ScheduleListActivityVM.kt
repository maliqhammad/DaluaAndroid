package com.dalua.app.ui.listschedule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.*
import com.dalua.app.models.schedulemodel.SingleSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ScheduleListActivityVM @Inject constructor(val repository: RemoteDataRepository) :
    ViewModel() {

    val selectedTab: MutableLiveData<Int> = MutableLiveData(2)
    val geoLocation: MutableLiveData<GeoLocationResponse> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val schedule: MutableLiveData<SingleSchedule> = MutableLiveData()
    val waterType: MutableLiveData<String> = MutableLiveData()
    val configuration: MutableLiveData<Configuration> = MutableLiveData()
    var launchFrom: MutableLiveData<String> = MutableLiveData()
    val device: MutableLiveData<Device> = MutableLiveData()
    val group: MutableLiveData<AquariumGroup> = MutableLiveData()
    val aquarium: MutableLiveData<SingleAquarium> = MutableLiveData()

    fun onTabClick(position: Int) {
        selectedTab.value = position
    }

    init {
        getLocations()
    }

    private fun getLocations() {

        viewModelScope.launch {

            repository.getLocation().collect {
                apiResponse.value = it
            }
        }
    }


}