package com.dalua.app.ui.taptocomplete


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.TabToConnectActivityBinding
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.interfaces.BleConnectionListener
import com.dalua.app.models.*
import com.dalua.app.ui.adddevice.AddDeviceActivity
import com.dalua.app.ui.customDialogs.connectionProgressDialog.ConnectionProgressDialog
import com.dalua.app.ui.customDialogs.connectionProgressDialog.ConnectionProgressDialogVM
import com.dalua.app.ui.updateDeviceOTA.UpdateDeviceOtaActivity
import com.dalua.app.ui.wifiactivity.ShowWifiConnectionActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.ApiTypes.CreateDeviceApi
import com.dalua.app.utils.AppConstants.ApiTypes.UpdateDeviceStatusApi
import com.dalua.app.utils.AppConstants.ApiTypes.DeleteDeviceApi
import com.dalua.app.utils.AppConstants.Companion.AWS_CONNECTED
import com.dalua.app.utils.AppConstants.Companion.AWS_CONNECTION_FAILED
import com.dalua.app.utils.AppConstants.Companion.AWS_CONNECTION_IN_PROGRESS
import com.dalua.app.utils.AppConstants.Companion.AWS_SUBSCRIBE_FAILED
import com.dalua.app.utils.AppConstants.Companion.AWS_SUBSCRIBE_SUCCESSFUL
import com.dalua.app.utils.AppConstants.Companion.COUNTDOWNTIMERTOREADSTATUS
import com.dalua.app.utils.AppConstants.Companion.DEVICE_CONNECTION_FAILED
import com.dalua.app.utils.AppConstants.Companion.DEVICE_RESET
import com.dalua.app.utils.AppConstants.Companion.STUCK_IN_WIFI
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


@AndroidEntryPoint
class TabToConnectActivity : BaseActivity(), BleConnectionListener,
    ConnectionProgressDialog.ConnectionProgressDialogCallback {

    companion object {
        const val TAG = "TabToConnectActivity"
        fun launchTabToConnect(context: Context, device: Device, singleAquarium: SingleAquarium) {
            context.startActivity(Intent(context, TabToConnectActivity::class.java).apply {
                putExtra("device", ProjectUtil.objectToString(device))
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
            })
        }
    }

    private var connectionProgressDialog: ConnectionProgressDialog? = null
    private val viewModelDialog: ConnectionProgressDialogVM by viewModels()
    lateinit var binding: TabToConnectActivityBinding
    val viewModel: TabToConnectVM by viewModels()
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    val list: MutableList<BleDevice> = mutableListOf()
    private var actionAlreadyPerformed = -1
    private var isIstTimeBleStatus = true
    private var isResetDevice = false
    private var isBluetoothBroadcastReceiverRegister = false
    private lateinit var uniqueID: String
    var bleStatus: MutableLiveData<Int> = MutableLiveData()
    private var scanningTime: Long = 9000
    private var isDestroyActivity = false

    private val gpsSwitchStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
                // Make an action or refresh an already managed state.
                val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (isGpsEnabled) {
                    checkIfBluetoothIsTurnOnOrOFF("gps", false)
                } else {
                    ProjectUtil.showToastMessage(
                        this@TabToConnectActivity,
                        false,
                        "Ble won't detect without Location on."
                    )
                }
                this@TabToConnectActivity.onBackPressed()
            }
        }
    }

    private val mBroadcastReceiver1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_OFF -> {
                        Log.d(TAG, "onReceive: STATE OFF")
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF")
                    }
                    BluetoothAdapter.STATE_ON -> {
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON")
                        updatedMessages("Bluetooth is turned on..")
                        checkIfBluetoothIsTurnOnOrOFF("ble state broadcast", false)
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON")
                    }
                }
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                initBleManager()
                checkIfBluetoothIsTurnOnOrOFF("wifi", true)
            } else if (result.resultCode == RESULT_CANCELED) {
                try {
                    Handler(Looper.getMainLooper()).postDelayed(
                        { this@TabToConnectActivity.onBackPressed() },
                        200
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    var countDownTimerWifi = object : CountDownTimer(COUNTDOWNTIMERTOREADSTATUS, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick: countDownTimerWifi: " + (millisUntilFinished / 1000))
            Log.d(TAG, "onTick: countDownTimerWifi: " + viewModelDialog.statusCode.value)
            if (viewModelDialog.statusCode.value == WIFI_CONNECTION_IN_PROGRESS)
                readStatusFromDevice(viewModel.deviceToConnected!!.bleDevice)
            else {
                Log.d(
                    TAG,
                    "onTick: waitingTimeForWifi: else: " + viewModelDialog.statusCode.value
                )
                bleStatus.value = viewModelDialog.statusCode.value
                cancel()
            }
        }

        override fun onFinish() {
            when (viewModelDialog.statusCode.value) {
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
                    bleStatus.value = STUCK_IN_WIFI
                }
            }
            Log.d(TAG, "onFinish: " + viewModelDialog.statusCode.value)
        }
    }
    var countDownTimerTopic = object : CountDownTimer(COUNTDOWNTIMERTOREADSTATUS, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick: " + (millisUntilFinished / 1000))
            Log.d(TAG, "onTick: TopicCode: " + viewModelDialog.statusCode.value)
            if (viewModelDialog.statusCode.value == AWS_CONNECTION_IN_PROGRESS)
                readStatusFromDevice(viewModel.deviceToConnected!!.bleDevice)
            else {
                Log.d(TAG, "onTick: countDownTimerTopic else: " + viewModelDialog.statusCode.value)
                bleStatus.value = viewModelDialog.statusCode.value
                cancel()
            }
        }

        override fun onFinish() {
            when (viewModelDialog.statusCode.value) {
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
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialogs()
        initObjects()
        observers()
        listeners()
        apiResponses()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("HardwareIds")
    private fun initObjects() {
        Log.d(TAG, "initObjects: " + getDeviceName(""))
        binding = DataBindingUtil.setContentView(this, R.layout.tab_to_connect_activity)
        if (intent.hasExtra("aquarium")) {
            viewModel.aquariumClicked = ProjectUtil.stringToObject(
                intent.getStringExtra("aquarium"),
                SingleAquarium::class.java
            )
        }
        myProgressDialog()

        uniqueID = 1.toString()
        if (intent.getIntExtra(AppConstants.INTENT_BLE_GRP, -1) != -1) {
            viewModel.groupId = intent.getIntExtra(AppConstants.INTENT_BLE_GRP, -1)
        }

        if (intent.getStringExtra("grp") != null) {
            viewModel.groupOfTheDevice =
                ProjectUtil.stringToObject(intent.getStringExtra("grp"), AquariumGroup::class.java)
        }
        if (intent.hasExtra("device")) {
            viewModel.backendAddedDevice =
                ProjectUtil.stringToObject(intent.getStringExtra("device"), Device::class.java)
            goToWifiActivity()
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            TedPermission.with(this)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        checkIfBluetoothIsTurnOnOrOFF("permission ted", false)
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        this@TabToConnectActivity.onBackPressed()
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
            TedPermission.with(this)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        if (!mBluetoothAdapter.isEnabled)
                            initBluetoothDialog()
                        initBleManager()
                        checkIfBluetoothIsTurnOnOrOFF("permission", false)
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        this@TabToConnectActivity.onBackPressed()
                    }
                })
                .setDeniedMessage("If you reject location permission,you can not use this service\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ).check()
        }

    }

    private fun initDialogs() {
        connectionProgressDialog = ConnectionProgressDialog()
        connectionProgressDialog!!.listener = this
    }

    private fun showDialog() {
        Log.d(TAG, "showDialog: ")
        connectionProgressDialog = ConnectionProgressDialog()
        connectionProgressDialog!!.listener = this
        connectionProgressDialog!!.show(supportFragmentManager, "Connection")
    }

    private fun dismissDialog() {
        if (connectionProgressDialog!!.isVisible)
            connectionProgressDialog!!.dismiss()
    }

    private fun apiResponses() {

        viewModel.apiResponse.observe(this) {
            when (it) {
                is Resource.Error -> {

                    when (it.api_Type) {
                        CreateDeviceApi.name -> {
                            hideWorking()
                            Log.d(TAG, "apiResponses: error: " + it.error)
                            showMessage(getString(R.string.something_went_wrong), false)
                        }
                        UpdateDeviceStatusApi.name -> {
                            hideWorking()
                            viewModelDialog.statusCode.value = -2
                            if (viewModel.groupOfTheDevice != null) {
                                var message = ""
                                if (viewModel.groupOfTheDevice!!.waterType == "Marine") {
                                    message =
                                        "Can not add ‘freshwater’ device to ‘saltwater’ group."
                                } else {
                                    message =
                                        "Can not add ‘saltwater’ device to ‘freshwater’ group."
                                }
                                showAlertDialogWithListener(
                                    this,
                                    "Can’t Add the Device",
                                    message,
                                    "Close",
                                    "",
                                    false,
                                    object : AlertDialogInterface {
                                        override fun onPositiveClickListener(dialog: Dialog?) {
                                            viewModel.deleteDeviceApi(viewModel.backendAddedDevice!!.id.toString())
                                            dialog?.dismiss()
                                        }

                                        override fun onNegativeClickListener(dialog: Dialog?) {
                                            dialog?.dismiss()
                                        }
                                    }
                                )
                            }
                        }
                        DeleteDeviceApi.name -> {
                            hideWorking()
                            showMessage(it.error, true)
                        }
                    }

                }
                is Resource.Loading -> {
                    showWorking()
                }
                is Resource.Success -> {
                    when (it.api_Type) {
                        UpdateDeviceStatusApi.name -> {
                            hideWorking()
                            it.data?.let { res ->

                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    CreateDeviceResponse::class.java
                                )
                                viewModel.backendAddedDevice = createDeviceResponse.data
                                Log.d(
                                    TAG,
                                    "apiResponses: updateDevice: " + ProjectUtil.objectToString(
                                        createDeviceResponse
                                    )
                                )
                            }
                            showSuccessDialog()
                        }
                        DeleteDeviceApi.name -> {
                            it.data?.let { res ->
                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    CreateDeviceResponse::class.java
                                )
                                showMessage(
                                    createDeviceResponse.message,
                                    createDeviceResponse.success
                                )
                                Handler(Looper.getMainLooper()).postDelayed(
                                    { this@TabToConnectActivity.onBackPressed() },
                                    500,
                                )
                            }
                        }
                    }
                }
            }
        }

        viewModel.finishActivityData.observe(this) {
            this@TabToConnectActivity.onBackPressed()
        }

    }

    private fun observers() {

        bleStatus.observe(this) {

            Log.d(TAG, "observers: outer: $it")
            if (it != actionAlreadyPerformed) {
                Log.d(TAG, "observers: inner: $it")
                actionAlreadyPerformed = it
                when (it) {

                    0 -> {
                        goToWifiActivity()
                    }

                    STUCK_IN_WIFI -> {
                        viewModelDialog.statusCode.value = STUCK_IN_WIFI
                    }

                    WiFi_SSID_NOT_AVAILABLE -> {
                        viewModelDialog.statusCode.value = WiFi_SSID_NOT_AVAILABLE
                    }

                    WiFi_WRONG_CREDENTIALS -> {
                        viewModelDialog.statusCode.value = WiFi_WRONG_CREDENTIALS
                    }

                    WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                        viewModelDialog.statusCode.value = WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS
                    }

                    WIFI_CONNECTION_IN_PROGRESS -> {
                        viewModelDialog.statusCode.value = WIFI_CONNECTION_IN_PROGRESS
                    }

                    WiFi_CONNECTED -> {
                        wifiConnected()
                    }

                    AWS_CONNECTION_IN_PROGRESS -> {
                        viewModelDialog.statusCode.value = AWS_CONNECTION_IN_PROGRESS
                    }

                    AWS_CONNECTION_FAILED -> {
                        viewModelDialog.statusCode.value = AWS_CONNECTION_FAILED
                    }

                    AWS_SUBSCRIBE_FAILED -> {
                        viewModelDialog.statusCode.value = AWS_SUBSCRIBE_FAILED
                    }

                    AWS_CONNECTED, AWS_SUBSCRIBE_SUCCESSFUL -> {
                        readDeviceIpAddress(viewModel.deviceToConnected!!.bleDevice)
                        awsSubscribeSuccessful()
                    }
                }
            }
        }

        viewModel.disconnectBluetooth.observe(this) {
            if (it) {
                BleManager.getInstance().disconnect(viewModel.deviceToConnected!!.bleDevice)
            }
        }

    }

    private fun wifiConnected() {
        viewModelDialog.statusCode.value = AWS_CONNECTION_IN_PROGRESS
        val topic = viewModel.backendAddedDevice.toString().replace("\\", "")
        Log.d(TAG, "observers: topic: $topic")
        writeTopicToAWS(
            viewModel.deviceToConnected!!.bleDevice,
            JSONObject().apply {
                put("deviceID", uniqueID)
                if (viewModel.groupOfTheDevice == null)
                    put("topic", viewModel.backendAddedDevice!!.topic)
                else
                    put("topic", viewModel.groupOfTheDevice!!.topic)

            }
        )
    }

    private fun awsSubscribeSuccessful() {
        viewModelDialog.statusCode.value = -2
    }

    private fun showSuccessDialog() {
        viewModelDialog.statusCode.value = AWS_SUBSCRIBE_SUCCESSFUL
        viewModelDialog.isAllDone.value = true
        AppConstants.apply {
            refresh_aquarium.value = true
            refresh_group_device.value = true
            refresh_group.value = true
            device_added_successfully = true
        }
        viewModelDialog.statusCode.removeObservers(this)
        bleStatus.removeObservers(this)
    }

    private fun updatedMessages(message: String) {
        Log.d(TAG, "updatedMessages: $message")
//        showDialog()
    }

    private fun listeners() {

    }

    private fun goToWifiActivity() {
        val intent = Intent(this, ShowWifiConnectionActivity::class.java).apply {
            putExtra("code", 0)
        }
        resultLauncher.launch(intent)
    }

    private fun initBleManager() {

        BleManager.getInstance().init(application)
        val scanRuleConfig = BleScanRuleConfig.Builder()
            .setScanTimeOut(scanningTime)
//            .setAutoConnect(true)
            .build()

        BleManager.getInstance().initScanRule(scanRuleConfig)

        val supportBle = BleManager.getInstance().isSupportBle
        if (!supportBle) {
            ProjectUtil.showToastMessage(
                this,
                false,
                "This Phone Don't Support Bluetooth Connection to the BLE Devices."
            )
            this@TabToConnectActivity.onBackPressed()
        }

    }

    private fun resetDevice(bleDevice: BleDevice) {
        val topic = JSONObject().apply {
            put("deviceID", "1")
        }.toString().replace("\\", "")
        Log.d(TAG, "resetDevice: $topic")
        BleManager.getInstance().write(
            bleDevice,
            "37e18c6b-0b9d-4180-ada2-fa4c901e8d7a",
            "b3b9c3c3-e50e-435e-902b-d64d004e3850",
            topic.toByteArray(),
            false,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    Log.d(TAG, "onWriteSuccess: reset: " + justWrite.first().toInt())
                }

                override fun onWriteFailure(exception: BleException) {
                    Log.d(TAG, "onWriteFailure: reset: " + exception.code)
                    Log.d(TAG, "onWriteFailure: reset: " + exception.description)
                }
            })
    }

    private fun initBluetoothDialog() {
        val dialog = Dialog(this)
        if (dialog.isShowing) dialog.cancel()
        dialog.apply {
            setContentView(R.layout.item_turn_bluetooth_on)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
//            window?.setWindowAnimations(R.style.DialogAnimation)
            findViewById<Button>(R.id.cancel).setOnClickListener {
                dismiss()
                this@TabToConnectActivity.onBackPressed()
            }
            findViewById<Button>(R.id.turnOn).setOnClickListener {
                registerReceiver(
                    mBroadcastReceiver1,
                    IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                )
                mBluetoothAdapter.enable()
                dismiss()
            }
            show()
        }
    }

    @SuppressLint("HardwareIds")
    private fun checkIfBluetoothIsTurnOnOrOFF(tag: String, scan: Boolean) {
        Log.d(TAG, "checkIfBluetoothIsTurnOnOrOFF: $tag")
        if (!mBluetoothAdapter.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
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
            isBluetoothBroadcastReceiverRegister = true
            registerReceiver(
                mBroadcastReceiver1,
                IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            )

        } else {
            val manager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDialog()
                return
            }
            if (scan) {
                statusCheck()
            }
        }
    }

    private fun statusCheck() {
        if (BleManager.getInstance().scanSate.name.contentEquals("STATE_IDLE")) {
            Log.d(TAG, "statusCheck: STATE_IDLE: ")
            startScanningOfBleDevice()
        } else {
            if (BleManager.getInstance().scanSate.name.contentEquals("STATE_SCANNING")) {
                Log.d(TAG, "statusCheck: STATE_SCANNING: ")
                BleManager.getInstance().cancelScan()
//                startScanningOfBleDevice()
            } else {
                Log.d(TAG, "statusCheck: STATE_SCANNING else: ")
            }
        }

    }

    private fun startScanningOfBleDevice() {
        BleManager.getInstance().disconnectAllDevice()
        Log.d(
            TAG,
            "startScanningOfBleDevice: connected: " + BleManager.getInstance()
                .isConnected(viewModel.backendAddedDevice!!.macAddress)
        )
        scanningBleDevice(
            viewModel.backendAddedDevice!!.macAddress,
            object : DeviceAddedPlaceCallback {
                override fun onScanStarted(success: Boolean) {
                    Log.d(TAG, "onScanStarted: $success")
                    showWorking()
                }

                override fun onScanning(deviceAddedPlace: DeviceAddedPlace?) {
                    Log.d(TAG, "onScanning: ")
                    hideWorking()
                    if (deviceAddedPlace != null) {
                        Log.d(TAG, "onScanning: connecting...")
                        showDialog()
                        connectToBleDevice(deviceAddedPlace.bleDevice)
                        viewModel.deviceToConnected = deviceAddedPlace
                    } else {
                        Log.d(TAG, "onScanning: null")
                        showAlertDialogWithListener(this@TabToConnectActivity,
                            "Device not found",
                            "Make sure your dalua device is in range",
                            "ok",
                            "",
                            false,
                            object : AlertDialogInterface {
                                override fun onPositiveClickListener(dialog: Dialog?) {
                                    dialog!!.dismiss()
                                    this@TabToConnectActivity.onBackPressed()
                                }

                                override fun onNegativeClickListener(dialog: Dialog?) {
                                    dialog!!.dismiss()
                                }
                            })
                    }
                }

                override fun onScanFinish(isSuccess: Boolean) {
                    Log.d(TAG, "onScanFinish: $isSuccess")
                    hideWorking()
                    if (!isSuccess) {
                        showAlertDialogWithListener(this@TabToConnectActivity,
                            "Device not found",
                            "Make sure your dalua device is in range",
                            "ok",
                            "",
                            false,
                            object : AlertDialogInterface {
                                override fun onPositiveClickListener(dialog: Dialog?) {
                                    dialog!!.dismiss()
                                    this@TabToConnectActivity.onBackPressed()
                                }

                                override fun onNegativeClickListener(dialog: Dialog?) {
                                    dialog!!.dismiss()
                                }
                            })
                    }
                }
            })
    }

    private fun cancelByForce() {

        BleManager.getInstance().cancelScan()
        if (BleManager.getInstance().scanSate.name.contentEquals("STATE_SCANNING")) {
            BleManager.getInstance().cancelScan()
            Log.d(
                TAG,
                "scan cancel is called again and again...!"
            )
            BleManager.getInstance().destroy()
//            Handler(Looper.getMainLooper()).postDelayed({ cancelByForce() }, 20)
            cancelByForce()
        }

    }

    private fun readStatusFromDevice(bleDevice: BleDevice?) {
        if (viewModel.isBluetoothConnected.value!!) {
            BleManager.getInstance().read(
                bleDevice,
                "00001000-0000-1000-8000-00805f9b34fb",
                "00002000-0000-1000-8000-00805f9b34fb",
                object : BleReadCallback() {
                    override fun onReadSuccess(data: ByteArray) {
                        Log.d(TAG, "onReadSuccess: Code: " + data.first().toInt())
                        bleStatus.value = data.first().toInt()
                    }

                    override fun onReadFailure(exception: BleException) {
                        Log.d(TAG, "onReadFailure: readStatusFromDevice: " + exception.code)
                        Log.d(TAG, "onReadFailure: readStatusFromDevice: " + exception.description)
                    }
                })
        }
    }

    private fun readDeviceIpAddress(bleDevice: BleDevice?) {
        if (viewModel.isBluetoothConnected.value!!) {
            BleManager.getInstance().read(
                bleDevice,
                "381fb6e9-964e-439d-b36c-85a481700b67",
                "9c80a619-cdb7-4caf-83ca-6e9e4bd09968",
                object : BleReadCallback() {
                    override fun onReadSuccess(data: ByteArray) {
                        Log.d(TAG, "onReadSuccess: ip: " + data.decodeToString())
                        viewModel.backendAddedDevice!!.ipAddress = data.decodeToString()
                    }

                    override fun onReadFailure(exception: BleException) {
                        Log.d(TAG, "onReadFailure: " + exception.code)
                        Log.d(TAG, "onReadFailure: " + exception.description)
                    }
                })
        }

    }

    private fun writeWifiCredentialToBleDevice(
        bleDevice: BleDevice?,
        SSID: String,
        password: String
    ) {
        val jsonObject = JSONObject().apply {
            put("deviceID", uniqueID)
            put("ssid", SSID)
            put("pass", password)
        }
        val topic = jsonObject.toString().replace("\\", "")
        Log.d(TAG, "sendDataToBleDevice: $topic")
        Log.d(TAG, "sendDataToBleDevice: ${jsonObject.toString(2)}")
        BleManager.getInstance().write(
            bleDevice,
            "00001000-0000-1000-8000-00805f9b34fb",
            "00002000-0000-1000-8000-00805f9b34fb",
            topic.toByteArray(),
            false,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    Log.d(TAG, "onWriteSuccess: " + justWrite.first().toInt())
                    countDownTimerWifi.start()
                }

                override fun onWriteFailure(exception: BleException) {
                    Log.d(TAG, "onWriteFailure: ${exception.description}")
                }
            })
    }

    private fun writeTopicToAWS(
        bleDevice: BleDevice?,
        jsonObject: JSONObject
    ) {
        val topic = jsonObject.toString().replace("\\", "")
        Log.d(TAG, "sendDataToBleDevice: $topic")
        Log.d(TAG, "sendDataToBleDevice: ${jsonObject.toString(2)}")
        BleManager.getInstance().write(
            bleDevice,
            "381fb6e9-964e-439d-b36c-85a481700b67",
            "9c80a619-cdb7-4caf-83ca-6e9e4bd09968",
            topic.toByteArray(),
            false,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    Log.d(TAG, "onWriteSuccess: " + justWrite.first().toInt())
                    countDownTimerTopic.start()
                }

                override fun onWriteFailure(exception: BleException) {
                    Log.d(TAG, "onWriteFailure: ${exception.description}")
                }
            })

    }

    private fun showGPSDialog() {
        Log.d(TAG, "dialog: showGPSDialog: ")

        val gpsDialog = Dialog(this)
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
            registerReceiver(
                gpsSwitchStateReceiver,
                filter
            )
            viewModel.unregisterTheReciver = true
        }

        gpsDialog.findViewById<Button>(R.id.button).setOnClickListener {
            gpsDialog.dismiss()
        }

        gpsDialog.show()

    }

    private fun connectToBleDevice(bleDevice: BleDevice) {
        Log.d(TAG, "connectToBleDevice: ")
        connectToBleDevice(bleDevice, this)
    }

    override fun onStartConnect() {
        Log.d(TAG, "onStartConnect: ")
        viewModelDialog.statusCode.value = AppConstants.DEVICE_CONNECTION_IN_PROGRESS
    }

    override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
        viewModelDialog.statusCode.value = DEVICE_CONNECTION_FAILED
        Log.d(TAG, "onConnectFail: ")
    }

    override fun onConnectSuccess(bleDevice: BleDevice?, gatt: BluetoothGatt?, status: Int) {
        Log.d(TAG, "onConnectSuccess: ")
        viewModel.isBluetoothConnected.value = true
        viewModelDialog.statusCode.value = WIFI_CONNECTION_IN_PROGRESS
        writeWifiCredentialToBleDevice(
            viewModel.deviceToConnected!!.bleDevice,
            AppConstants.SSID,
            AppConstants.PASSWORD
        )
    }

    override fun onDisConnected(
        isActiveDisConnected: Boolean,
        device: BleDevice?,
        gatt: BluetoothGatt?,
        status: Int
    ) {
        Log.d(TAG, "onDisConnected: ")
        if (!isDestroyActivity) {
            if (!viewModelDialog.isAllDone.value!!) {
                viewModelDialog.statusCode.value = DEVICE_CONNECTION_FAILED
                viewModel.isBluetoothConnected.value = false
                Log.d(TAG, "onDisConnected: call inner: ")
                if (isResetDevice) {
                    Log.d(TAG, "onDisConnected: call inner: ")
                    connectToBleDevice(device!!)
                }
                if (bleStatus.value in 1..9) {
                    isIstTimeBleStatus = true
                    updatedMessages("Device adding unsuccessful Try again.")
                }
            }
        }
    }

    override fun onChangeStatus(code: Int?) {
        Log.d(TAG, "onChangeStatus: $code")
        when (code) {
            DEVICE_RESET -> {
                connectToBleDevice(viewModel.deviceToConnected!!.bleDevice)
            }
            DEVICE_CONNECTION_FAILED -> {
                connectToBleDevice(viewModel.deviceToConnected!!.bleDevice)
            }
            STUCK_IN_WIFI -> {
                goToWifiActivity()
                restartDevice()
            }
            WiFi_SSID_NOT_AVAILABLE -> {
                BleManager.getInstance().disconnect(viewModel.deviceToConnected!!.bleDevice)
                goToWifiActivity()
            }
            WiFi_WRONG_CREDENTIALS -> {
                BleManager.getInstance().disconnect(viewModel.deviceToConnected!!.bleDevice)
                goToWifiActivity()
            }
            WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                BleManager.getInstance().disconnect(viewModel.deviceToConnected!!.bleDevice)
                goToWifiActivity()
            }
            AWS_CONNECTION_FAILED -> {
                viewModelDialog.statusCode.value = AWS_CONNECTION_IN_PROGRESS
                writeTopicToAWS(
                    viewModel.deviceToConnected!!.bleDevice,
                    JSONObject().apply {
                        put("deviceID", uniqueID)
                        if (viewModel.groupOfTheDevice == null)
                            put("topic", viewModel.backendAddedDevice!!.topic)
                        else
                            put("topic", viewModel.groupOfTheDevice!!.topic)

                    }
                )
            }
            AWS_SUBSCRIBE_FAILED -> {
                viewModelDialog.statusCode.value = AWS_CONNECTION_IN_PROGRESS
                writeTopicToAWS(
                    viewModel.deviceToConnected!!.bleDevice,
                    JSONObject().apply {
                        put("deviceID", uniqueID)
                        if (viewModel.groupOfTheDevice == null)
                            put("topic", viewModel.backendAddedDevice!!.topic)
                        else
                            put("topic", viewModel.groupOfTheDevice!!.topic)

                    }
                )
            }
        }
    }

    private fun restartDevice() {
        val topic = JSONObject().apply {
            put("deviceID", "1")
            put("restart", "1")
        }.toString().replace("\\", "")
        Log.d(TAG, "observers: $topic")
        BleManager.getInstance().write(
            viewModel.deviceToConnected!!.bleDevice,
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
                    Log.d(TAG, "onWriteSuccess: restartDevice: " + justWrite.first().toInt())
                }

                override fun onWriteFailure(exception: BleException) {
                    Log.d(TAG, "onWriteFailure: restartDevice: " + exception.code)
                    Log.d(TAG, "onWriteFailure: restartDevice: " + exception.description)
                }
            })
//        BleManager.getInstance().disconnect(viewModel.deviceToConnected!!.bleDevice)
//        cancelByForce()
    }

    override fun close(refresh: Boolean) {
        if (refresh) {
            AppConstants.apply {
                refresh_aquarium
                refresh_device
                refresh_group
                refresh_group_device
            }
            Log.d(TAG, "close: version: " + viewModel.backendAddedDevice!!.version)
            if (viewModel.backendAddedDevice!!.ipAddress.isNotEmpty()) {
                if (viewModel.backendAddedDevice!!.version == null) {
                    startActivity(
                        Intent(
                            this@TabToConnectActivity,
                            UpdateDeviceOtaActivity::class.java
                        ).apply {
                            putExtra(
                                "device",
                                ProjectUtil.objectToString(viewModel.backendAddedDevice)
                            )
                            putExtra(
                                "wifi",
                                AppConstants.SSID
                            )
                        })
                }
            } else {
                showAlertDialogWithListener(
                    this,
                    "IP Address",
                    "IP Address not found.",
                    "Ok",
                    "",
                    false,
                    object : AlertDialogInterface {
                        override fun onPositiveClickListener(dialog: Dialog?) {
                            dialog?.dismiss()
                            dismissDialog()
                            this@TabToConnectActivity.onBackPressed()
                        }

                        override fun onNegativeClickListener(dialog: Dialog?) {
                            dialog?.dismiss()
                        }
                    }
                )
            }
        }
        dismissDialog()
        this@TabToConnectActivity.onBackPressed()
    }

    override fun onSelectDeviceType(waterType: String) {
        viewModel.updateDeviceStatusApi(
            viewModel.backendAddedDevice!!,
            completed = 1,
            wifi = AppConstants.SSID,
            water_type = waterType
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        Log.d(
            TAG,
            "onDestroy: isConnected: " + BleManager.getInstance()
                .isConnected(viewModel.backendAddedDevice!!.macAddress)
        )
        isDestroyActivity = true
        bleStatus.removeObservers(this)
        viewModelDialog.statusCode.removeObservers(this)
        if (viewModel.unregisterTheReciver) {
            unregisterReceiver(gpsSwitchStateReceiver)
        }
        if (viewModel.isBluetoothConnected.value!!) {
            BleManager.getInstance().disconnect(viewModel.deviceToConnected?.bleDevice)
        }
        BleManager.getInstance().disconnectAllDevice()
        if (BleManager.getInstance().scanSate.name.contentEquals("STATE_SCANNING")) {
            cancelByForce()
            BleManager.getInstance().destroy()
        }
        if (isBluetoothBroadcastReceiverRegister) {
            unregisterReceiver(mBroadcastReceiver1)
        }
        if (BleManager.getInstance().scanSate.name.contentEquals("STATE_IDLE")) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}
