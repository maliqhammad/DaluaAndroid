package com.dalua.app.ui.devicedetails

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.iot.AWSIotClient
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityDeviceDetailsBinding
import com.dalua.app.databinding.ItemDeviceDetailsBinding
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.interfaces.OnSelectDeviceType
import com.dalua.app.models.*
import com.dalua.app.ui.updateDeviceOTA.UpdateDeviceOtaActivity
import com.dalua.app.ui.changeadvance.ChangeAdvanceValuesActivity
import com.dalua.app.ui.createschedule.CreateScheduleActivityVM
import com.dalua.app.ui.createschedule.adapters.MasterControlStateAdapter
import com.dalua.app.ui.createschedule.fragments.instantcontrol.InstantControlFragment
import com.dalua.app.ui.customDialogs.troubleshootDialog.TroubleshootDialog
import com.dalua.app.ui.previewscreen.SchedulePreviewActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.ApiTypes.*
import com.dalua.app.utils.AppConstants.Companion.CUSTOMER_SPECIFIC_ENDPOINT
import com.dalua.app.utils.AppConstants.Companion.identityPoolID
import com.dalua.app.utils.AppConstants.Companion.refresh_device
import com.dalua.app.utils.AppConstants.Companion.refresh_group
import com.dalua.app.utils.AppConstants.IsDeviceOrGroup.DEVICE
import com.dalua.app.utils.ProjectUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*


@AndroidEntryPoint
class DeviceDetailsActivity : BaseActivity() {

    private var resendUploadSchedule: Boolean = false
    private val viewModel: DeviceDetailsVM by viewModels()
    lateinit var binding: ActivityDeviceDetailsBinding
    lateinit var behavior: BottomSheetBehavior<*>
    lateinit var mqttManager: AWSIotMqttManager
    lateinit var client: AWSIotClient
    lateinit var uniqueUserIdForThisPhone: String
    var credentials = MutableLiveData<CognitoCachingCredentialsProvider>()
    var isSubscribed = false
    var isAwsConnectionInProgress = false
    var currentTopic: String? = null
    private val createscheduleActivityVM: CreateScheduleActivityVM by viewModels()
    var isPublish = false
    var aValue = 25
    var bValue = 50
    var cValue = 75
    var pAValue = 0
    var pBValue = 0
    var pCValue = 0

    private val deviceBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("DevicesList")) {
                val socketDevice = ProjectUtil.stringToObject(
                    intent.getStringExtra("DevicesList"),
                    SocketResponseModel::class.java
                )
                if (socketDevice.macAddress == viewModel.device.value!!.macAddress)
                    viewModel.device.value =
                        viewModel.device.value!!.apply { status = socketDevice.status }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObjects()
        listeners()
        observers()
        apiResponse()

    }

    private fun apiResponse() {

        viewModel.apiResponse.observe(this) {
            when (it) {
                is Resource.Error -> {
                    when (it.api_Type) {
                        GetDeviceDetails.name,
                        ReUploadSchedule.name,
                        DeleteDeviceApi.name,
                        UpdateDeviceApi.name -> {
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
                        GetDeviceDetails.name -> {

                            it.data?.let { responseBody ->
                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateDeviceResponse::class.java
                                )
                                viewModel.device.value = createDeviceResponse.data
                                viewModel.configuration.value =
                                    createDeviceResponse.data.configuration
                                setViewPager(createDeviceResponse.data.configuration)
                                AppConstants.apply {
                                    ISGroupOrDevice = "D"
                                    DeviceOrGroupID = createDeviceResponse.data.id
                                    DeviceTopic = createDeviceResponse.data.topic
                                    DeviceMacAdress =
                                        createDeviceResponse.data.macAddress.uppercase(Locale.getDefault())
                                }
                            }

                        }

                        ReUploadSchedule.name -> {

                            it.data?.let { res ->
                                resendUploadSchedule = false
                                Log.d(TAG, "apiResponse: $res")
                            }

                        }

                        DeleteDeviceApi.name -> {
                            it.data?.let { responseBody ->
                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateDeviceResponse::class.java
                                )
                                AppConstants.apply {
                                    refresh_group.value = true
                                    refresh_device.value = true
                                    refresh_aquarium.value = true
                                    refresh_group_device.value = true
                                }
                                showMessage(
                                    createDeviceResponse.message,
                                    createDeviceResponse.success
                                )
                                refresh_group.value = true
                                refresh_device.value = true
//                            viewModel.resetDevice.value = true
                                finish()
                            }
                        }

                        UpdateDeviceApi.name -> {
                            it.data?.let { responseBody ->
                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateDeviceResponse::class.java
                                )
                                AppConstants.apply {
                                    refresh_group.value = true
                                    refresh_device.value = true
                                    refresh_group_device.value = true
                                    refresh_aquarium.value = true
                                }
                                showMessage(
                                    createDeviceResponse.message,
                                    createDeviceResponse.success
                                )
                                viewModel.getDeviceDetails(createDeviceResponse.data.id)
//                                AppConstants.apply {
//                                    ISGroupOrDevice = "D"
//                                    DeviceOrGroupID = createDeviceResponse.data.id
//                                    DeviceTopic = createDeviceResponse.data.topic
//                                    DeviceMacAdress =
//                                        createDeviceResponse.data.macAddress!!.uppercase(Locale.getDefault())
//                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observers() {
        viewModel.device.observe(this) {
            binding.dvcrv.adapter = AquariumDeviceAdapter(viewModel, it)
        }

        viewModel.onScheduleControlClicked.observe(this) {
            if (viewModel.device.value!!.status == 0 || viewModel.device.value!!.status == 2) {
                TroubleshootDialog(
                    viewModel.aquariumClicked!!,
                    object : TroubleshootDialog.TroubleShootCallback {
                        override fun onDone(device: Device) {
                            Log.d(TAG, "onDone: ")
                            if (device.macAddress == viewModel.device.value!!.macAddress)
                                viewModel.device.value =
                                    viewModel.device.value!!.apply { status = device.status }
                        }
                    }).apply {
                    arguments =
                        Bundle().apply {
                            putString(
                                "device",
                                ProjectUtil.objectToString(this@DeviceDetailsActivity.viewModel.device.value!!)
                            )
                        }
                    show(supportFragmentManager, TimeZone.getDefault().toString())
                }
            } else {
                AppConstants.ScheDuleID = viewModel.device.value!!.schedule.id
                AppConstants.deviceorgroup = DEVICE.name
                SchedulePreviewActivity.launchFromDeviceDetail(
                    this,
                    viewModel.device.value!!,
                    viewModel.device.value!!.schedule,
                    viewModel.device.value!!.waterType,
                    viewModel.configuration.value!!,
                    viewModel.aquariumClicked!!
                )
            }
        }
        viewModel.instantControlClick.observe(this) {
            if (viewModel.device.value!!.status == 0 || viewModel.device.value!!.status == 2) {
                TroubleshootDialog(
                    viewModel.aquariumClicked!!,
                    object : TroubleshootDialog.TroubleShootCallback {
                        override fun onDone(device: Device) {
                            Log.d(TAG, "onDone: ")
                            if (device.macAddress == viewModel.device.value!!.macAddress)
                                viewModel.device.value =
                                    viewModel.device.value!!.apply { status = device.status }
                        }
                    }).apply {
                    arguments =
                        Bundle().apply {
                            putString(
                                "device",
                                ProjectUtil.objectToString(this@DeviceDetailsActivity.viewModel.device.value!!)
                            )
                        }
                    show(supportFragmentManager, TimeZone.getDefault().toString())
                }
            } else {
                startActivity(
                    Intent(this, ChangeAdvanceValuesActivity::class.java).apply {
                        putExtra("instant", true)
                        putExtra("waterType", viewModel.device.value!!.waterType)
                        putExtra(
                            "configuration",
                            ProjectUtil.objectToString(viewModel.configuration.value!!)
                        )
                    }
                )
            }
        }

        viewModel.resetDevice.observe(this) {
            if (it)
                resetBleDevice()
        }

        credentials.observe(this) {
            mqttManager =
                AWSIotMqttManager(uniqueUserIdForThisPhone, CUSTOMER_SPECIFIC_ENDPOINT)
            Thread {
                client = AWSIotClient(credentials.value?.credentials)
                client.setRegion(Region.getRegion(Regions.US_EAST_2))
            }.start()
            callServiceThroughCredentials()
        }

        viewModel.showMenuItem.observe(this) {
            onMenuObjectClicked()
        }

        viewModel.finishActivityData.observe(this) {
            finish()
        }

        viewModel.showInstantDialogInfo.observe(this) {
            showInstantControlInfoDialog()
        }

        viewModel.showMasterDialogInfo.observe(this) {
            showMasterControlInfoDialog()
        }

    }

    private fun listeners() {

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                val currentItem: Fragment? = supportFragmentManager.findFragmentByTag("f$position")
                if (currentItem is InstantControlFragment) {
                    binding.instantcontrol.visibility = View.VISIBLE
                    binding.upDownImgView.visibility = View.VISIBLE
                } else {
                    binding.instantcontrol.visibility = View.GONE
                    binding.upDownImgView.visibility = View.GONE
                }
            }
        })

        createscheduleActivityVM.croller1Progress.observe(this) {
            aValue = it
            viewModel.valueA.value = it.toString()

        }

        createscheduleActivityVM.croller2Progress.observe(this) {
            bValue = it
            viewModel.valueB.value = it.toString()
        }

        createscheduleActivityVM.croller3Progress.observe(this) {
            cValue = it
            viewModel.valueC.value = it.toString()
        }
    }

    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_details)

        setWidthForBottomSheet()

        myProgressDialog()
        binding.lifecycleOwner = this
        viewModel.device.value =
            ProjectUtil.stringToObject(intent.getStringExtra("device"), Device::class.java)
        viewModel.aquariumType.value = intent.getIntExtra("type", 0)
        AppConstants.apply {
            ISGroupOrDevice = "D"
            GroupTopic = viewModel.device.value!!.topic
            DeviceOrGroupID = viewModel.device.value!!.id
        }
        viewModel.getDeviceDetails(viewModel.device.value!!.id)

        if (intent.hasExtra("aquarium")) {
            viewModel.aquariumClicked = ProjectUtil.stringToObject(
                intent.getStringExtra("aquarium"),
                SingleAquarium::class.java
            )
        }

        binding.viewModel = viewModel
        binding.item = viewModel.device.value

//        aws initialization here
        uniqueUserIdForThisPhone = UUID.randomUUID().toString()
//        connectToAws()

        viewModel.valueA.value = "30"
        viewModel.valueB.value = "50"
        viewModel.valueC.value = "70"
        connectNotificationService(viewModel.aquariumClicked!!.id)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceBroadcastReceiver, IntentFilter("AWSConnection"))

    }

    private fun setViewPager(configuration: Configuration) {
        binding.viewPager.adapter = MasterControlStateAdapter(
            supportFragmentManager,
            lifecycle,
            viewModel.device.value!!.waterType,
            configuration
        )
    }

    private fun setWidthForBottomSheet() {
        binding.parentLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.parentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height = binding.parentLayout.height
                val layoutParams = binding.viewPager.layoutParams

                layoutParams.height = (height * 0.5).toInt()
                binding.viewPager.layoutParams = layoutParams
            }
        })

    }

    private fun codeDataToAwsFormat() {
        if (isAwsConnectionInProgress) {
            pAValue = aValue
            pBValue = bValue
            pCValue = cValue
            val jsonObject = JSONObject()
            jsonObject.put("commandID", "3")
            jsonObject.put("deviceID", "1")
            jsonObject.put(
                "macAddress", viewModel.device.value?.macAddress?.uppercase(Locale.getDefault())
            )
            jsonObject.put("isGroup", true)// if specific device in group then false
            jsonObject.put("timestamp", System.currentTimeMillis())
            jsonObject.put("a_value", cValue)
            jsonObject.put("b_value", bValue)
            jsonObject.put("c_value", aValue)


            val data = jsonObject.toString()
            if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    publishToTopic(data)
                } else {
                    Log.d(TAG, "behaviour problem: 21")
                }
            } else {
                Log.d(TAG, "Activity is not in resume state: 21")
            }


            Handler(Looper.getMainLooper()).postDelayed({
                codeDataToAwsFormat()
            }, 500)

        } else {
            connectToAws()
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // aws implementation here
    private fun connectToAws() {

        val credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext,  /* get the context for the application */
            identityPoolID,  /* Identity Pool ID */
            Regions.US_EAST_2 /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )

        getCredentialsInBackgroundThread(credentialsProvider)
    }

    private fun getCredentialsInBackgroundThread(credentialsProvider: CognitoCachingCredentialsProvider) {
        credentials.postValue(credentialsProvider)
    }

    private fun callServiceThroughCredentials() {

        mqttManager.connect(
            credentials.value
        ) { status: AWSIotMqttClientStatus, throwable: Throwable? ->
            runOnUiThread {
                if (currentTopic != null || isPublish) return@runOnUiThread
                Log.d(TAG, "callServiceThroughCredentials: ")
                if (status == AWSIotMqttClientStatus.Connecting) {
                    Log.d(
                        TAG, "Connecting..."
                    )
                    isAwsConnectionInProgress = false

                } else if (status == AWSIotMqttClientStatus.Connected) {

                    isAwsConnectionInProgress = true
                    codeDataToAwsFormat()
                    Log.d(TAG, "Connection Successfull.")

                } else if (status == AWSIotMqttClientStatus.Reconnecting) {


                    if (throwable != null) {
                        isAwsConnectionInProgress = false
                        Log.d(TAG, "Reconnecting...", throwable)
                    }

                } else if (status == AWSIotMqttClientStatus.ConnectionLost) {

                    if (throwable != null) {
                        Log.d(TAG, "Aws Service Disconnected...", throwable)
                        isAwsConnectionInProgress = false
                    }

                } else {

                    isAwsConnectionInProgress = false
                    Log.d(TAG, "Disconnected for no reason bro..")
                }
            }
        }
    }

    private fun publishToTopic(data: String) {

        isPublish = true
        try {
            mqttManager.publishString(
                data,
                viewModel.device.value!!.topic,
                AWSIotMqttQos.QOS0,
                { status, userData ->
                    runOnUiThread {
                        isPublish = false
                        resendUploadSchedule = true
                    }
                },
                "Check if data is given"
            )
        } catch (e: java.lang.Exception) {
//            publishToTopic(data)
            Log.d(TAG, "Publish error." + e.localizedMessage)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isAwsConnectionInProgress)
            mqttManager.disconnect()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceBroadcastReceiver)
    }

    private fun resetBleDevice() {

        val jsonObject = JSONObject()
        jsonObject.put("commandID", "2")
        jsonObject.put("deviceID", "1")
        jsonObject.put(
            "macAddress",
            viewModel.device.value?.macAddress?.uppercase(Locale.getDefault())
        )// no colon + lower case
        jsonObject.put("isGroup", true)
        jsonObject.put("timestamp", System.currentTimeMillis())

        val data = jsonObject.toString()

        isPublish = true
        try {
            mqttManager.publishString(
                data,
                viewModel.device.value!!.topic,
                AWSIotMqttQos.QOS0,
                { status, userData ->
                    runOnUiThread {
                        isPublish = false
                        finish()
                    }
                },
                "Check if data is given"
            )
        } catch (e: java.lang.Exception) {
            resetBleDevice()
            Log.d(TAG, "Publish error." + e.localizedMessage)
        }


    }

    private fun updateUserDeviceDialog() {

        val updateGroupDialog = Dialog(this)
        if (updateGroupDialog.isShowing) updateGroupDialog.cancel()
        updateGroupDialog.setContentView(R.layout.item_create_device)
        updateGroupDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        updateGroupDialog.setCancelable(true)
//        updateGroupDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        updateGroupDialog.findViewById<EditText>(R.id.edittext)
            .setText(viewModel.device.value?.name)
        updateGroupDialog.findViewById<Button>(R.id.button1).text = "Update"
        updateGroupDialog.findViewById<Button>(R.id.button1).setOnClickListener {
            val name = updateGroupDialog.findViewById<EditText>(R.id.edittext).text.toString()
            if (!name.isNullOrBlank()) {
                updateGroupDialog.dismiss()
                if (ProjectUtil.IsConnected(this)) {
                    viewModel.updateDeviceApi(name, TimeZone.getDefault().id)
                } else
                    showMessage("Check your internet connection", false)
            } else
                showMessage("Enter name plz", false)

        }

        updateGroupDialog.findViewById<Button>(R.id.button).setOnClickListener {
            updateGroupDialog.dismiss()
        }

        updateGroupDialog.show()

    }

    fun onMenuObjectClicked() {

        val popupMenu = PopupMenu(this, findViewById(R.id.menudevice))
        popupMenu.menu.add("Rename")
        if (viewModel.device.value!!.completed != 0) {
            popupMenu.menu.add("Change Type")
            popupMenu.menu.add("Troubleshoot")
        }
        Log.d(TAG, "onDeviceMenuClicked: " + viewModel.device.value!!.ipAddress)
        if (!viewModel.device.value!!.ipAddress.isNullOrEmpty()) {
            popupMenu.menu.add("IP Address")
        }
        popupMenu.menu.add("Delete")
        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.title.toString()) {
                "Troubleshoot" -> {
                    TroubleshootDialog(
                        viewModel.aquariumClicked!!,
                        object : TroubleshootDialog.TroubleShootCallback {
                            override fun onDone(device: Device) {
                                Log.d(TAG, "onDone: ")
                                if (device.macAddress == viewModel.device.value!!.macAddress)
                                    viewModel.device.value =
                                        viewModel.device.value!!.apply { status = device.status }
                            }
                        }).apply {
                        arguments =
                            Bundle().apply {
                                putString(
                                    "device",
                                    ProjectUtil.objectToString(this@DeviceDetailsActivity.viewModel.device.value!!)
                                )
                            }
                        show(supportFragmentManager, TimeZone.getDefault().toString())
                    }
                }
                "Rename" -> {
                    updateUserDeviceDialog()
                }
                "Change Type" -> {
                    if (viewModel.device.value!!.waterType.lowercase(Locale.getDefault()) == "marine") {
                        showDeviceTypeDialog(true, false, object : OnSelectDeviceType {
                            override fun onSelectType(waterType: String) {
                                viewModel.updateDeviceWaterType(viewModel.device.value!!, waterType)
                            }
                        })
                    } else if (viewModel.device.value!!.waterType.lowercase(Locale.getDefault()) == "fresh") {
                        showDeviceTypeDialog(true, true, object : OnSelectDeviceType {
                            override fun onSelectType(waterType: String) {
                                viewModel.updateDeviceWaterType(viewModel.device.value!!, waterType)
                            }
                        })
                    }
                }
                "Delete" -> {
                    deleteUserDevice()
                }
                "IP Address" -> {
                    showIpAddress(viewModel.device.value!!.ipAddress)
                }
            }
            false
        }
        popupMenu.show()
    }


    private fun updateDevice(device: Device) {
        if (device.version ==null) {
            startActivity(
                Intent(
                    this@DeviceDetailsActivity,
                    UpdateDeviceOtaActivity::class.java
                ).apply {
                    putExtra("device", ProjectUtil.objectToString(device))
//            putExtra("deviceConnected", ProjectUtil.objectToString(viewModel.deviceToConnected))
                })
        }
    }

    private fun showIpAddress(ipAddress: String) {
        showAlertDialogWithListener(this,
            "Device Ip Address:",
            ipAddress,
            "Ok",
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
    }

    private fun deleteUserDevice() {

        val deleteGroupDialog = Dialog(this)
        if (deleteGroupDialog.isShowing) deleteGroupDialog.cancel()
        deleteGroupDialog.apply {

            setContentView(R.layout.item_delete_dialog)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(true)
            findViewById<TextView>(R.id.textView1).text =
                getString(R.string.delete_device_message)
//            window?.setWindowAnimations(R.style.DialogAnimation)
            findViewById<Button>(R.id.button1).setOnClickListener {


                if (ProjectUtil.IsConnected(context)) {
                    deleteGroupDialog.dismiss()
                    viewModel.deleteDeviceApi()
                } else
                    ProjectUtil.showToastMessage(
                        context as DeviceDetailsActivity,
                        false,
                        "Check your internet connection"
                    )
            }
            findViewById<Button>(R.id.button).setOnClickListener {
                deleteGroupDialog.dismiss()
            }

            show()
        }

    }

    private fun showMasterControlInfoDialog() {

        val masterControlDialog = Dialog(this)
        if (masterControlDialog.isShowing) masterControlDialog.cancel()
        masterControlDialog.setContentView(R.layout.item_info)
        masterControlDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        masterControlDialog.setCancelable(true)
//        masterControlDialog.window?.setWindowAnimations(R.style.DialogAnimation)

        masterControlDialog.findViewById<TextView>(R.id.textView2).text = "Master Control"
//        masterControlDialog.show()
    }

    private fun showInstantControlInfoDialog() {

        val masterControlDialog = Dialog(this)
        if (masterControlDialog.isShowing) masterControlDialog.cancel()
        masterControlDialog.setContentView(R.layout.item_info)
        masterControlDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        masterControlDialog.setCancelable(true)
//        masterControlDialog.window?.setWindowAnimations(R.style.DialogAnimation)

        masterControlDialog.findViewById<TextView>(R.id.textView2).text = "Instant Control"
//        masterControlDialog.show()

    }

    class AquariumDeviceAdapter(
        val viewModell: DeviceDetailsVM,
        val list: Device
    ) : RecyclerView.Adapter<AquariumDeviceAdapter.AquariumVH>() {

        class AquariumVH(itemView: ItemDeviceDetailsBinding) :
            RecyclerView.ViewHolder(itemView.root) {
            val madapterbinding = itemView

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AquariumVH {

            val binding: ItemDeviceDetailsBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_device_details,
                parent,
                false
            )
            return AquariumVH(binding)
        }

        override fun onBindViewHolder(holder: AquariumVH, position: Int) {
            holder.madapterbinding.apply {
                viewModel = viewModell
                item = list
            }
        }

        override fun getItemCount(): Int {
            return 1
        }
    }

    companion object {
        const val TAG = "DeviceDetailsActivity"
    }


}