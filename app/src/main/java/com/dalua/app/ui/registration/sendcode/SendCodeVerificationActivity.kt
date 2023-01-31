package com.dalua.app.ui.registration.sendcode

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivitySendCodeVerificationBinding
import com.dalua.app.models.LoginResponse
import com.dalua.app.ui.registration.verifycode.VerifyCodeActivity
import com.dalua.app.utils.AppConstants.ApiTypes.ResetPasswordRequest
import com.dalua.app.utils.AppConstants.CurrentActivity.*
import com.dalua.app.utils.ProjectUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SendCodeVerificationActivity : BaseActivity() {

    var currentActivity: String? = null
    var isPasswordActivity: Boolean? = null
    val viewModel: SendCodeVerificationVM by viewModels()
    lateinit var binding: ActivitySendCodeVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObject()
        observers()
        apiResponse()

    }

    private fun observers() {

        viewModel.showMessage.observe(this) {
            showMessage(it.message, it.boolean)
        }

        viewModel.backPressed.observe(this) {
            finish()
        }

    }

    private fun initObject() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_code_verification)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        myProgressDialog()
        currentActivity = intent.getStringExtra(IntentKey1.toString())!!

        if (currentActivity.contentEquals(PasswordVerification.toString())) {
            findViewById<ImageView>(R.id.image_view).setImageResource(R.drawable.ic_forgotpassword_icon)
            findViewById<TextView>(R.id.name).text = "Don’t worry"
            findViewById<Button>(R.id.button).text = "Next"
            findViewById<TextView>(R.id.textView1).text =
                "Please add your email, we’ll send a code. Then you Can rest your password"
            isPasswordActivity = true
        }


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

                        ResetPasswordRequest.name -> {

                            it.data?.let { res ->
                                val response = ProjectUtil.stringToObject(
                                    res.string(),
                                    LoginResponse::class.java
                                )

                                if (response.success) {
                                    showMessage(response.message, true)
                                    Handler(Looper.myLooper()!!).postDelayed({
                                        startActivity(
                                            Intent(this, VerifyCodeActivity::class.java)
                                                .putExtra(
                                                    "email",
                                                    viewModel.verificationEmail.value
                                                ).putExtra(IntentKey1.toString(), currentActivity)
                                        )
                                    }, 200)
                                }

                            }
                        }

                    }

                }


            }

        }


    }

    companion object{
        const val TAG = "SendCodeVerificationActivity"
    }

}