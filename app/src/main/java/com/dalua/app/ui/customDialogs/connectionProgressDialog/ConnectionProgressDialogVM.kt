package com.dalua.app.ui.customDialogs.connectionProgressDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dalua.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectionProgressDialogVM @Inject constructor() : ViewModel() {

    val title: MutableLiveData<String> = MutableLiveData()
    val deviceType: MutableLiveData<String> = MutableLiveData("Marine")
    val description: MutableLiveData<String> = MutableLiveData()
    val button: MutableLiveData<String> = MutableLiveData()
    val progressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val statusCode: MutableLiveData<Int> = MutableLiveData()
    val isAllDone: MutableLiveData<Boolean> = MutableLiveData(false)
    val connectionState: MutableLiveData<Int> =
        MutableLiveData(R.drawable.ic_error_connection_24)
}