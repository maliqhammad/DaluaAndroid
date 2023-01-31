package com.dalua.app.ui.home.fragments.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.ListAllAquariumResponse
import com.dalua.app.models.LoginResponse
import com.dalua.app.models.User
import com.dalua.app.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(val repository: RemoteDataRepository) :
    ViewModel() {

    var showCreateAquariumDialog: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var aquariumClicked: SingleLiveEvent<ListAllAquariumResponse.SharedAquariums> = SingleLiveEvent()
    var aquariumList: MutableLiveData<ListAllAquariumResponse> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val logout: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val aquariumType: MutableLiveData<String> = MutableLiveData()
    val loginResponse: MutableLiveData<LoginResponse> = MutableLiveData()

    fun showCreateAquariumDialog() {
        showCreateAquariumDialog.value = true
    }

    fun onLogoutClicked() {
        logout.value = true
    }

    fun getUserAquariums() {

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.getUserAquarium().collect {
                apiResponse.value = it
            }
        }

    }

    fun onAquariumClicked(aquarium: ListAllAquariumResponse.SharedAquariums) {
        Log.d("TAG", "onAquariumClicked: " + aquarium.aquariumType)
        if (aquarium.aquariumType == 2 && aquarium.users[0].pivot.status.contains("0")) {
            return
        } else {
            aquariumClicked.value = aquarium
        }


    }

    fun onAcceptAquarium(user: ListAllAquariumResponse.User) {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.approveOrRejectAquarium(user.pivot.id.toString(), 1.toString()).collect {
                apiResponse.value = it
            }
        }
    }

    fun onRejectAquarium(user: ListAllAquariumResponse.User) {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.approveOrRejectAquarium(user.pivot.id.toString(), 2.toString()).collect {
                apiResponse.value = it
            }
        }
    }

}