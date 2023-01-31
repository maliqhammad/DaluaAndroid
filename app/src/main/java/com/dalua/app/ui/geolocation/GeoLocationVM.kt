package com.dalua.app.ui.geolocation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.geolocation.GeoLocationResponseAll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class GeoLocationVM @Inject constructor(val repository: RemoteDataRepository) : ViewModel() {

    val goBackButton: MutableLiveData<Boolean> = MutableLiveData()
    val itemClickedButton: MutableLiveData<Int> = MutableLiveData()
    val apiResponses: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()

    init {
        getGeoLocation()
    }

    fun backPressed() {
        goBackButton.value = true
    }

    fun itemPressed(value: GeoLocationResponseAll.Datum) {
        itemClickedButton.value = value.id
    }

    private fun getGeoLocation() {

        viewModelScope.launch {

            apiResponses.value=Resource.Loading()
            repository.getLocation().collect {
                apiResponses.value = it
            }

        }


    }

}