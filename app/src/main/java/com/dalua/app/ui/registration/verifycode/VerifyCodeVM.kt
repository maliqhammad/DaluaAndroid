package com.dalua.app.ui.registration.verifycode

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
class VerifyCodeVM @Inject constructor(val repository: RegistrationRepository) : ViewModel() {

    var isPassword: Boolean = false
    val timerTime: MutableLiveData<String> = MutableLiveData("00:00")
    val enableNow: MutableLiveData<Boolean> = MutableLiveData(false)
    val showMessage: MutableLiveData<ShowMessage> = MutableLiveData()
    val backPressed: MutableLiveData<Boolean> = MutableLiveData()
    val sendEmail: MutableLiveData<String> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    var code: String = ""

    fun onBackPressed() {
        backPressed.value = true
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        code = s.toString()
        if (s.length == 6)
            verifyCode(s.toString())

    }

    fun verifyCode(code: String) {

        if (code.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Please enter the code.", false)
            return
        }

        if (code.length < 6) {
            showMessage.value = ShowMessage("Please enter the complete code.", false)
            return
        }

        viewModelScope.launch {

            if (isPassword) {
                apiResponse.value = Resource.Loading()
                repository.verifyPasswordCode(sendEmail.value!!, code).collect {
                    apiResponse.value = it
                }
            } else {
                apiResponse.value = Resource.Loading()
                repository.verifyEmail(sendEmail.value!!, code).collect {
                    apiResponse.value = it
                }
            }
        }
    }

    fun resendVerificationCode() {

        viewModelScope.launch {
            if (isPassword) {
                apiResponse.value=Resource.Loading()
                repository.resetPasswordMail(sendEmail.value!!).collect {
                    apiResponse.value=it
                }
            } else {
            apiResponse.value = Resource.Loading()
            repository.resendVerificationCode(sendEmail.value!!).collect {
                apiResponse.value = it
            }
            }
        }
    }

}