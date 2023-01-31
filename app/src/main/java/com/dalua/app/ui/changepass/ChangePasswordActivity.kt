package com.dalua.app.ui.changepass

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityChangePasswordBinding
import com.dalua.app.models.LoginResponse
import com.dalua.app.ui.registration.login.LoginActivity
import com.dalua.app.utils.AppConstants.ApiTypes.UpdateUserPassword
import com.dalua.app.utils.ProjectUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity() {

    lateinit var binding: ActivityChangePasswordBinding
    val viewModel: ChangePasswordVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObject()
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

    private fun initObject() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        myProgressDialog()

        viewModel.email = intent.getStringExtra("email")!!
        viewModel.code = intent.getStringExtra("code")!!

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

                        UpdateUserPassword.name -> {

                            it.data?.let { res ->
                                val response = ProjectUtil.stringToObject(
                                    res.string(),
                                    LoginResponse::class.java
                                )

                                if (response.success) {
                                    showMessage(response.message, true)
                                    Intent(this, LoginActivity::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(this)
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

        }


    }

    companion object{
        const val TAG = "ChangePasswordActivity"
    }
}