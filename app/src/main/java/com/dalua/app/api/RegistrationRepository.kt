package com.dalua.app.api

import com.dalua.app.baseclasses.BaseApiConverter
import com.dalua.app.models.apiparameter.ChangePassword
import com.dalua.app.models.apiparameter.SignupUser
import com.dalua.app.utils.AppConstants.ApiTypes.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named

class RegistrationRepository @Inject constructor(
    private val apiService: ApiService,
    @Named("uniqueID") private val uniqueId: String,
) :
    BaseApiConverter() {

    suspend fun getUserToken() = flow {
        emit(
            safeApiCall(
                { apiService.loginUserWithUniqueID(uniqueId) }, apiName = GetUserToken.name
            )
        )
    }.flowOn(Dispatchers.IO)


    suspend fun registerUser(signupUser: SignupUser) = flow {
        emit(
            safeApiCall(
                { apiService.signUpNewUser(signupUser) }, apiName = GetUserToken.name
            )
        )
    }.flowOn(Dispatchers.IO)


    suspend fun resetPasswordMail(email: String) = flow {
        emit(
            safeApiCall(
                { apiService.resetPasswordRequest(email) }, apiName = ResetPasswordRequest.name
            )
        )
    }.flowOn(Dispatchers.IO)


    suspend fun verifyEmail(
        email: String,
        code: String,
    ) = flow {
        emit(
            safeApiCall(
                { apiService.verifyCode(email, code) }, apiName = VerifyUserCode.name
            )
        )
    }.flowOn(Dispatchers.IO)


    suspend fun verifyPasswordCode(
        email: String,
        code: String,
    ) = flow {
        emit(
            safeApiCall(
                { apiService.verifyPasswordCode(email, code) }, apiName = VerifyPasswordCode.name
            )
        )
    }.flowOn(Dispatchers.IO)


    suspend fun resendVerificationCode(
        email: String,
    ) = flow {
        emit(
            safeApiCall(
                { apiService.resendVerificationCode(email) }, apiName = ResendUserCode.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun changeUserPassword(
        changePassword: ChangePassword,
    ) = flow {
        emit(
            safeApiCall(
                { apiService.changeUserPassword(changePassword) }, apiName = UpdateUserPassword.name)
        )
    }.flowOn(Dispatchers.IO)


    suspend fun loginUser(
        email: String,
        password: String,
        login_type: String
    ) = flow {
        emit(
            safeApiCall(
                { apiService.loginUser(email,password,login_type) }, apiName = LoginApi.name
            )
        )
    }.flowOn(Dispatchers.IO)


}