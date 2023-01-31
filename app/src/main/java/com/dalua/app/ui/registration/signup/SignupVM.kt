package com.dalua.app.ui.registration.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RegistrationRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.ShowMessage
import com.dalua.app.models.apiparameter.SignupUser
import com.dalua.app.utils.AppConstants.Companion.isValidName
import com.dalua.app.utils.AppConstants.Companion.passwordValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class SignupVM @Inject constructor(private val registrationRepository: RegistrationRepository) :
    ViewModel() {

    val firstName: MutableLiveData<String> = MutableLiveData()
    val lastName: MutableLiveData<String> = MutableLiveData()
    val middleName: MutableLiveData<String> = MutableLiveData()
    val phoneNumber: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val backPressed: MutableLiveData<Boolean> = MutableLiveData()
    val phoneCode: MutableLiveData<String> = MutableLiveData()
    val country: MutableLiveData<String> = MutableLiveData()
    var phoneCountryShortcut: String = ""
    val userName: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val confirmPassword: MutableLiveData<String> = MutableLiveData()
    val tankSize: MutableLiveData<String> = MutableLiveData()
    val showPicker: MutableLiveData<Boolean> = MutableLiveData()
    val showTankSizePicker: MutableLiveData<Boolean> = MutableLiveData()
    val signInWithGoogle: MutableLiveData<Boolean> = MutableLiveData()
    val showMessage: MutableLiveData<ShowMessage> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    var phoneNumberUtil: PhoneNumberUtil? = null

    fun showCodePicker() {
        showPicker.value = true
    }

    fun signInWithGooglePressed() {
        signInWithGoogle.value = true
    }

    fun showTankSizePicker() {
        showTankSizePicker.value = true
    }

    fun onBackPressed() {
        backPressed.value = true
    }

    fun registerUser(apiType: String) {

        if (firstName.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Name cannot be empty...", false)
            return
        }
        if (!isValidName(firstName.value!!)) {
            showMessage.value = ShowMessage("Please enter valid name...", false)
            return
        }


        if (email.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Email cannot be Empty.", false)
            return
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()) {
                showMessage.value = ShowMessage("Enter Valid Email Address.", false)
                return
            }
        }

        if (password.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Password cannot be Empty.", false)
            return
        } else {
            if (!passwordValidation(password.value.toString())) {
                showMessage.value = ShowMessage(
                    "Password must have at least 8 characters including 1 alphabet, 1 number, and 1 special character.",
                    false
                )
                return
            }
        }

        if (confirmPassword.value.isNullOrEmpty() || !confirmPassword.value.equals(password.value)) {
            showMessage.value = ShowMessage("Passwords do not match.", false)
            return
        }

        if (tankSize.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Tank size cannot be Empty.", false)
            return
        }

        if (phoneNumber.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Phone Number cannot be Empty.", false)
            return
        } else {
            val phoneNumber: PhoneNumber =
                phoneNumberUtil!!.parse(phoneNumber.value!!, phoneCountryShortcut)
            if (phoneNumberUtil?.isValidNumberForRegion(
                    phoneNumber,
                    phoneCountryShortcut
                ) == false
            ) {
                showMessage.value = ShowMessage("Enter Valid Phone Number.", false)
                return
            }

        }


        viewModelScope.launch {

            apiResponse.value = Resource.Loading()
            if (country.value != null) {
                registrationRepository.registerUser(
                    SignupUser(
                        first_name = firstName.value!!,
                        email = email.value!!,
                        phone_no = phoneNumber.value,
                        country_code = phoneCode.value,
                        password = password.value!!,
                        login_type = apiType,
                        tank_size = tankSize.value!!,
                        country = country.value!!

                    )
                ).collect {
                    apiResponse.value = it
                }
            } else {
                registrationRepository.registerUser(
                    SignupUser(
                        first_name = firstName.value!!,
                        email = email.value!!,
                        phone_no = phoneNumber.value,
                        country_code = phoneCode.value,
                        password = password.value!!,
                        login_type = apiType,
                        tank_size = tankSize.value!!

                    )
                ).collect {
                    apiResponse.value = it
                }
            }
        }

    }


    fun registerUserGmail(signupUser: SignupUser) {

        viewModelScope.launch {

            apiResponse.value = Resource.Loading()
            registrationRepository.registerUser(
                signupUser
            ).collect {
                apiResponse.value = it
            }
        }

    }
}