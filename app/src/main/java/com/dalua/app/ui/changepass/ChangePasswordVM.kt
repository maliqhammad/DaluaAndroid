package com.dalua.app.ui.changepass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RegistrationRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.ShowMessage
import com.dalua.app.models.apiparameter.ChangePassword
import com.dalua.app.utils.AppConstants.Companion.passwordValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ChangePasswordVM @Inject constructor(val repository: RegistrationRepository) : ViewModel() {

    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val showMessage: MutableLiveData<ShowMessage> = MutableLiveData()
    val backPressed: MutableLiveData<Boolean> = MutableLiveData()
    var isPasswordMatched: MutableLiveData<Boolean> = MutableLiveData(true)
    var userPassword: MutableLiveData<String> = MutableLiveData()
    var email: String = ""
    var code: String = ""
    var confirmUserPassword: MutableLiveData<String> = MutableLiveData()


    fun onBackedPressed() {
        backPressed.value = true
    }


    fun onTextChangedPass(s: CharSequence, start: Int, before: Int, count: Int) {

        userPassword.value = s.toString()

        if (s.isEmpty()) {
            isPasswordMatched.value = true
            return
        }

        if (confirmUserPassword.value != null) {
            isPasswordMatched.value = confirmUserPassword.value.toString().contentEquals(s)
        }

    }

    fun onTextChangedCon(s: CharSequence, start: Int, before: Int, count: Int) {

        if (s.isEmpty()) {
            isPasswordMatched.value = true
            return
        }

        if (userPassword.value != null) {
            isPasswordMatched.value = userPassword.value.toString().contentEquals(s)
        }
    }


    fun callChangePassword() {


        if (userPassword.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Password cannot be Empty.", false)
            return
        } else if (isPasswordMatched.value == false) {
            showMessage.value = ShowMessage("Passwords do not match...", false)
            return
        } else if (!passwordValidation(userPassword.value.toString())) {
            showMessage.value = ShowMessage(
                "Password must include special character, number and a capital letter.",
                false
            )
            return
        }

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.changeUserPassword(
                ChangePassword(
                    email,
                    code,
                    userPassword.value!!,
                    confirmUserPassword.value!!
                )
            ).collect {
                apiResponse.value = it
            }
        }

    }

//    private fun passwordValidation(password: String): Boolean {
//
//        return if (password.length >= 6) {
//            val letter: Pattern = Pattern.compile("[a-z]")
//            val cletter: Pattern = Pattern.compile("[A-Z]")
//            val digit: Pattern = Pattern.compile("[0-9]")
//            val special: Pattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]")
//            //Pattern eight = Pattern.compile (".{8}");
//            val hasLetter: Matcher = letter.matcher(password)
//            val hascletter: Matcher = cletter.matcher(password)
//            val hasDigit: Matcher = digit.matcher(password)
//            val hasSpecial: Matcher = special.matcher(password)
//            hasLetter.find() && hasDigit.find() && hasSpecial.find() && hascletter.find()
//        } else false
//    }
}