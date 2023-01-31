package com.dalua.app.ui.adddevice


import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
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
import com.dalua.app.databinding.AddDeviceActivityBinding
import com.dalua.app.databinding.ItemCreateDeviceBinding
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.interfaces.BleConnectionListener
import com.dalua.app.models.*
import com.dalua.app.ui.customDialogs.connectionProgressDialog.ConnectionProgressDialog
import com.dalua.app.ui.customDialogs.connectionProgressDialog.ConnectionProgressDialogVM
import com.dalua.app.ui.taptocomplete.TabToConnectActivity
import com.dalua.app.ui.updateDeviceOTA.UpdateDeviceOtaActivity
import com.dalua.app.ui.wifiactivity.ShowWifiConnectionActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.ApiTypes.*
import com.dalua.app.utils.AppConstants.Companion.AWS_CONNECTED
import com.dalua.app.utils.AppConstants.Companion.AWS_CONNECTION_FAILED
import com.dalua.app.utils.AppConstants.Companion.AWS_CONNECTION_IN_PROGRESS
import com.dalua.app.utils.AppConstants.Companion.AWS_SUBSCRIBE_FAILED
import com.dalua.app.utils.AppConstants.Companion.AWS_SUBSCRIBE_SUCCESSFUL
import com.dalua.app.utils.AppConstants.Companion.COUNTDOWNTIMERTOREADSTATUS
import com.dalua.app.utils.AppConstants.Companion.DEVICE_CONNECTION_FAILED
import com.dalua.app.utils.AppConstants.Companion.DEVICE_CONNECTION_IN_PROGRESS
import com.dalua.app.utils.AppConstants.Companion.DEVICE_CONNECTION_SUCCESS
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
import java.util.*
import java.util.stream.Collectors
import kotlin.math.abs


@AndroidEntryPoint
class AddDeviceActivity : BaseActivity(), BleConnectionListener,
    ConnectionProgressDialog.ConnectionProgressDialogCallback {

    companion object {
        const val TAG = "AddDeviceActivity"

        fun launchToAddDevice(context: Context, singleAquarium: SingleAquarium) {
            context.startActivity(
                Intent(context, AddDeviceActivity::class.java).apply {
                    putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
                }
            )
        }

        fun launchToAddDeviceFromGroup(
            context: Context,
            aquariumGroup: AquariumGroup,
            singleAquarium: SingleAquarium
        ) {
            context.startActivity(
                Intent(context, AddDeviceActivity::class.java).apply {
                    putExtra(AppConstants.INTENT_BLE_GRP, aquariumGroup.id)
                    putExtra("grp", ProjectUtil.objectToString(aquariumGroup))
                    putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
                }
            )
        }
    }

    private var connectionProgressDialog: ConnectionProgressDialog? = null
    private val viewModelDialog: ConnectionProgressDialogVM by viewModels()
    lateinit var binding: AddDeviceActivityBinding
    val viewModel: AddDeviceVM by viewModels()
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    var widthOfRadar: Int = 0
    var itemViewWidth = 80
    var macBle: MutableList<MacBle> = mutableListOf()
    var xCenterPositionOfRadar: Int = 0
    val list: MutableList<BleDevice> = mutableListOf()
    private var availablePosition: MutableList<Int> = arrayListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    var xStartPositionOfRadar: Int = 0
    var xEndPositionOfRadar: Int = 0
    var yStartPositionOfRadar: Int = 0
    var yEndPositionOfRadar: Int = 0
    var yCenterPositionOfRadar: Int = 0
    var heightOfRadar: Int = 0
    private var actionAlreadyPerformed = -1
    private var isBluetoothBroadcastReceiverRegister = false
    private var alreadyFullRoundOne = false
    private var alreadyFullRoundTwo = false
    private lateinit var uniqueID: String
    var bleStatus: MutableLiveData<Int> = MutableLiveData()
    var devicesInflated: MutableList<DeviceAddedPlace> = mutableListOf()
    private var scanningTime: Long = 9000

    private val gpsSwitchStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
                // Make an action or refresh an already managed state.
                val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (isGpsEnabled) {
                    checkIfBluetoothIsTurnOnOrOFF()
                } else {
                    ProjectUtil.showToastMessage(
                        this@AddDeviceActivity,
                        false,
                        "Ble won't detect without Location on."
                    )
                }
                onBackPressed()
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
                        binding.content.stopRippleAnimation()
                        binding.linearFoundDevice.visibility = View.GONE
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF")
                    }
                    BluetoothAdapter.STATE_ON -> {
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON")
                        checkIfBluetoothIsTurnOnOrOFF()
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
                Log.d(
                    TAG,
                    "registerForActivityResult: " + BleManager.getInstance()
                        .isConnected(viewModel.deviceToConnected!!.bleDevice)
                )
                writeWifiCredential()
            } else if (result.resultCode == RESULT_CANCELED) {
                viewModelDialog.statusCode.value = 1
            }
        }

    var countDownTimerWifi = object : CountDownTimer(COUNTDOWNTIMERTOREADSTATUS, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick: " + (millisUntilFinished / 1000))
            Log.d(TAG, "onTick: " + viewModelDialog.statusCode.value)
            if (viewModelDialog.statusCode.value == WIFI_CONNECTION_IN_PROGRESS)
                readStatusFromDevice(viewModel.deviceToConnected!!.bleDevice)
            else {
                Log.d(TAG, "onTick: waitingTimeForWifi: " + viewModelDialog.statusCode.value)
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
            Log.d(TAG, "onTick: countDownTimerTopic: " + (millisUntilFinished / 1000))
            Log.d(TAG, "onTick: countDownTimerTopic: " + viewModelDialog.statusCode.value)
            if (viewModelDialog.statusCode.value == AWS_CONNECTION_IN_PROGRESS)
                readStatusFromDevice(viewModel.deviceToConnected!!.bleDevice)
            else {
                Log.d(TAG, "onTick: waitingTimeForAWS else: " + viewModelDialog.statusCode.value)
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
        binding = DataBindingUtil.setContentView(this, R.layout.add_device_activity)
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

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            TedPermission.with(this)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        if (!mBluetoothAdapter.isEnabled)
                            initBluetoothDialog()
                        initBleManager()
                        checkIfBluetoothIsTurnOnOrOFF()
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        onBackPressed()
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
                        checkIfBluetoothIsTurnOnOrOFF()
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        onBackPressed()
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
                                            viewModel.deleteDeviceApi(viewModel.backEndAddedDevice!!.id.toString())
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
                                viewModel.backEndAddedDevice = createDeviceResponse.data
                                Log.d(
                                    TAG,
                                    "apiResponses: updateDevice: " + ProjectUtil.objectToString(
                                        createDeviceResponse
                                    )
                                )
                            }
                            showSuccessDialog()
                        }
                        CheckMacAddressApi.name -> {
                            hideWorking()
                            it.data?.let { res ->

                                val checkMacAddressResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    CheckMacAddressResponse::class.java
                                )

                                for (i in 0 until checkMacAddressResponse.data.size) {
                                    if (!checkMacAddressResponse.data[i] && !macBle[i].alreadyAdded) {
                                        val x = abs(macBle[i].bleDevice.rssi)
                                        if (x <= 63)
                                            addDeviceToTheRadar(macBle[i].bleDevice, true)
                                        else
                                            addDeviceToTheRadar(macBle[i].bleDevice, false)
                                        macBle[i].alreadyAdded = true
                                    }
                                }


                            }
                        }
                        CreateDeviceApi.name -> {
                            Log.d(TAG, "apiResponses: response arrived: ")
                            hideWorking()
                            showDialog()
                            viewModelDialog.statusCode.value =
                                AppConstants.DEVICE_CONNECTION_IN_PROGRESS
                            Handler(Looper.getMainLooper()).postDelayed({
                                Log.d(TAG, "apiResponses: start: ")
                                it.data?.let { res ->
                                    val createDeviceResponse = ProjectUtil.stringToObject(
                                        res.string(),
                                        CreateDeviceResponse::class.java
                                    )
                                    viewModel.backEndAddedDevice = createDeviceResponse.data
                                    AppConstants.apply {
                                        refresh_aquarium.value = true
                                        refresh_group_device.value = true
                                        refresh_group.value = true
                                        device_added_successfully = true
                                    }
                                }
                                connectAfterCreateDevice()
                            }, 10000)

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
                                    { onBackPressed() },
                                    500,
                                )
                            }
                        }

                    }

                }
            }
        }

        viewModel.deviceClicked.observe(this) {
            for (inflatedDevice in viewModel.inflatedDevices) {
                if (inflatedDevice.position == it) {
                    viewModel.deviceToConnected = inflatedDevice
                }
            }
            cancelByForce()
            createDeviceDialog(getDeviceName(viewModel.deviceToConnected!!.bleDevice.name!!))
        }

        viewModel.finishActivityData.observe(this) {
            onBackPressed()
        }

    }

    private fun observers() {

        retryCode.observe(this) {
            Log.d(TAG, "observers: $it")
        }

        bleStatus.observe(this) {

            if (it != actionAlreadyPerformed) {
                actionAlreadyPerformed = it
                Log.d(TAG, "observers: bleStatus: $it")
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

                    WIFI_CONNECTION_IN_PROGRESS -> {
                        viewModelDialog.statusCode.value = WIFI_CONNECTION_IN_PROGRESS
                    }

                    WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                        viewModelDialog.statusCode.value = WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS
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
            viewModel.unregisterTheReceiver = true
        }

        gpsDialog.findViewById<Button>(R.id.button).setOnClickListener {
            gpsDialog.dismiss()
        }

        gpsDialog.show()

    }

    private fun wifiConnected() {
        viewModelDialog.statusCode.value = AWS_CONNECTION_IN_PROGRESS
        val topic = viewModel.backEndAddedDevice.toString().replace("\\", "")
        Log.d(TAG, "observers: topic: $topic")
        writeTopicToAWS(
            viewModel.deviceToConnected!!.bleDevice,
            JSONObject().apply {
                put("deviceID", uniqueID)
                if (viewModel.groupOfTheDevice == null)
                    put("topic", viewModel.backEndAddedDevice!!.topic)
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
            refresh_device.value = true
            device_added_successfully = true
        }
        viewModelDialog.statusCode.removeObservers(this)
        bleStatus.removeObservers(this)
    }

    private fun listeners() {

        binding.content.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    binding.content.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    widthOfRadar = binding.content.width //width and height
                    heightOfRadar = binding.content.height //width and height
                    val rect = Rect()
                    binding.content.getLocalVisibleRect(rect)
//                    binding.content.getGlobalVisibleRect(rect)

                    xCenterPositionOfRadar = rect.centerX()
                    xStartPositionOfRadar = 0
                    xEndPositionOfRadar = rect.right - itemViewWidth

                    yCenterPositionOfRadar = rect.centerY()
                    yStartPositionOfRadar = rect.centerY() - rect.centerX()
                    yEndPositionOfRadar = rect.centerY() + rect.centerX() - itemViewWidth

                    val rect1 = Rect()
                    binding.centerImage.getLocalVisibleRect(rect1)


                }
            })

        binding.refresh.setOnClickListener {
            rescanBleDevices()
        }

    }

    private fun rescanBleDevices() {
        Log.d(TAG, "rescanBleDevices: ")
        if (mBluetoothAdapter.isEnabled) {
            if (BleManager.getInstance().scanSate.name.contentEquals("STATE_IDLE")) {
                scanningTime = 9000
                initBleManager()
                checkIfBluetoothIsTurnOnOrOFF()
                availablePosition = arrayListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                alreadyFullRoundOne = false
                alreadyFullRoundTwo = false
                devicesInflated.clear()
                viewModel.inflatedDevices.clear()
                binding.commonGroup.visibility = View.GONE
            }
        } else {
            initBluetoothDialog()
        }

    }

    private fun goToWifiActivity() {
//        dismissBluetoothDialog()
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
            onBackPressed()
        }

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
                onBackPressed()
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

    private fun foundDevice(device_view: View) {

        val animatorSet = AnimatorSet()
        animatorSet.duration = 400
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        val animatorList = ArrayList<Animator>()
        val scaleXAnimator = ObjectAnimator.ofFloat(device_view, "ScaleX", 0f, 1.2f, 1f)
        animatorList.add(scaleXAnimator)
        val scaleYAnimator = ObjectAnimator.ofFloat(device_view, "ScaleY", 0f, 1.2f, 1f)
        animatorList.add(scaleYAnimator)
        animatorSet.playTogether(animatorList)
        device_view.visibility = View.VISIBLE
        animatorSet.start()

    }

    @SuppressLint("HardwareIds")
    private fun checkIfBluetoothIsTurnOnOrOFF() {

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
            statusCheck()
        }

    }

    private fun statusCheck() {

        if (BleManager.getInstance().scanSate.name.contentEquals("STATE_IDLE")) {
            startScanningDevices()
        } else {
            if (BleManager.getInstance().scanSate.name.contentEquals("STATE_SCANNING")) {
                BleManager.getInstance().cancelScan()
                startScanningDevices()
            }
        }

    }

    private fun startScanningDevices() {
        BleManager.getInstance().disconnectAllDevice()
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                Log.d(TAG, "onScanStarted: startScanningOfBleDevice: $success")
                binding.refresh.visibility = View.GONE
                binding.content.startRippleAnimation()
                binding.textView2.text = getString(R.string.discovering_dalua_ecosystem)
                binding.textView1.text = resources.getString(R.string.we_are_looking_for)
                viewModel.inflatedDevices.clear()
                macBle = mutableListOf()
            }

            override fun onScanning(bleDevice: BleDevice) {
                Log.d(TAG, "onScanning: " + bleDevice.name)
                if (bleDevice.name != null) {
                    if (bleDevice.name.contains("Dalua")) {
                        val macAddress = bleDevice.name.split("-")
                        val realMac = macAddress[macAddress.size - 1].uppercase(Locale.ROOT)
                        macBle.add(MacBle(bleDevice, realMac, false))
                        callApiAfter3Sec()
                    }
                }
            }

            override fun onScanFinished(scanResultList: List<BleDevice>) {
                Log.d(TAG, "onScanFinished: ${list.size}")
                binding.refresh.visibility = View.VISIBLE
                viewModel.inflatedDevices = devicesInflated
                binding.content.stopRippleAnimation()
                binding.textView2.text = getString(R.string.scan_completed)
                binding.textView1.text = resources.getString(R.string.no_device_found)

            }
        })
    }

    private fun callApiAfter3Sec() {
        Log.d(TAG, "callApiAfter3Sec: ")
        val mutableList: MutableList<String> = mutableListOf()
        for (macBle in macBle) {
            mutableList.add(macBle.macAddress)
            Log.d(TAG, "callApiAfter3Sec: " + mutableList.size)
        }
        if (mutableList.isEmpty())
            return
        viewModel.checkMacAddress(mutableList)
    }

    private fun addDeviceToTheRadar(bleDevice: BleDevice, is_ring_one: Boolean) {
        Log.d(TAG, "addDeviceToTheRadar: First Tab")
        //if round one and its not full else go to the next ring
        if (availablePosition.isNotEmpty()) {

            if (is_ring_one && !alreadyFullRoundOne || alreadyFullRoundTwo) {
                for (i in availablePosition) {
                    if (i < 4) {

                        when (i) {
                            0 -> {
                                val d = DeviceAddedPlace(
                                    i,
                                    bleDevice,
                                    binding.linearFoundDevice7
                                )
                                binding.textFoundDevice7.text = getDeviceName(d.bleDevice.name)
                                Log.d(TAG, "addDeviceToTheRadar: 0: " + d.bleDevice.name)
                                devicesInflated.add(d)
                                viewModel.inflatedDevices.add(d)
                                foundDevice(binding.linearFoundDevice7)
                                availablePosition.remove(i)
                                val result = availablePosition.stream().filter { s: Int -> s < 4 }
                                    .collect(Collectors.toList()).isEmpty()
                                alreadyFullRoundOne = result

                                return
                            }
                            1 -> {
                                val d = DeviceAddedPlace(
                                    i,
                                    bleDevice,
                                    binding.linearFoundDevice8
                                )
                                binding.textFoundDevice8.text = getDeviceName(d.bleDevice.name)
                                Log.d(TAG, "addDeviceToTheRadar: 1: " + d.bleDevice.name)
                                devicesInflated.add(d)
                                viewModel.inflatedDevices.add(d)
                                availablePosition.remove(i)
                                val result = availablePosition.stream().filter { s: Int -> s < 4 }
                                    .collect(Collectors.toList()).isEmpty()
                                alreadyFullRoundOne = result
                                foundDevice(binding.linearFoundDevice8)
                                return
                            }
                            2 -> {
                                val d = DeviceAddedPlace(
                                    i,
                                    bleDevice,
                                    binding.linearFoundDevice9
                                )

                                binding.textFoundDevice9.text = getDeviceName(d.bleDevice.name)
                                Log.d(TAG, "addDeviceToTheRadar: 2: " + d.bleDevice.name)
                                devicesInflated.add(d)
                                viewModel.inflatedDevices.add(d)
                                availablePosition.remove(i)
                                val result = availablePosition.stream().filter { s: Int -> s < 4 }
                                    .collect(Collectors.toList()).isEmpty()
                                alreadyFullRoundOne = result
                                foundDevice(binding.linearFoundDevice9)
                                return
                            }
                            3 -> {
                                val d = DeviceAddedPlace(
                                    i,
                                    bleDevice,
                                    binding.linearFoundDevice10
                                )

                                binding.textFoundDevice10.text = getDeviceName(d.bleDevice.name)
                                Log.d(TAG, "addDeviceToTheRadar: 3: " + d.bleDevice.name)
                                devicesInflated.add(d)
                                viewModel.inflatedDevices.add(d)
                                availablePosition.remove(i)

                                val result = availablePosition.stream().filter { s: Int -> s < 4 }
                                    .collect(Collectors.toList()).isEmpty()
                                alreadyFullRoundOne = result
                                foundDevice(binding.linearFoundDevice10)
                                return
                            }
                        }

                    }
                }
            } else {
                for (i in availablePosition) {
                    when (i) {
                        4 -> {
                            foundDevice(binding.linearFoundDevice)
                            val d = DeviceAddedPlace(
                                i,
                                bleDevice
                            )

                            binding.textFoundDevice.text = getDeviceName(d.bleDevice.name)
                            Log.d(TAG, "addDeviceToTheRadar: 4: " + d.bleDevice.name)
                            devicesInflated.add(d)
                            viewModel.inflatedDevices.add(d)
                            availablePosition.remove(i)
                            val result = availablePosition.stream().filter { s: Int -> s > 3 }
                                .collect(Collectors.toList()).isEmpty()
                            alreadyFullRoundOne = result
                            return
                        }
                        5 -> {
                            foundDevice(binding.linearFoundDevice2)
                            val d = DeviceAddedPlace(
                                i,
                                bleDevice,
                                binding.linearFoundDevice2
                            )

                            binding.textFoundDevice2.text = getDeviceName(d.bleDevice.name)
                            Log.d(TAG, "addDeviceToTheRadar: 5: " + d.bleDevice.name)
                            devicesInflated.add(d)
                            viewModel.inflatedDevices.add(d)
                            availablePosition.remove(i)

                            val result = availablePosition.stream().filter { s: Int -> s > 3 }
                                .collect(Collectors.toList()).isEmpty()
                            alreadyFullRoundOne = result
                            return
                        }
                        6 -> {
                            foundDevice(binding.linearFoundDevice3)
                            val d = DeviceAddedPlace(
                                i,
                                bleDevice,
                                binding.linearFoundDevice3
                            )

                            binding.textFoundDevice3.text = getDeviceName(d.bleDevice.name)
                            Log.d(TAG, "addDeviceToTheRadar: 6: " + d.bleDevice.name)
                            devicesInflated.add(d)
                            viewModel.inflatedDevices.add(d)
                            availablePosition.remove(i)

                            val result = availablePosition.stream().filter { s: Int -> s > 3 }
                                .collect(Collectors.toList()).isEmpty()
                            alreadyFullRoundOne = result
                            return
                        }
                        7 -> {
                            foundDevice(binding.linearFoundDevice4)

                            val d = DeviceAddedPlace(
                                i,
                                bleDevice,
                                binding.linearFoundDevice4
                            )

                            binding.textFoundDevice4.text = getDeviceName(d.bleDevice.name)
                            Log.d(TAG, "addDeviceToTheRadar: 7: " + d.bleDevice.name)
                            devicesInflated.add(d)
                            viewModel.inflatedDevices.add(d)
                            availablePosition.remove(i)

                            val result = availablePosition.stream().filter { s: Int -> s > 3 }
                                .collect(Collectors.toList()).isEmpty()
                            alreadyFullRoundOne = result
                            return
                        }
                        8 -> {
                            foundDevice(binding.linearFoundDevice5)
                            val d = DeviceAddedPlace(
                                i,
                                bleDevice,
                                binding.linearFoundDevice5
                            )

                            binding.textFoundDevice5.text = getDeviceName(d.bleDevice.name)
                            Log.d(TAG, "addDeviceToTheRadar: 8: " + d.bleDevice.name)
                            devicesInflated.add(d)
                            viewModel.inflatedDevices.add(d)
                            availablePosition.remove(i)

                            val result = availablePosition.stream().filter { s: Int -> s > 3 }
                                .collect(Collectors.toList()).isEmpty()
                            alreadyFullRoundOne = result
                            return
                        }
                        9 -> {
                            foundDevice(binding.linearFoundDevice6)
                            val d = DeviceAddedPlace(
                                i,
                                bleDevice,
                                binding.linearFoundDevice6
                            )
                            binding.textFoundDevice6.text = getDeviceName(d.bleDevice.name)
                            Log.d(TAG, "addDeviceToTheRadar: 9: " + d.bleDevice.name)
                            devicesInflated.add(d)
                            viewModel.inflatedDevices.add(d)
                            availablePosition.remove(i)

                            val result = availablePosition.stream().filter { s: Int -> s > 3 }
                                .collect(Collectors.toList()).isEmpty()
                            alreadyFullRoundOne = result
                            return
                        }
                    }
                }
            }
        } else {
            BleManager.getInstance().cancelScan()
        }
    }

    fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
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

    private fun writeWifiCredential() {
        if (BleManager.getInstance().isConnected(viewModel.deviceToConnected!!.bleDevice)) {
            viewModel.isBluetoothConnected.value = true
            viewModelDialog.statusCode.value = WIFI_CONNECTION_IN_PROGRESS
            writeWifiCredentialToBleDevice(
                viewModel.deviceToConnected!!.bleDevice,
                AppConstants.SSID,
                AppConstants.PASSWORD
            )
        } else {
            connectToBleDevice(
                viewModel.deviceToConnected!!.bleDevice,
                object : BleConnectionListener {
                    override fun onStartConnect() {
                        Log.d(TAG, "onStartConnect: last ")
                        viewModelDialog.statusCode.value = DEVICE_CONNECTION_IN_PROGRESS
                    }

                    override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                        Log.d(TAG, "onConnectFail: last ")
                        viewModelDialog.statusCode.value = DEVICE_CONNECTION_FAILED
                    }

                    override fun onConnectSuccess(
                        bleDevice: BleDevice?,
                        gatt: BluetoothGatt?,
                        status: Int
                    ) {
                        Log.d(TAG, "onConnectSuccess: last ")
                        writeWifiCredential()
                    }

                    override fun onDisConnected(
                        isActiveDisConnected: Boolean,
                        device: BleDevice?,
                        gatt: BluetoothGatt?,
                        status: Int
                    ) {
                        Log.d(TAG, "onDisConnected: last ")
                        if (!viewModelDialog.isAllDone.value!!) {
                            viewModelDialog.statusCode.value = DEVICE_CONNECTION_FAILED
                            viewModel.isBluetoothConnected.value = false
                        }
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
                    Log.d(TAG, "onWriteSuccess: " + justWrite.first().toString())
                    countDownTimerTopic.start()
                }

                override fun onWriteFailure(exception: BleException) {
                    Log.d(TAG, "onWriteFailure: ${exception.description}")
                }
            })

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
                        Log.d(TAG, "onReadSuccess: Code: " + data.first().toString())
                        bleStatus.value = data.first().toInt()
                    }

                    override fun onReadFailure(exception: BleException) {
                        Log.d(TAG, "onReadFailure: " + exception.code)
                        Log.d(TAG, "onReadFailure: " + exception.description)
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
                        viewModel.backEndAddedDevice!!.ipAddress = data.decodeToString()
                    }

                    override fun onReadFailure(exception: BleException) {
                        Log.d(TAG, "onReadFailure: ip address failed: " + exception.code)
                        Log.d(TAG, "onReadFailure: ip address failed: " + exception.description)
                        readDeviceIpAddress(bleDevice)
                    }
                })
        }

    }

    private fun createDeviceDialog(deviceName: String) {
        connectBeforeCreateDevice()
        val dialog = Dialog(this@AddDeviceActivity)
        if (dialog.isShowing) dialog.cancel()
        dialog.apply {
            with(ItemCreateDeviceBinding.inflate(layoutInflater)) {
                setContentView(root)
                window?.setBackgroundDrawableResource(R.color.transparent)
                setCancelable(false)
//                window?.setWindowAnimations(R.style.DialogAnimation)
                title.text = getString(R.string.add_device_name_title, deviceName)
                description.text = getString(
                    R.string.please_choose_a_unique_name_for_the_device_you_are_adding,
                    deviceName
                )
                button1.setOnClickListener {
                    val name = edittext.text.toString().trim()
                    if (name.isEmpty()) {
                        showMessage("Please enter device name", true)
                    } else if (name.length < 3) {
                        showMessage("Please enter at least 3 characters", true)
                    } else {
                        viewModel.createDeviceApi(
                            name = name,
                            deviceName = deviceName,
                            timezone = TimeZone.getDefault().id
                        )
                        dismiss()
                    }
                }
                button.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }
    }

    private fun connectBeforeCreateDevice() {
        Log.d(
            TAG,
            "connectBeforeCreateDevice: is connected: " + BleManager.getInstance()
                .isConnected(viewModel.deviceToConnected!!.bleDevice)
        )
        connectToBleDevice(viewModel.deviceToConnected!!.bleDevice, object : BleConnectionListener {
            override fun onStartConnect() {
                Log.d(TAG, "onStartConnect: create: ")
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                Log.d(TAG, "onConnectFail: create: ")
            }

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                Log.d(TAG, "onConnectSuccess: create: ")
                resetDevice(bleDevice!!)
                BleManager.getInstance().disconnect(bleDevice)
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                device: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                Log.d(TAG, "onDisConnected: create: ")
            }
        })
    }

    var retryCode: MutableLiveData<Int> = MutableLiveData(0)
    private fun connectAfterCreateDevice() {
        Log.d(
            TAG,
            "connectAfterCreateDevice: is connect: " + BleManager.getInstance()
                .isConnected(viewModel.deviceToConnected!!.bleDevice)
        )
        connectToBleDevice(viewModel.deviceToConnected!!.bleDevice, object : BleConnectionListener {
            override fun onStartConnect() {
                Log.d(TAG, "onStartConnect: connectAfterCreateDevice: ")
                viewModelDialog.statusCode.value = AppConstants.DEVICE_CONNECTION_IN_PROGRESS
            }

            override fun onConnectFail(
                bleDevice: BleDevice?,
                exception: BleException?
            ) {
                Log.d(TAG, "onConnectFail: connectAfterCreateDevice: ")
                viewModelDialog.statusCode.value = DEVICE_CONNECTION_FAILED
            }

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                Log.d(TAG, "onConnectSuccess: connectAfterCreateDevice: ")
                viewModel.isBluetoothConnected.value = true
                viewModelDialog.statusCode.value = DEVICE_CONNECTION_SUCCESS
                readStatusFromDevice(bleDevice)
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                device: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                Log.d(
                    TAG,
                    "onDisConnected: connectAfterCreateDevice: " + viewModelDialog.statusCode.value
                )
                if (!viewModelDialog.isAllDone.value!!) {
                    retryCode.value = viewModelDialog.statusCode.value
                    viewModelDialog.statusCode.value = DEVICE_CONNECTION_FAILED
                    viewModel.isBluetoothConnected.value = false
                }
            }
        })
    }

    //connection to ble device here
    private fun connectToBleDeviceAfterRetry(bleDevice: BleDevice) {
        Log.d(TAG, "connectToBleDeviceAfterRetry: retry: " + retryCode.value)
        connectToBleDevice(bleDevice, this)
    }

    override fun onStartConnect() {
        Log.d(TAG, "onStartConnect: ")
        viewModelDialog.statusCode.value = AppConstants.DEVICE_CONNECTION_IN_PROGRESS
    }

    override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
        Log.d(TAG, "onConnectFail: ")
        viewModelDialog.statusCode.value = DEVICE_CONNECTION_FAILED
    }

    override fun onConnectSuccess(bleDevice: BleDevice?, gatt: BluetoothGatt?, status: Int) {
        Log.d(TAG, "onConnectSuccess: retry: " + retryCode.value)
        viewModel.isBluetoothConnected.value = true
        viewModelDialog.statusCode.value = DEVICE_CONNECTION_SUCCESS
        if (retryCode.value == WIFI_CONNECTION_IN_PROGRESS) {
            writeWifiCredential()
        } else {
            readStatusFromDevice(bleDevice)
        }
    }

    override fun onDisConnected(
        isActiveDisConnected: Boolean,
        device: BleDevice?,
        gatt: BluetoothGatt?,
        status: Int
    ) {
        Log.d(TAG, "onDisConnected: retry: " + retryCode.value)
        if (!viewModelDialog.isAllDone.value!!) {
            viewModelDialog.statusCode.value = DEVICE_CONNECTION_FAILED
            viewModel.isBluetoothConnected.value = false
        }
    }

    override fun onChangeStatus(code: Int?) {
        Log.d(TAG, "onChangeStatus: $code")
        when (code) {
            DEVICE_RESET -> {
                connectToBleDeviceAfterRetry(viewModel.deviceToConnected!!.bleDevice)
            }
            DEVICE_CONNECTION_FAILED -> {
                connectToBleDeviceAfterRetry(viewModel.deviceToConnected!!.bleDevice)
            }
            STUCK_IN_WIFI -> {
                goToWifiActivity()
                restartDevice()
            }
            WiFi_SSID_NOT_AVAILABLE -> {
                goToWifiActivity()
            }
            WiFi_WRONG_CREDENTIALS -> {
                goToWifiActivity()
            }
            WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS -> {
                goToWifiActivity()
            }
            AWS_CONNECTION_FAILED -> {
                viewModelDialog.statusCode.value = AWS_CONNECTION_IN_PROGRESS
                writeTopicToAWS(
                    viewModel.deviceToConnected!!.bleDevice,
                    JSONObject().apply {
                        put("deviceID", uniqueID)
                        if (viewModel.groupOfTheDevice == null)
                            put("topic", viewModel.backEndAddedDevice!!.topic)
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
                            put("topic", viewModel.backEndAddedDevice!!.topic)
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
        Log.d(TabToConnectActivity.TAG, "observers: $topic")
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
                    Log.d(
                        TabToConnectActivity.TAG,
                        "onWriteSuccess: restartDevice: " + justWrite.first().toInt()
                    )
                }

                override fun onWriteFailure(exception: BleException) {
                    Log.d(
                        TabToConnectActivity.TAG,
                        "onWriteFailure: restartDevice: " + exception.code
                    )
                    Log.d(
                        TabToConnectActivity.TAG,
                        "onWriteFailure: restartDevice: " + exception.description
                    )
                }
            })
//        BleManager.getInstance().disconnect(viewModel.deviceToConnected!!.bleDevice)
//        cancelByForce()
    }

    override fun close(refresh: Boolean) {
        Log.d(TAG, "close: " + viewModel.backEndAddedDevice?.version)
        if (refresh) {
            AppConstants.apply {
                refresh_aquarium
                refresh_device
                refresh_group
                refresh_group_device
            }
            Log.d(TAG, "close: version: " + viewModel.backEndAddedDevice!!.version)
            if (viewModel.backEndAddedDevice!!.ipAddress.isNotEmpty()) {
                if (viewModel.backEndAddedDevice!!.version == null) {
                    startActivity(
                        Intent(
                            this@AddDeviceActivity,
                            UpdateDeviceOtaActivity::class.java
                        ).apply {
                            putExtra(
                                "device",
                                ProjectUtil.objectToString(viewModel.backEndAddedDevice)
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
                            onBackPressed()
                        }

                        override fun onNegativeClickListener(dialog: Dialog?) {
                            dialog?.dismiss()
                        }
                    }
                )
            }
        }
        dismissDialog()
        onBackPressed()
    }

    override fun onSelectDeviceType(waterType: String) {
        viewModel.updateDeviceStatusApi(
            viewModel.backEndAddedDevice!!,
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
        bleStatus.removeObservers(this)
        viewModelDialog.statusCode.removeObservers(this)
        if (viewModel.unregisterTheReceiver) {
            unregisterReceiver(gpsSwitchStateReceiver)
        }
        if (viewModel.isBluetoothConnected.value!!) {
            BleManager.getInstance().disconnect(viewModel.deviceToConnected!!.bleDevice)
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
