package com.dalua.app.ui.registration.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivitySignupBinding
import com.dalua.app.models.LoginResponse
import com.dalua.app.models.apiparameter.SignupUser
import com.dalua.app.ui.home.homeactivity.HomeActivity
import com.dalua.app.ui.registration.login.LoginActivity
import com.dalua.app.ui.registration.verifycode.VerifyCodeActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.CurrentActivity.EmailVerification
import com.dalua.app.utils.AppConstants.CurrentActivity.IntentKey1
import com.dalua.app.utils.AppConstants.RegistrationType
import com.dalua.app.utils.ProjectUtil
import com.ehsanmashhadi.library.view.CountryPicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.*

@AndroidEntryPoint
class SignupActivity : BaseActivity() {

    private lateinit var countryCodePicker: CountryPicker
    val viewmodel: SignupVM by viewModels()
    lateinit var binding: ActivitySignupBinding
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObjects()
        observers()
        apiResponse()

    }

    private fun apiResponse() {

        viewmodel.apiResponse.observe(this) {
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
                    it.data?.let { res ->
                        val response = ProjectUtil.stringToObject(
                            res.string(),
                            LoginResponse::class.java
                        )

                        if (response.success) {

                            showMessage(response.message, true)

                            if (response.message.contentEquals("Registration Completed")) {
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

                            } else {

                                Handler(Looper.myLooper()!!).postDelayed({
                                    startActivity(
                                        Intent(
                                            this,
                                            VerifyCodeActivity::class.java
                                        ).putExtra("email", viewmodel.email.value)
                                            .putExtra(
                                                IntentKey1.toString(),
                                                EmailVerification.toString()
                                            )
                                    )
                                }, 200)
                            }
                        } else {
                            ProjectUtil.getErrorMessage(res.toString())
                            showMessage(response.message, true)
                        }
                    }

                }
            }

        }

    }

    private fun observers() {

        viewmodel.signInWithGoogle.observe(this) {
            val signInIntent: Intent = googleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        }

        viewmodel.showPicker.observe(this) {
            countryCodePicker.show(this)
        }

        viewmodel.showTankSizePicker.observe(this) {

            val popupMenu = PopupMenu(this, findViewById(R.id.sdfsd))
            popupMenu.menu.add("Nano")
            popupMenu.menu.add("3\'")
            popupMenu.menu.add("4\'")
            popupMenu.menu.add("5\'")
            popupMenu.menu.add("6\'")
            popupMenu.menu.add("Monster")
            popupMenu.gravity = Gravity.RIGHT
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.title.toString()) {
                    "Nano" -> {
                        viewmodel.tankSize.value = "Nano"
                    }
                    "3\'" -> {
                        viewmodel.tankSize.value = "3'"
                    }
                    "4\'" -> {
                        viewmodel.tankSize.value = "4'"
                    }
                    "5\'" -> {
                        viewmodel.tankSize.value = "5'"
                    }
                    "6\'" -> {
                        viewmodel.tankSize.value = "6'"
                    }
                    "Monster" -> {
                        viewmodel.tankSize.value = "Monster"
                    }
                }
                false
            }
            popupMenu.show()


        }


        viewmodel.backPressed.observe(this) {
            finish()
        }


        viewmodel.showMessage.observe(this) {
            showMessage(it.message, it.boolean)
        }


    }

    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            binding.countryFlag.setImageDrawable(getDrawable(tm.networkCountryIso))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.viewModel = viewmodel
        binding.lifecycleOwner = this
        viewmodel.phoneNumberUtil = PhoneNumberUtil.createInstance(this)
        myProgressDialog()
        binding.viewModel = viewmodel
        binding.lifecycleOwner = this
        val locales: String = resources.configuration.locales[0].displayCountry
        Log.d(TAG, "initObjects: default country: $locales")
        countryCodePicker = CountryPicker.Builder(this).run {
            setCountrySelectionListener {
                viewmodel.phoneCountryShortcut = it.code
                viewmodel.phoneCode.value =
                    "+" + viewmodel.phoneNumberUtil?.getCountryCodeForRegion(viewmodel.phoneCountryShortcut)
                binding.countryFlag.setImageDrawable(getDrawable(it.flagName))
                viewmodel.country.value = it.name
                Log.d(TAG, "initObjects: " + it.name)
                Log.d(TAG, "initObjects: " + it.code)
                Log.d(TAG, "initObjects: " + it.dialCode)
            }
            showingFlag(true)
            enablingSearch(true)
            setPreSelectedCountry(locales) {
                viewmodel.phoneCountryShortcut = it.code
                viewmodel.phoneCode.value =
                    "+" + viewmodel.phoneNumberUtil?.getCountryCodeForRegion(viewmodel.phoneCountryShortcut)
                binding.countryFlag.setImageDrawable(getDrawable(it.flagName))
                viewmodel.country.value = it.name
                Log.d(TAG, "initObjects: name: " + it.name)
                Log.d(TAG, "initObjects: dialCode: " + it.dialCode)
                Log.d(TAG, "initObjects: code: " + it.code)
            }
            build()
        }

        // google signIn
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(AppConstants.GoogleClientID)
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    fun goToLoginSignup(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun signInResultFromGoogle(data: Intent) {

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personToken = acct.idToken
            val personPhoto: Uri? = acct.photoUrl


            viewmodel.registerUserGmail(
                SignupUser(
                    acct.givenName!!,
                    null,
                    acct.familyName!!,
                    acct.displayName?.replace(" ", "")!!,
                    acct.email!!,
                    null,
                    null,
                    null,
                    social_user_id = personId,
                    social_token = personToken,
                    login_type = "2",
                )
            )
            googleSignInClient.signOut()

        }


    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {

                // There are no request codes
                signInResultFromGoogle(result.data!!)

            }
        }

    fun signUp(view: View) {

        viewmodel.registerUser(RegistrationType.Email.toString())

    }

    companion object {
        const val TAG = "SignupActivity"
    }


}