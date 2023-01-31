package com.dalua.app.ui.home.homeactivity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.view.iterator
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityHomeBinding
import com.dalua.app.models.CreateAquariumResponse
import com.dalua.app.models.LoginResponse
import com.dalua.app.utils.AppConstants.ApiTypes.CreateAquariumApi
import com.dalua.app.utils.AppConstants.ApiTypes.GetUserToken
import com.dalua.app.utils.ProjectUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObjects()
        listeners()
        observers()
    }

    @SuppressLint("HardwareIds")
    override fun onStart() {
        super.onStart()

        val k = Settings.Secure.getString((this).contentResolver, Settings.Secure.ANDROID_ID)
        Log.d(TAG, "onStart: $k")

    }

    companion object {
        const val TAG = "HomeActivity"
    }

    private fun observers() {

        viewModel.apiResponse.observe(this) { response ->
            when (response) {

                is Resource.Error -> {
                    when (response.api_Type) {
                        CreateAquariumApi.name,
                        GetUserToken.name -> {
                            hideWorking()
                            showMessage(response.error, false)
                            Log.d(
                                TAG,
                                "ResponseError: " + response.error + " \n type: " + response.api_Type
                            )
                        }
                    }
                }

                is Resource.Loading -> {
                    showWorking()
                }

                is Resource.Success -> {
                    hideWorking()
                    when (response.api_Type) {

                        CreateAquariumApi.name -> {
                            response.data?.let { responseBody ->
                                val createAquariumResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateAquariumResponse::class.java
                                )
                                viewModel.aquariumCreated.value = true
                                viewModel.callUserAquarium.postValue(true)
                                showMessage(
                                    createAquariumResponse.message,
                                    createAquariumResponse.success
                                )
                            }

                        }

                        GetUserToken.name -> {

                            response.data?.let {

                                val loginResponse = ProjectUtil.stringToObject(
                                    it.string(),
                                    LoginResponse::class.java
                                )

                                ProjectUtil.putApiTokenBearer(
                                    this,
                                    "Bearer ${loginResponse.data.token}"
                                )
                                Log.d(TAG, "Token: Bearer ${loginResponse.data.token}")
                                viewModel.callUserAquarium.value = true

                            }

                        }

                    }

                }

            }
        }

        viewModel.showAquariumDialog.observe(this) {
            if (!isShowing) {
                isShowing = true
                showCreateAquariumDialog()
            } else {
                isShowing = false
            }
        }
    }

    private fun listeners() {

        navController.addOnDestinationChangedListener { _, destination, _ ->

            for (menuItem in binding.bottomNavigationView.menu.iterator()) {
                menuItem.isEnabled = true
            }

            val menu = binding.bottomNavigationView.menu.findItem(destination.id)
            menu?.isEnabled = false
        }

        binding.bottomNavigationView.menu.findItem(R.id.home)
            .setOnMenuItemClickListener { menuItem ->
                false
            }

        binding.bottomNavigationView.menu.findItem(R.id.add)
            .setOnMenuItemClickListener { menuItem ->
                if (!isShowing) {
                    isShowing = true
                    showCreateAquariumDialog()
                } else {
                    isShowing = false
                }
                true
            }

        binding.bottomNavigationView.menu.findItem(R.id.profile)
            .setOnMenuItemClickListener { menuItem ->
                false
            }

    }

    var name: String = ""
    var isShowing = false

    private fun showCreateAquariumDialog() {
        val createAquariumDialog = Dialog(this)
        if (createAquariumDialog.isShowing) createAquariumDialog.cancel()
        createAquariumDialog.setContentView(R.layout.item_create_aquarium)
        createAquariumDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        createAquariumDialog.setCancelable(true)
//        createAquariumDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        createAquariumDialog.findViewById<Button>(R.id.button1).setOnClickListener {
            if (ProjectUtil.IsConnected(this)) {
                name = createAquariumDialog.findViewById<EditText>(R.id.editText)
                    .text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.getAquariumApi(name, 1.toString(), 1.toString())
                    createAquariumDialog.dismiss()
                } else
                    showMessage("Please enter aquarium name", false)


            } else
                showMessage("Check your internet connection", false)
        }

        createAquariumDialog.findViewById<Button>(R.id.button).setOnClickListener {
            isShowing = false
            createAquariumDialog.dismiss()
        }
        createAquariumDialog.setOnCancelListener {
            isShowing = false
            createAquariumDialog.findViewById<EditText>(R.id.editText)!!.text =
                Editable.Factory.getInstance().newEditable("")
        }
        createAquariumDialog.show()
    }

    private fun initObjects() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        window.statusBarColor = resources.getColor(R.color.white, theme)
        myProgressDialog()
        binding.lifecycleOwner = this
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        viewModel.callUserAquarium.value = true
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Stop")
    }

}