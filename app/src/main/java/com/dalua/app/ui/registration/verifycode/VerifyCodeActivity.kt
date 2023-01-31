package com.dalua.app.ui.registration.verifycode

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityVerifyCodeBinding
import com.dalua.app.models.LoginResponse
import com.dalua.app.ui.changepass.ChangePasswordActivity
import com.dalua.app.ui.home.homeactivity.HomeActivity
import com.dalua.app.utils.AppConstants.ApiTypes.*
import com.dalua.app.utils.AppConstants.CurrentActivity.*
import com.dalua.app.utils.ProjectUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyCodeActivity : BaseActivity() {

    private var currentActivity: String? = null
    lateinit var binding: ActivityVerifyCodeBinding
    val viewModel: VerifyCodeVM by viewModels()
    lateinit var countDownTimer: CountDownTimer
    var isTimerRunning = true


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

    }

    private fun apiResponse() {

        viewModel.apiResponse.observe(this) {

            when (it) {

                is Resource.Error -> {
                    hideWorking()
                    showMessage(it.error, false)
                    Log.d(TAG, "ResponseError: " + it.error + " \n type: " + it.api_Type)
                    when (it.api_Type) {

                        VerifyUserCode.name -> {
                            binding.lineField.text = Editable.Factory.getInstance().newEditable("")
                        }

                    }
                }

                is Resource.Loading -> {
                    showWorking()
                }

                is Resource.Success -> {

                    hideWorking()

                    when (it.api_Type) {

                        VerifyUserCode.name -> {

                            it.data?.let { res ->
                                val response = ProjectUtil.stringToObject(
                                    res.string(),
                                    LoginResponse::class.java
                                )

                                if (response.success) {

                                    showMessage(response.message, true)

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
                                                Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            )
                                        )
                                        finish()
                                    }, 200)
                                    binding.lineField.text =
                                        Editable.Factory.getInstance().newEditable("")

                                } else {
                                    showMessage(response.message, true)
                                }

                            }
                        }

                        VerifyPasswordCode.name -> {
                            it.data?.let { res ->
                                val response = ProjectUtil.stringToObject(
                                    res.string(),
                                    LoginResponse::class.java
                                )

                                if (response.success) {
                                    showMessage(response.message, true)
                                    startActivity(
                                        Intent(this, ChangePasswordActivity::class.java)
                                            .putExtra("email", viewModel.sendEmail.value)
                                            .putExtra("code", viewModel.code)
                                    )
                                }

                            }
                        }

                        ResendUserCode.name -> {
                            it.data?.let { res ->
                                val response = ProjectUtil.stringToObject(
                                    res.string(),
                                    LoginResponse::class.java
                                )

                                if (response.success) {
                                    showMessage(response.message, true)
                                    countDownTimer()
                                } else {
                                    ProjectUtil.getErrorMessage(res.toString())
                                    showMessage(response.message, true)
                                }
                            }
                        }

                        ResetPasswordRequest.name -> {
                            it.data?.let { res ->
                                val response = ProjectUtil.stringToObject(
                                    res.string(),
                                    LoginResponse::class.java
                                )

                                if (response.success) {
                                    showMessage(response.message, true)
                                    countDownTimer()
                                } else {
                                    ProjectUtil.getErrorMessage(res.toString())
                                    showMessage(response.message, true)
                                }
                            }
                        }

                    }

                }

            }
        }

    }

    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_code)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        currentActivity = intent.getStringExtra(IntentKey1.toString())!!
        viewModel.sendEmail.value = intent.getStringExtra("email")!!

        myProgressDialog()
        if (currentActivity != null)
            if (currentActivity.contentEquals(PasswordVerification.toString())) {
                viewModel.isPassword = true
                findViewById<ImageView>(R.id.image_view).setImageResource(R.drawable.ic_forgotpassword_icon)
                findViewById<TextView>(R.id.name).text = "Don’t worry"
                findViewById<Button>(R.id.button).text = "Next"
            } else {
                viewModel.isPassword = false
            }

        countDownTimer()

    }

    private fun countDownTimer() {

        countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                isTimerRunning = true
                var seconds = (millisUntilFinished / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60
                Log.d(
                    TAG, "onTick: TIME : " + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
                )
                viewModel.timerTime.value = String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
                viewModel.enableNow.value = false
            }

            override fun onFinish() {
                isTimerRunning = false
                viewModel.enableNow.value = true
                viewModel.timerTime.value = "00:00"
            }
        }.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isTimerRunning)
            countDownTimer.cancel()
    }

    companion object {
        const val TAG = "VerifyCodeActivity"
    }

}