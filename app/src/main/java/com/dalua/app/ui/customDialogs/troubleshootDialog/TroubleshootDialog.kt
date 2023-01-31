package com.dalua.app.ui.customDialogs.troubleshootDialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.dalua.app.R
import com.dalua.app.databinding.TroubleshootDialogBinding
import com.dalua.app.models.Device
import com.dalua.app.models.DeviceAddedPlace
import com.dalua.app.models.SingleAquarium
import com.dalua.app.ui.wifiactivity.ShowWifiConnectionActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.Companion.AWS_CONNECTED
import com.dalua.app.utils.AppConstants.Companion.AWS_CONNECTION_FAILED
import com.dalua.app.utils.AppConstants.Companion.AWS_CONNECTION_IN_PROGRESS
import com.dalua.app.utils.AppConstants.Companion.AWS_SUBSCRIBE_FAILED
import com.dalua.app.utils.AppConstants.Companion.AWS_SUBSCRIBE_SUCCESSFUL
import com.dalua.app.utils.AppConstants.Companion.COUNTDOWNTIMERTOREADSTATUS
import com.dalua.app.utils.AppConstants.Companion.DEVICE_CONNECTION_FAILED
import com.dalua.app.utils.AppConstants.Companion.DEVICE_CONNECTION_IN_PROGRESS
import com.dalua.app.utils.AppConstants.Companion.DEVICE_CONNECTION_SUCCESS
import com.dalua.app.utils.AppConstants.Companion.DEVICE_DISCONNECTION_FAILED
import com.dalua.app.utils.AppConstants.Companion.DEVICE_FOUND_IN_PROGRESS
import com.dalua.app.utils.AppConstants.Companion.DEVICE_FOUND_SUCCESSFUL
import com.dalua.app.utils.AppConstants.Companion.DEVICE_NOT_FOUND
import com.dalua.app.utils.AppConstants.Companion.DEVICE_RESET
import com.dalua.app.utils.AppConstants.Companion.WIFI_CONNECTION_IN_PROGRESS
import com.dalua.app.utils.AppConstants.Companion.WiFi_CONNECTED
import com.dalua.app.utils.AppConstants.Companion.WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS
import com.dalua.app.utils.AppConstants.Companion.WiFi_SSID_NOT_AVAILABLE
import com.dalua.app.utils.AppConstants.Companion.WiFi_WRONG_CREDENTIALS
import com.dalua.app.utils.ProjectUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class TroubleshootDialog(
    var singleAquarium: SingleAquarium,
    var troubleShootCallback: TroubleShootCallback
) : DialogFragment() {
    //    val viewModel: TroubleshootDialogVM by activityViewModels()
    var isThreeCheck = true
    lateinit var viewModel: TroubleshootDialogVM
    lateinit var binding: TroubleshootDialogBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    val list: MutableList<BleDevice> = mutableListOf()
    private var actionAlreadyPerformed = -1
    private var isIstTimeBleStatus = false
    lateinit var uniqueID: String
    var bleStatus: MutableLiveData<Int> = MutableLiveData()
    var bluetoothConnectionCode: MutableLiveData<Int> = MutableLiveData(1234)
    var ifBluetoothIsWorkingFine = false
    var isCountDownTimerRunning = false
    var scanningTime: Long = 9000
    private lateinit var mContext: Context
    private var isBluetoothConnected = false

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.stateCode.value = WIFI_CONNECTION_IN_PROGRESS
                writeWifiCredential(
                    viewModel.deviceToConnected!!.bleDevice,
                    JSONObject().apply {
                        put("deviceID", uniqueID)
                        put("ssid", AppConstants.SSID)
                        put("pass", AppConstants.PASSWORD)
                    }
                )
            }
        }
    private val gpsSwitchStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
                // Make an action or refresh an already managed state.
                val locationManager =
                    context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (isGpsEnabled) {
                    checkIfBluetoothIsTurnOnOrOFF()
                } else {
                    ProjectUtil.showToastMessage(
                        requireActivity(),
                        false,
                        "Ble won't detect without Location on."
                    )
                    dismiss()
                }
            }
        }
    }
    private val permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Log.d(TAG, "onPermissionGranted: ")
//            initBleManager()
//            checkIfBluetoothIsTurnOnOrOFF()

        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            dismiss()
        }

    }
    private val mBroadcastReceiver1: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_OFF -> {
                        Log.d(TAG, "onReceive: STATE OFF")
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        Log.d(
                            TAG,
                            "mBroadcastReceiver1: STATE TURNING OFF"
                        )
                    }
                    BluetoothAdapter.STATE_ON -> {
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON")
                        checkIfBluetoothIsTurnOnOrOFF()
//                        WifiUtils.withContext(context).enableWifi { checkResult(it) }

                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                        Log.d(TAG, "onReceive: STATE_TURNING_ON")
                    }
                }
            }
        }
    }
    private val countDownTimer = object : CountDownTimer(3000, 500) {

        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            Log.d(
                TAG,
                "onFinish: called with ble status ${BleManager.getInstance().scanSate.name}...!!!"
            )

            isCountDownTimerRunning = false

            if (!ifBluetoothIsWorkingFine) {
                val time = scanningTime - 3000
                if (BleManager.getInstance().scanSate.name.contentEquals("STATE_SCANNING")) {
                    cancelByForce()
                    scanningTime = time
                    val scanRuleConfig = BleScanRuleConfig.Builder()
                        .setScanTimeOut(time)
                        .build()

                    BleManager.getInstance().initScanRule(scanRuleConfig)
                    Log.d(
                        TAG,
                        "onFinish: scanning is restarted inside time: $time $scanningTime"
                    )

                    checkIfBluetoothIsTurnOnOrOFF()
                }

            }
        }
    }
    private val countDownTimerForWifi = object : CountDownTimer(COUNTDOWNTIMERTOREADSTATUS, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick: countDownTimerForWifi: " + (millisUntilFinished / 1000))
            Log.d(TAG, "onTick: countDownTimerForWifi: " + viewModel.stateCode.value)
            if (viewModel.stateCode.value == WIFI_CONNECTION_IN_PROGRESS) {
                readStatusFromDevice(viewModel.deviceToConnected!!.bleDevice)
            } else {
                bleStatus.value = viewModel.stateCode.value
                cancel()
            }
        }

        override fun onFinish() {
            when (viewModel.stateCode.value) {
                WiFi_WRONG_CREDENTIALS -> {
                    bleStatus.value = WiFi_WRONG_CREDENTIALS
                }
                WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                    bleStatus.value = WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS
                }
                WiFi_SSID_NOT_AVAILABLE -> {
                    bleStatus.value = WiFi_SSID_NOT_AVAILABLE
                }
                WIFI_CONNECTION_IN_PROGRESS -> {
                    bleStatus.value = WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS
                }
            }
            Log.d(TAG, "onFinish: countDownTimerForWifi: " + viewModel.stateCode.value)
        }
    }
    private val countDownTimerForAWS = object : CountDownTimer(COUNTDOWNTIMERTOREADSTATUS, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick: countDownTimerForAWS: " + (millisUntilFinished / 1000))
            Log.d(TAG, "onTick: countDownTimerForAWS: " + viewModel.stateCode.value)
            if (viewModel.stateCode.value == AWS_CONNECTION_IN_PROGRESS) {
                readStatusFromDevice(viewModel.deviceToConnected!!.bleDevice)
            } else {
                bleStatus.value = viewModel.stateCode.value
                cancel()
            }
        }

        override fun onFinish() {
            when (viewModel.stateCode.value) {
                AWS_CONNECTION_FAILED -> {
                    bleStatus.value = AWS_CONNECTION_FAILED
                }
                AWS_SUBSCRIBE_FAILED -> {
                    bleStatus.value = AWS_SUBSCRIBE_FAILED
                }
                AWS_CONNECTION_IN_PROGRESS -> {
                    bleStatus.value = AWS_CONNECTION_FAILED
                }
            }
            Log.d(TAG, "onFinish: countDownTimerForAWS: " + viewModel.stateCode.value)
        }
    }
    private val countDownTimerForInitial = object : CountDownTimer(COUNTDOWNTIMERTOREADSTATUS, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick: countDownInitial: " + (millisUntilFinished / 1000))
            readStatusFromDevice(viewModel.deviceToConnected!!.bleDevice)
            if (viewModel.stateCode.value == AWS_SUBSCRIBE_SUCCESSFUL) {
                Log.d(TAG, "onTick: countDownInitial: " + viewModel.stateCode.value)
                cancel()
            } else {
                bleStatus.value = viewModel.stateCode.value
                cancel()
            }
        }

        override fun onFinish() {
            when (viewModel.stateCode.value) {
                AWS_CONNECTION_FAILED -> {
                    bleStatus.value = AWS_CONNECTION_FAILED
                }
                AWS_SUBSCRIBE_FAILED -> {
                    bleStatus.value = AWS_SUBSCRIBE_FAILED
                }
                AWS_CONNECTION_IN_PROGRESS -> {
                    bleStatus.value = AWS_CONNECTION_FAILED
                }
            }
            Log.d(TAG, "onFinish: countDownTimerForAWS: " + viewModel.stateCode.value)
        }
    }
    private val progressCountDown = object : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick: progressCountDown: " + (millisUntilFinished / 1000))
        }

        override fun onFinish() {
            if (bleStatus.value == AWS_CONNECTION_IN_PROGRESS && viewModel.progress.value!!) {
                viewModel.stateCode.value = AWS_SUBSCRIBE_FAILED
            }
            Log.d(TAG, "onFinish: progressCountDown: " + viewModel.stateCode.value)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.troubleshoot_dialog,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(TroubleshootDialogVM::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        Log.d(TAG, "onViewCreated: ")
        initObjects()
        setListener()
        observers()
    }

    @SuppressLint("HardwareIds")
    private fun initObjects() {
        viewModel.aquariumClicked = singleAquarium
        uniqueID = 1.toString()
        if (requireArguments().getString("device") != null) {
            viewModel.backendAddedDevice = ProjectUtil.stringToObject(
                requireArguments().getString("device"),
                Device::class.java
            )
        }

        val bluetoothManager =
            mContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            TedPermission.with(requireContext())
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        Log.d(TAG, "onPermissionGranted: ")
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        dismiss()
                    }
                })
                .setDeniedMessage("If you reject this permission ,you can not use the bluetooth service\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ).check()
        } else {
            TedPermission.with(requireContext())
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject location permission,you can not use this service\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ).check()
        }
    }

    private fun setListener() {
        binding.button.setOnClickListener {
            if (mBluetoothAdapter.isEnabled) {
                if (isBluetoothConnected) {
                    performAction(viewModel.stateCode.value!!)
                } else {
                    performAction(bluetoothConnectionCode.value!!)
                }
            } else {
                ProjectUtil.showToastMessage(
                    requireActivity(),
                    true,
                    "Please turn on your mobile bluetooth."
                )
            }
        }
        binding.close.setOnClickListener {
            dialog!!.dismiss()
        }
    }

    private fun observers() {
        bluetoothConnectionCode.observe(viewLifecycleOwner) {
            Log.d(TAG, "observers: bluetoothConnection: $it")
            updateUIForBluetoothConnection(it)
        }
        bleStatus.observe(viewLifecycleOwner) {
            Log.d(TAG, "observers: statusCode: $it")
            when (it) {
                DEVICE_RESET -> {
                    viewModel.stateCode.value = DEVICE_RESET
                }
                WiFi_SSID_NOT_AVAILABLE -> {
                    viewModel.stateCode.value = WiFi_SSID_NOT_AVAILABLE
                }
                WiFi_WRONG_CREDENTIALS -> {
                    viewModel.stateCode.value = WiFi_WRONG_CREDENTIALS
                }
                WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                    viewModel.stateCode.value = WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS
                }
                WIFI_CONNECTION_IN_PROGRESS -> {
                    viewModel.stateCode.value = WIFI_CONNECTION_IN_PROGRESS
                }
                WiFi_CONNECTED -> {
                    viewModel.stateCode.value = AWS_CONNECTION_IN_PROGRESS
                    writeTopicToConnectAWS(
                        viewModel.deviceToConnected!!.bleDevice,
                        JSONObject().apply {
                            put("deviceID", uniqueID)
                            put("topic", viewModel.backendAddedDevice!!.topic)
                        }
                    )
                }
                AWS_CONNECTION_IN_PROGRESS -> {
                    viewModel.stateCode.value = AWS_CONNECTION_IN_PROGRESS
                }
                AWS_CONNECTION_FAILED -> {
                    viewModel.stateCode.value = AWS_CONNECTION_FAILED
                }
                AWS_SUBSCRIBE_FAILED -> {
                    viewModel.stateCode.value = AWS_SUBSCRIBE_FAILED
                }
                AWS_CONNECTED, AWS_SUBSCRIBE_SUCCESSFUL -> {
                    Log.d(TAG, "observers: connected: start")
                    viewModel.stateCode.value = AWS_SUBSCRIBE_SUCCESSFUL
                }
            }
        }
        with(viewModel) {
            title.observe(viewLifecycleOwner) {
                binding.title.text = it
            }
            description.observe(viewLifecycleOwner) {
                binding.description.text = it
            }
            connectivity.observe(viewLifecycleOwner) {
                binding.connectivity.setImageResource(it)
            }
            buttonText.observe(viewLifecycleOwner) {
                binding.button.text = it
            }
            stateCode.observe(viewLifecycleOwner) {
                Log.d(TAG, "observers: stateCode: $it | $actionAlreadyPerformed")
                if (it != actionAlreadyPerformed) {
                    actionAlreadyPerformed = it
                    updateUI(it)
                }
            }
        }
        viewModel.finishActivityData.observe(viewLifecycleOwner) {
            dismiss()
        }

    }

    private fun performAction(code: Int) {
        Log.d(TAG, "performAction: $code")
        when (code) {
            1234 -> {
                isThreeCheck = true
                initBleManager()
                checkIfBluetoothIsTurnOnOrOFF()
                viewModel.stateCode.value = DEVICE_CONNECTION_IN_PROGRESS
            }
            DEVICE_NOT_FOUND -> {
                isThreeCheck = true
                initBleManager()
                checkIfBluetoothIsTurnOnOrOFF()
            }
            DEVICE_CONNECTION_FAILED -> {
                isThreeCheck = true
                initBleManager()
                checkIfBluetoothIsTurnOnOrOFF()
            }
            DEVICE_DISCONNECTION_FAILED -> {
                isThreeCheck = true
                initBleManager()
                checkIfBluetoothIsTurnOnOrOFF()
            }
            DEVICE_RESET -> {
                isThreeCheck = false
                openWifiActivity()
            }
            WiFi_SSID_NOT_AVAILABLE -> {
                isThreeCheck = false
                openWifiActivity()
            }
            WiFi_WRONG_CREDENTIALS -> {
                isThreeCheck = false
                openWifiActivity()
            }
            WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                isThreeCheck = false
                openWifiActivity()
            }
            AWS_CONNECTION_IN_PROGRESS -> {
                isThreeCheck = false
                viewModel.stateCode.value = AWS_CONNECTION_IN_PROGRESS
                writeTopicToConnectAWS(
                    viewModel.deviceToConnected!!.bleDevice,
                    JSONObject().apply {
                        put("deviceID", uniqueID)
                        put("topic", viewModel.backendAddedDevice!!.topic)
                    })
            }
            AWS_CONNECTION_FAILED -> {
                isThreeCheck = false
                viewModel.stateCode.value = AWS_CONNECTION_IN_PROGRESS
                writeTopicToConnectAWS(
                    viewModel.deviceToConnected!!.bleDevice,
                    JSONObject().apply {
                        put("deviceID", uniqueID)
                        put("topic", viewModel.backendAddedDevice!!.topic)
                    }
                )
            }
            AWS_SUBSCRIBE_FAILED -> {
                isThreeCheck = false
                viewModel.stateCode.value = AWS_CONNECTION_IN_PROGRESS
                writeTopicToConnectAWS(
                    viewModel.deviceToConnected!!.bleDevice,
                    JSONObject().apply {
                        put("deviceID", uniqueID)
                        put("topic", viewModel.backendAddedDevice!!.topic)
                    }
                )
            }
            AWS_SUBSCRIBE_SUCCESSFUL -> {
                if (isThreeCheck) {
                    if (viewModel.backendAddedDevice!!.status != 1) {
                        restartDevice()
                    }
                    Log.d(TAG, "performAction: status: " + viewModel.backendAddedDevice!!.status)
                    Log.d(TAG, "performAction: instant three check: ")
                }
                troubleShootCallback.onDone(viewModel.backendAddedDevice!!.apply { status = 1 })
                dismiss()
                AppConstants.apply {
                    refresh_aquarium.value = true
                    refresh_group_device.value = true
                    refresh_group.value = true
                    refresh_device.value = true
                    device_added_successfully = true
                }
            }

        }
    }

    private fun showIcons(code: Int) {
        when (code) {
            DEVICE_NOT_FOUND, DEVICE_CONNECTION_FAILED, DEVICE_DISCONNECTION_FAILED, DEVICE_RESET -> {
                viewModel.ble_1.value = true
                viewModel.ble.value = false
                viewModel.wifi_1.value = true
                viewModel.wifi.value = false
                viewModel.aws_1.value = true
                viewModel.aws.value = false
            }
            WiFi_SSID_NOT_AVAILABLE, WiFi_WRONG_CREDENTIALS, WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                viewModel.ble_1.value = true
                viewModel.ble.value = true
                viewModel.wifi_1.value = true
                viewModel.wifi.value = false
                viewModel.aws_1.value = true
                viewModel.aws.value = false
            }
            AWS_SUBSCRIBE_FAILED, AWS_CONNECTION_FAILED -> {
                viewModel.ble_1.value = true
                viewModel.ble.value = true
                viewModel.wifi_1.value = true
                viewModel.wifi.value = true
                viewModel.aws_1.value = true
                viewModel.aws.value = false
            }


        }
    }

    private fun updateUIForBluetoothConnection(code: Int) {
        when (code) {
            DEVICE_FOUND_IN_PROGRESS -> {
                isBluetoothConnected = false
                viewModel.description.value = "Discovering your device"
                viewModel.title.value = "Bluetooth Discover"
                viewModel.buttonText.value = "Discovering"
                viewModel.progress.value = true
                viewModel.connectivity.value = R.drawable.ic_bluetooth_connectivity
            }
            DEVICE_FOUND_SUCCESSFUL -> {
                isBluetoothConnected = true
                viewModel.description.value =
                    "Hang tight! we are connecting to your Dalua device. This may take a few moments"
                viewModel.title.value = "Bluetooth Connection"
                viewModel.buttonText.value = "Connecting"
                viewModel.progress.value = true
                viewModel.connectivity.value = R.drawable.ic_bluetooth_connectivity
            }
            DEVICE_NOT_FOUND -> {
                isBluetoothConnected = false
                viewModel.description.value =
                    "Seems like your device is powered off or not in bluetooth range"
                viewModel.title.value = "Bluetooth Connection"
                viewModel.buttonText.value = "Try Again"
                viewModel.progress.value = false
                viewModel.connectivity.value = R.drawable.ic_bluetooth_disconnectivity
            }
            DEVICE_CONNECTION_IN_PROGRESS -> {
                isBluetoothConnected = false
                viewModel.description.value =
                    "Hang tight! we are connecting to your Dalua device. This may take a few moments"
                viewModel.title.value = "Bluetooth Connection"
                viewModel.buttonText.value = "Connecting"
                viewModel.progress.value = true
                viewModel.connectivity.value = R.drawable.ic_bluetooth_connectivity
            }
            DEVICE_CONNECTION_SUCCESS -> {
                isBluetoothConnected = true
                viewModel.ble.value = true
                viewModel.bleConnectivity.value = true
                if (viewModel.backendAddedDevice!!.status == 2) {
                    Log.d(TAG, "updateUIForBluetoothConnection: Write topic: ")
                    viewModel.stateCode.value = AWS_CONNECTION_FAILED
                } else {
                    viewModel.description.value = "Connecting to Wi-Fi...."
                    viewModel.title.value = "Wi-Fi Connection"
                    viewModel.buttonText.value = "Connecting..."
                    viewModel.progress.value = true
                    viewModel.connectivity.value = R.drawable.ic_icon_wifi_blue
                    readStatusFromDevice(viewModel.deviceToConnected!!.bleDevice)
//                    countDownTimerForInitial.start()
                }
            }
            DEVICE_CONNECTION_FAILED -> {
                isBluetoothConnected = false
                if (isIstTimeBleStatus) {
                    viewModel.bleConnectivity.value = false
                    viewModel.description.value =
                        getString(R.string.pairing_dalua_device_failed_message)
                    viewModel.title.value = "Bluetooth Connection"
                    viewModel.buttonText.value = "Try Again"
                    viewModel.progress.value = false
                    viewModel.connectivity.value = R.drawable.ic_bluetooth_disconnectivity
                }
            }
            DEVICE_DISCONNECTION_FAILED -> {
                isBluetoothConnected = false
                viewModel.bleConnectivity.value = false
                viewModel.description.value =
                    "Seems like your device is powered off or not in bluetooth range"
                viewModel.title.value = "Bluetooth Connection"
                viewModel.buttonText.value = "Try Again"
                viewModel.progress.value = false
                viewModel.connectivity.value = R.drawable.ic_bluetooth_disconnectivity
            }
        }
        showIcons(code)
    }

    private fun updateUI(code: Int) {
        when (code) {
            DEVICE_RESET -> {
                viewModel.ble.value = false
                viewModel.wifi.value = false
                viewModel.aws.value = false
                viewModel.title.value = "Bluetooth Connection"
                viewModel.description.value =
                    "The device has been reset, please continue to the troubleshoot process"
                viewModel.buttonText.value = "Fix"
                viewModel.progress.value = false
                viewModel.connectivity.value = R.drawable.ic_error_connection_24
            }
            WiFi_SSID_NOT_AVAILABLE -> {
                viewModel.ble.value = true
                viewModel.wifi.value = false
                viewModel.aws.value = false
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.description.value = "Wi-Fi SSID not Available"
                viewModel.buttonText.value = "Fix"
                viewModel.progress.value = false
                viewModel.connectivity.value = R.drawable.ic_icon_wifi_blue_disable
            }
            WiFi_WRONG_CREDENTIALS -> {
                viewModel.ble.value = true
                viewModel.wifi.value = false
                viewModel.aws.value = false
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.description.value = "Wi-Fi wrong credentials"
                viewModel.buttonText.value = "Fix"
                viewModel.progress.value = false
                viewModel.connectivity.value = R.drawable.ic_icon_wifi_blue_disable
            }
            WIFI_CONNECTION_IN_PROGRESS -> {
                viewModel.ble.value = true
                viewModel.wifi.value = false
                viewModel.aws.value = false
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.description.value = "Wi-Fi connection in progress"
                viewModel.buttonText.value = "Fix"
                viewModel.progress.value = true
                viewModel.connectivity.value = R.drawable.ic_icon_wifi_blue
            }
            WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                viewModel.ble.value = true
                viewModel.wifi.value = false
                viewModel.aws.value = false
                viewModel.title.value = "Wi-Fi Connection"
                viewModel.description.value =
                    "No internet available, Please check your internet and try again"
                viewModel.buttonText.value = "Fix"
                viewModel.progress.value = false
                viewModel.connectivity.value = R.drawable.ic_icon_wifi_blue_disable
            }
            WiFi_CONNECTED -> {
                viewModel.ble.value = true
                viewModel.wifi.value = true
                viewModel.aws.value = false
                viewModel.title.value = "Cloud Connection"
                viewModel.description.value = "Registering on Cloud..."
                viewModel.buttonText.value = "Connecting"
                viewModel.progress.value = true
                viewModel.connectivity.value = R.drawable.cloud_connectivity
            }
            AWS_CONNECTION_IN_PROGRESS -> {
                viewModel.ble.value = true
                viewModel.wifi.value = true
                viewModel.title.value = "Cloud Connection"
                viewModel.description.value =
                    "Registering on Cloud. Hang tight! we are almost there."
                viewModel.buttonText.value = "Connecting"
                viewModel.progress.value = true
                viewModel.connectivity.value = R.drawable.cloud_connectivity
                progressCountDown.start()
            }
            AWS_CONNECTION_FAILED -> {
                viewModel.ble.value = true
                viewModel.wifi.value = true
                viewModel.aws.value = false
                viewModel.title.value = "Cloud Connection"
                viewModel.description.value = "Failed to connect with Dalua Cloud"
                viewModel.buttonText.value = "Fix"
                viewModel.progress.value = false
                viewModel.connectivity.value = R.drawable.cloud_disconnectivity
            }
            AWS_SUBSCRIBE_FAILED -> {
                viewModel.ble.value = true
                viewModel.wifi.value = true
                viewModel.aws.value = false
                viewModel.title.value = "Cloud Connection"
                viewModel.description.value = "Failed to connect with Dalua Cloud"
                viewModel.buttonText.value = "Fix"
                viewModel.progress.value = false
                viewModel.connectivity.value = R.drawable.cloud_disconnectivity
            }
            AWS_CONNECTED -> {
                viewModel.ble.value = true
                viewModel.wifi.value = true
                viewModel.aws.value = true
                viewModel.title.value = "Cloud Connection"
                viewModel.description.value = "Connecting with Dalua Cloud..."
                viewModel.buttonText.value = "Done"
                viewModel.progress.value = true
                viewModel.connectivity.value = R.drawable.cloud_connectivity
            }
            AWS_SUBSCRIBE_SUCCESSFUL -> {
                viewModel.ble.value = true
                viewModel.ble_1.value = true
                progressCountDown.cancel()
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.wifi.value = true
                    viewModel.wifi_1.value = true
                }, 350)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.aws.value = true
                    viewModel.aws_1.value = true
                }, 700)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.progress.value = false
                }, 1000)
                viewModel.title.value = "Connection Successful"
                viewModel.description.value =
                    "Congratulations, your device has been successfully added to your Dalua Ecosystem"
                viewModel.buttonText.value = "Done"
                viewModel.connectivity.value = R.drawable.ic_success_icon
            }
        }
        showIcons(code)
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkIfBluetoothIsTurnOnOrOFF() {
        if (!mBluetoothAdapter.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivity(enableBTIntent)
            mContext.registerReceiver(
                mBroadcastReceiver1,
                IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            )

        } else {

            val manager =
                requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDialog()
                return
            }
            statusCheck()
        }

    }

    private fun statusCheck() {
        Log.d(TAG, "statusCheck: ")
        if (BleManager.getInstance().scanSate.name.contentEquals("STATE_IDLE")) {
            startScanningOfBleDevice()
        } else {
            if (BleManager.getInstance().scanSate.name.contentEquals("STATE_SCANNING")) {
                BleManager.getInstance().cancelScan()
                startScanningOfBleDevice()
            }
        }

    }

    private fun startScanningOfBleDevice() {
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                Log.d(TAG, "onScanStarted: ")
                bluetoothConnectionCode.value = DEVICE_FOUND_IN_PROGRESS
                isCountDownTimerRunning = true
                countDownTimer.start()
            }

            override fun onScanning(bleDevice: BleDevice) {
                Log.d(TAG, "onScanning: ")
                ifBluetoothIsWorkingFine = true
                if (bleDevice.name != null) {
                    if (bleDevice.name.contains("Dalua")) {
                        val macAddress = bleDevice.name.split("-")
                        val realMac = macAddress[macAddress.size - 1].uppercase(Locale.ROOT)
                        if (viewModel.backendAddedDevice?.macAddress.contentEquals(realMac)) {
                            if (BleManager.getInstance().scanSate.name.contentEquals("STATE_SCANNING")) {
                                BleManager.getInstance().cancelScan()
                            }
                            viewModel.deviceToConnected = DeviceAddedPlace(bleDevice)
                        }
                    }
                }
                bluetoothConnectionCode.value = DEVICE_FOUND_IN_PROGRESS

            }

            override fun onScanFinished(scanResultList: List<BleDevice>) {
                if (viewModel.deviceToConnected == null && lifecycle.currentState.isAtLeast(
                        Lifecycle.State.STARTED
                    )
                ) {
                    bluetoothConnectionCode.value = DEVICE_NOT_FOUND
                } else {
                    bluetoothConnectionCode.value = DEVICE_FOUND_SUCCESSFUL
                    if (viewModel.deviceToConnected == null) {
                        return
                    }
                    if (viewModel.deviceToConnected!!.bleDevice != null)
                        connectToBleDevice(viewModel.deviceToConnected!!.bleDevice)
                }
            }

        })
    }

    private fun connectToBleDevice(bleDevice: BleDevice) {
        isIstTimeBleStatus = true
        BleManager.getInstance().connect(bleDevice.mac, object : BleGattCallback() {
            override fun onStartConnect() {
                Log.d(TAG, "onStartConnect: ")
                bluetoothConnectionCode.value = DEVICE_CONNECTION_IN_PROGRESS
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                Log.d(TAG, "onConnectFail: ")
                bluetoothConnectionCode.value = DEVICE_CONNECTION_FAILED
            }

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                Log.d(TAG, "onConnectSuccess: ")
                bluetoothConnectionCode.value = DEVICE_CONNECTION_SUCCESS
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                device: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                Log.d(TAG, "onDisConnected: ")
                bluetoothConnectionCode.value = DEVICE_DISCONNECTION_FAILED
            }

        })
    }

    //    fix ble connection in progress
    private fun readStatusFromDevice(bleDevice: BleDevice?) {
        if (viewModel.bleConnectivity.value!!) {
            BleManager.getInstance().read(
                bleDevice,
                "00001000-0000-1000-8000-00805f9b34fb",
                "00002000-0000-1000-8000-00805f9b34fb",
                object : BleReadCallback() {
                    override fun onReadSuccess(data: ByteArray) {
                        Log.d(TAG, "onReadSuccess: " + data.first().toInt())
                        bleStatus.value = data.first().toInt()
                    }

                    override fun onReadFailure(exception: BleException) {
                        Log.d(TAG, "onReadFailure: Code: " + exception.code)
                        Log.d(TAG, "onReadFailure: Description: " + exception.description)

                    }
                })
        }
    }

    private fun openWifiActivity() {
        resultLauncher.launch(Intent(mContext, ShowWifiConnectionActivity::class.java))
    }

    private fun restartDevice() {
        BleManager.getInstance().connect(viewModel.deviceToConnected!!.bleDevice.mac,
            object : BleGattCallback() {
                override fun onStartConnect() {
                    Log.d(TAG, "onStartConnect: restart: connection start: ")
                }

                override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                    Log.d(TAG, "onConnectFail: restart: connection fail: ")
                }

                override fun onConnectSuccess(
                    bleDevice: BleDevice?,
                    gatt: BluetoothGatt?,
                    status: Int
                ) {
                    Log.d(TAG, "onConnectSuccess: restart: connected: ")
                    val topic = JSONObject().apply {
                        put("deviceID", "1")
                        put("restart", "1")
                    }.toString().replace("\\", "")
                    Log.d(TAG, "observers: $topic")
                    BleManager.getInstance().write(
                        bleDevice,
                        "37e18c6b-0b9d-4180-ada2-fa4c901e8d7a",
                        "b3b9c3c3-e50e-435e-902b-d64d004e3850",
                        topic.toByteArray(),
                        false,
                        object : BleWriteCallback() {
                            override fun onWriteSuccess(
                                current: Int,
                                total: Int,
                                justWrite: ByteArray
                            ) {
                                Log.d(
                                    TAG,
                                    "onWriteSuccess: restartDevice: " + justWrite.first().toInt()
                                )
                                BleManager.getInstance().disconnect(bleDevice)
                                BleManager.getInstance().destroy()
                            }

                            override fun onWriteFailure(exception: BleException) {
                                Log.d(TAG, "onWriteFailure: restartDevice: " + exception.code)
                                Log.d(
                                    TAG,
                                    "onWriteFailure: restartDevice: " + exception.description
                                )
                            }
                        })
                }

                override fun onDisConnected(
                    isActiveDisConnected: Boolean,
                    device: BleDevice?,
                    gatt: BluetoothGatt?,
                    status: Int
                ) {
                    Log.d(TAG, "onDisConnected: restart: disConnected: ")
                }
            })

    }

    private fun resetDevice(bleDevice: BleDevice?) {
        val topic = JSONObject().apply {
            put("deviceID", "1")
        }.toString().replace("\\", "")
        Log.d(TAG, "observers: $topic")
        BleManager.getInstance().write(
            bleDevice,
            "37e18c6b-0b9d-4180-ada2-fa4c901e8d7a",
            "b3b9c3c3-e50e-435e-902b-d64d004e3850",
            topic.toByteArray(),
            false,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    Log.d(TAG, "onWriteSuccess: " + justWrite.first().toInt())
                }

                override fun onWriteFailure(exception: BleException) {
                    Log.d(TAG, "onWriteFailure: " + exception.code)
                    Log.d(TAG, "onWriteFailure: " + exception.description)
                }
            })
    }

    private fun initBleManager() {
        viewModel.ble.value = false
        viewModel.wifi.value = false
        viewModel.aws.value = false
        BleManager.getInstance().init(requireActivity().application)
        val scanRuleConfig = BleScanRuleConfig.Builder()
            .setScanTimeOut(scanningTime)
            .build()

        BleManager.getInstance().initScanRule(scanRuleConfig)

        val supportBle = BleManager.getInstance().isSupportBle
        if (!supportBle) {
            viewModel.description.value =
                "This Phone Don't Support Bluetooth Connection to the BLE Devices."
        }


    }

    fun cancelByForce() {

        BleManager.getInstance().cancelScan()
        if (BleManager.getInstance().scanSate.name.contentEquals("STATE_SCANNING")) {
            BleManager.getInstance().cancelScan()
            BleManager.getInstance().destroy()
//            Handler(Looper.getMainLooper()).postDelayed({ cancelByForce() }, 20)
            cancelByForce()
        }

    }

    private fun writeTopicToConnectAWS(
        bleDevice: BleDevice?,
        jsonObject: JSONObject,
    ) {

        val topic = jsonObject.toString().replace("\\", "")
        Log.d(TAG, "writeTopicToConnectAWS: $topic")
        BleManager.getInstance().write(
            bleDevice,
            "381fb6e9-964e-439d-b36c-85a481700b67",
            "9c80a619-cdb7-4caf-83ca-6e9e4bd09968",
            topic.toByteArray(),
            false,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    Log.d(TAG, "onWriteSuccess: " + justWrite.first().toInt())
                    countDownTimerForAWS.start()
                }

                override fun onWriteFailure(exception: BleException) {
                    Log.d(TAG, "onWriteFailure: " + exception.code)
                    Log.d(TAG, "onWriteFailure: " + exception.description)
                }
            })
    }

    private fun writeWifiCredential(
        bleDevice: BleDevice?,
        jsonObject: JSONObject
    ) {

        val topic = jsonObject.toString().replace("\\", "")
        Log.d(TAG, "sendDataToBleDevice: $topic")
        BleManager.getInstance().write(
            bleDevice,
            "00001000-0000-1000-8000-00805f9b34fb",
            "00002000-0000-1000-8000-00805f9b34fb",
            topic.toByteArray(),
            false,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    Log.d(TAG, "onWriteSuccess: " + justWrite.first().toInt())
                    viewModel.backendAddedDevice!!.status = 1
                    countDownTimerForWifi.start()
                }

                override fun onWriteFailure(exception: BleException) {
                    Log.d(TAG, "onWriteFailure: " + exception.code)
                    Log.d(TAG, "onWriteFailure: " + exception.description)
                }
            })
    }

    private fun showGPSDialog() {

        val gpsDialog = Dialog(mContext)
        if (gpsDialog.isShowing) gpsDialog.cancel()
        gpsDialog.setContentView(R.layout.item_turn_gps_on)
        gpsDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        gpsDialog.setCancelable(true)
//        gpsDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        gpsDialog.findViewById<Button>(R.id.button1).setOnClickListener {
            gpsDialog.dismiss()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            filter.addAction(Intent.ACTION_PROVIDER_CHANGED)
            mContext.registerReceiver(
                gpsSwitchStateReceiver,
                filter
            )
            viewModel.unregisterTheReciver = true
        }

        gpsDialog.findViewById<Button>(R.id.button).setOnClickListener {
            gpsDialog.dismiss()
            dismiss()
        }

        gpsDialog.show()

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
//        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        dialog!!.window!!.setStatusBarColor(ResourcesCompat.getColor(resources, R.color.white, null))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (viewModel.bleConnectivity.value!!)
            BleManager.getInstance().disconnect(viewModel.deviceToConnected!!.bleDevice)
        onDestroyView()
        onDestroy()
        onDetach()
        Log.d(TAG, "onDismiss: ")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    interface TroubleShootCallback {
        fun onDone(device: Device)
    }

    companion object {
        const val TAG = "TroubleshootDialog"
    }


}