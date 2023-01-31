package com.dalua.app.ui.registration.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityLoginBinding
import com.dalua.app.models.LoginResponse
import com.dalua.app.models.apiparameter.SignupUser
import com.dalua.app.ui.home.homeactivity.HomeActivity
import com.dalua.app.ui.registration.sendcode.SendCodeVerificationActivity
import com.dalua.app.ui.registration.signup.SignupActivity
import com.dalua.app.ui.registration.verifycode.VerifyCodeActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.ApiTypes.GetUserToken
import com.dalua.app.utils.AppConstants.ApiTypes.LoginApi
import com.dalua.app.utils.AppConstants.CurrentActivity.*
import com.dalua.app.utils.ProjectUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding
    val viewModel: LoginVM by viewModels()
    lateinit var gso: GoogleSignInOptions
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObjects()
        apiResponse()
        observers()

    }

    private fun observers() {

        viewModel.showMessage.observe(this) {
            showMessage(it.message, it.boolean)
        }

        viewModel.backPressed.observe(this) {
            finish()
        }

        viewModel.signInWithGoogle.observe(this) {
            val signInIntent: Intent = googleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        }

    }


    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                // There are no request codes
                signInResultFromGoogle(result.data!!)

            }
            Log.d("TAG", "resultLauncher: " + result.resultCode)
            Log.d("TAG", "resultLauncher: " + Activity.RESULT_OK)
        }

    private fun signInResultFromGoogle(data: Intent) {

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
//            Log.d("TAG", "signInResultFromGoogle: " + ProjectUtil.objectToString(acct))
//            val personName = acct.displayName
//            val personGivenName = acct.givenName
//            val personFamilyName = acct.familyName
//            val personEmail = acct.email
//            val personId = acct.id
//            val personToken = acct.idToken
//            val personPhoto: Uri? = acct.photoUrl


            viewModel.loginWithGoogleClicked(
                SignupUser(
                    first_name = acct.givenName!!,
                    null,
                    null,
                    null,
                    email = acct.email!!,
                    null,
                    null,
                    social_user_id = acct.id,
                    social_token = acct.idToken,
                    login_type = "2",
                    tank_size = "3'"
                )
            )

            googleSignInClient.signOut()

        }


    }

    fun goToSignupLogin(view: View) {
        startActivity(Intent(this, SignupActivity::class.java))
        finish()
    }

    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        myProgressDialog()

        // google signIn
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(AppConstants.GoogleClientID)
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    fun goToForgotPassword(view: View) {

        startActivity(
            Intent(
                this,
                SendCodeVerificationActivity::class.java
            ).putExtra(IntentKey1.toString(), PasswordVerification.toString())
        )

    }

    private fun apiResponse() {

        viewModel.apiResponse.observe(this) {
            when (it) {
                is Resource.Error -> {
                    hideWorking()
                    showMessage(it.error, false)
                    Log.d(TAG, "ResponseError: " + it.error + " \n type: " + it.api_Type)
                }
                is Resource.Loading -> {
                    showWorking()
                }
                is Resource.Success -> {

                    hideWorking()

                    when (it.api_Type) {

                        LoginApi.name -> {

                            it.data?.let { res ->
                                val response = ProjectUtil.stringToObject(
                                    res.string(),
                                    LoginResponse::class.java
                                )

                                if (response.success) {
                                    if (response.message!!.contentEquals(
                                            "You're account is not verified. New Verification code sent to provided email"
                                        )
                                    ) {

                                        Handler(Looper.myLooper()!!).postDelayed({
                                            startActivity(
                                                Intent(
                                                    this,
                                                    VerifyCodeActivity::class.java
                                                ).putExtra("email", response.data.email)
                                                    .putExtra(
                                                        IntentKey1.toString(),
                                                        EmailVerification.toString()
                                                    )
                                            )
                                        }, 200)

                                    } else {

                                        ProjectUtil.putApiTokenBearer(
                                            this,
                                            "Bearer ${response.data.token}"
                                        )
                                        ProjectUtil.putUserObjects(
                                            this,
                                            response
                                        )

                                        Handler(Looper.myLooper()!!).postDelayed({
                                            startActivity(
                                                Intent(
                                                    this,
                                                    HomeActivity::class.java
                                                ).addFlags(
                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                )
                                            )
                                        }, 200)
                                    }
                                } else {
                                    ProjectUtil.getErrorMessage(res.toString())
                                }
                                showMessage(response.message, response.success)
                            }

                        }

                        GetUserToken.name -> {

                            it.data?.let { res ->
                                val response = ProjectUtil.stringToObject(
                                    res.string(),
                                    LoginResponse::class.java
                                )
                                if (response.success) {
                                    ProjectUtil.putApiTokenBearer(
                                        this,
                                        "Bearer ${response.data.token}"
                                    )

                                    Log.d("api_token", "apiResponse: ${response.data.token}")

                                    ProjectUtil.putUserObjects(
                                        this,
                                        response
                                    )

                                    Handler(Looper.myLooper()!!).postDelayed({
                                        startActivity(
                                            Intent(
                                                this,
                                                HomeActivity::class.java
                                            ).addFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            )
                                        )
                                    }, 200)


                                } else {
                                    ProjectUtil.getErrorMessage(res.toString())
                                }
                                showMessage(response.message, response.success)
                            }

                        }

                    }


                }
            }

        }

    }

    companion object {
        const val TAG = "LoginActivity"
    }

}