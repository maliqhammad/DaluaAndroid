package com.dalua.app.ui.aquariumdetails

import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup.GONE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityAquariumDetailsBinding
import com.dalua.app.databinding.ItemCreateDeviceBinding
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.interfaces.OnCheckIfInArea
import com.dalua.app.interfaces.OnSelectDeviceType
import com.dalua.app.interfaces.onDeviceDraged
import com.dalua.app.models.*
import com.dalua.app.ui.adddevice.AddDeviceActivity
import com.dalua.app.ui.taptocomplete.TabToConnectActivity
import com.dalua.app.ui.updateDeviceOTA.UpdateDeviceOtaActivity
import com.dalua.app.ui.customDialogs.shareAquariumDialog.ShareAquariumDialog
import com.dalua.app.ui.customDialogs.troubleshootDialog.TroubleshootDialog
import com.dalua.app.ui.devicedetails.DeviceDetailsActivity
import com.dalua.app.ui.groupdetails.GroupDetailsActivity
import com.dalua.app.ui.home.homeactivity.HomeViewModel
import com.dalua.app.utils.*
import com.dalua.app.utils.AppConstants.ApiTypes.*
import dagger.hilt.android.AndroidEntryPoint
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import java.util.*


@AndroidEntryPoint
class AquariumDetailsActivity : BaseActivity(), OnCheckIfInArea, onDeviceDraged {

    lateinit var binding: ActivityAquariumDetailsBinding
    val viewModel: AquariumDetailsVM by viewModels()
    private var groupPaginationAdapter: GroupPaginationAdapter? = null
    var devicePaginationAdapter: DevicesPaginationAdapter? = null
    private var deviceDragged: Device? = null
    private var ifDeviceIsInArea: Boolean = false
    private lateinit var uniqueUserIdForThisPhone: String
    private var ifIsIstTime = true
    private val homeViewModel: HomeViewModel by viewModels()
    private var selectedGroup: AquariumGroup? = null

    private var isGroupLoading = false
    private var isGroupLastPage = false
    private var currentGroupPage: Int = 1
    private var totalGroupPage: Int = 1

    private var isDevicesLoading = false
    private var isDevicesLastPage = false
    private var currentDevicesPage: Int = 1
    private var totalDevicesPage: Int = 1

    private val deviceBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("DevicesList")) {
                val socketDevice = ProjectUtil.stringToObject(
                    intent.getStringExtra("DevicesList"),
                    SocketResponseModel::class.java
                )
                if (viewModel.deviceList.value != null) {
                    val devicesList = viewModel.deviceList.value
                    devicesList!!.filter { it.macAddress == socketDevice.macAddress }
                        .forEach { it.status = socketDevice.status }
                    viewModel.deviceList.value = devicesList
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObjects()
        observers()
    }

    override fun onStart() {
        super.onStart()
        loadWholeScreenData()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    private fun initObjects() {


        myProgressDialog()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aquarium_details)

        viewModel.aquarium.value = ProjectUtil.stringToObject(
            intent.getStringExtra("aquarium")!!,
            SingleAquarium::class.java
        )
//        ProjectUtil.putAquariumCicked(this, ProjectUtil.objectToString(viewModel.aquarium.value))
        viewModel.aquariumType.value = intent.getIntExtra("type", 0)

        uniqueUserIdForThisPhone = UUID.randomUUID().toString()
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        connectNotificationService(viewModel.aquarium.value!!.id)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceBroadcastReceiver, IntentFilter("AWSConnection"))
        setDevicesRecycler()
        setGroupsRecycler()

    }

    private fun setDevicesRecycler() {
        val linearLayoutManager =
            WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        devicePaginationAdapter = DevicesPaginationAdapter(
            this,
            viewModel,
            this
        )
        binding.devicesRecyclerView.layoutManager = linearLayoutManager
        binding.devicesRecyclerView.adapter = devicePaginationAdapter
        binding.devicesRecyclerView.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isDevicesLoading = true
                currentDevicesPage += 1
                viewModel.loadDevicesNextPage(viewModel.aquarium.value!!.id, currentDevicesPage)
            }

            override val isLastPage: Boolean get() = isDevicesLastPage
            override val isLoading: Boolean get() = isDevicesLoading
        })
    }

    private fun setGroupsRecycler() {
        val gridLayoutManager = WrapContentLinearGridManager(this, 3)
        groupPaginationAdapter = GroupPaginationAdapter(
            viewModel,
            this@AquariumDetailsActivity,
            binding.groupRecyclerView.width
        )

        binding.groupRecyclerView.layoutManager = gridLayoutManager
        binding.groupRecyclerView.adapter = groupPaginationAdapter
        binding.groupRecyclerView.addOnScrollListener(object :
            PaginationScrollListener(gridLayoutManager) {
            override fun loadMoreItems() {
                isGroupLoading = true
                currentGroupPage += 1
                viewModel.loadGroupsNextPage(viewModel.aquarium.value!!.id, currentGroupPage)
            }

            override val isLastPage: Boolean get() = isGroupLastPage
            override val isLoading: Boolean get() = isGroupLoading
        })
    }

    private fun observers() {
        // all api responses
        viewModel.apiResponse.observe(this) {

            when (it) {
                is Resource.Error -> {
                    hideWorking()
                    when (it.api_Type) {
                        UpdateAquariumApi.name,
                        ShareUserApi.name,
                        RemoveShareAquariumApi.name,
                        DeleteDeviceApi.name,
                        UpdateDeviceApi.name,
                        DeleteAquariumApi.name,
                        CreateGroupApi.name,
                        GetAquariumDetails.name,
                        ShareAquariumApi.name,
                        -> {
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

                        GetAquariumDetails.name -> {
                            it.data?.let { it1 ->
                                val aquariumDetailsResponse = ProjectUtil.stringToObject(
                                    it1.string(),
                                    AquariumDetailsResponse::class.java
                                )
                                viewModel.aquarium.value = aquariumDetailsResponse.data
                            }
                        }

                        ShareUserApi.name -> {
                            it.data?.let { responseBody ->
                                val sharedUserResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    SharedUserResponse::class.java
                                )
                                viewModel.shareUSerList.value = sharedUserResponse.data.data
                            }
                        }

                        CreateGroupApi.name -> {
                            it.data?.let { res ->
                                val createGroupResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    CreateGroupResponse::class.java
                                )
                                showMessage(
                                    createGroupResponse.message,
                                    createGroupResponse.success
                                )
                                AppConstants.apply {
                                    refresh_group.value = true
                                }
                                loadWholeScreenData()
                            }

                        }

                        DeleteAquariumApi.name -> {
                            it.data?.let { responseBody ->
                                val createAquariumResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateAquariumResponse::class.java
                                )
                                showMessage(
                                    createAquariumResponse.message,
                                    createAquariumResponse.success
                                )
                                AppConstants.refresh_aquarium.value = true
                                finish()

                            }
                        }

                        UpdateAquariumApi.name -> {
                            it.data?.let { responseBody ->
                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateAquariumResponse::class.java
                                )

                                AppConstants.refresh_aquarium.value = true
                                val q = viewModel.aquarium.value
                                q!!.name = createDeviceResponse.data.name
                                viewModel.aquarium.value = q
                                showMessage(
                                    createDeviceResponse.message,
                                    createDeviceResponse.success
                                )

                            }

                        }

                        RemoveShareAquariumApi.name -> {
                            it.data?.let { responseBody ->
                                val removeShareAquarium = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    SharedUserResponse::class.java
                                )
                                showMessage(
                                    removeShareAquarium.message,
                                    removeShareAquarium.success
                                )
                                AppConstants.refresh_aquarium.value = true
                                viewModel.refreshShareUsers.value = true

                            }
                        }

                        UpdateDeviceApi.name -> {
                            it.data?.let { responseBody ->
                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateDeviceResponse::class.java
                                )
                                viewModel.groupTheDevice.value = true
                                AppConstants.apply {
                                    refresh_aquarium.value = true
                                    refresh_group.value = true
                                    refresh_device.value = true
                                    refresh_group_device.value = true
                                }
                                showMessage(
                                    createDeviceResponse.message,
                                    createDeviceResponse.success
                                )
                                loadWholeScreenData()
                            }
                        }

                        DeleteDeviceApi.name -> {
                            it.data?.let { res ->
                                val createDeviceResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    CreateDeviceResponse::class.java
                                )
                                AppConstants.apply {
                                    refresh_group.value = true
                                    refresh_device.value = true
                                    refresh_aquarium.value = true
                                    refresh_group_device.value = true
                                }
                                Log.d(TAG, "observers: " + res.string())
                                showMessage(
                                    createDeviceResponse.message,
                                    createDeviceResponse.success
                                )
                                loadWholeScreenData()
                            }
                        }

                        ShareAquariumApi.name -> {
                            it.data?.let { res ->
                                val aquariumResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    CreateAquariumResponse::class.java
                                )
                                showMessage(
                                    aquariumResponse.message,
                                    aquariumResponse.success
                                )
                            }
                        }

                    }

                }
            }
        }

        viewModel.apiResponse1.observe(this) {

            when (it) {
                is Resource.Error -> {
                    hideWorking()
                    when (it.api_Type) {
                        GetUserGroup.name,
                        GetUserDevicesApi.name -> {
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

                        GetUserGroup.name -> {
                            it.data?.let { it1 ->

                                val listUserGroupResponse = ProjectUtil.stringToObject(
                                    it1.string(),
                                    ListGroupResponse::class.java
                                )
                                currentGroupPage = listUserGroupResponse.data.currentPage
                                totalGroupPage = listUserGroupResponse.data.lastPage
                                viewModel.groupList.value = listUserGroupResponse!!.data.data

                            }

                        }

                        GetUserDevicesApi.name -> {
                            it.data?.let { it1 ->
                                val listDevicesResponse = ProjectUtil.stringToObject(
                                    it1.string(),
                                    ListDevicesResponse::class.java
                                )
                                currentDevicesPage = listDevicesResponse.data.currentPage
                                totalDevicesPage = listDevicesResponse.data.lastPage
                                viewModel.deviceList.value = listDevicesResponse.data.data
                            }
                        }

                    }

                }
            }
        }

        homeViewModel.apiResponse.observe(this) {
            when (it) {
                is Resource.Error -> {

                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {

                    when (it.api_Type) {

                        // delete device api response
                        DeleteDeviceApi.name -> {
                            AppConstants.apply {
                                refresh_group.value = true
                                refresh_device.value = true
                                refresh_group_device.value = true
                            }
                            Log.d(TAG, "observers: from Home: ")
                        }

                    }

                }
            }
        }

        viewModel.menuItemClicked.observe(this) {
            onMenuObjectClicked()
        }

        viewModel.device.observe(this) {
            onDeviceMenuClicked()
        }

        viewModel.deviceClick.observe(this) {
            onDeviceClick(it)
        }

        viewModel.finishThisActivity.observe(this) {
            finish()
        }

        viewModel.addDeviceClicked.observe(this) {
            AddDeviceActivity.launchToAddDevice(
                this@AquariumDetailsActivity,
                viewModel.aquarium.value!!
            )
        }

        viewModel.addGroup.observe(this) {
            onGroupClicked()
        }

        viewModel.aquariumGroup.observe(this) {
            onGroupClicked(it)
        }

//        AppConstants.refresh_group.observe(this) {
//            if (it) {
//                viewModel.getAquariumDetails(viewModel.aquarium.value!!.id.toString())
//                loadDevices()
//                loadGroups()
//            }
//        }

//        AppConstants.refresh_device.observe(this) {
//            if (it) {
//                viewModel.getAquariumDetails(viewModel.aquarium.value!!.id.toString())
//                loadDevices()
//                loadGroups()
//            }
//        }

        viewModel.aquarium.observe(this) {

            if (ifIsIstTime) {
                ifIsIstTime = false
                return@observe
            }

            Log.d(TAG, "observers: aquarium: " + it.groups.size + " | devices: " + it.devices.size)

            if (it.devices.size > 0 || it.groups.size > 0) {
                binding.emptyCardView.visibility = GONE
                if (viewModel.aquariumType.value == 2) {
                    binding.textAddDevice.visibility = GONE
                } else {
                    binding.textAddDevice.visibility = VISIBLE
                }
            } else {
                if (viewModel.aquariumType.value == 2) {
                    binding.buttonAddDevice.visibility = GONE
                    binding.textClickBelow.visibility = GONE
                }
                binding.emptyCardView.visibility = VISIBLE
                binding.textAddDevice.visibility = GONE
            }
        }

        viewModel.deviceList.observe(this) {
            setDevicesAdapter(it)
        }

        viewModel.groupList.observe(this) {
            Log.d(TAG, "observers: groups added: ")
            setGroupAdapter(it)
        }

        viewModel.sharedUserClick.observe(this) {
            viewModel.removeShareAquarium(
                viewModel.aquarium.value!!.id.toString(),
                it.id.toString()
            )
        }

        viewModel.refreshShareUsers.observe(this) {
            viewModel.getShareAquariumWithUser(viewModel.aquarium.value!!.id.toString())
        }

    }

    private fun loadWholeScreenData() {
        viewModel.initializedAquarium(viewModel.aquarium.value!!.id)
        loadDevices()
        loadGroups()
    }

    private fun loadDevices() {
        devicePaginationAdapter!!.clearList()
        viewModel.loadDevicesFirstPage(viewModel.aquarium.value!!.id)
    }

    private fun loadGroups() {
        groupPaginationAdapter!!.clearList()
        viewModel.loadGroupsFirstPage(viewModel.aquarium.value!!.id)
    }

    private fun setDevicesAdapter(it: MutableList<Device>) {
        if (it.size > 0) {
            binding.deviceGrp.visibility = VISIBLE
            binding.textNoDevice.visibility = GONE
        } else {
            binding.deviceGrp.visibility = GONE
            binding.textNoDevice.visibility = VISIBLE
        }
        devicePaginationAdapter?.removeLoadingFooter()
        isDevicesLoading = false
        devicePaginationAdapter?.addAll(it)
        if (currentDevicesPage != totalDevicesPage) {
            devicePaginationAdapter?.addLoadingFooter()
        } else {
            isDevicesLastPage = true
        }
        if (it.size > 0) {
            showToolTip(binding.devicesRecyclerView)
        }
    }

    private fun setGroupAdapter(
        it: MutableList<AquariumGroup>
    ) {
        binding.deviceGroupGrp.visibility = VISIBLE
        groupPaginationAdapter?.removeLoadingFooter()
        isGroupLoading = false
        groupPaginationAdapter?.addAll(currentGroupPage, it)
        if (currentGroupPage != totalGroupPage) {
            groupPaginationAdapter?.addLoadingFooter()
        } else {
            isGroupLastPage = true
        }
    }

    private fun onDeviceClick(device: Device?) {
        if (device!!.completed == 0) {
            TabToConnectActivity.launchTabToConnect(
                this,
                device,
                viewModel.aquarium.value!!
            )
        } else {
            if (device.status == 0 || device.status == 2) {
                TroubleshootDialog(viewModel.aquarium.value!!,
                    object : TroubleshootDialog.TroubleShootCallback {
                        override fun onDone(device: Device) {
                            Log.d(TAG, "onDone: ")
                            if (viewModel.deviceList.value != null) {
                                val devicesList = viewModel.deviceList.value
                                devicesList!!.filter { it.macAddress == device.macAddress }
                                    .forEach { it.status = device.status }
                                viewModel.deviceList.value = devicesList
                            }
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
            } else {
                if (device.ipAddress.isNullOrEmpty() || device.version == null) {
                    updateDevice(device)
                } else {
                    startActivity(
                        Intent(this, DeviceDetailsActivity::class.java).apply {
                            putExtra(
                                "device",
                                ProjectUtil.objectToString(device)
                            )
                            putExtra("type", viewModel.aquariumType.value)
                            putExtra(
                                "aquarium",
                                ProjectUtil.objectToString(viewModel.aquarium.value!!)
                            )
                        }
                    )
                }
            }
        }

    }

    private fun updateDevice(device: Device) {
        if (device.version == null) {
            startActivity(
                Intent(
                    this@AquariumDetailsActivity,
                    UpdateDeviceOtaActivity::class.java
                ).apply {
                    putExtra("device", ProjectUtil.objectToString(device))
                })
        }
    }

    override fun onOptionFollowed(
        is_in_area: Boolean, group_type: String,
        item_viewHolder: GroupPaginationAdapter.AquariumViewHolder?
    ) {
        if (is_in_area) {

//            if (group_type.toInt() % 2 == 0) {
            ifDeviceIsInArea = true
            item_viewHolder?.binding!!.linearview.setBackgroundResource(R.drawable.card_bg)
//            }

        } else {

//            if (group_type.toInt() % 2 == 0) {
            ifDeviceIsInArea = false
            groupPaginationAdapter?.highlight?.postValue(false)
            item_viewHolder?.binding!!.linearview.setBackgroundResource(0)
//            }

        }
    }

    override fun onItemDroped(
        is_in_area: Boolean, group_type: String?,
        item_viewHolder: GroupPaginationAdapter.AquariumViewHolder
        ?,
        group: AquariumGroup
        ?
    ) {
        if (ifDeviceIsInArea) {
            selectedGroup = group
            moveToGroupDialog(group!!, deviceDragged!!)
        }
    }

    override fun onDeviceDragged(
        device: Device?
    ) {
        deviceDragged = device
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceBroadcastReceiver)
    }

    private fun moveToGroupDialog(group: AquariumGroup, deviceDragged: Device) {
        Log.d(TAG, "moveToGroupDialog: group: " + group.id)
        Log.d(TAG, "moveToGroupDialog: group: " + group.name)
        val updateGroupDialog = Dialog(this)
        if (updateGroupDialog.isShowing) updateGroupDialog.cancel()
        updateGroupDialog.apply {
            setContentView(R.layout.item_move_to_group_dialog)
            setCancelable(true)
//            window?.setWindowAnimations(R.style.DialogAnimation)
            window?.setBackgroundDrawableResource(R.color.transparent)
        }

        updateGroupDialog.findViewById<TextView>(R.id.textView1).text =
            getString(R.string.device_move_to_group_message, deviceDragged.name, group.name)

        updateGroupDialog.findViewById<Button>(R.id.button1).setOnClickListener {

            updateGroupDialog.dismiss()
            if (ProjectUtil.IsConnected(this))
                viewModel.updateDeviceApi(
                    group.id.toString(),
                    deviceDragged,
                    TimeZone.getDefault().id
                )
            else
                ProjectUtil.showToastMessage(
                    this,
                    false,
                    "Check your internet connection"
                )

        }

        updateGroupDialog.findViewById<Button>(R.id.button).setOnClickListener {
            updateGroupDialog.dismiss()
        }

        updateGroupDialog.show()

    }

    private fun onMenuObjectClicked() {

        val popupMenu = PopupMenu(this, findViewById(R.id.aquairum_menu))
        popupMenu.menu.add("Rename")
        popupMenu.menu.add("Delete")
        popupMenu.menu.add("Share")
        popupMenu.menu
        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.title.toString()) {
                "Rename" -> {
                    updateUserAquariumDialog()
                }
                "Delete" -> {
                    deleteUserAquariumDialog()
                }
                "Share" -> {
                    shareAquarium()
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun shareAquarium() {
        if (viewModel.aquariumType.value == 0) {
            val shareAquariumDialog = ShareAquariumDialog(viewModel.aquarium.value!!.id,
                object : ShareAquariumDialog.ShareAquariumCallback {
                    override fun onClose() {
                        Log.d(TAG, "onClose: ")
                        AppConstants.refresh_aquarium.value = true
                        viewModel.refreshShareUsers.value = true
                    }

                    override fun onFinish() {
                        Log.d(TAG, "onFinish: ")
                    }
                })
            if (shareAquariumDialog.isVisible) {
                shareAquariumDialog.dismiss()
            }
            shareAquariumDialog.show(supportFragmentManager, "ShareAquarium")
        } else if (viewModel.aquariumType.value == 1) {
            if (viewModel.shareUSerList.value != null) {
                SharedAquariumBottomSheetDialog().apply {
                    show(supportFragmentManager, "Share Users")
                }
            } else {
                viewModel.getShareAquariumWithUser(viewModel.aquarium.value!!.id.toString())
            }
        }
    }

    private fun onDeviceMenuClicked() {

        val popupMenu = PopupMenu(this, viewModel.view)

        popupMenu.menu.add("Rename")
        if (viewModel.device.value!!.completed != 0) {
            popupMenu.menu.add("Change Type")
            popupMenu.menu.add("Troubleshoot")
        }
        popupMenu.menu.add("Delete")
        Log.d(TAG, "onDeviceMenuClicked: " + viewModel.device.value!!.ipAddress)
        if (!viewModel.device.value!!.ipAddress.isNullOrEmpty()) {
            popupMenu.menu.add("IP Address")
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.title.toString()) {
                "Troubleshoot" -> {
                    TroubleshootDialog(viewModel.aquarium.value!!,
                        object : TroubleshootDialog.TroubleShootCallback {
                            override fun onDone(device: Device) {
                                Log.d(TAG, "onDone: ")
                                if (viewModel.deviceList.value != null) {
                                    val devicesList = viewModel.deviceList.value
                                    devicesList!!.filter { it.macAddress == device.macAddress }
                                        .forEach { it.status = device.status }
                                    viewModel.deviceList.value = devicesList
                                }
                            }
                        }).apply {
                        arguments =
                            Bundle().apply {
                                putString(
                                    "device",
                                    ProjectUtil.objectToString(this@AquariumDetailsActivity.viewModel.device.value!!)
                                )
                            }
                        show(supportFragmentManager, TimeZone.getDefault().toString())
                    }
                }
                "Change Type" -> {
                    changeDeviceType()
                }
                "Rename" -> {
                    renameDeviceDialog(viewModel.device.value!!)
                }
                "Delete" -> {
                    deleteDeviceDialog()
                }
                "IP Address" -> {
                    showIpAddress(viewModel.device.value!!.ipAddress)
                }
            }
            false
        }
        popupMenu.show()
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

    private fun changeDeviceType() {
        if (viewModel.device.value!!.waterType.lowercase(Locale.getDefault()) == "marine") {
            showDeviceTypeDialog(
                isCancelAble = true,
                isFreshWater = false,
                object : OnSelectDeviceType {
                    override fun onSelectType(waterType: String) {
                        viewModel.updateDeviceWaterType(viewModel.device.value!!, waterType)
                    }
                })
        } else if (viewModel.device.value!!.waterType.lowercase(Locale.getDefault()) == "fresh") {
            showDeviceTypeDialog(
                isCancelAble = true,
                isFreshWater = true,
                object : OnSelectDeviceType {
                    override fun onSelectType(waterType: String) {
                        viewModel.updateDeviceWaterType(viewModel.device.value!!, waterType)
                    }
                })
        }
    }

    private fun renameDeviceDialog(device: Device) {
        val renameDeviceDialog = Dialog(this)
        if (renameDeviceDialog.isShowing) renameDeviceDialog.cancel()
        val itemCreateDeviceBinding = ItemCreateDeviceBinding.inflate(layoutInflater)
        renameDeviceDialog.apply {
            with(itemCreateDeviceBinding) {
                setContentView(root)
                window?.setBackgroundDrawableResource(R.color.transparent)
                setCancelable(false)
//                window?.setWindowAnimations(R.style.DialogAnimation)
                button1.text = getString(R.string.rename)
                edittext.text =
                    Editable.Factory.getInstance().newEditable(device.name)
                button1.setOnClickListener {
                    val name = edittext.text.toString().trim()
                    if (name.isNotEmpty()) {
                        if (ProjectUtil.IsConnected(context)) {
                            dismiss()
                            device.name = name
                            viewModel.updateDeviceApi(
                                "",
                                device,
                                TimeZone.getDefault().id
                            )
                        } else {
                            ProjectUtil.showToastMessage(
                                context as AquariumDetailsActivity,
                                false,
                                "Check your internet connection"
                            )
                        }
                    } else
                        ProjectUtil.showToastMessage(
                            this@AquariumDetailsActivity,
                            false,
                            "Please Enter device name"
                        )
                }
                button.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }
    }

    companion object {
        const val TAG = "AquariumDetailsActivity"
    }

    private fun deleteDeviceDialog() {
        val deleteDeviceDialog = Dialog(this)
        if (deleteDeviceDialog.isShowing) deleteDeviceDialog.cancel()
        deleteDeviceDialog.apply {
            setContentView(R.layout.item_delete_dialog)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(true)
//            window?.setWindowAnimations(R.style.DialogAnimation)

            findViewById<TextView>(R.id.textView1).text =
                getString(R.string.delete_device_message)

            findViewById<Button>(R.id.button1).text = getString(R.string.delete)
            findViewById<Button>(R.id.button1).setOnClickListener {

                if (ProjectUtil.IsConnected(context)) {
                    deleteDeviceDialog.dismiss()
                    viewModel.deleteDeviceApi()
                } else
                    ProjectUtil.showToastMessage(
                        context as AquariumDetailsActivity,
                        false,
                        "Check your internet connection"
                    )
            }
            findViewById<Button>(R.id.button).setOnClickListener {
                deleteDeviceDialog.dismiss()
            }

            show()
        }
    }

    private fun updateUserAquariumDialog() {
        val updateAquariumDialog = Dialog(this)
        if (updateAquariumDialog.isShowing) updateAquariumDialog.cancel()
        updateAquariumDialog.apply {
            setContentView(R.layout.item_create_aquarium)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(true)
//            window?.setWindowAnimations(R.style.DialogAnimation)
            val title = findViewById<TextView>(R.id.title)
            val editText = findViewById<EditText>(R.id.editText)
            val buttonDone = findViewById<Button>(R.id.button1)
            val buttonCancel = findViewById<Button>(R.id.button)
            title.setText(R.string.rename)
            editText.setText(viewModel.aquarium.value!!.name.toString())
            buttonDone.text = getString(R.string.update)
            buttonDone.setOnClickListener {

                if (ProjectUtil.IsConnected(this@AquariumDetailsActivity)) {

                    val name = editText.text.toString().trim()

                    if (name.isNotEmpty()) {
                        dismiss()
                        viewModel.updateAquariumApi(name)
                    } else
                        ProjectUtil.showToastMessage(
                            this@AquariumDetailsActivity,
                            false,
                            "Please enter aquarium name"
                        )
                } else
                    ProjectUtil.showToastMessage(
                        this@AquariumDetailsActivity,
                        false,
                        "Check your internet connection"
                    )

            }
            buttonCancel.setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    private fun deleteUserAquariumDialog() {

        val deleteAquariumDialog = Dialog(this)
        if (deleteAquariumDialog.isShowing) deleteAquariumDialog.cancel()
        deleteAquariumDialog.setContentView(R.layout.item_delete_dialog)
        deleteAquariumDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        deleteAquariumDialog.setCancelable(true)
//        deleteAquariumDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        deleteAquariumDialog.findViewById<TextView>(R.id.textView1).text =
            getString(R.string.delete_aquarium_message)
        deleteAquariumDialog.findViewById<Button>(R.id.button1).setOnClickListener {

            if (ProjectUtil.IsConnected(this)) {
//                val name =
//                    deleteAquariumDialog.findViewById<EditText>(R.id.edittext).text.toString()
//                        .trim()


                deleteAquariumDialog.dismiss()
                viewModel.deleteAquariumApi(viewModel.aquarium.value!!.id.toString())

            } else
                showMessage("Check your internet connection", false)

        }

        deleteAquariumDialog.findViewById<Button>(R.id.button).setOnClickListener {
            deleteAquariumDialog.dismiss()
        }

        deleteAquariumDialog.show()
    }

    private fun onGroupClicked() {
        val createGroupDialog = Dialog(this)
        if (createGroupDialog.isShowing) createGroupDialog.cancel()
        createGroupDialog.setContentView(R.layout.item_create_group)
        createGroupDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        createGroupDialog.setCancelable(true)
//        createGroupDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        createGroupDialog.findViewById<Button>(R.id.button1).setOnClickListener {

            if (ProjectUtil.IsConnected(this)) {

                val name =
                    createGroupDialog.findViewById<EditText>(R.id.edittext).text.toString()
                        .trim()
                if (name.isNotEmpty()) {
                    createGroupDialog.dismiss()
                    viewModel.createGroupApi(
                        name,
                        viewModel.aquarium.value!!.id.toString(),
                        TimeZone.getDefault().id
                    )
                } else
                    ProjectUtil.showToastMessage(
                        this,
                        false,
                        "Please enter group name"
                    )
            } else
                ProjectUtil.showToastMessage(
                    this,
                    false,
                    "Check your internet connection"
                )
        }
        createGroupDialog.findViewById<Button>(R.id.button).setOnClickListener {
            createGroupDialog.dismiss()
        }
        createGroupDialog.show()
    }

    private fun onGroupClicked(aquariumGroup: AquariumGroup) {
        startActivity(
            Intent(this, GroupDetailsActivity::class.java).apply {
                putExtra(
                    AppConstants.GROUP,
                    ProjectUtil.objectToString(aquariumGroup)
                )
                putExtra("type", viewModel.aquariumType.value)
                putExtra("aquarium", ProjectUtil.objectToString(viewModel.aquarium.value!!))
            }
        )
    }

    private fun showToolTip(view: View) {
        if (ProjectUtil.isShowToolTip(this)) {
            ProjectUtil.toolTip(this, false)
            SimpleTooltip.Builder(this)
                .anchorView(view)
                .text("Information..!\nDrag and drop a device to a group to add the respective device into that group.")
                .gravity(Gravity.BOTTOM)
                .animated(true)
                .transparentOverlay(false)
                .build()
                .show()
        }
    }

}