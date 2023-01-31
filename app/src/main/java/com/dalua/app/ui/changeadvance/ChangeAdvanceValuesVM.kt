package com.dalua.app.ui.changeadvance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.models.Configuration
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ChangeAdvanceValuesVM @Inject constructor(val repository: RemoteDataRepository) :
    ViewModel() {

    val sendIntentBack: MutableLiveData<Boolean> = MutableLiveData()
    val aValue: MutableLiveData<String> = MutableLiveData()
    val bValue: MutableLiveData<String> = MutableLiveData()
    val cValue: MutableLiveData<String> = MutableLiveData()
    val isMasterControll: MutableLiveData<Boolean> = MutableLiveData()
    val isInstantControll: MutableLiveData<Boolean> = MutableLiveData()
    val waterType: MutableLiveData<String> = MutableLiveData()
    val camera: MutableLiveData<Boolean> = MutableLiveData()
    val configuration: MutableLiveData<Configuration> = MutableLiveData()
    val inputStream: MutableLiveData<InputStream> = MutableLiveData()
    val isAwsConnected: MutableLiveData<Boolean> = MutableLiveData(false)

    fun saveScheduleClicked() {
        sendIntentBack.value = true
    }

    fun showMasterControl() {
        isMasterControll.value = true
        isInstantControll.value = false
    }

    fun showCamera() {
        camera.value = true
    }

    fun showInstantControl() {
        isMasterControll.value = false
        isInstantControll.value = true
    }
}