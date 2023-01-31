package com.dalua.app.ui.groupdetails

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.iot.AWSIotClient
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityGroupDetailsBinding
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.models.*
import com.dalua.app.ui.adddevice.AddDeviceActivity
import com.dalua.app.ui.taptocomplete.TabToConnectActivity
import com.dalua.app.ui.updateDeviceOTA.UpdateDeviceOtaActivity
import com.dalua.app.ui.changeadvance.ChangeAdvanceValuesActivity
import com.dalua.app.ui.createschedule.CreateScheduleActivityVM
import com.dalua.app.ui.createschedule.adapters.MasterControlStateAdapter
import com.dalua.app.ui.createschedule.fragments.instantcontrol.InstantControlFragment
import com.dalua.app.ui.customDialogs.troubleshootDialog.TroubleshootDialog
import com.dalua.app.ui.previewscreen.SchedulePreviewActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.ApiTypes.*
import com.dalua.app.utils.AppConstants.IsDeviceOrGroup.GROUP
import com.dalua.app.utils.PaginationScrollListener
import com.dalua.app.utils.ProjectUtil
import com.dalua.app.utils.WrapContentLinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class GroupDetailsActivity : BaseActivity() {

    lateinit var binding: ActivityGroupDetailsBinding
    val viewModel: GroupDetailsVM by viewModels()
    private lateinit var fragmentStateAdapter: MasterControlStateAdapter
    lateinit var mqttManager: AWSIotMqttManager
    lateinit var client: AWSIotClient
    lateinit var adapter: GroupDeviceAdapter

    private var isDevicesLoading = false
    private var isDevicesLastPage = false
    private var currentDevicesPage: Int = 1
    private var totalDevicesPage: Int = 1

    private val createScheduleActivityVM: CreateScheduleActivityVM by viewModels()
    private var resendUploadSchedule: Boolean = false
    var currentTopic: String? = null
    var isAwsConnectionInProgress = false
    var isPublish = false
    var aValue = 25
    var bValue = 50
    var cValue = 75
    var pAValue = 0
    var pBValue = 0
    var pCValue = 0
    var credentials = MutableLiveData<CognitoCachingCredentialsProvider>()
    lateinit var uniqueUserIdForThisPhone: String

    private val deviceBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("DevicesList")) {
                val socketDevice = ProjectUtil.stringToObject(
                    intent.getStringExtra("DevicesList"),
                    SocketResponseModel::class.java
                )
                val devicesList = viewModel.deviceList.value
                devicesList!!.filter { it.macAddress == socketDevice.macAddress }
                    .forEach { it.status = socketDevice.status }
                viewModel.deviceList.value = devicesList
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObjects()
        observer()
        listeners()
        apiResponses()

    }

    private fun apiResponses() {

        viewModel.apiResponse.observe(this) {

            when (it) {
                is Resource.Error -> {

                    when (it.api_Type) {
                        UnGroupDeviceApi.name,
                        GetUserDevicesApi.name,
                        UpdateGroupApi.name,
                        DeleteGroupApi.name,
                        ReUploadSchedule.name,
                        UpdateDeviceApi.name,
                        DeleteDeviceApi.name,
                        UpdateDeviceStatusApi.name -> {
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

                        UnGroupDeviceApi.name -> {

                            showMessage("UnGroup Device successfully", true)
                            viewModel.deviceUngroup = viewModel.ungroupDevice.value
                            viewModel.changeTopicOfTheDevice.value = true
                            adapter.clearList()
                            viewModel.loadDevicesFirstPage()
                            AppConstants.apply {
                                refresh_group.value = true
                                refresh_device.value = true
                                refresh_aquarium.value = true
                                refresh_group_device.value = true
                            }

                        }

                        GetUserDevicesApi.name -> {
                            it.data?.let { res ->
                                val listDevicesResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    ListDevicesResponse::class.java
                                )
                                currentDevicesPage = listDevicesResponse.data.currentPage
                                totalDevicesPage = listDevicesResponse.data.lastPage
                                viewModel.deviceList.value = listDevicesResponse.data.data
                                viewModel.group.value!!.devices = listDevicesResponse.data.data
                                viewModel.group.value = viewModel.group.value!!.apply {
                                    devices = listDevicesResponse.data.data
                                    if (listDevicesResponse.data.data.size > 0)
                                        waterType = listDevicesResponse.data.data[0].waterType
                                }
                                Log.d(
                                    TAG,
                                    "apiResponses: deviceList: " + viewModel.deviceList.value!!.size
                                )
                            }
                        }

                        UpdateGroupApi.name -> {
                            it.data?.let { responseBody ->
                                val createGroupResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateGroupResponse::class.java
                                )

                                AppConstants.apply {
                                    refresh_group.value = true
                                    refresh_device.value = true
                                    refresh_aquarium.value = true
                                    refresh_group_device.value = true
                                }
                                viewModel.group.value = createGroupResponse.data
                                showMessage(
                                    createGroupResponse.message,
                                    createGroupResponse.success
                                )
                            }
                        }

                        DeleteGroupApi.name -> {
                            it.data?.let { responseBody ->
                                val createGroupResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateGroupResponse::class.java
                                )
                                AppConstants.refresh_group.value = true
                                showMessage(
                                    createGroupResponse.message,
                                    createGroupResponse.success
                                )
                                finish()
                            }
                        }

                        ReUploadSchedule.name -> {

                            it.data?.let { res ->
                                resendUploadSchedule = false
                                Log.d("ReUploadSchedule", "apiResponse: $res")
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
                                    refresh_group_device.value = true
                                    refresh_aquarium.value = true
                                    refresh_device.value = true
                                }
                                showMessage(
                                    createDeviceResponse.message,
                                    createDeviceResponse.success
                                )
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
                            }

                        }

                        UpdateDeviceStatusApi.name -> {
                            it.data?.let { responseBody ->
                                hideWorking()
                                AppConstants.refresh_aquarium.value = true
                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateDeviceResponse::class.java
                                )
                                AppConstants.apply {
                                    refresh_group.value = true
                                    refresh_group_device.value = true
                                    refresh_device.value = true
                                    refresh_aquarium.value = true
                                }
                                showMessage(
                                    createDeviceResponse.message,
                                    createDeviceResponse.success
                                )
                            }
                        }

                    }

                }

            }
        }

    }

    override fun onStart() {
        super.onStart()

        if (AppConstants.device_added_successfully) {
            AppConstants.device_added_successfully = false
        }

    }

    private fun observer() {

        viewModel.ungroupDevice.observe(this) {
            onDeviceMenuObjectClicked(it)
        }

        viewModel.deviceClick.observe(this) {
            onDeviceClick(it)
        }

        viewModel.callMenu.observe(this) {
            onMenuObjectClicked()
        }

        viewModel.goBackActivity.observe(this) {
            finish()
        }

        viewModel.goToNextActivity.observe(this) {
            AddDeviceActivity.launchToAddDeviceFromGroup(
                this,
                viewModel.group.value!!,
                viewModel.aquariumClicked!!
            )
        }

        viewModel.instantControlClick.observe(this) {
            if (viewModel.isAllDevicesActive.value!!) {
                startActivity(
                    Intent(this, ChangeAdvanceValuesActivity::class.java).apply {
                        putExtra("instant", true)
                        putExtra("waterType", viewModel.group.value!!.waterType)
                        putExtra(
                            "configuration",
                            ProjectUtil.objectToString(viewModel.group.value!!.configuration)
                        )
                    }
                )
            } else {
                showAlertDialogWithListener(
                    this,
                    getString(R.string.device_disconnected),
                    getString(R.string.group_device_disconnected_message),
                    getString(R.string.mdtp_ok),
                    "",
                    true, object : AlertDialogInterface {
                        override fun onPositiveClickListener(dialog: Dialog?) {
                            dialog!!.dismiss()
                        }

                        override fun onNegativeClickListener(dialog: Dialog?) {

                        }

                    }
                )
            }
        }

        viewModel.changeTopicOfTheDevice.observe(this) {
            if (it) {
                changeDeviceTopicFromGroupToDevice()
            }
        }

        viewModel.onScheduleControlClicked.observe(this) {
            if (viewModel.isAllDevicesActive.value!!) {
                AppConstants.ScheDuleID = viewModel.group.value!!.schedule.id
                AppConstants.deviceorgroup = GROUP.name
                Log.d(TAG, "observer: devices: " + viewModel.group.value!!.devices.size)
                SchedulePreviewActivity.launchFromGroupDetail(
                    this,
                    viewModel.group.value!!,
                    viewModel.group.value!!.schedule,
                    viewModel.group.value!!.waterType,
                    viewModel.group.value!!.configuration,
                    viewModel.aquariumClicked!!
                )
            } else {
                showAlertDialogWithListener(
                    this,
                    getString(R.string.device_disconnected),
                    getString(R.string.group_device_disconnected_message),
                    getString(R.string.mdtp_ok),
                    "",
                    true, object : AlertDialogInterface {
                        override fun onPositiveClickListener(dialog: Dialog?) {
                            dialog!!.dismiss()
                        }

                        override fun onNegativeClickListener(dialog: Dialog?) {

                        }

                    }
                )
            }

        }

        viewModel.deviceList.observe(this) {
            setDevicesAdapter(it)
        }

        AppConstants.refresh_group_device.observe(this) {
            if (it) {
                adapter.clearList()
                viewModel.loadDevicesFirstPage()
                AppConstants.refresh_group_device.value = false
            }
        }

        credentials.observe(this) {
            mqttManager =
                AWSIotMqttManager(
                    uniqueUserIdForThisPhone,
                    AppConstants.CUSTOMER_SPECIFIC_ENDPOINT
                )
            Thread {
                client = AWSIotClient(credentials.value?.credentials)
                client.setRegion(Region.getRegion(Regions.US_EAST_2))
            }.start()
            callServiceThroughCredentials()
        }


    }

    private fun setDevicesAdapter(it: MutableList<Device>) {
        if (it.isNotEmpty()) {
            viewModel.isAllDevicesActive.value = isAllDevicesActivated(it)
            binding.notEmptyGroup.visibility = View.VISIBLE
            binding.emptyGroup.visibility = View.GONE
        } else {
            binding.notEmptyGroup.visibility = View.GONE
            binding.emptyGroup.visibility = View.VISIBLE
        }
        if (viewModel.aquariumType.value == 2) {
            binding.textAddDevice.visibility = View.GONE
            binding.buttonAddDevice.visibility = View.GONE
            binding.textClickBelow.visibility = View.GONE
            binding.menuGroup.visibility = View.GONE
        }
        adapter.removeLoadingFooter()
        isDevicesLoading = false
        adapter.addAll(it)
        if (currentDevicesPage != totalDevicesPage) {
            adapter.addLoadingFooter()
        } else {
            isDevicesLastPage = true
        }
    }

    private fun onDeviceClick(device: Device?) {
        if (device!!.completed == 0) {
            TabToConnectActivity.launchTabToConnect(
                this,
                device,
                viewModel.aquariumClicked!!
            )
        } else {
            if (device.status == 0 || device.status == 2) {
                TroubleshootDialog(
                    viewModel.aquariumClicked!!,
                    object : TroubleshootDialog.TroubleShootCallback {
                        override fun onDone(device: Device) {
                            Log.d(TAG, "onDone: ")
                            val devicesList = viewModel.deviceList.value
                            devicesList!!.filter { it.macAddress == device.macAddress }
                                .forEach { it.status = device.status }
                            viewModel.deviceList.value = devicesList
                        }
                    }).apply {
                    arguments =
                        Bundle().apply { putString("device", ProjectUtil.objectToString(device)) }
                    show(supportFragmentManager, TimeZone.getDefault().toString())
                }
            } else {
                if (device.ipAddress.isNullOrEmpty() || device.version == null) {
                    updateDevice(device)
                }
            }
        }
    }

    private fun isAllDevicesActivated(devicesList: MutableList<Device>): Boolean {
        for (device in devicesList) {
            if (device.status == 0 || device.status == 2) {
                return false
            }
        }
        return true
    }

    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_details)

        if (intent.hasExtra("aquarium")) {
            viewModel.aquariumClicked = ProjectUtil.stringToObject(
                intent.getStringExtra("aquarium"),
                SingleAquarium::class.java
            )
        }

        myProgressDialog()
        viewModel.group.value =
            ProjectUtil.stringToObject(intent.getStringExtra("group"), AquariumGroup::class.java)
        viewModel.aquariumType.value = intent.getIntExtra("type", 0)
        AppConstants.apply {
            ISGroupOrDevice = "G"
            GroupTopic = viewModel.group.value!!.topic
            DeviceOrGroupID = viewModel.group.value!!.id
        }
        if (viewModel.group.value!!.waterType != null) {
            fragmentStateAdapter = MasterControlStateAdapter(
                supportFragmentManager,
                lifecycle,
                viewModel.group.value!!.waterType,
                viewModel.group.value!!.configuration
            )
            binding.viewPager.adapter = fragmentStateAdapter
        }

        AppConstants.GroupTopic = viewModel.group.value!!.topic

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // aws initilization here
        uniqueUserIdForThisPhone = UUID.randomUUID().toString()
//        connectToAws()
        connectNotificationService(viewModel.aquariumClicked!!.id)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceBroadcastReceiver, IntentFilter("AWSConnection"))
        setDevicesRecycler()
        adapter.clearList()
        viewModel.loadDevicesFirstPage()

    }

    private fun setDevicesRecycler() {
        val linearLayoutManager =
            WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = GroupDeviceAdapter(viewModel)
        binding.devicesRecyclerView.layoutManager = linearLayoutManager
        binding.devicesRecyclerView.adapter = adapter
        binding.devicesRecyclerView.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isDevicesLoading = true
                currentDevicesPage += 1
                viewModel.loadDevicesNextPage(currentDevicesPage)
            }

            override val isLastPage: Boolean get() = isDevicesLastPage
            override val isLoading: Boolean get() = isDevicesLoading
        })
    }

    private fun listeners() {

//        behavior = BottomSheetBehavior.from<View>(binding.bottomSheet)
//        behavior.isDraggable = false

        createScheduleActivityVM.croller1Progress.observe(this) {
            aValue = it
            viewModel.valueA.value = it.toString()
        }

        createScheduleActivityVM.croller2Progress.observe(this) {
            bValue = it
            viewModel.valueB.value = it.toString()
        }

        createScheduleActivityVM.croller3Progress.observe(this) {
            cValue = it
            viewModel.valueC.value = it.toString()
        }

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

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun connectToAws() {

        val credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext,  /* get the context for the application */
            AppConstants.identityPoolID,  /* Identity Pool ID */
            Regions.US_EAST_2 /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )
        credentials.postValue(credentialsProvider)

    }

    private fun callServiceThroughCredentials() {

        mqttManager.connect(
            credentials.value
        ) { status: AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus, throwable: Throwable? ->
            runOnUiThread {
                if (currentTopic != null || isPublish) return@runOnUiThread
                Log.d("ssssssss", "callServiceThroughCredentials: ")
                if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting) {
                    Log.d(
                        "sssssssssss", "Connecting..."
                    )

                    isAwsConnectionInProgress = false

                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {

                    isAwsConnectionInProgress = true

                    viewModel.valueA.value = "25"
                    viewModel.valueB.value = "50"
                    viewModel.valueC.value = "75"
//                    binding.seekBar1.progress = 25
//                    binding.seekBar2.progress = 50
//                    binding.seekBar3.progress = 75

                    codeDataToAwsFormate()
                    Log.d("ssssssss", "Connection Successfull.")

                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting) {


                    if (throwable != null) {
                        isAwsConnectionInProgress = false
                        Log.d("ssssssss", "Reconnecting...", throwable)
                    }

                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {

                    if (throwable != null) {
                        Log.d("sssssssss", "Aws Service Disconnected...", throwable)
                        isAwsConnectionInProgress = false
                    }

                } else {

                    isAwsConnectionInProgress = false
                    Log.d("sssssss", "Disconnected for no reason bro..")
                }
            }
        }
    }

    private fun codeDataToAwsFormate() {

        if (isAwsConnectionInProgress) {

//            if (a_value == p_a_value && b_value == p_b_value && c_value == p_c_value) {
//                Handler(Looper.getMainLooper()).postDelayed({
//                    codeDataToAwsFormate()
//                }, 500)
//                return
//            } else {
            pAValue = aValue
            pBValue = bValue
            pCValue = cValue
//            }

            val jsonObject = JSONObject()
            jsonObject.put("commandID", "3")
//        jsonObject.put("deviceID", group.id)
            jsonObject.put("deviceID", "1")
            jsonObject.put("macAddress", "1")// no colon + lower case
            jsonObject.put("isGroup", true)// if specific device in group then false
            jsonObject.put("timestamp", System.currentTimeMillis())
            jsonObject.put("a_value", cValue)
            jsonObject.put("b_value", bValue)
            jsonObject.put("c_value", aValue)

            Log.d("mmmmmmmmmmmmmm", "A:$aValue :B: $bValue C:$aValue")

            Handler(Looper.getMainLooper()).postDelayed({
                codeDataToAwsFormate()
            }, 500)

        } else {
            connectToAws()
        }


    }

    private fun changeDeviceTopicFromGroupToDevice() {

        val jsonObject = JSONObject()
        jsonObject.put("commandID", "5")
        jsonObject.put("deviceID", "1")
//        jsonObject.put("deviceID", "1")
        jsonObject.put(
            "macAddress",
            viewModel.deviceUngroup!!.macAddress.uppercase(Locale.getDefault())
        )// no colon + lower case
        jsonObject.put("isGroup", false)// if specific device in group then false
        jsonObject.put("timestamp", System.currentTimeMillis())
        jsonObject.put("topic", viewModel.deviceUngroup!!.topic.replace("\\", ""))
        val data = jsonObject.toString()

        if (isAwsConnectionInProgress) {
            publishToTopic(data)
        } else {
            connectToAws()
        }

    }

    private fun publishToTopic(data: String) {

        isPublish = true
        try {
            mqttManager.publishString(
                data,
                viewModel.group.value!!.topic,
                AWSIotMqttQos.QOS0,
                { _, _ ->
                    runOnUiThread {
//                        publish_confirm_dialog.dismiss()
                        resendUploadSchedule = true
                        isPublish = false
                    }
                },
                "Check if data is given"
            )
            Log.d("sssssssss", "Publish data.$data")
        } catch (e: java.lang.Exception) {
            publishToTopic(data)
            Log.d("sssssssss", "Publish error." + e.localizedMessage)
        }

    }

    private fun onMenuObjectClicked() {

        val popupMenu = PopupMenu(this, findViewById(R.id.menuGroup))
        popupMenu.menu.add("Rename Group")
        popupMenu.menu.add("Delete Group")
        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.title.toString()) {
                "Rename Group" -> {
                    renameGroupDialog()
                }
                "Delete Group" -> {
                    deleteGroup()
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun onDeviceMenuObjectClicked(device: Device) {

        val popupMenu = PopupMenu(this, viewModel.viewDeviveInGroup!!)

        popupMenu.menu.add("UnGroup")
        if (viewModel.aquariumType.value != 2) {
            popupMenu.menu.add("Rename")
            popupMenu.menu.add("Delete")
            if (device.completed != 0)
                popupMenu.menu.add("Troubleshoot")

            Log.d(TAG, "onDeviceMenuClicked: " + device.ipAddress)
            if (!device.ipAddress.isNullOrEmpty()) {
                popupMenu.menu.add("IP Address")
            }
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.title.toString()) {
                "Troubleshoot" -> {
                    TroubleshootDialog(
                        viewModel.aquariumClicked!!,
                        object : TroubleshootDialog.TroubleShootCallback {
                            override fun onDone(device: Device) {
                                Log.d(TAG, "onDone: ")
                                val devicesList = viewModel.deviceList.value
                                devicesList!!.filter { it.macAddress == device.macAddress }
                                    .forEach { it.status = device.status }
                                viewModel.deviceList.value = devicesList
                            }
                        }).apply {
                        arguments =
                            Bundle().apply {
                                putString(
                                    "device",
                                    ProjectUtil.objectToString(device)
                                )
                            }
                        show(supportFragmentManager, TimeZone.getDefault().toString())
                    }
                }
                "UnGroup" -> {
                    unGroupDevice(device)
                }
                "Rename" -> {
                    renameDeviceFromGroup(device)
                }
                "Delete" -> {
                    deleteDeviceFromGroup(viewModel.ungroupDevice.value!!)
                }
                "IP Address" -> {
                    showIpAddress(device.ipAddress)
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun updateDevice(device: Device) {
        if (device.version == null) {
            startActivity(
                Intent(
                    this@GroupDetailsActivity,
                    UpdateDeviceOtaActivity::class.java
                ).apply {
                    putExtra("device", ProjectUtil.objectToString(device))
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

    private fun renameGroupDialog() {

        val updateGroupDialog = Dialog(this)
        if (updateGroupDialog.isShowing) updateGroupDialog.cancel()
        updateGroupDialog.setContentView(R.layout.item_create_group)
        updateGroupDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        updateGroupDialog.setCancelable(true)
//        updateGroupDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        updateGroupDialog.findViewById<TextView>(R.id.textView2).text =
            getString(R.string.rename_group)
        updateGroupDialog.findViewById<EditText>(R.id.edittext).setText(viewModel.group.value?.name)
        updateGroupDialog.findViewById<Button>(R.id.button1).text = getString(R.string.rename)
        updateGroupDialog.findViewById<Button>(R.id.button1).setOnClickListener {

            if (ProjectUtil.IsConnected(this)) {
                val name =
                    updateGroupDialog.findViewById<EditText>(R.id.edittext).text.toString().trim()
                if (name.isNotEmpty()) {
                    updateGroupDialog.dismiss()
                    viewModel.updateGroupApi(name, TimeZone.getDefault().id)
                } else
                    showMessage("Enter name plz", false)

            } else
                showMessage("Check your internet connection", false)

        }

        updateGroupDialog.findViewById<Button>(R.id.button).setOnClickListener {
            updateGroupDialog.dismiss()
        }

        updateGroupDialog.show()

    }

    private fun deleteGroup() {
        showAlertDialogWithListener(this,
            getString(R.string.delete_group),
            getString(R.string.delete_group_message),
            getString(R.string.delete),
            getString(R.string.cancel),
            true,
            object : AlertDialogInterface {
                override fun onPositiveClickListener(dialog: Dialog?) {
                    if (ProjectUtil.IsConnected(this@GroupDetailsActivity)) {
                        dialog?.dismiss()
                        viewModel.deleteGroupApi()
                    } else {
                        showMessage("Check your internet connection", false)
                    }

                }

                override fun onNegativeClickListener(dialog: Dialog?) {
                    dialog?.dismiss()
                }
            })
    }

    private fun unGroupDevice(singleDevice: Device) {
        showAlertDialogWithListener(this,
            getString(R.string.un_group),
            getString(R.string.un_group_dialog_message),
            getString(R.string.yes),
            getString(R.string.cancel),
            true, object : AlertDialogInterface {
                override fun onPositiveClickListener(dialog: Dialog?) {
                    if (ProjectUtil.IsConnected(this@GroupDetailsActivity)) {
                        viewModel.deleteDeviceFromGroupApi(singleDevice, TimeZone.getDefault().id)
                        dialog?.dismiss()
                    } else {
                        ProjectUtil.showToastMessage(
                            this@GroupDetailsActivity,
                            false,
                            "Check your internet connection"
                        )
                    }
                }

                override fun onNegativeClickListener(dialog: Dialog?) {
                    dialog?.dismiss()
                }
            })
    }

    private fun renameDeviceFromGroup(singleDevice: Device) {
        val updateDeviceDialog = Dialog(this)
        if (updateDeviceDialog.isShowing) updateDeviceDialog.cancel()
        updateDeviceDialog.setContentView(R.layout.item_create_group)
        updateDeviceDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        updateDeviceDialog.setCancelable(true)
//        updateDeviceDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        updateDeviceDialog.findViewById<TextView>(R.id.textView2).text =
            getString(R.string.rename_device)
        updateDeviceDialog.findViewById<TextView>(R.id.textView1).text =
            getString(R.string.please_choose_a_unique_name_for_the_device_you_are_adding1)
        updateDeviceDialog.findViewById<EditText>(R.id.edittext).setText(singleDevice.name)
        updateDeviceDialog.findViewById<Button>(R.id.button1).text = getString(R.string.rename)
        updateDeviceDialog.findViewById<Button>(R.id.button1).setOnClickListener {
            if (ProjectUtil.IsConnected(this)) {
                val name =
                    updateDeviceDialog.findViewById<EditText>(R.id.edittext).text.toString().trim()
                if (name.isNotEmpty()) {
                    updateDeviceDialog.dismiss()
                    viewModel.updateDeviceApi(
                        name,
                        singleDevice.id.toString(),
                        viewModel.aquariumClicked!!.id.toString(),
                        viewModel.group.value!!.id.toString(),
                        singleDevice.ipAddress,
                        TimeZone.getDefault().id
                    )
                } else
                    showMessage("Enter name plz", false)

            } else {
                showMessage("Check your internet connection", false)
            }
        }

        updateDeviceDialog.findViewById<Button>(R.id.button).setOnClickListener {
            updateDeviceDialog.dismiss()
        }
        updateDeviceDialog.show()
    }

    private fun deleteDeviceFromGroup(singleDevice: Device) {
        showAlertDialogWithListener(this,
            getString(R.string.delete_device),
            getString(R.string.delete_device_message),
            getString(R.string.delete),
            getString(R.string.cancel),
            true, object : AlertDialogInterface {
                override fun onPositiveClickListener(dialog: Dialog?) {
                    if (ProjectUtil.IsConnected(this@GroupDetailsActivity)) {
                        viewModel.deleteDeviceApi(singleDevice)
                        dialog?.dismiss()
                    } else {
                        ProjectUtil.showToastMessage(
                            this@GroupDetailsActivity,
                            false,
                            "Check your internet connection"
                        )
                    }
                }

                override fun onNegativeClickListener(dialog: Dialog?) {
                    dialog?.dismiss()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isAwsConnectionInProgress) {
            mqttManager.disconnect()
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceBroadcastReceiver)
    }

    companion object {
        const val TAG = "GroupDetailsActivity"
    }

}