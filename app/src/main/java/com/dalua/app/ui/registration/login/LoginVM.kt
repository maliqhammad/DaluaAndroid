package com.dalua.app.ui.registration.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RegistrationRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.ShowMessage
import com.dalua.app.models.apiparameter.SignupUser
import com.dalua.app.utils.AppConstants.RegistrationType.Email
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(val repository: RegistrationRepository) : ViewModel() {

    val userName: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val showMessage: MutableLiveData<ShowMessage> = MutableLiveData()
    val backPressed: MutableLiveData<Boolean> = MutableLiveData()
    val signInWithGoogle: MutableLiveData<Boolean> = MutableLiveData()

    fun loginClicked() {


        if (userName.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Middle Name cannot be Empty.", false)
            return
        }

        if (password.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Password cannot be Empty.", false)
            return
        }

//        else if (!passwordValidation(password.value.toString())) {
//            showMessage.value = ShowMessage(
//                "Please enter the correct password",
//                false
//            )
//            return
//
//        }

        viewModelScope.launch {

            apiResponse.value = Resource.Loading()
            repository.loginUser(
                userName.value!!,
                password.value!!,
                Email.toString()
            ).collect {
                apiResponse.value = it

            }

        }

    }

    fun loginWithGoogleClicked(signupUser: SignupUser) {

        viewModelScope.launch {

            apiResponse.value = Resource.Loading()
            repository.registerUser(
                signupUser
            ).collect {
                apiResponse.value = it

            }

        }

    }

    fun onBackedPressed() {
        backPressed.value = true
    }

    fun signInWithGooglePressed() {

        signInWithGoogle.value = true

    }

    private fun passwordValidation(password: String): Boolean {

        return if (password.length >= 6) {
            val letter: Pattern = Pattern.compile("[a-z]")
            val cletter: Pattern = Pattern.compile("[A-Z]")
            val digit: Pattern = Pattern.compile("[0-9]")
            val special: Pattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]")
            //Pattern eight = Pattern.compile (".{8}");
            val hasLetter: Matcher = letter.matcher(password)
            val hascletter: Matcher = cletter.matcher(password)
            val hasDigit: Matcher = digit.matcher(password)
            val hasSpecial: Matcher = special.matcher(password)
            hasLetter.find() && hasDigit.find()
//                    && hasSpecial.find() && hascletter.find()
        } else false

    }
}