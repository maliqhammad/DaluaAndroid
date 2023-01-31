package com.dalua.app.ui.home.fragments.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.ShowMessage
import com.dalua.app.models.User
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.Companion.isValidName
import com.dalua.app.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.InputStream
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(val repository: RemoteDataRepository) : ViewModel() {
    val saveClick: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val profileClick: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val inputStream: SingleLiveEvent<InputStream> = SingleLiveEvent()
    val selectCountry: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val selectTankSize: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val changePasswordClick: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showPicker: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val phoneCode: MutableLiveData<String> = MutableLiveData()
    val country: MutableLiveData<String> = MutableLiveData()
    var phoneCountryShortcut: String = ""
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val showMessage: SingleLiveEvent<ShowMessage> = SingleLiveEvent()
    val user: MutableLiveData<User> = MutableLiveData()
    val tankSize: MutableLiveData<String> = MutableLiveData()
    val userImage: MutableLiveData<String> = MutableLiveData()
    val oldPassword: MutableLiveData<String> = MutableLiveData()
    val newPassword: MutableLiveData<String> = MutableLiveData()
    val confirmPassword: MutableLiveData<String> = MutableLiveData()
    val isPasswordMatched: MutableLiveData<Boolean> = MutableLiveData()
    val deleteAccount: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var phoneNumberUtil: PhoneNumberUtil? = null

    fun onDeleteAccount() {
        deleteAccount.value = true
    }

    fun deleteAccount() {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.deleteAccount().collect {
                apiResponse.value = it
            }
        }
    }

    fun showCodePicker() {
        showPicker.value = true
    }


    fun getUserProfile() {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.getUserProfile().collect {
                apiResponse.value = it
            }
        }
    }


    fun onProfileClick() {
        profileClick.value = true
    }

    fun onSaveClick() {
        saveClick.value = true
    }

    fun onSelectCountry() {
        selectCountry.value = true
    }

    fun updateProfile() {
        if (user.value!!.firstName.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Name cannot be Empty.", false)
            return
        }
        if (!isValidName(user.value!!.firstName)) {
            showMessage.value = ShowMessage("Please enter a valid name.", false)
            return
        }
        if (user.value!!.tankSize.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Tank size cannot be Empty.", false)
            return
        }
        if (user.value!!.phoneNo.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Phone Number cannot be Empty.", false)
            return
        } else {
            var phoneNumber: Phonenumber.PhoneNumber? = null
            try {
                phoneNumber = phoneNumberUtil!!.parse(user.value!!.phoneNo, phoneCountryShortcut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (phoneNumber != null) {
                if (phoneNumberUtil?.isValidNumberForRegion(
                        phoneNumber,
                        phoneCountryShortcut
                    ) == false
                ) {
                    showMessage.value = ShowMessage("Enter Valid Phone Number.", false)
                    return
                }
            } else {
                showMessage.value = ShowMessage("Enter Valid Phone Number.", false)
                return
            }
            viewModelScope.launch {
                apiResponse.value = Resource.Loading()
                val country = country.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
                val phoneNo =
                    user.value!!.phoneNo.toRequestBody("text/plain".toMediaTypeOrNull())
                val firstName =
                    user.value!!.firstName.toRequestBody("text/plain".toMediaTypeOrNull())
                val tankSize1 =
                    user.value!!.tankSize.toRequestBody("text/plain".toMediaTypeOrNull())
                val countryCode = phoneCode.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
                val part: MultipartBody.Part?
                if (inputStream.value != null) {
                    part = MultipartBody.Part.createFormData(
                        "image", "myPic", RequestBody.create(
                            "image/*".toMediaTypeOrNull(),
                            inputStream.value!!.readBytes()
                        )
                    )
                } else {
                    part = null
                }
                repository.updateProfile(country, phoneNo, firstName, tankSize1, countryCode, part)
                    .collect {
                        apiResponse.value = it
                    }
            }
        }
    }

    fun updatePassword() {
        if (oldPassword.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Please enter your old password", false)
            return
        }
        if (newPassword.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Please enter your new password", false)
            return
        } else {
            if (!AppConstants.passwordValidation(newPassword.value.toString())) {
                showMessage.value = ShowMessage(
                    "Password must have at least 8 characters including 1 alphabet, 1 number, and 1 special character.",
                    false
                )
                return
            }
        }
        if (confirmPassword.value.isNullOrEmpty()) {
            showMessage.value = ShowMessage("Please re-enter your new password", false)
            return
        }


        if (!newPassword.value.equals(confirmPassword.value)) {
            showMessage.value = ShowMessage("Passwords do not match", false)
            return
        }
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.updatePassword(oldPassword.value.toString(), newPassword.value.toString())
                .collect {
                    apiResponse.value = it
                }
        }
    }

    fun onTextChangedName(s: CharSequence, start: Int, before: Int, count: Int) {
        user.value!!.firstName = s.toString()
    }

    fun onTextChangedPhone(s: CharSequence, start: Int, before: Int, count: Int) {
        user.value!!.phoneNo = s.toString()
    }

    fun onTankSizeClicked() {
        selectTankSize.value = true
    }

    fun onTextChangedOldPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        oldPassword.value = s.toString()
    }

    fun onTextChangedNewPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        newPassword.value = s.toString()
        if (s.isEmpty()) {
            isPasswordMatched.value = true
            return
        }

        if (confirmPassword.value != null) {
            isPasswordMatched.value = confirmPassword.value.toString().contentEquals(s)
        }
    }

    fun onTextChangedConfirmPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        confirmPassword.value = s.toString()
        if (s.isEmpty()) {
            isPasswordMatched.value = true
            return
        }

        if (newPassword.value != null) {
            isPasswordMatched.value = newPassword.value.toString().contentEquals(s)
        }
    }

    fun onChangePassword() {
        changePasswordClick.value = true
    }

}