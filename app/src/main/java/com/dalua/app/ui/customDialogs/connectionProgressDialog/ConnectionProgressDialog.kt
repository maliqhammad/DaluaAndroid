package com.dalua.app.ui.customDialogs.connectionProgressDialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.dalua.app.R
import com.dalua.app.databinding.ConnectionProgressDialogBinding
import com.dalua.app.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectionProgressDialog() : DialogFragment() {
    val viewModel: ConnectionProgressDialogVM by activityViewModels()
    lateinit var listener: ConnectionProgressDialogCallback
    lateinit var binding: ConnectionProgressDialogBinding
    lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.connection_progress_dialog,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        Log.d(TAG, "onViewCreated: ")
        initObjects()
        setListener()
        observers()
    }

    @SuppressLint("HardwareIds")
    private fun initObjects() {
        viewModel.title.value = "Troubleshoot"
    }

    private fun setListener() {
        binding.button.setOnClickListener {
            if (viewModel.statusCode.value == -1) {
                listener.close(true)
            } else if (viewModel.statusCode.value == -2) {
                //-2 is for device type and set from add device after aws subscribe
                if (binding.saltwaterCheck.isChecked) {
                    listener.onSelectDeviceType("Marine")
                } else {
                    listener.onSelectDeviceType("Fresh")
                }
                viewModel.progressBar.value = true
            } else {
                listener.onChangeStatus(viewModel.statusCode.value)
            }
        }
        binding.close.setOnClickListener {
            listener.close(false)
        }
    }

    private fun observers() {
        viewModel.title.observe(viewLifecycleOwner) {
            binding.title.text = it
        }
        viewModel.description.observe(viewLifecycleOwner) {
            binding.description.text = it
        }
        viewModel.connectionState.observe(viewLifecycleOwner) {
            binding.connectionState.setImageResource(it)
        }
        viewModel.button.observe(viewLifecycleOwner) {
            binding.button.text = it
        }
        viewModel.statusCode.observe(viewLifecycleOwner) {
            setStatusCode(it)
        }
        viewModel.isAllDone.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.description.value =
                    getString(R.string.congratulations_your_device_has_been_successfully_added_to_your_dalua_ecosystem)
                viewModel.title.value = "Successfully Connected"
                viewModel.button.value = "Done"
                viewModel.statusCode.value = -1
            }
        }
    }

    private fun setStatusCode(code: Int?) {
        Log.d(TAG, "setStatusCode: $code")
        when (code) {
            AppConstants.DEVICE_CONNECTION_IN_PROGRESS -> {
                viewModel.description.value = getString(R.string.pairing_dalua_device_message)
                viewModel.title.value = "Bluetooth Connection"
                viewModel.button.value = "Connecting"
                viewModel.progressBar.value = true
                viewModel.connectionState.value = R.drawable.ic_bluetooth_connectivity
            }
            AppConstants.DEVICE_CONNECTION_FAILED -> {
                viewModel.description.value =
                    getString(R.string.pairing_dalua_device_failed_message)
                viewModel.title.value = "Bluetooth Connection"
                viewModel.button.value = "Try Again"
                viewModel.progressBar.value = false
                viewModel.connectionState.value = R.drawable.ic_bluetooth_disconnectivity
            }
            AppConstants.DEVICE_CONNECTION_SUCCESS -> {
                viewModel.description.value = "Connecting to Wi-Fi...."
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.button.value = "Connecting..."
                viewModel.progressBar.value = true
                viewModel.connectionState.value = R.drawable.ic_icon_wifi_blue
            }
            AppConstants.STUCK_IN_WIFI -> {
                viewModel.description.value = "Something went wrong with the Wi-Fi"
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.button.value = "Try Again"
                viewModel.progressBar.value = false
                viewModel.connectionState.value = R.drawable.ic_icon_wifi_blue_disable
            }
            AppConstants.DEVICE_RESET -> {
                viewModel.description.value = "Device has been reset, Please try again"
                viewModel.title.value = "Bluetooth Connection"
                viewModel.button.value = "Try Again"
                viewModel.progressBar.value = false
                viewModel.connectionState.value = R.drawable.ic_error_connection_24
            }
            AppConstants.WiFi_SSID_NOT_AVAILABLE -> {
                viewModel.description.value = "Wi-Fi SSID not available"
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.button.value = "Try Again"
                viewModel.progressBar.value = false
                viewModel.connectionState.value = R.drawable.ic_icon_wifi_blue_disable
            }
            AppConstants.WiFi_WRONG_CREDENTIALS -> {
                viewModel.description.value = "Wi-Fi wrong credential"
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.button.value = "Try Again"
                viewModel.progressBar.value = false
                viewModel.connectionState.value = R.drawable.ic_icon_wifi_blue_disable
            }
            AppConstants.WIFI_CONNECTION_IN_PROGRESS -> {
                viewModel.description.value = "Connecting to Wi-Fi..."
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.button.value = "Connecting..."
                viewModel.progressBar.value = true
                viewModel.connectionState.value = R.drawable.ic_icon_wifi_blue
            }
            AppConstants.WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                viewModel.description.value =
                    "No internet available, Please check your internet and try again"
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.button.value = "Try Again"
                viewModel.progressBar.value = false
                viewModel.connectionState.value = R.drawable.ic_icon_wifi_blue_disable
            }
            AppConstants.WiFi_CONNECTED -> {
                viewModel.description.value = "Registering on Cloud..."
                viewModel.title.value = "Cloud Connection"
                viewModel.button.value = "Connecting"
                viewModel.progressBar.value = true
                viewModel.connectionState.value = R.drawable.cloud_connectivity
            }
            AppConstants.AWS_CONNECTION_IN_PROGRESS -> {
                viewModel.description.value = "Registering on Cloud..."
                viewModel.title.value = "Cloud Connection"
                viewModel.button.value = "Connecting"
                viewModel.progressBar.value = true
                viewModel.connectionState.value = R.drawable.cloud_connectivity
            }
            AppConstants.AWS_CONNECTION_FAILED -> {
                viewModel.description.value = "Failed to connect with Dalua Cloud"
                viewModel.title.value = "Cloud Connection"
                viewModel.button.value = "Try Again"
                viewModel.progressBar.value = false
                viewModel.connectionState.value = R.drawable.cloud_disconnectivity
            }
            AppConstants.AWS_SUBSCRIBE_FAILED -> {
                viewModel.description.value = "Failed to connect with Dalua Cloud"
                viewModel.title.value = "Cloud Connection"
                viewModel.button.value = "Try Again"
                viewModel.progressBar.value = false
                viewModel.connectionState.value = R.drawable.cloud_disconnectivity
            }
            AppConstants.AWS_CONNECTED -> {
                viewModel.description.value = "Connecting with Dalua Cloud"
                viewModel.title.value = "Cloud Connection"
                viewModel.button.value = "Connecting"
                viewModel.progressBar.value = true
                viewModel.connectionState.value = R.drawable.cloud_connectivity
            }
            AppConstants.AWS_SUBSCRIBE_SUCCESSFUL -> {
                viewModel.description.value = "Successfully connected with Dalua Cloud"
                viewModel.title.value = "Cloud Connection"
                viewModel.button.value = "Connected!"
                viewModel.progressBar.value = false
                viewModel.connectionState.value = R.drawable.ic_success_icon
            }
            -2 -> {
                viewModel.title.value = "Device Type"
                viewModel.button.value = "Continue"
                viewModel.progressBar.value = false
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    companion object {
        const val TAG = "ConnectionProgressDialog"
    }

    interface ConnectionProgressDialogCallback {
        fun onChangeStatus(code: Int?)
        fun close(refresh: Boolean)
        fun onSelectDeviceType(waterType: String)
    }


}