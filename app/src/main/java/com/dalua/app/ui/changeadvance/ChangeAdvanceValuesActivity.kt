package com.dalua.app.ui.changeadvance

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.services.iot.AWSIotClient
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.baseclasses.BaseApplication
import com.dalua.app.databinding.ActivityChangeAdvanceValuesBinding
import com.dalua.app.interfaces.AWSDialogInterface
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.models.ABCValuesModel
import com.dalua.app.models.Configuration
import com.dalua.app.ui.createschedule.CreateScheduleActivityVM
import com.dalua.app.ui.createschedule.adapters.MasterControlStateAdapter
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.Companion.DeviceMacAdress
import com.dalua.app.utils.AppConstants.Companion.DeviceOrGroupID
import com.dalua.app.utils.AppConstants.Companion.DeviceTopic
import com.dalua.app.utils.AppConstants.Companion.GroupTopic
import com.dalua.app.utils.AppConstants.Companion.ISGroupOrDevice
import com.dalua.app.utils.AppConstants.Companion.IsFromChangeValue
import com.dalua.app.utils.ColorTransparentUtils
import com.dalua.app.utils.GetGradientDrawable
import com.dalua.app.utils.ProjectUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class ChangeAdvanceValuesActivity : BaseActivity(), AWSDialogInterface {

    lateinit var chart: LineChart
    lateinit var binding: ActivityChangeAdvanceValuesBinding
    val viewModel: ChangeAdvanceValuesVM by viewModels()
    private val isOpenCamera: MutableLiveData<Boolean> = MutableLiveData(false)
    private var resendUploadSchedule: Boolean = false
    private val createScheduleActivityVM: CreateScheduleActivityVM by viewModels()
    var isReconnecting = false;
    var aValue = 25
    var bValue = 25
    var cValue = 25
    var pAValue = 0
    var pBValue = 0
    var pCValue = 0
    private lateinit var fragmentStateAdapter: MasterControlStateAdapter
    var isSendValues = true
    private var uniqueUserIdForThisPhone = ""
    var isPublish = false
    var credentials = MutableLiveData<CognitoCachingCredentialsProvider>()
    private var mqttManager: AWSIotMqttManager? = null
    lateinit var client: AWSIotClient
    var isInstantPlay = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObjects()
        initChartData()
        observers()
        listeners()
        apiResponse()

    }

    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_advance_values)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        Log.d(TAG, "initObjects: " + intent.getStringExtra("waterType"))
        viewModel.waterType.value = intent.getStringExtra("waterType")
        viewModel.configuration.value =
            ProjectUtil.stringToObject(
                intent.getStringExtra("configuration"),
                Configuration::class.java
            )
        fragmentStateAdapter = MasterControlStateAdapter(
            supportFragmentManager,
            lifecycle,
            viewModel.waterType.value!!,
            viewModel.configuration.value!!
        )
        uniqueUserIdForThisPhone = UUID.randomUUID().toString()

        myProgressDialog()
        IsFromChangeValue = true

        if (intent.hasExtra("a")) {
            createScheduleActivityVM.croller1Progress.value = intent.getStringExtra("a")!!.toInt()
            createScheduleActivityVM.croller2Progress.value = intent.getStringExtra("b")!!.toInt()
            createScheduleActivityVM.croller3Progress.value = intent.getStringExtra("c")!!.toInt()
        }
        if (intent.hasExtra("instant")) {
            isInstantPlay = intent.getBooleanExtra("instant", false)
            binding.saveButton.visibility = View.GONE
            binding.backButton.visibility = View.VISIBLE
        } else {
            binding.saveButton.visibility = View.VISIBLE
            binding.backButton.visibility = View.GONE
        }
        binding.viewPager.adapter = fragmentStateAdapter
        binding.viewPager.isUserInputEnabled = false

        awsConnection()
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
                        mqttManager = (application as BaseApplication).mqttManager!!
                        viewModel.isAwsConnected.value = true
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
            (application as BaseApplication).mqttManager = null
            if (mqttManager != null) {
                mqttManager?.disconnect()
            }
        }
    }

    private fun initChartData() {

        // // Chart Style // //
        chart = binding.chart1

        // background color
        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)
        chart.setDrawGridBackground(false)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
        val xAxis: XAxis = chart.xAxis
        xAxis.setDrawGridLines(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 25
        xAxis.gridColor = resources.getColor(R.color.graph_lines_color, theme)
        xAxis.labelRotationAngle = 45f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return GetGradientDrawable.isContainList()[value.toInt()].toString()
            }
        }
        val yAxis: YAxis = chart.axisLeft
        chart.axisRight.isEnabled = false
        yAxis.gridColor = resources.getColor(R.color.graph_lines_color, theme)
        // horizontal grid lines
        yAxis.setDrawGridLines(true)
        yAxis.labelCount = 10
        // axis range
        yAxis.axisMaximum = 1f
        yAxis.axisMinimum = 0f

        setData()
        chart.animateY(1500)
        val l: Legend = chart.legend
        l.form = LegendForm.NONE

    }

    private fun observers() {

        viewModel.camera.observe(this) {
            if (it) {
                isOpenCamera.value = true
                ImagePicker.with(this)
                    .compress(256)
                    .maxResultSize(420, 420)
                    .cameraOnly()
                    .burstMode(true)
                    .start()
            }
        }
        viewModel.isMasterControll.observe(this) {
            Log.d(TAG, "observers: master: $it")
            if (it) {
                binding.viewPager.setCurrentItem(1, true)
            }
        }
        viewModel.isInstantControll.observe(this) {
            Log.d(TAG, "observers: instant: $it")
            if (it) {
                binding.viewPager.setCurrentItem(0, true)
            }
        }

        createScheduleActivityVM.sendDataToBleNow.observe(this) {
            codeDataToAwsFormat()
        }

        createScheduleActivityVM.croller1Progress.observe(this) {
            setData()
            aValue = it

        }

        createScheduleActivityVM.croller2Progress.observe(this) {
            bValue = it
            setData()
        }

        createScheduleActivityVM.croller3Progress.observe(this) {
            cValue = it
            setData()
        }

        viewModel.sendIntentBack.observe(this) {

            if (!it)
                return@observe

            if (isInstantPlay) {
                if (resendUploadSchedule) {
                    isSendValues = false
                    if (ISGroupOrDevice.contentEquals("D"))
                        createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "device_id")
                    else
                        createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "group_id")
                } else {
                    sendIntentBack()
                }
            }

            awsDisconnect()

        }

    }

    private fun listeners() {

        binding.croller.setOnProgressChangedListener {
            aValue = it
            viewModel.aValue.value = it.toString()
            setValuesCroller1(it)
        }

        binding.croller1.setOnProgressChangedListener {
            bValue = it
            viewModel.bValue.value = it.toString()
            setValuesCroller2(it)
        }

        binding.croller2.setOnProgressChangedListener {
            cValue = it
            viewModel.cValue.value = it.toString()
            setValuesCroller3(it)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.

            }
        })

    }

    private fun apiResponse() {
        createScheduleActivityVM.apiResponse.observe(this) {
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
                                resendUploadSchedule = false
                                if (viewModel.sendIntentBack.value != false)
                                    sendIntentBack()
                                Log.d(TAG, "apiResponse: $res")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendIntentBack() {

        val mintent = Intent()
        mintent.putExtra("mdata", intent.getStringExtra("w"));
        mintent.putExtra(
            "data",
            ABCValuesModel(
                createScheduleActivityVM.croller1Progress.value.toString(),
                createScheduleActivityVM.croller2Progress.value.toString(),
                createScheduleActivityVM.croller3Progress.value.toString()
            )
        )
        setResult(Activity.RESULT_OK, mintent)
        finish()

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        if (resendUploadSchedule && !isOpenCamera.value!!) {
            isSendValues = false
            viewModel.sendIntentBack.value = false
            if (ISGroupOrDevice.contentEquals("D"))
                createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "device_id")
            else
                createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "group_id")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        if (resendUploadSchedule && !isOpenCamera.value!!) {
            isSendValues = false
            viewModel.sendIntentBack.value = false
            if (ISGroupOrDevice.contentEquals("D"))
                createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "device_id")
            else
                createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "group_id")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        viewModel.sendIntentBack.value = true
    }

    private fun codeDataToAwsFormat() {
        if (viewModel.isAwsConnected.value!!) {
            pAValue = aValue
            pBValue = bValue
            pCValue = cValue
            val jsonObject = JSONObject()
            jsonObject.put("commandID", "3")
            jsonObject.put("deviceID", "1")

            if (ISGroupOrDevice.contentEquals("D"))
                jsonObject.put("macAddress", DeviceMacAdress)// no colon + lower case
            else
                jsonObject.put("macAddress", "1")// no colon + lower case

            jsonObject.put("isGroup", true)// if specific device in group then false
            jsonObject.put("timestamp", System.currentTimeMillis().toString())
            jsonObject.put("a_value", cValue)
            jsonObject.put("b_value", bValue)
            jsonObject.put("c_value", aValue)


            val data = jsonObject.toString()
            if (ISGroupOrDevice.contentEquals("D"))
                publishToTopic(data, DeviceTopic)
            else
                publishToTopic(data, GroupTopic)
            Log.d(TAG, "a= $aValue b= $bValue c= $cValue ")
        } else {
            showAlertDialogWithListener(
                this,
                "Cloud connection failed",
                "We are trying to connect with cloud, Please try again after some time",
                "Ok",
                "",
                true,
                object : AlertDialogInterface {
                    override fun onPositiveClickListener(dialog: Dialog?) {
                        dialog?.dismiss()
                        onBackPressed()
                    }

                    override fun onNegativeClickListener(dialog: Dialog?) {

                    }
                }
            )
        }

    }

    private fun publishToTopic(data: String, topic: String) {

        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
            return

        if (!isInstantPlay)
            return

        isPublish = true
        try {
            mqttManager?.publishString(
                data,
                topic,
                AWSIotMqttQos.QOS0,
                { status, userData ->
                    runOnUiThread {
//                        publish_confirm_dialog.dismiss()
                        isPublish = false
                        resendUploadSchedule = true
                    }
                },
                "Check if data is given"
            )
            Log.d(TAG, "publishToTopic: ")
        } catch (e: java.lang.Exception) {
            publishToTopic(data, topic)
            Log.d(TAG, "Publish error." + e.localizedMessage)
        }

    }

    @SuppressLint("NewApi")
    private fun setData() {

        val set1: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = GetGradientDrawable.getLineDataSet(
                viewModel.waterType.value,
                createScheduleActivityVM.croller3Progress.value!!.toDouble(),
                createScheduleActivityVM.croller2Progress.value!!.toDouble(),
                createScheduleActivityVM.croller1Progress.value!!.toDouble()
            )

//            below line gradient
            setCirclesGradient(set1)
            setupGradient(chart)
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
            chart.invalidate()

        } else {

            // create a dataset and give it a type
            set1 = LineDataSet(
                GetGradientDrawable.getLineDataSet(
                    viewModel.waterType.value,
                    1.0,
                    1.0,
                    1.0
                ), ""
            )
            //            set1.setGradientColors(GetGradientDrawable.getLineDataSetGradientColors(this));
            set1.setDrawFilled(true)
            set1.color = Color.WHITE
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER

            // line thickness and point size
            set1.lineWidth = 2f
            set1.circleRadius = 1f
            val list: MutableList<Int> = ArrayList()
            for (color in GetGradientDrawable.colors) {
                list.add(Color.parseColor(color))
            }
            set1.setGradientColor(R.color.gradient_color_1, R.color.gradient_color_200)
            //            set1.setGradientColors(GetGradientDrawable.getLineDataSetGradientColors(this));

            // draw points as solid circles
            set1.setDrawCircles(false)

            // customize legend entry
            set1.formLineWidth = 1f
            //set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
//            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.fillFormatter =
                IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets

            // set gradient under the line
            setCirclesGradient(set1)

            // set gradient of the line
            setupGradient(chart)
            val data = LineData(dataSets)
            chart.data = data
            chart.invalidate()
        }
    }

    private fun setCirclesGradient(set1: LineDataSet) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            GetGradientDrawable.getGradientDrawable(this)
        )
        set1.fillDrawable = gradientDrawable
    }

    private fun setupGradient(mChart: LineChart) {
        val paint = mChart.renderer.paintRender
        val height = mChart.height
    }

    private fun setCirclesGradient(startColor: Int, endColor: Int): GradientDrawable {

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                endColor,
                startColor
            )
        )
        gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
        gradientDrawable.gradientRadius = 25f;
        gradientDrawable.shape = GradientDrawable.RECTANGLE;
        return gradientDrawable
    }

    private fun setValuesCroller3(it: Int) {

//        val k = ColorUtils.setAlphaComponent(Color.argb(255,255,255,255), abs(it * 2.5).toInt());// real color
        val k = ColorUtils.setAlphaComponent(Color.argb(255, 255, 255, 255), abs(it * 1.0).toInt())
        if (it < 100) {

            val s = Color.parseColor(
                ColorTransparentUtils.transparentColor(
                    Color.parseColor("#65FF00"),
                    it
                )
            )
            val s1 = Color.parseColor(
                ColorTransparentUtils.transparentColor(
                    Color.parseColor("#F1520B"),
                    it
                )
            )
            val s2 = Color.parseColor(
                ColorTransparentUtils.transparentColor(
                    Color.parseColor("#43a9ff"),
                    it
                )
            )
            val s3 = Color.parseColor(
                ColorTransparentUtils.transparentColor(
                    Color.parseColor("#FEF82F"),
                    it
                )
            )
            binding.gradientImage9.setImageDrawable(
                setCirclesGradient(
                    s,
                    Color.parseColor("#65FF00")
                )
            )
            binding.gradientImage10.setImageDrawable(
                setCirclesGradient(
                    s1,
                    Color.parseColor("#F1520B")
                )
            )
            binding.gradientImage11.setImageDrawable(
                setCirclesGradient(
                    s2,
                    Color.parseColor("#43a9ff")
                )
            )
            binding.gradientImage12.setImageDrawable(
                setCirclesGradient(
                    s3,
                    Color.parseColor("#FEF82F")
                )
            )

        }

        binding.image3.setColorFilter(k, PorterDuff.Mode.DARKEN)

    }

    private fun setValuesCroller1(it: Int) {

        val k = ColorUtils.setAlphaComponent(Color.argb(106, 102, 0, 204), abs(it * 2).toInt())
        binding.image1.setColorFilter(k, PorterDuff.Mode.ADD)

        if (it < 100) {
            val color = Color.parseColor("#8837F9")
            val s = Color.parseColor(ColorTransparentUtils.transparentColor(color, it))
            binding.gradientImage1.setImageDrawable(setCirclesGradient(s, color))
            binding.gradientImage2.setImageDrawable(setCirclesGradient(s, color))
            binding.gradientImage3.setImageDrawable(setCirclesGradient(s, color))
            binding.gradientImage4.setImageDrawable(setCirclesGradient(s, color))
        }

    }

    private fun setValuesCroller2(it: Int) {

//        val k = ColorUtils.setAlphaComponent(Color.argb(13,48,191,255), abs(it * 1.65).toInt()); real color

        val k = ColorUtils.setAlphaComponent(Color.argb(13, 48, 191, 255), abs(it * 1.1).toInt())
        binding.image2.setColorFilter(k, PorterDuff.Mode.ADD)
        if (it < 100) {
            val color = Color.parseColor("#0830bf")
            val s = Color.parseColor(ColorTransparentUtils.transparentColor(color, it))
            val s1 = Color.parseColor(
                ColorTransparentUtils.transparentColor(
                    Color.parseColor("#43a9ff"),
                    it
                )
            )

            binding.gradientImage5.setImageDrawable(setCirclesGradient(s, color))
            binding.gradientImage6.setImageDrawable(setCirclesGradient(s, color))
            binding.gradientImage7.setImageDrawable(setCirclesGradient(s, color))
            binding.gradientImage8.setImageDrawable(
                setCirclesGradient(
                    s1,
                    Color.parseColor("#43a9ff")
                )
            )

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                Log.d(TAG, "onActivityResult: ")
                isOpenCamera.value = false
                data?.data!!.let {
//                    viewModel.inputStream.value = contentResolver.openInputStream(it)
//                    viewModel.userImage.value = it.toString()
                    Log.d(TAG, "onActivityResult: " + it.toString())
                    ProjectUtil.showToastMessage(this, true, "Image saved")
                }
            }
            ImagePicker.RESULT_ERROR -> {
                isOpenCamera.value = false
                Log.d(TAG, "onActivityResult: " + ImagePicker.getError(data))
            }
            else -> {
                isOpenCamera.value = false
                Log.d(TAG, "onActivityResult: mage Not Picked: ")
            }
        }
    }

    companion object {
        const val TAG = "ChangeAdvanceValuesActivity"
    }

    override fun onCancel() {
        onBackPressed()
    }

}