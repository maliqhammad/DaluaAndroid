package com.dalua.app.ui.customDialogs.shareAquariumDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ShareAquariumDialogVM @Inject constructor(private val repository: RemoteDataRepository) :
    ViewModel() {

    var message: MutableLiveData<String> = MutableLiveData()
    var progressBar: MutableLiveData<Boolean> = MutableLiveData()
    var isSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
    var isError: MutableLiveData<Boolean> = MutableLiveData(false)
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()


    fun shareAquarium(aquarium_id: String, email: String) {
        viewModelScope.launch {
            progressBar.value = true
            repository.shareAquarium(aquarium_id, email).collect {
                apiResponse.value = it
            }
        }
    }
}