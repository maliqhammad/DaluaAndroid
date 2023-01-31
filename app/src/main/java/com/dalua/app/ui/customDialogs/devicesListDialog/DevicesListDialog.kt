package com.dalua.app.ui.customDialogs.devicesListDialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dalua.app.R
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.DevicesListDialogBinding
import com.dalua.app.models.Device
import com.dalua.app.models.SingleAquarium
import com.dalua.app.ui.createschedule.CreateScheduleActivityVM
import com.dalua.app.ui.customDialogs.troubleshootDialog.UploadScheduleTroubleshootDialog
import com.dalua.app.utils.ProjectUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DevicesListDialog(
) : BaseActivity(),
    UploadScheduleTroubleshootDialog.TroubleshootOnUploading {
    lateinit var viewModel: DevicesListDialogVM
    lateinit var binding: DevicesListDialogBinding
    lateinit var adapter: DevicesListAdapter

    companion object {
        const val TAG = "DevicesListDialog"
        var listener: DevicesListCallback? = null
        var isDevicesConnected: Boolean? = null
        var from: String = ""
        var progressBar: Boolean = false
        var singleAquarium: SingleAquarium? = null

        var createScheduleActivityVM: CreateScheduleActivityVM? = null
        fun launchActivityForDevice(
            singleAquarium: SingleAquarium,
            context: Context,
            createScheduleActivityVM: CreateScheduleActivityVM,
            devicesListCallback: DevicesListCallback
        ) {
            from = "device"
            this.singleAquarium = singleAquarium
            this.createScheduleActivityVM = createScheduleActivityVM
            this.isDevicesConnected = false
            this.listener = devicesListCallback
            context.startActivity(Intent(context, DevicesListDialog::class.java))
        }

        fun launchActivityForGroup(
            singleAquarium: SingleAquarium,
            context: Context,
            createScheduleActivityVM: CreateScheduleActivityVM,
            devicesListCallback: DevicesListCallback
        ) {
            from = "group"
            this.singleAquarium = singleAquarium
            this.isDevicesConnected = false
            this.createScheduleActivityVM = createScheduleActivityVM
            this.listener = devicesListCallback
            context.startActivity(Intent(context, DevicesListDialog::class.java))
        }

        fun launchActivityForDeviceAck(
            singleAquarium: SingleAquarium,
            context: Context,
            createScheduleActivityVM: CreateScheduleActivityVM,
            devicesListCallback: DevicesListCallback
        ) {
            from = "deviceAck"
            this.singleAquarium = singleAquarium
            progressBar = true
            this.createScheduleActivityVM = createScheduleActivityVM
            this.isDevicesConnected = true
            this.listener = devicesListCallback
            context.startActivity(Intent(context, DevicesListDialog::class.java))
        }

        fun launchActivityForGroupAck(
            singleAquarium: SingleAquarium,
            context: Context,
            createScheduleActivityVM: CreateScheduleActivityVM,
            devicesListCallback: DevicesListCallback
        ) {
            from = "groupAck"
            this.singleAquarium = singleAquarium
            progressBar = true
            this.createScheduleActivityVM = createScheduleActivityVM
            this.isDevicesConnected = true
            this.listener = devicesListCallback
            context.startActivity(Intent(context, DevicesListDialog::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.setFinishOnTouchOutside(false)
        initObjects()
        setListener()
        observers()
        apiResponses()
    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    @SuppressLint("HardwareIds")
    private fun initObjects() {
        binding = DataBindingUtil.setContentView(this, R.layout.devices_list_dialog)
        viewModel = ViewModelProvider(this).get(DevicesListDialogVM::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        if (from == "device" || from == "deviceAck") {
            viewModel.devicesList.value = listOf(createScheduleActivityVM!!.device.value!!)
        } else if (from == "group" || from == "groupAck") {
            viewModel.devicesList.value = createScheduleActivityVM!!.group.value!!.devices
        }
        viewModel.isDevicesConnected.value = isDevicesConnected
        adapter = DevicesListAdapter(viewModel)
        binding.recyclerView.adapter = adapter
    }

    private fun setListener() {
        binding.button.setOnClickListener {
            if (viewModel.isDevicesConnected.value!!) {
                if (viewModel.isReceiveACK.value!!) {
                    listener!!.onFinish(true)
                } else {
                    listener!!.onReUploadSchedule()
                }
            } else {
                listener!!.onUploadSchedule()
            }
            onBackPressed()
        }
        binding.close.setOnClickListener {
            listener!!.onFinish(true)
            onBackPressed()
        }
    }

    private fun observers() {
        createScheduleActivityVM!!.group.observe(this) {
            Log.d(
                TAG,
                "observers: group: " + (createScheduleActivityVM!!.group.value!!.devices.size == it.devices.filter { it.status == 1 }.size)
            )

            viewModel.isReceiveACK.value =
                createScheduleActivityVM!!.group.value!!.devices.size == it.devices.filter { it.status == 1 }.size
            viewModel.devicesList.value = it.devices
            viewModel.success.value = true
            if (viewModel.isReceiveACK.value!! && from == "groupAck")
                viewModel.isDevicesConnected.value = true
        }
        createScheduleActivityVM!!.device.observe(this) {
            Log.d(TAG, "observers: device: ")
            viewModel.isReceiveACK.value = it.status == 1
            viewModel.devicesList.value = listOf(it)
            viewModel.success.value = true
            if (viewModel.isReceiveACK.value!! && from == "deviceAck")
                viewModel.isDevicesConnected.value = true
        }
        createScheduleActivityVM!!.progress.observe(this) {
            Log.d(TAG, "observers: progress: $it")
            viewModel.progressBar.value = it
        }

        viewModel.devicesList.observe(this) {
            adapter.addList(it)
        }
        viewModel.isDevicesConnected.observe(this) {
            Log.d(TAG, "observers: isDevicesConnected: $it")
            if (it) {
                if (viewModel.isReceiveACK.value!!) {
                    viewModel.title.value = "Uploading schedule"
                    viewModel.button.value = "Done"
                } else {
                    viewModel.title.value = "Uploading schedule"
                    viewModel.button.value = "Re-Upload"
                }
            } else {
                viewModel.title.value = "Troubleshoot"
                viewModel.button.value = "Upload schedule"
            }
        }
        viewModel.title.observe(this) {
            binding.title.text = it
        }
        viewModel.button.observe(this) {
            binding.button.text = it
        }
        viewModel.device.observe(this) {
            val fm = supportFragmentManager.beginTransaction()
            val fragment = supportFragmentManager.findFragmentByTag("Troubleshoot")
            if (fragment != null) {
                fm.remove(fragment)
            }
            fm.addToBackStack(null)
            UploadScheduleTroubleshootDialog(singleAquarium!!, this).apply {
                arguments =
                    Bundle().apply { putString("device", ProjectUtil.objectToString(it)) }
                show(supportFragmentManager, "Troubleshoot")
            }
        }
    }

//    var ss = createScheduleActivityVM!!.group.value!!.apply { devices.filter { it.status == 1 } }
//    private fun isAllAckReceived(): Boolean {
//
//
//        if (from == "group") {
//            return
//
//
//        }
//    }

    private fun apiResponses() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    interface DevicesListCallback {
        fun onUploadSchedule()
        fun onReUploadSchedule()
        fun onFinish(finishBack: Boolean)
    }

    override fun onSuccess(device: Device) {
        Log.d(TAG, "onSuccess: ")
        if (from == "device") {
            createScheduleActivityVM!!.device.value =
                createScheduleActivityVM!!.device.value!!.apply {
                    status = device.status
                }
        } else if (from == "group") {
            createScheduleActivityVM!!.group.value =
                createScheduleActivityVM!!.group.value!!.apply {
                    devices.filter { it.macAddress == device.macAddress }
                        .forEach { it.status = device.status }
                }
        }
        Log.d(TAG, "onSuccess: from: $from")
        viewModel.isDevicesConnected.value = true
    }

}