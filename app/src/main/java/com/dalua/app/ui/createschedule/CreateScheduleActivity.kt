package com.dalua.app.ui.createschedule

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.mobileconnectors.iot.AWSIotMqttSubscriptionStatusCallback
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.baseclasses.BaseApplication
import com.dalua.app.databinding.ActivityCreateScheduleBinding
import com.dalua.app.interfaces.AWSDialogInterface
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.models.*
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.ui.createschedule.adapters.EasyAdvanceFragmentStateAdapter
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.Companion.GEOLOCATIONID
import com.dalua.app.utils.AppConstants.Companion.GEOLOCATIONIDEasy
import com.dalua.app.utils.AppConstants.Companion.IsEditOrPreviewOrCreate
import com.dalua.app.utils.ProjectUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

@AndroidEntryPoint
class CreateScheduleActivity : BaseActivity(), AWSDialogInterface {

    companion object {
        const val TAG = "CreateScheduleActivity"
        fun launchEasyFromDevice(
            context: Context,
            device: Device,
            waterType: String,
            configuration: Configuration,
            scheduleType: Int,
            singleAquarium: SingleAquarium
        ) {
            context.startActivity(Intent(context, CreateScheduleActivity::class.java).apply {
                putExtra("device", ProjectUtil.objectToString(device))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("What", "EASY")
                putExtra("launchFrom", "device")
                putExtra("scheduleType", scheduleType)
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
            })
        }

        fun launchEasyFromGroup(
            context: Context,
            group: AquariumGroup,
            waterType: String,
            configuration: Configuration,
            scheduleType: Int,
            singleAquarium: SingleAquarium
        ) {
            context.startActivity(Intent(context, CreateScheduleActivity::class.java).apply {
                putExtra("group", ProjectUtil.objectToString(group))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("What", "EASY")
                putExtra("launchFrom", "group")
                putExtra("scheduleType", scheduleType)
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
            })
        }

        fun launchEasyPreviewFromDevice(
            context: Context,
            device: Device,
            schedule: SingleSchedule,
            waterType: String,
            configuration: Configuration,
            scheduleType: Int,
            singleAquarium: SingleAquarium
        ) {
            context.startActivity(Intent(context, CreateScheduleActivity::class.java).apply {
                putExtra("device", ProjectUtil.objectToString(device))
                putExtra("sc", ProjectUtil.objectToString(schedule))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("What", "EASY_PREVIEW")
                putExtra("launchFrom", "device")
                putExtra("scheduleType", scheduleType)
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
            })
        }

        fun launchEasyPreviewFromGroup(
            context: Context,
            group: AquariumGroup,
            schedule: SingleSchedule,
            waterType: String,
            configuration: Configuration,
            scheduleType: Int,
            singleAquarium: SingleAquarium
        ) {
            context.startActivity(Intent(context, CreateScheduleActivity::class.java).apply {
                putExtra("group", ProjectUtil.objectToString(group))
                putExtra("sc", ProjectUtil.objectToString(schedule))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("What", "EASY_PREVIEW")
                putExtra("launchFrom", "group")
                putExtra("scheduleType", scheduleType)
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
            })
        }

        fun launchAdvancePreviewFromDevice(
            context: Context,
            device: Device,
            schedule: SingleSchedule,
            waterType: String,
            configuration: Configuration,
            scheduleType: Int,
            singleAquarium: SingleAquarium
        ) {
            context.startActivity(Intent(context, CreateScheduleActivity::class.java).apply {
                putExtra("device", ProjectUtil.objectToString(device))
                putExtra("sc", ProjectUtil.objectToString(schedule))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("What", "ADVANCE_PREVIEW")
                putExtra("launchFrom", "device")
                putExtra("scheduleType", scheduleType)
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
            })
        }

        fun launchAdvancePreviewFromGroup(
            context: Context,
            group: AquariumGroup,
            schedule: SingleSchedule,
            waterType: String,
            configuration: Configuration,
            scheduleType: Int,
            singleAquarium: SingleAquarium
        ) {
            context.startActivity(Intent(context, CreateScheduleActivity::class.java).apply {
                putExtra("group", ProjectUtil.objectToString(group))
                putExtra("sc", ProjectUtil.objectToString(schedule))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("What", "ADVANCE_PREVIEW")
                putExtra("launchFrom", "group")
                putExtra("scheduleType", scheduleType)
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
            })
        }

    }

    val viewModel: CreateScheduleActivityVM by viewModels()
    lateinit var binding: ActivityCreateScheduleBinding
    private var mode: String = "EASY"
    private lateinit var easyAdvanceFragmentStateAdapter: EasyAdvanceFragmentStateAdapter
    private lateinit var uniqueUserIdForThisPhone: String

    private val deviceBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("DevicesList")) {
                val socketDevice = ProjectUtil.stringToObject(
                    intent.getStringExtra("DevicesList"),
                    SocketResponseModel::class.java
                )
                if (viewModel.launchFrom.value == "group") {
                    Log.d(TAG, "onReceive: group mac: " + socketDevice.macAddress)
                    viewModel.group.value = viewModel.group.value!!.apply {
                        devices.filter { it.macAddress == socketDevice.macAddress }
                            .forEach { it.status = socketDevice.status }
                    }
                } else if (viewModel.launchFrom.value == "device") {
                    Log.d(TAG, "onReceive: device mac: " + socketDevice.macAddress)
                    if (socketDevice.macAddress == viewModel.device.value!!.macAddress)
                        viewModel.device.value =
                            viewModel.device.value!!.apply { status = socketDevice.status }
                }
            }
        }
    }

    private val ackBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("DeviceAck")) {
                viewModel.socketResponseModel.value =
                    ProjectUtil.stringToObject(
                        intent.getStringExtra("DeviceAck"),
                        SocketACKResponseModel::class.java
                    )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObjects()
        observers()

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        viewModel.backPressed.value = true
    }

    private fun observers() {

        viewModel.apiResponse2.observe(this) {
            when (it) {
                is Resource.Error -> {

                    when (it.api_Type) {
                        AppConstants.ApiTypes.ReUploadSchedule.name -> {
                            hideWorking()
                            showMessage(it.error, false)
                            Log.d(TAG, "ResponseError: " + it.error + " \n type: " + it.api_Type)
                        }
                    }

                }
                is Resource.Loading -> {
                    showWorking()
                }
                is Resource.Success -> {
                    hideWorking()
                    when (it.api_Type) {
                        AppConstants.ApiTypes.ReUploadSchedule.name -> {
                            it.data?.let { res ->
                                Log.d(TAG, "apiResponse: $res")
                                isResend = true
                                Handler(Looper.getMainLooper()).postDelayed({ finish() }, 100)
                            }
                        }
                    }
                }
            }
        }

        viewModel.group.observe(this) {
            viewModel.isAllGroupDevicesConnect.value = isAllDevicesActivated(it.devices)
        }

        viewModel.backPressed.observe(this) {
            Log.d(TAG, "observers: " + viewModel.killActivity)
            if (viewModel.killActivity) {
                goBackWarningDialog()
            } else {
                viewModel.saveClicked.value = true
            }
            Log.d(TAG, "observers: $IsEditOrPreviewOrCreate")

        }

        viewModel.sendDataToBleNow.observe(this) {

        }

    }

    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_schedule)
        easyAdvanceFragmentStateAdapter =
            EasyAdvanceFragmentStateAdapter(supportFragmentManager, lifecycle)
        myProgressDialog()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.viewPager.adapter = easyAdvanceFragmentStateAdapter
        binding.viewPager.isUserInputEnabled = false
        getIntentData()
        selectMode()
        uniqueUserIdForThisPhone = UUID.randomUUID().toString()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceBroadcastReceiver, IntentFilter("AWSConnection"))

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(ackBroadcastReceiver, IntentFilter("ACK"))


    }

    private fun awsConnection() {
        awsProgressDialog(this)
        (application as BaseApplication).connectToAws()
        (application as BaseApplication).connectionStatus.observe(this) {
            Log.d(TAG, "awsConnection: connectionStatus: $it")
            when (it) {
                0 -> {
                    Log.d(TAG, "awsConnection: Disconnected for no reason")
                    hideAWSProgress()
                }
                1 -> {
                    Log.d(TAG, "awsConnection: Connecting: ")
                    showAWSProgress()
                }
                2 -> {
                    Log.d(TAG, "awsConnection: connected: ")
                    if ((application as BaseApplication).mqttManager != null) {
                        viewModel.mqttManager = (application as BaseApplication).mqttManager!!
                        viewModel.isAwsConnected.value = true
//                        subscribeToTopic()
                    }
                    hideAWSProgress()
                }
                3 -> {
                    Log.d(TAG, "awsConnection: Reconnecting")
                    showAWSProgress()
                }
                4 -> {
                    hideAWSProgress()
                    Log.d(TAG, "awsConnection: Disconnected")
                    viewModel.isAwsConnected.value = false
                }
            }
        }
    }

    private fun awsDisconnect() {
        if ((application as BaseApplication).mqttManager != null) {
            (application as BaseApplication).mqttManager!!.disconnect()
            if (viewModel.mqttManager != null) {
                viewModel.mqttManager?.disconnect()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
//        awsConnection()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: " + viewModel.isDialogOpen.value)
        if (viewModel.isDialogOpen.value!!) {
            viewModel.isDialogOpen.value = false
        } else {
            awsConnection()
        }
    }

    var isResend = false

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: " + viewModel.isDialogOpen.value)
        if (!viewModel.isDialogOpen.value!!) {
            awsDisconnect()
        }
        if (!isResend)
            if (viewModel.resendSchedule.value!!) {
                Log.d(TAG, "onPause: CreateScheduleActivityVM")
                if (AppConstants.ISGroupOrDevice.contentEquals("D"))
                    viewModel.reSendSchedule(AppConstants.DeviceOrGroupID, "device_id")
                else
                    viewModel.reSendSchedule(AppConstants.DeviceOrGroupID, "group_id")
            }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        GEOLOCATIONIDEasy = 0
        GEOLOCATIONID = 0
//        awsDisconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceBroadcastReceiver)
    }

    private fun getIntentData() {
        viewModel.schedule.value =
            ProjectUtil.stringToObject(intent.getStringExtra("sc"), SingleSchedule::class.java)
        viewModel.waterType.value = intent.getStringExtra("waterType")
        viewModel.configuration.value = ProjectUtil.stringToObject(
            intent.getStringExtra("configuration"),
            Configuration::class.java
        )
        viewModel.launchFrom.value = intent.getStringExtra("launchFrom")
        viewModel.scheduleType.value = intent.getIntExtra("scheduleType", 0)
        if (viewModel.launchFrom.value == "device") {
            viewModel.device.value =
                ProjectUtil.stringToObject(intent.getStringExtra("device"), Device::class.java)
        } else if (viewModel.launchFrom.value == "group") {
            viewModel.group.value = ProjectUtil.stringToObject(
                intent.getStringExtra("group"),
                AquariumGroup::class.java
            )
            viewModel.isAllGroupDevicesConnect.value =
                isAllDevicesActivated(viewModel.group.value!!.devices)
        }

        if (intent.hasExtra("aquarium")) {
            viewModel.aquarium.value = ProjectUtil.stringToObject(
                intent.getStringExtra("aquarium"),
                SingleAquarium::class.java
            )
        }
    }

    private fun selectMode() {

        mode = intent.getStringExtra("What")!!
        when (mode) {

            "EASY" -> {
                switchButtons(0)
            }

            "ADVANCE" -> {
                switchButtons(1)
            }

            "EASY_PREVIEW" -> {
//                switchButtons(2)
                switchButtons(0)
            }

            "ADVANCE_PREVIEW" -> {
//                switchButtons(3)
                switchButtons(1)
            }

        }

    }

    private fun switchButtons(i: Int) {

        when (i) {

            0 -> {
                viewModel.tabVisibility.value = true
                binding.easyButton.setBackgroundResource(R.drawable.btn_tab_blue_txt)
                binding.easyButton.setTextColor(Color.WHITE)
                binding.advanceButton.setBackgroundResource(R.drawable.btn_comment_black_txt)
                binding.advanceButton.setTextColor(resources.getColor(R.color.black, theme))
                viewModel.saveText.value = "Next"
                binding.saveButton.setTextColor(getColor(R.color.blue_selected_led_color))

            }

            1 -> {
                binding.advanceButton.setBackgroundResource(R.drawable.btn_tab_blue_txt)
                binding.advanceButton.setTextColor(Color.WHITE)
                binding.easyButton.setBackgroundResource(R.drawable.btn_comment_black_txt)
                binding.easyButton.setTextColor(resources.getColor(R.color.black, theme))
                viewModel.saveText.value = "Next"
                binding.saveButton.setTextColor(getColor(R.color.blue_selected_led_color))
            }

            2 -> {
                binding.linearLayout3.visibility = View.GONE
                viewModel.saveClicked.value = true
                binding.saveButton.setTextColor(resources.getColor(R.color.backgroundColor, theme))
                viewModel.saveText.value = "" // Edit
                binding.saveButton.setTextColor(getColor(R.color.blue_selected_led_color))
            }

            3 -> {
                viewModel.saveText.value = "" // Edit
                binding.linearLayout3.visibility = View.GONE
                viewModel.saveClicked.value = true
                binding.saveButton.setTextColor(getColor(R.color.blue_selected_led_color))
            }

        }

        if (i == 0 || i == 2)
            binding.viewPager.currentItem = 0
        else
            binding.viewPager.currentItem = 1


    }

    fun easyButtonClicked(view: View) {
        switchButtons(0)
    }

    fun advanceButtonClicked(view: View) {
        switchButtons(1)
        viewModel.killActivity = true
    }

    private fun isAllDevicesActivated(devicesList: MutableList<Device>): Boolean {
        for (device in devicesList) {
            if (device.status == 0 || device.status == 2) {
                return false
            }
        }
        return true
    }

    private fun subscribeToTopic() {

        try {
            viewModel.mqttManager!!.subscribeToTopic(
                when {
                    AppConstants.ISGroupOrDevice.contentEquals("G") -> "${AppConstants.GroupTopic}/ack"
                    else -> "${AppConstants.DeviceTopic}/ack"
                }, AWSIotMqttQos.QOS0, object : AWSIotMqttSubscriptionStatusCallback {
                    override fun onSuccess() {
                        Log.d(TAG, "onSuccess: subscribeToTopic: ")
                    }

                    override fun onFailure(exception: Throwable) {
                        Log.d(TAG, "onFailure: subscribeToTopic: " + exception.message)
                        Log.d(TAG, "onFailure: subscribeToTopic: $exception")
                    }
                }
            ) { topic1, data ->
                runOnUiThread {
                    try {
                        val message = String(data!!, Charset.defaultCharset())
                        //"UTF-8"
                        Log.d(TAG, "Message arrived:")
                        Log.d(TAG, "Topic: $topic1")
                        Log.d(TAG, "Message: $message")
                        viewModel.socketResponseModel.value =
                            ProjectUtil.stringToObject(message, SocketACKResponseModel::class.java)
                    } catch (e: UnsupportedEncodingException) {
                        Log.e(TAG, "Message encoding error.", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Subscription error.", e)
        }
    }

    private fun goBackWarningDialog() {
        showAlertDialogWithListener(this,
            getString(R.string.warning),
            getString(R.string.lost_schedule_data_message),
            getString(R.string.mdtp_ok),
            getString(R.string.cancel),
            true,
            object : AlertDialogInterface {
                override fun onPositiveClickListener(dialog: Dialog?) {
                    dialog!!.dismiss()
                    Handler(Looper.getMainLooper()).postDelayed({
                        Log.d(TAG, "onPause: CreateScheduleActivityVM")
                        if (AppConstants.ISGroupOrDevice.contentEquals("D"))
                            viewModel.reSendSchedule2(AppConstants.DeviceOrGroupID, "device_id")
                        else
                            viewModel.reSendSchedule2(AppConstants.DeviceOrGroupID, "group_id")
                    }, 100)

                }

                override fun onNegativeClickListener(dialog: Dialog?) {
                    dialog!!.dismiss()
                }
            })
    }

    override fun onCancel() {
        finish()
    }

}