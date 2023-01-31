package com.dalua.app.ui.registration.sendcode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RegistrationRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.ShowMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class SendCodeVerificationVM @Inject constructor(val repository: RegistrationRepository) :
    ViewModel() {

    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val showMessage: MutableLiveData<ShowMessage> = MutableLiveData()
    val backPressed: MutableLiveData<Boolean> = MutableLiveData()
    val verificationEmail: MutableLiveData<String> = MutableLiveData()

    fun onBackPressed() {
        backPressed.value = true
    }

    fun sendVerificationCode() {

        if (verificationEmail.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Email Name cannot be Empty.", false)
            return
        } else
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(verificationEmail.value!!).matches()) {
                showMessage.value = ShowMessage("Enter Valid Email Address.", false)
                return
            }

        viewModelScope.launch {
            apiResponse.value=Resource.Loading()
            repository.resetPasswordMail(verificationEmail.value!!).collect {
                apiResponse.value=it
            }
        }

    }


}