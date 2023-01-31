package com.dalua.app.baseclasses

import android.app.ActivityManager
import android.app.Dialog
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.dalua.app.R
import com.dalua.app.databinding.AlertDialogLayoutBinding
import com.dalua.app.interfaces.AWSDialogInterface
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.interfaces.BleConnectionListener
import com.dalua.app.interfaces.OnSelectDeviceType
import com.dalua.app.models.DeviceAddedPlace
import com.dalua.app.models.EasySlots
import com.dalua.app.models.schedulemodel.Slot
import com.dalua.app.utils.ProjectUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private val showToast = true
    private var progressDialog: Dialog? = null
    private var awsProgress: Dialog? = null
    private val TAG = "BaseActivity"

    fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null) ?: return null
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(column_index)
        cursor.close()
        return s
    }

    fun isFileExist(dirPath: String, fileName: String): Boolean {
        return File(dirPath, fileName).exists()
    }

    fun isFileExist(filePath: String): Boolean {
        return File(filePath).exists()
    }

    fun deleteFileFromStorage(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
            Log.d(TAG, "deleteFileFromStorage: deleted: ")
        }
    }

    fun getAppFileDirectoryToSaveOTA(): String {
        val file =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/Dalua/OTA-Versions")
        return if (file.exists()) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/Dalua/OTA-Versions"
        } else {
            file.mkdirs()
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/Dalua/OTA-Versions"
        }
    }

    fun getDeviceName(name: String): String {
        var deviceName = name
        deviceName = deviceName.replaceBefore("-", "").replaceAfterLast("-", "").replace("-", "")
        return deviceName
    }

    fun showDeviceTypeDialog(
        isCancelAble: Boolean,
        isFreshWater: Boolean,
        listener: OnSelectDeviceType
    ) {
        val dialog = Dialog(this)
        if (dialog.isShowing) dialog.cancel()
        dialog.apply {
            setContentView(R.layout.device_type_dialog)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(isCancelAble)
            window?.setWindowAnimations(R.style.DialogAnimation)
            if (isFreshWater) {
                findViewById<RadioButton>(R.id.freshwaterCheck).isChecked = true
            } else {
                findViewById<RadioButton>(R.id.saltwaterCheck).isChecked = true
            }
            findViewById<Button>(R.id.doneButton).setOnClickListener {
                if (findViewById<RadioButton>(R.id.saltwaterCheck).isChecked) {
                    if (isFreshWater)
                        listener.onSelectType("Marine")
                } else if (findViewById<RadioButton>(R.id.freshwaterCheck).isChecked) {
                    if (!isFreshWater)
                        listener.onSelectType("Fresh")
                }
                dismiss()
                Log.d(TAG, "showDeviceTypeDialog: $isFreshWater")
            }
            show()
        }
    }

    fun showWorking() {
        if (progressDialog?.isShowing != true) {
            progressDialog?.show()
        }
    }

    fun awsProgressDialog(awsDialogInterface: AWSDialogInterface) {
        awsProgress = Dialog(this)
        awsProgress?.apply {
            setContentView(R.layout.item_aws_progress_dialog)
            window!!.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(false)
            window!!.setWindowAnimations(R.style.DialogAnimation)
            Handler(Looper.getMainLooper()).postDelayed({
                findViewById<ImageView>(R.id.close).visibility = View.VISIBLE
            }, 10000)
            findViewById<ImageView>(R.id.close).setOnClickListener {
                awsDialogInterface.onCancel()
            }
        }
    }

    fun showAWSProgress() {
        if (awsProgress?.isShowing != true) {
            awsProgress?.show()
        }
    }

    fun hideAWSProgress() {
        awsProgress?.dismiss()
    }

    fun hideWorking() {
        progressDialog?.dismiss()
    }

    fun getDrawable(name: String): Drawable? {
        Log.d(TAG, "getDrawable: $name")
        val resourceId: Int = resources.getIdentifier(name, "drawable", packageName)
        return ResourcesCompat.getDrawable(resources, resourceId, theme)
    }

    fun myProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog?.apply {
            setContentView(R.layout.item_progress_dialog)
            window!!.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(false)
            window!!.setWindowAnimations(R.style.DialogAnimation)
        }
    }

    fun showMessage(message: String?, errorOrSuccess: Boolean) {
        if (showToast)
            ProjectUtil.showToastMessage(this, errorOrSuccess, message)
    }

    fun showAlertDialogWithListener(
        context: Context,
        title: String,
        message: String,
        positiveButton: String,
        negativeButton: String,
        isCancelAble: Boolean,
        listener: AlertDialogInterface
    ) = try {

        lifecycleScope.launch {

            val dialog = Dialog(context)
            if (dialog.isShowing) dialog.cancel()
            val binding = AlertDialogLayoutBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)
            dialog.setCanceledOnTouchOutside(isCancelAble)
            dialog.setCancelable(isCancelAble)
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
//            dialog.window?.setWindowAnimations(R.style.DialogAnimation)
            binding.textTitle.text = title
            binding.textMessage.text = message
            if (positiveButton != "") {
                binding.positiveButton.visibility = View.VISIBLE
                binding.positiveButton.text = positiveButton
                binding.positiveButton.setOnClickListener {
                    listener.onPositiveClickListener(dialog)
                }
            } else {
                binding.positiveButton.visibility = View.GONE
            }
            if (negativeButton != "") {
                binding.negativeButton.visibility = View.VISIBLE
                binding.negativeButton.text = negativeButton
                binding.negativeButton.setOnClickListener {
                    listener.onNegativeClickListener(dialog)
                }
            } else {
                binding.negativeButton.visibility = View.GONE
            }
            dialog.show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    fun showAlertDialogWithOutListener(
        context: Context,
        title: String,
        message: String,
        button: String
    ) = try {

        lifecycleScope.launch {
            val dialog = Dialog(context)
            if (dialog.isShowing) dialog.cancel()
            val binding = AlertDialogLayoutBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
//            dialog.window?.setWindowAnimations(R.style.DialogAnimation)
            binding.textTitle.text = title
            binding.textMessage.text = message
            binding.positiveButton.text = button
            binding.positiveButton.setOnClickListener {
                dialog.dismiss()
            }
            binding.negativeButton.visibility = View.GONE
            dialog.show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    fun showChart(
        context: Context,
        chart: LineChart,
        easySlots: EasySlots?,
        slots: MutableList<Slot>
    ) {

        // // Chart Style // //

        // background color
        chart.setBackgroundColor(Color.WHITE)

        // disable description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(false)

        // set listeners
        chart.setDrawGridBackground(false)

        // enable scaling and dragging
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)

        // force pinch zoom along both axis
        chart.setPinchZoom(false)

        // // X-Axis Style // //
        val xAxis: XAxis = chart.xAxis
        xAxis.axisLineColor = Color.rgb(0, 0, 255)

        xAxis.setDrawGridLines(true)
        xAxis.gridLineWidth = 0.5f
        xAxis.gridColor = context.getColor(R.color.bar_white_color)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(6, true)
        xAxis.setDrawLabels(false)
        xAxis.axisLineWidth = 0.1f

        // // Y-Axis Style // //
        val yAxis: YAxis = chart.axisLeft
        yAxis.gridLineWidth = 0.5f

        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false

        // axis range
        yAxis.axisMaximum = 200f
        yAxis.axisMinimum = 0f

        // Create Limit Lines //
        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        setData(180f, chart, easySlots, slots)

        // draw points over time
        chart.animateX(1000, Easing.EaseInBack)
//        chart.animateX(1500, Easing.EaseInSine)

        // get the legend (only possible after setting data)
        chart.legend.isEnabled = false

    }

    private fun setData(
        range: Float,
        chart: LineChart,
        easySlots: EasySlots?,
        slots: MutableList<Slot>
    ) {
        var count = 6
        val values = ArrayList<Entry>()
        val values2 = ArrayList<Entry>()
        val values3 = ArrayList<Entry>()

        if (easySlots != null) {

            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values.add(Entry(i.toFloat(), 0f))
                    else -> values.add(Entry(i.toFloat(), easySlots.getValueA()!!.toFloat()))
                }
            }
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values2.add(Entry(i.toFloat(), 0f))
                    else -> values2.add(Entry(i.toFloat(), easySlots.getValueB()!!.toFloat()))
                }
            }
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values3.add(Entry(i.toFloat(), 0f))
                    else -> values3.add(Entry(i.toFloat(), easySlots.getValueC()!!.toFloat()))
                }
            }
        } else {
            count = slots.size
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values.add(Entry(i.toFloat(), 0f))
                    else -> values.add(Entry(i.toFloat(), slots.get(i).getValueA()!!.toFloat()))
                }
            }
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values2.add(Entry(i.toFloat(), 0f))
                    else -> values2.add(Entry(i.toFloat(), slots.get(i).getValueB()!!.toFloat()))
                }

            }
            for (i in 0 until count) {

                when (i) {
                    0, 5 -> values3.add(Entry(i.toFloat(), 0f))
                    else -> values3.add(Entry(i.toFloat(), slots.get(i).getValueC()!!.toFloat()))
                }

            }
        }


        var set2: LineDataSet? = null
        var set3: LineDataSet? = null
        var set1: LineDataSet? = null

        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {

            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            set2 = chart.data.getDataSetByIndex(1) as LineDataSet
            set2.values = values2
            set2.notifyDataSetChanged()
            set3 = chart.data.getDataSetByIndex(2) as LineDataSet
            set3.values = values3
            set3.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()

        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)
            set2 = LineDataSet(values2, "")
            set2.setDrawIcons(false)
            set3 = LineDataSet(values3, "")
            set3.setDrawIcons(false)

            // black lines and points
            set1.color = chart.context.getColor(R.color.purple_led_color)
            set2.color = chart.context.getColor(R.color.blue_selected_led_color)
            set3.color = chart.context.getColor(R.color.bar_white_color)

            // line thickness and point size
            set1.lineWidth = 2f
            set2.lineWidth = 2f
            set3.lineWidth = 2f

            // draw points as solid circles
            set1.setDrawCircleHole(false)
            set1.setDrawCircles(false)
            set2.setDrawCircleHole(false)
            set2.setDrawCircles(false)
            set3.setDrawCircleHole(false)
            set3.setDrawCircles(false)

            // text size of values
            set1.valueTextSize = 9f
            set2.valueTextSize = 9f
            set3.valueTextSize = 9f

            // set the filled area
            set1.setDrawFilled(false)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                chart.axisLeft.axisMinimum
            }

            // set the filled area
            set2.setDrawFilled(false)
            set2.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                chart.axisLeft.axisMinimum
            }

            // set the filled area
            set3.setDrawFilled(false)
            set3.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                chart.axisLeft.axisMinimum
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets
            dataSets.add(set2) // add the data sets
            dataSets.add(set3) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)
            data.setDrawValues(false)
            // set data
            chart.data = data
            chart.invalidate();
        }


    }

    fun connectToBleDevice(bleDevice: BleDevice, listener: BleConnectionListener) {
        BleManager.getInstance().connect(bleDevice.mac, object : BleGattCallback() {
            override fun onStartConnect() {
                Log.d(TAG, "onStartConnect: ")
                listener.onStartConnect()
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                Log.d(TAG, "onConnectFail: ")
                listener.onConnectFail(bleDevice, exception)
            }

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                Log.d(TAG, "onConnectSuccess: ")
                listener.onConnectSuccess(bleDevice, gatt, status)
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                device: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                Log.d(TAG, "onDisConnected: ")
                listener.onDisConnected(isActiveDisConnected, device, gatt, status)
            }

        })

    }

    open fun connectNotificationService(aquariumId: Int) {
        Log.d(TAG, "connectNotificationService: ")
        try {
            if (isMyServiceRunning()) {
                if (NotificationsService.socket != null && !NotificationsService.socket!!.connected()) {
                    NotificationsService.socket!!.connected()
                    Log.d(TAG, "connectNotificationService: connected: ")
                }
            } else {
                Log.d(TAG, "connectNotificationService: start: ")
                startService(
                    Intent(
                        this@BaseActivity,
                        NotificationsService::class.java
                    ).putExtra("aquariumID", aquariumId)
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    open fun stopServices() {
        Log.d(TAG, "stopServices: ")
        if (isMyServiceRunning()) {
            val intent = Intent(
                this@BaseActivity,
                NotificationsService::class.java
            )
            stopService(intent)
        }
    }

    open fun isMyServiceRunning(): Boolean {
        try {
            val manager: ActivityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (NotificationsService::class.java.name == service.service.getClassName()) {
                    return true
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return false
    }

    interface DeviceAddedPlaceCallback {
        fun onScanStarted(success: Boolean)
        fun onScanning(deviceAddedPlace: DeviceAddedPlace?)
        fun onScanFinish(isSuccess: Boolean)
    }

    fun scanningBleDevice(
        macAddress: String,
        listener: DeviceAddedPlaceCallback
    ) {
        var deviceAddedPlace: DeviceAddedPlace? = null
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                Log.d(TAG, "onScanStarted: $success")
                listener.onScanStarted(success)
            }

            override fun onScanning(bleDevice: BleDevice) {
                Log.d(TAG, "onScanning: " + bleDevice.name)
                if (bleDevice.name != null) {
                    if (bleDevice.name.contains("Dalua")) {
                        val macAddressList = bleDevice.name.split("-")
                        val realMac = macAddressList[macAddressList.size - 1].uppercase(Locale.ROOT)
                        Log.d(TAG, "onScanning: mac: $realMac : $macAddress")
                        if (macAddress.contentEquals(realMac)) {
                            if (BleManager.getInstance().scanSate.name.contentEquals("STATE_SCANNING")) {
                                BleManager.getInstance().cancelScan()
                            }
                            deviceAddedPlace = DeviceAddedPlace(bleDevice)
                            listener.onScanning(deviceAddedPlace)
                        }
                    }
                }
            }

            override fun onScanFinished(scanResultList: List<BleDevice>) {
                Log.d(TAG, "onScanFinished: " + scanResultList.size)
                if (deviceAddedPlace != null) {
                    listener.onScanFinish(true)
                } else {
                    listener.onScanFinish(false)
                }
            }
        })
    }
}