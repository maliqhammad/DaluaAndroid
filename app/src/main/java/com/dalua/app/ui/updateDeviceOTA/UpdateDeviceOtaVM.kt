package com.dalua.app.ui.updateDeviceOTA

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cunoraz.gifview.library.GifView
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.Device
import com.dalua.app.models.DeviceAddedPlace
import com.dalua.app.models.ResponseOtaFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class UpdateDeviceOtaVM @Inject constructor(private var repository: RemoteDataRepository) :
    ViewModel() {
    val TAG = "UpdateDeviceOtaVM"
    var otaFile: MutableLiveData<ResponseOtaFiles.OtaFile> = MutableLiveData()
    var device: MutableLiveData<Device> = MutableLiveData()
    var deviceToConnected: DeviceAddedPlace? = null
    var wifiMessage: MutableLiveData<String> = MutableLiveData()
    var wifiSSID: MutableLiveData<String> = MutableLiveData()
    var updatingTitle: MutableLiveData<String> = MutableLiveData("What is your product type?")
    var updatingMessage: MutableLiveData<String> = MutableLiveData()
    var buttonText: MutableLiveData<String> = MutableLiveData("Update")
    var showMessage: MutableLiveData<String> = MutableLiveData()
    var status: MutableLiveData<Int> = MutableLiveData()
    var backPress: MutableLiveData<Boolean> = MutableLiveData()
    var buttonClick: MutableLiveData<Boolean> = MutableLiveData()
    var apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()

    fun playGifView(gifView: GifView, status: Int) {
        if (status == 1) {
            gifView.play()
            gifView.visibility = View.VISIBLE
        } else {
            gifView.pause()
            gifView.visibility = View.GONE
        }
    }

    fun onDeviceClick(otaFile: ResponseOtaFiles.OtaFile) {
        this.otaFile.value = otaFile
    }

    fun backPressed() {
        backPress.value = true
    }

    fun onButtonClick() {
        buttonClick.value = true
    }

    fun changeProductType(id: String, productId: Int) {
        viewModelScope.launch {
            repository.changeProductType(id, productId).collect {
                apiResponse.value = it
            }
        }
    }

    fun getDeviceDetails(id: Int) {
        viewModelScope.launch {
            repository.getDeviceDetails(id.toString()).collect {
                apiResponse.value = it
            }
        }

    }
}