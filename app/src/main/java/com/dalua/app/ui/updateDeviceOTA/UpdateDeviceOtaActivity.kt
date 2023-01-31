package com.dalua.app.ui.updateDeviceOTA

import android.Manifest
import android.app.Dialog
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.api.VolleyMultiPartModule.VolleyMultipartRequest
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityUpdateDeviceOtaBinding
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.models.CreateDeviceResponse
import com.dalua.app.models.Device
import com.dalua.app.models.ResponseOtaFiles
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.ProjectUtil
import com.dalua.app.utils.ResourceOTARepository
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.dhaval2404.imagepicker.util.PermissionUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class UpdateDeviceOtaActivity : BaseActivity() {
    lateinit var binding: ActivityUpdateDeviceOtaBinding
    private val viewModel: UpdateDeviceOtaVM by viewModels()

    private val countDownTimer = object : CountDownTimer(50000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val progress = 70 - (millisUntilFinished / 1000)
            binding.progressBarLoading.max = 70
            binding.progressBarLoading.setProgress(progress.toInt(), true)
        }

        override fun onFinish() {
            Log.d(TAG, "onFinish: ")
            viewModel.getDeviceDetails(viewModel.device.value!!.id)
        }
    }
    private val countDownTimerOnSuccess = object : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick: countDownTimerOnSuccess: " + (millisUntilFinished / 1000))
        }

        override fun onFinish() {
            successfullyUpdated()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObjects()
        observers()
        apiResponse()
        listener()
    }

    private fun initObjects() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_device_ota)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        myProgressDialog()
        viewModel.device.value =
            ProjectUtil.stringToObject(intent.getStringExtra("device"), Device::class.java)
        if (intent.hasExtra("wifi")) {
            viewModel.wifiSSID.value = intent.getStringExtra("wifi")
        } else {
            viewModel.wifiSSID.value = viewModel.device.value!!.wifi
        }
        viewModel.wifiMessage.value = getString(
            R.string.please_make_sure_your_phone_is_connected_to_wifi_network_and_has_a_stable_internet_connection_update_may_take_2_5_minutes,
            viewModel.wifiSSID.value
        )
        setAdapter()
    }

    private fun apiResponse() {
        viewModel.apiResponse.observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    hideWorking()
                    when (response.api_Type) {
                        AppConstants.ApiTypes.ChangeProductType.name -> {
                            countDownTimerOnSuccess.cancel()
                            failedToUpdate(2)
                        }
                        AppConstants.ApiTypes.GetDeviceDetails.name -> {
                            countDownTimerOnSuccess.cancel()
                            failedToUpdate(1)
                        }
                    }
                }
                is Resource.Loading -> {
                    showWorking()
                }
                is Resource.Success -> {
                    hideWorking()
                    when (response.api_Type) {

                        AppConstants.ApiTypes.GetDeviceDetails.name -> {
                            response.data?.let { responseBody ->
                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateDeviceResponse::class.java
                                )
                                viewModel.device.value = createDeviceResponse.data
                                if (viewModel.device.value!!.version != null) {
                                    viewModel.changeProductType(
                                        viewModel.device.value!!.id.toString(),
                                        viewModel.otaFile.value!!.product.id
                                    )
                                } else {
                                    failedToUpdate(0)
                                }
                            }
                        }

                        AppConstants.ApiTypes.ChangeProductType.name -> {
                            viewModel.status.value = 1
                            viewModel.updatingTitle.value = "Installing Firmware"
                            viewModel.updatingMessage.value = "We're almost done."
                            viewModel.buttonText.value = "Update"
                            countDownTimerOnSuccess.start()
                        }
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        val otaFiles: ArrayList<ResponseOtaFiles.OtaFile> = arrayListOf()
        otaFiles.add(ResourceOTARepository(resources).getBlazeXOtaFile())
        otaFiles.add(ResourceOTARepository(resources).getX4OtaFile())
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = UpdateDeviceOtaAdapter(
            this,
            otaFiles,
            object : UpdateDeviceOtaAdapter.UpdateDeviceOtaAdapterCallback {
                override fun onDeviceClick(otaFile: ResponseOtaFiles.OtaFile) {
                    Log.d(TAG, "onDeviceClick: ")
                    viewModel.otaFile.value = otaFile
                }
            })
    }

    private fun observers() {
        viewModel.status.observe(this) {

        }
        viewModel.backPress.observe(this) {
            onBackPressed()
        }
        viewModel.buttonClick.observe(this) {
            if (viewModel.otaFile.value != null) {
                if (viewModel.status.value == 3) {
                    onBackPressed()
                } else if (viewModel.status.value == 4) {
                    onBackPressed()
                } else {
                    if (viewModel.device.value!!.version == null) {
                        updateDeviceVersion1(viewModel.otaFile.value!!)
                    } else {
                        deviceIsAlreadyUpdated()
                    }
                }
            } else {
                showAlertDialogWithOutListener(
                    this,
                    "Alert",
                    "Please select your product type",
                    "Ok"
                )
            }
        }
    }

    private fun deviceIsAlreadyUpdated() {
        viewModel.status.value = 4
        viewModel.updatingTitle.value = "Updated Software"
        viewModel.updatingMessage.value = "Device is already up to date."
        viewModel.buttonText.value = "Done"
        binding.progressBarLoading.progress = 100
    }

    private fun updateDeviceVersion1(otaFile: ResponseOtaFiles.OtaFile) {
        viewModel.status.value = 1
        viewModel.updatingTitle.value = "Updating Firmware"
        viewModel.updatingMessage.value = "It will take a few moments, please sit back and relax."
        viewModel.buttonText.value = "Update"
        Log.d(TAG, "updateDeviceOta: " + ProjectUtil.objectToString(otaFile))
        Log.d(TAG, "updateDeviceOta: productId: " + otaFile.productId)
        Log.d(
            TAG,
            "updateDeviceOta: URL: " + "http://" + viewModel.device.value!!.ipAddress + "/update"
        )
        if (viewModel.device.value!!.ipAddress.isNotEmpty()) {
            countDownTimer.start()
            Handler(Looper.getMainLooper()).postDelayed({
                Log.d(TAG, "updateDeviceVersion: send call: ")
                val volleyMultipartRequest: VolleyMultipartRequest =
                    object : VolleyMultipartRequest(
                        Method.POST,
                        "http://" + viewModel.device.value!!.ipAddress + "/update",
                        Response.Listener<NetworkResponse> { response ->
                            Log.d(
                                TAG,
                                "updateDeviceVersion: response: " + response.statusCode
                            )
                        },
                        Response.ErrorListener { error ->
                            Log.d(TAG, "updateDeviceVersion: error" + error.cause)
                        }) {
                        override fun getByteData(): Map<String, VolleyMultipartRequest.DataPart> {
                            val params: MutableMap<String, DataPart> = HashMap()
                            params["file-name"] = DataPart(
                                otaFile.name,
                                otaFile.bytesArray!!
                            )
                            return params
                        }
                    }
                volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(40000, -1, 0f)
                Volley.newRequestQueue(this).add(volleyMultipartRequest)
            }, 10000)
        } else {
            showAlertDialogWithListener(this,
                "Ip Address not available",
                "No Ip Address found from your device, Would you like to add this device again after delete?",
                "Yes",
                "Cancel", false,
                object : AlertDialogInterface {
                    override fun onPositiveClickListener(dialog: Dialog?) {
                        onBackPressed()
                        dialog?.dismiss()
                    }

                    override fun onNegativeClickListener(dialog: Dialog?) {
                        dialog?.dismiss()
                    }
                })
        }
    }

    private fun failedToUpdate(status: Int) {
        if (status != 0) {
            viewModel.status.value = 2
            viewModel.updatingTitle.value = "Error"
            viewModel.updatingMessage.value = "Oops, something went wrong, Please try again."
            viewModel.buttonText.value = "Retry"
            binding.progressBarLoading.progress = 0
        } else {
            updateDeviceVersion1(viewModel.otaFile.value!!)
        }
    }

    private fun successfullyUpdated() {
        showAlertDialogWithListener(this,
            "Warning",
            "If the device shows “Not Connected” status OR incorrect device image after the update, please delete and re-add the device.",
            "OK",
            "",
            true,
            object : AlertDialogInterface {
                override fun onPositiveClickListener(dialog: Dialog?) {
                    dialog?.dismiss()
                }

                override fun onNegativeClickListener(dialog: Dialog?) {
                    dialog?.cancel()
                }
            })
        countDownTimer.cancel()
        viewModel.status.value = 3
        viewModel.updatingTitle.value = "Update Complete"
        viewModel.updatingMessage.value =
            "Congratulations! You've now updated to the latest firmware."
        viewModel.buttonText.value = "Done"
        binding.progressBarLoading.progress = 100
        deleteFileFromStorage(viewModel.otaFile.value!!.location)
    }

    private fun listener() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        countDownTimerOnSuccess.cancel()
        countDownTimer.cancel()
    }

    companion object {
        const val TAG = "UpdateDeviceOtaActivity"
    }
}