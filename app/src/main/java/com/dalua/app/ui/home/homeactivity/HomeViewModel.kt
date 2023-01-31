package com.dalua.app.ui.home.homeactivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: RemoteDataRepository) :
    ViewModel() {

    var callUserAquarium: MutableLiveData<Boolean> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val showAquariumDialog: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val aquariumCreated: MutableLiveData<Boolean> = MutableLiveData()

    fun authenticateUser() {

        viewModelScope.launch {
            repository.getUserToken().collect()
            {
                apiResponse.value = it
            }
        }

    }

    fun getAquariumApi(name: String, test_frequency: String, clean_frequency: String) {

        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.createAquariumApi(
                name, "25",
                "7", "23", "45", "33", "87", "21", test_frequency, clean_frequency
            ).collect {
                apiResponse.value = it
            }
        }

    }

    fun deleteDevice(id: String) {
//        apiResponse.value = Resource.Loading()
//        viewModelScope.launch {
//            repository.deleteDeviceApi(id).collect {
//                apiResponse.value = it
//            }
//        }
    }
}
