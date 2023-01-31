package com.dalua.app.ui.previewscreen

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.LinearInterpolator
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivitySchedulePreviewBinding
import com.dalua.app.models.*
import com.dalua.app.models.schedulemodel.ScheduleResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.ui.listschedule.ScheduleListActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.Companion.DeviceOrGroupID
import com.dalua.app.utils.AppConstants.Companion.ISGroupOrDevice
import com.dalua.app.utils.AppConstants.Companion.RefreshSchedulePreview
import com.dalua.app.utils.MyFillFormatter
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
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time
import java.util.*

@AndroidEntryPoint
class SchedulePreviewActivity : BaseActivity() {

    lateinit var singleSchedule: SingleSchedule
    lateinit var binding: ActivitySchedulePreviewBinding
    var easyChart: LineChart? = null
    var advanceChart: LineChart? = null
    val viewModel: SchedulePreviewVM by viewModels()
    var aValue = 25
    var bValue = 25
    var graphPC = 20f
    var cValue = 25
    val listOfXValues: MutableList<Float> = mutableListOf()
    private var sunriseSec = 8 * 60
    var sunRise: String = "08:00"
    private var rampTime = 240
    var screenHeight = 0.0
    var threePC = 0.0
    var yellowTextWidth = 10.0
    private var sunsetSec = 17 * 60
    var isGoneAbove = false
    var chartWidth = 0.0
    var screenWidth = 0.0
    var ifCalled = false

    private val values = ArrayList<Entry>()
    private val values2 = ArrayList<Entry>()
    private val values3 = ArrayList<Entry>()

    private val deviceBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("DevicesList")) {
                val socketDevice = ProjectUtil.stringToObject(
                    intent.getStringExtra("DevicesList"),
                    SocketResponseModel::class.java
                )
                if (launchFrom == "group") {
                    Log.d(TAG, "onReceive: group mac: " + socketDevice.macAddress)
                    viewModel.group.value!!.devices.filter { it.macAddress == socketDevice.macAddress }
                        .forEach { it.status = socketDevice.status }
                } else if (launchFrom == "device") {
                    Log.d(TAG, "onReceive: device mac: " + socketDevice.macAddress)
                    if (socketDevice.macAddress == viewModel.device.value!!.macAddress)
                        viewModel.device.value =
                            viewModel.device.value!!.apply { status = socketDevice.status }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObjects()
        initChart()
        initChartA()
        observers()
        apiResponses()
//        setScheduleData()

    }

    private fun animateMoon(moonLight: Int?) {
        Log.d(TAG, "animateMoon: $moonLight")
        viewModel.moonLightEnable.value = moonLight == 1

//        there is need to be fixed
        if (screenHeight.toInt() == 0)
            return
        var animate: ObjectAnimator? = null
        if (moonLight == 1) {
            isGoneAbove = true
            animate = ObjectAnimator.ofFloat(
                binding.moonImageeiw2,
                "translationY",
                -screenHeight.toFloat() + threePC.toInt()
            ).apply {
                duration = 1000
                interpolator = LinearInterpolator()
                start()
            }
        } else if (isGoneAbove) {
            isGoneAbove = false
            binding.moonImageeiw2.animate().translationY(0f).duration = 1000
        }


    }

    private fun apiResponses() {

        viewModel.apiResponse.observe(this) {

            when (it) {
                is Resource.Error -> {
                    hideWorking()

                    when (it.api_Type) {
                        AppConstants.ApiTypes.GetMySchedules.name -> {

                        }
                    }

                }
                is Resource.Loading -> {
                    showWorking()
                }
                is Resource.Success -> {
                    hideWorking()
                    when (it.api_Type) {
                        AppConstants.ApiTypes.GetMySchedules.name -> {
                            it.data?.let { response ->

                                val daluaSchedule = ProjectUtil.stringToObject(
                                    response.string(),
                                    ScheduleResponse::class.java
                                )
                                val listSchedule = daluaSchedule.data.data
                                for (updSchedule in listSchedule) {

                                    if (ISGroupOrDevice.contentEquals("D")) {

                                        if (DeviceOrGroupID == updSchedule.deviceId && updSchedule.enabled!!.contentEquals(
                                                "1"
                                            )
                                        ) {
                                            singleSchedule = updSchedule
                                            Log.d(
                                                TAG,
                                                "apiResponses: " + ProjectUtil.objectToString(
                                                    updSchedule
                                                )
                                            )
                                            setScheduleData()
                                        }

                                    } else {

                                        if (DeviceOrGroupID == updSchedule.groupId && updSchedule.enabled!!.contentEquals(
                                                "1"
                                            )
                                        ) {
                                            singleSchedule = updSchedule
                                            Log.d(
                                                TAG,
                                                "apiResponses: " + ProjectUtil.objectToString(
                                                    updSchedule
                                                )
                                            )
                                            setScheduleData()
                                        }
                                    }
                                }

                            }

                        }
                    }

                }
            }

        }
    }

    private fun observers() {

        viewModel.backPressed.observe(this) {
            finish()
        }
    }

    private fun setScheduleData() {

//        binding.parentLayout.viewTreeObserver.addOnGlobalLayoutListener { //                binding.parentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
        screenWidth = binding.parentLayout.width.toDouble() //height is ready

        if (singleSchedule.mode!!.contentEquals("1")) {
            binding.one.visibility = View.GONE
            binding.two.visibility = View.GONE
            binding.three.visibility = View.GONE
            binding.four.visibility = View.GONE
            binding.five.visibility = View.GONE
            binding.six.visibility = View.GONE
            binding.seven.visibility = View.GONE
            binding.previewScheduleChartA.visibility = View.GONE
            binding.moonGraphImageView.visibility = View.GONE
            binding.moonImageeiw2.visibility = View.GONE
            setDefaultEasy()

        } else
            setAdvanceData()
//        }

        setWeatherData()

    }

    private fun setWeatherData() {
        if (singleSchedule.location != null && singleSchedule.geoLocation == 1) {
            viewModel.location.value = true
            viewModel.rain.value =
                singleSchedule.location.weatherData.current.weather[0].description
            viewModel.temperature.value = singleSchedule.location.weatherData.current.temp
            viewModel.name.value = singleSchedule.location.name
            viewModel.weatherImage.value = singleSchedule.location.weatherData.current.weather[0].id

        } else {
            viewModel.location.value = false
        }
    }

    private fun setDefaultEasy() {
        binding.linearLayout5.visibility = View.VISIBLE
        binding.sunsetLayout.visibility = View.VISIBLE
        binding.sunriseRampLayout.visibility = View.VISIBLE
        binding.linearLayout10.visibility = View.VISIBLE
        binding.previewScheduleChart.visibility = View.VISIBLE

        val rap = singleSchedule.easySlots?.getRampTime()!!.split(":")

        rampTime = when (rap.size) {
            2 -> rap[0].toInt() * 60 + rap[1].toInt()
            else -> rap[0].toInt() * 60
        }

        val xy = singleSchedule.easySlots?.getSunrise()!!.split(":")
        val yz = singleSchedule.easySlots?.getSunset()!!.split(":")
        viewModel.sunRise.value = xy[0] + ":" + xy[1]
        viewModel.sunSet.value = yz[0] + ":" + yz[1]

        sunriseSec = xy[0].toInt() * 60 + xy[1].toInt()
        sunsetSec = yz[0].toInt() * 60 + yz[1].toInt()

        var scurrtime = 0

        scurrtime = if ((sunriseSec + rampTime) >= 1440) {
            sunriseSec + rampTime - 1440
        } else
            sunriseSec + rampTime

        var shou = ""
        var smin = ""
        shou = if ((scurrtime / 60) < 10)
            "0" + (scurrtime / 60).toString()
        else
            (scurrtime / 60).toString()

        smin = if ((scurrtime % 60) < 10)
            "0" + (scurrtime % 60).toString()
        else
            (scurrtime % 60).toString()

        viewModel.rampStart.value = "$shou:$smin"

        val currentTime = yz[0].toInt() * 60 + yz[1].toInt()
        var currtime = 0

        currtime = if ((currentTime + rampTime) >= 1440) {
            currentTime + rampTime - 1440
        } else
            currentTime + rampTime

        val hr: String = if (currtime / 60 < 10)
            "0" + currtime / 60
        else
            (currtime / 60).toString()

        val mi: String = if (currtime % 60 < 10)
            "0" + currtime % 60
        else
            (currtime % 60).toString()

        viewModel.rampEnd.value = "$hr:$mi"
        viewModel.ramp.value = singleSchedule.easySlots.getRampTime()

        val x = singleSchedule.easySlots.getValueA()!!.toString() + ":" +
                singleSchedule.easySlots.getValueB()!!.toFloat() + ":" +
                singleSchedule.easySlots.getValueC()!!.toFloat()
        Log.d(TAG, "setDefaultEasy: $x")
        setData(
            singleSchedule.easySlots.getValueA()!!.toFloat(),
            singleSchedule.easySlots.getValueB()!!.toFloat(),
            singleSchedule.easySlots.getValueC()!!.toFloat()
        )

    }

    private fun setData(count: MutableList<AdvanceValuesModel>) {

        if (screenWidth.toInt() != 0) {
            graphPC = ((screenWidth * (AppConstants.screenPC2)) / 100).toFloat()
        }

        binding.previewScheduleChartA.visibility = View.VISIBLE
        binding.moonGraphImageView.visibility = View.VISIBLE
        binding.moonImageeiw2.visibility = View.VISIBLE
        binding.previewScheduleChart.visibility = View.GONE
        binding.linearLayout5.visibility = View.GONE
        binding.sunsetLayout.visibility = View.GONE
        binding.sunriseRampLayout.visibility = View.GONE
        binding.linearLayout10.visibility = View.GONE
        binding.textView3.text = singleSchedule.name
        binding.textView25.text = singleSchedule.name
        binding.easyYaxisLabels.visibility = View.GONE
        binding.advanceYaxisLabels.visibility = View.VISIBLE

        values.clear()
        values2.clear()
        values3.clear()

        if (listOfXValues.isNotEmpty())
            listOfXValues.clear()

        if (listOfXValues.isNotEmpty())
            listOfXValues.clear()

        val zeroST = viewModel.listOfTimeValues[0].startTime.split(":")
        val oneST = viewModel.listOfTimeValues[1].startTime.split(":")
        val twoST = viewModel.listOfTimeValues[2].startTime.split(":")
        val threeST = viewModel.listOfTimeValues[3].startTime.split(":")
        val fourST = viewModel.listOfTimeValues[4].startTime.split(":")
        val fiveST = viewModel.listOfTimeValues[5].startTime.split(":")


        val step2Difference = AppConstants.differenceTime(
            Time(oneST[0].toInt(), oneST[1].toInt(), 0),
            Time(zeroST[0].toInt(), zeroST[1].toInt(), 0)
        )
        val step3Difference = AppConstants.differenceTime(
            Time(twoST[0].toInt(), twoST[1].toInt(), 0),
            Time(oneST[0].toInt(), oneST[1].toInt(), 0)
        )
        val step4Difference = AppConstants.differenceTime(
            Time(threeST[0].toInt(), threeST[1].toInt(), 0),
            Time(twoST[0].toInt(), twoST[1].toInt(), 0)
        )
        val step5Difference = AppConstants.differenceTime(
            Time(fourST[0].toInt(), fourST[1].toInt(), 0),
            Time(threeST[0].toInt(), threeST[1].toInt(), 0)
        )
        val step6Difference = AppConstants.differenceTime(
            Time(fiveST[0].toInt(), fiveST[1].toInt(), 0),
            Time(fourST[0].toInt(), fourST[1].toInt(), 0)
        )

//        val step7Difference = AppConstants.differenceTime(
//            Time(zeroST[0].toInt(), zeroST[1].toInt(), 0),
//            Time(fiveST[0].toInt(), fiveST[1].toInt(), 0)
//        )

        listOfXValues.add(0f)

        listOfXValues.add(step2Difference.hours + getMinutes(step2Difference.minutes))
        listOfXValues.add(
            listOfXValues[1] + step3Difference.hours + getMinutes(
                step3Difference.minutes
            )
        )

        listOfXValues.add(
            listOfXValues[2] + step4Difference.hours + getMinutes(
                step4Difference.minutes
            )
        )

        listOfXValues.add(
            listOfXValues[3] + step5Difference.hours + getMinutes(
                step5Difference.minutes
            )
        )

        listOfXValues.add(
            listOfXValues[4] + step6Difference.hours + getMinutes(
                step6Difference.minutes
            )
        )

//        listOfXValues.add(
//            listOfXValues[5] + step7Difference.hours + getMinutes(
//                step7Difference.minutes
//            )
//        )

        val x = listOfXValues
        Log.d("dddddddd", "setData: ${x.size}")

        //step one
        values.add(Entry(listOfXValues[0], count[0].startValues.a.toFloat()))
        values2.add(Entry(listOfXValues[0], count[0].startValues.b.toFloat()))
        values3.add(Entry(listOfXValues[0], count[0].startValues.c.toFloat()))

        //two or three
        if (count[0].stepGradual == 1) {
            values.add(Entry(listOfXValues[1], count[1].startValues.a.toFloat()))
            values2.add(Entry(listOfXValues[1], count[1].startValues.b.toFloat()))
            values3.add(Entry(listOfXValues[1], count[1].startValues.c.toFloat()))
        } else {
            values.add(Entry(listOfXValues[1], count[0].startValues.a.toFloat()))
            values2.add(Entry(listOfXValues[1], count[0].startValues.b.toFloat()))
            values3.add(Entry(listOfXValues[1], count[0].startValues.c.toFloat()))

            values.add(
                Entry(
                    listOfXValues[1] + 0.01.toFloat(),
                    count[1].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[1] + 0.001.toFloat(),
                    count[1].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[1] + 0.001.toFloat(),
                    count[1].startValues.c.toFloat()
                )
            )

        }

        if (count[1].stepGradual == 1) {
            values.add(
                Entry(
                    listOfXValues[2] + 0.001.toFloat(),
                    count[2].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[2] + 0.001.toFloat(),
                    count[2].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[2] + 0.001.toFloat(),
                    count[2].startValues.c.toFloat()
                )
            )
        } else {
            values.add(
                Entry(
                    listOfXValues[2] + 0.001.toFloat(),
                    count[1].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[2] + 0.001.toFloat(),
                    count[1].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[2] + 0.001.toFloat(),
                    count[1].startValues.c.toFloat()
                )
            )

            values.add(
                Entry(
                    listOfXValues[2] + 0.002.toFloat(),
                    count[2].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[2] + 0.002.toFloat(),
                    count[2].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[2] + 0.002.toFloat(),
                    count[2].startValues.c.toFloat()
                )
            )

        }

        if (count[2].stepGradual == 1) {
            values.add(
                Entry(
                    listOfXValues[3] + 0.002.toFloat(),
                    count[3].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[3] + 0.002.toFloat(),
                    count[3].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[3] + 0.002.toFloat(),
                    count[3].startValues.c.toFloat()
                )
            )
        } else {
            values.add(
                Entry(
                    listOfXValues[3] + 0.002.toFloat(),
                    count[2].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[3] + 0.002.toFloat(),
                    count[2].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[3] + 0.002.toFloat(),
                    count[2].startValues.c.toFloat()
                )
            )

            values.add(
                Entry(
                    listOfXValues[3] + 0.003.toFloat(),
                    count[3].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[3] + 0.003.toFloat(),
                    count[3].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[3] + 0.003.toFloat(),
                    count[3].startValues.c.toFloat()
                )
            )

        }

        if (count[3].stepGradual == 1) {
            values.add(
                Entry(
                    listOfXValues[4] + 0.003.toFloat(),
                    count[4].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[4] + 0.003.toFloat(),
                    count[4].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[4] + 0.003.toFloat(),
                    count[4].startValues.c.toFloat()
                )
            )
        } else {
            values.add(
                Entry(
                    listOfXValues[4] + 0.003.toFloat(),
                    count[3].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[4] + 0.003.toFloat(),
                    count[3].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[4] + 0.003.toFloat(),
                    count[3].startValues.c.toFloat()
                )
            )

            values.add(
                Entry(
                    listOfXValues[4] + 0.004.toFloat(),
                    count[4].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[4] + 0.004.toFloat(),
                    count[4].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[4] + 0.004.toFloat(),
                    count[4].startValues.c.toFloat()
                )
            )

        }

        if (count[4].stepGradual == 1) {
            values.add(
                Entry(
                    listOfXValues[5] + 0.004.toFloat(),
                    count[5].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[5] + 0.004.toFloat(),
                    count[5].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[5] + 0.004.toFloat(),
                    count[5].startValues.c.toFloat()
                )
            )
        } else {
            values.add(
                Entry(
                    listOfXValues[5] + 0.004.toFloat(),
                    count[4].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[5] + 0.004.toFloat(),
                    count[4].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[5] + 0.004.toFloat(),
                    count[4].startValues.c.toFloat()
                )
            )

            values.add(
                Entry(
                    listOfXValues[5] + 0.005.toFloat(),
                    count[5].startValues.a.toFloat()
                )
            )
            values2.add(
                Entry(
                    listOfXValues[5] + 0.005.toFloat(),
                    count[5].startValues.b.toFloat()
                )
            )
            values3.add(
                Entry(
                    listOfXValues[5] + 0.005.toFloat(),
                    count[5].startValues.c.toFloat()
                )
            )

        }

        val location = IntArray(2)
        binding.previewScheduleChartA.getLocationOnScreen(location)


        val chartWidth = screenWidth - (0.14) * screenWidth
        val startedGap = 0.07 * screenWidth


        val kk = chartWidth.toFloat() / listOfXValues[listOfXValues.size - 1]
        hideAllTextViews()
        binding.one.visibility = View.VISIBLE
        binding.two.visibility = View.VISIBLE
        binding.three.visibility = View.VISIBLE
        binding.four.visibility = View.VISIBLE
        binding.five.visibility = View.VISIBLE
        binding.six.visibility = View.VISIBLE
        binding.seven.visibility = View.VISIBLE

        binding.one.text = viewModel.listOfTimeValues[0].startTime
        binding.two.text = viewModel.listOfTimeValues[1].startTime
        binding.three.text = viewModel.listOfTimeValues[2].startTime
        binding.four.text = viewModel.listOfTimeValues[3].startTime
        binding.five.text = viewModel.listOfTimeValues[4].startTime
        binding.six.text = viewModel.listOfTimeValues[5].startTime
        binding.seven.text = viewModel.listOfTimeValues[0].startTime
        val position: MutableList<Float> = mutableListOf()
//        binding.one.x = startedGap.toFloat() - graphPC

        position.add(binding.one.x)

        val distance1 =
            startedGap.toFloat() + kk * x[1] - graphPC - (startedGap.toFloat() - graphPC)
        var isDistance1Up = false

        if (distance1 < 35) {
            binding.two.x = startedGap.toFloat() + kk * x[1] - graphPC
            binding.two.y = binding.two.y - 20
            isDistance1Up = true

        } else
            binding.two.x = startedGap.toFloat() + kk * x[1] - graphPC

        position.add(binding.two.x)
        var isDistance2Up = false
        val distance2 =
            startedGap.toFloat() + kk * x[2] - graphPC - (startedGap.toFloat() + kk * x[1] - graphPC)
        if (distance2 < 35 && !isDistance1Up) {
            isDistance2Up = true
            binding.three.x = startedGap.toFloat() + kk * x[2] - graphPC - 5
            binding.three.y = binding.three.y - 20
        } else
            binding.three.x = startedGap.toFloat() + kk * x[2] - graphPC - 5
        position.add(binding.three.x)
        var isDistance3Up = false
        val distance3 =
            startedGap.toFloat() + kk * x[3] - graphPC - (startedGap.toFloat() + kk * x[2] - graphPC)
        if (distance3 < 35 && !isDistance2Up) {
            isDistance3Up = true
            binding.four.x = startedGap.toFloat() + kk * x[3] - graphPC - 10
            binding.four.y = binding.four.y - 20
        } else
            binding.four.x = startedGap.toFloat() + kk * x[3] - graphPC - 10
        position.add(binding.four.x)
        var isDistance4Up = false
        val distance4 =
            startedGap.toFloat() + kk * x[4] - graphPC - (startedGap.toFloat() + kk * x[3] - graphPC)
        if (distance4 < 35 && !isDistance3Up) {
            binding.five.x = startedGap.toFloat() + kk * x[4] - graphPC - 25
            isDistance4Up = true
            binding.five.y = binding.five.y - 20
        } else
            binding.five.x = startedGap.toFloat() + kk * x[4] - graphPC - 25
        position.add(binding.five.x)
        position.add(binding.six.x)

        animateMoon(singleSchedule.moonlightEnable)

        var set2: LineDataSet? = null
        var set3: LineDataSet? = null
        var set1: LineDataSet? = null

        if (advanceChart!!.data != null &&
            advanceChart!!.data.dataSetCount > 0
        ) {

            set1 = advanceChart!!.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            set2 = advanceChart!!.data.getDataSetByIndex(1) as LineDataSet
            set2.values = values2
            set2.notifyDataSetChanged()
            set3 = advanceChart!!.data.getDataSetByIndex(2) as LineDataSet
            set3.values = values3
            set3.notifyDataSetChanged()
            advanceChart!!.data.notifyDataChanged()
            advanceChart!!.notifyDataSetChanged()
            advanceChart!!.invalidate()

        } else {

            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)
            set2 = LineDataSet(values2, "")
            set2.setDrawIcons(false)
            set3 = LineDataSet(values3, "")
            set3.setDrawIcons(false)

            // black lines and points
            setGraphLineColors(advanceChart!!, set1, set2, set3)

            // line thickness and point size
            set1.fillFormatter = MyFillFormatter(set2)
            set2.fillFormatter = MyFillFormatter(set3)

//            set1.setGradientColor(R.color.purple_led_color, R.color.white)
//            set2.setGradientColor(R.color.blue_selected_led_color, R.color.white)

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
            set1.setDrawFilled(true)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                advanceChart!!.axisLeft.axisMinimum
            }
//
//            // set the filled area
            set2.setDrawFilled(true)
            set2.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                advanceChart!!.axisLeft.axisMinimum
            }

//            // set the filled area
            set3.setDrawFilled(true)
            set3.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                advanceChart!!.axisLeft.axisMinimum
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets
            dataSets.add(set2) // add the data sets
            dataSets.add(set3) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)
            data.setDrawValues(false)
            // set data
            advanceChart!!.data = data
            advanceChart!!.invalidate();
        }

//        valueAddedToTheGraph = true

    }

    private fun hideAllTextViews() {

        binding.one.visibility = View.GONE
        binding.two.visibility = View.GONE
        binding.three.visibility = View.GONE
        binding.four.visibility = View.GONE
        binding.five.visibility = View.GONE
        binding.six.visibility = View.GONE
        binding.seven.visibility = View.GONE

        binding.one.y = 140f
        binding.two.y = 140f
        binding.three.y = 140f
        binding.four.y = 140f
        binding.five.y = 140f
        binding.six.y = 140f
        binding.seven.y = 140f


    }

    private fun setAdvanceData() {

        viewModel.listOfValueAndStep.clear()
        viewModel.listOfTimeValues.clear()

        for (i in 0..5) {

            val slot = singleSchedule.slots[i]

            if (i < 5) {
                viewModel.listOfValueAndStep.add(
                    i,
                    AdvanceValuesModel(
                        ABCValuesModel(
                            slot.valueA.toString(),
                            slot.valueB.toString(),
                            slot.valueC.toString()
                        ), ABCValuesModel(
                            singleSchedule.slots[i + 1].valueA.toString(),
                            singleSchedule.slots[i + 1].valueB.toString(),
                            singleSchedule.slots[i + 1].valueC.toString()
                        ), slot.type.toInt()
                    )
                )

            } else {
                viewModel.listOfValueAndStep.add(
                    i,
                    AdvanceValuesModel(
                        ABCValuesModel(
                            slot.valueA.toString(),
                            slot.valueB.toString(),
                            slot.valueC.toString()
                        ), ABCValuesModel(
                            singleSchedule.slots[0].valueA.toString(),
                            singleSchedule.slots[0].valueB.toString(),
                            singleSchedule.slots[0].valueC.toString()
                        ), slot.type.toInt()
                    )
                )
            }

            if (i == 5) {
                val startTime = slot.startTime.split(":")
                val endTime = singleSchedule.slots[0].startTime.split(":")
                viewModel.listOfTimeValues.add(
                    AdvanceTimeModel(
                        startTime[0] + ":" + startTime[1],
                        endTime[0] + ":" + endTime[1]
                    )
                )
            } else {

                val startTime = slot.startTime.split(":")
                val endTime = singleSchedule.slots[i + 1].startTime.split(":")

                viewModel.listOfTimeValues.add(
                    AdvanceTimeModel(
                        startTime[0] + ":" + startTime[1],
                        endTime[0] + ":" + endTime[1]
                    )
                )
            }
        }




        setData(viewModel.listOfValueAndStep)
    }

    private fun getMinutes(minutes: Int): Float {
        var lastIndex = 0f

        when (minutes) {
            in 1..15 -> {
                lastIndex = 0.25f
            }
            in 16..30 -> {
                lastIndex = 0.5f
            }
            in 31..45 -> {

                lastIndex = 0.75f
            }
            in 46..59 -> {
                lastIndex = 1f
            }

        }
        return lastIndex

    }

    override fun onResume() {
        super.onResume()
        if (RefreshSchedulePreview) {
            viewModel.getMyScheduleApi(singleSchedule.deviceId, singleSchedule.groupId)
            RefreshSchedulePreview = false
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceBroadcastReceiver)
    }

    private fun setData(valueA: Float, valueB: Float, valueC: Float) {

        binding.textView3.text = singleSchedule.name
        binding.textView25.text = singleSchedule.name
        binding.easyYaxisLabels.visibility = View.VISIBLE
        binding.advanceYaxisLabels.visibility = View.GONE
        val k = viewModel.sunRise.value!!.split(":")
        val n = viewModel.rampEnd.value!!.split(":")
        val l = viewModel.sunSet.value!!.split(":")
        val m = viewModel.rampStart.value!!.split(":")


        if (listOfXValues.isNotEmpty())
            listOfXValues.clear()

        val sunSetAndSunRise =
            AppConstants.differenceTime(
                Time(l[0].toInt(), l[1].toInt(), 0),
                Time(k[0].toInt(), k[1].toInt(), 0)
            )
        val sunRiseAndRampStart =
            AppConstants.differenceTime(
                Time(m[0].toInt(), m[1].toInt(), 0),
                Time(k[0].toInt(), k[1].toInt(), 0)
            )
        val completeScheduleTime =
            AppConstants.differenceTime(
                Time(n[0].toInt(), n[1].toInt(), 0),
                Time(k[0].toInt(), k[1].toInt(), 0)
            )

        listOfXValues.add(0f)

        val srrM = getMinutes(sunRiseAndRampStart.minutes)

        listOfXValues.add(sunRiseAndRampStart.hours + srrM)

        val ssrM = getMinutes(sunSetAndSunRise.minutes)

        listOfXValues.add(sunSetAndSunRise.hours + ssrM)

        val lastIndex = getMinutes(completeScheduleTime.minutes)

        listOfXValues.add(completeScheduleTime.hours + lastIndex)

        var hasToRemove = false
        if (listOfXValues[1] == listOfXValues[2]) {
            hasToRemove = true
        }


        binding.previewScheduleChart.xAxis.setLabelCount(4, true)

        val values = ArrayList<Entry>()
        val values2 = ArrayList<Entry>()
        val values3 = ArrayList<Entry>()

        for (listOfXValue in 0 until listOfXValues.size) {

            if (listOfXValue == 0 || listOfXValue == listOfXValues.size - 1) {
                values.add(Entry(listOfXValues[listOfXValue], 0f))
                values2.add(Entry(listOfXValues[listOfXValue], 0f))
                values3.add(Entry(listOfXValues[listOfXValue], 0f))
            } else {
                values.add(Entry(listOfXValues[listOfXValue], valueA))
                values2.add(Entry(listOfXValues[listOfXValue], valueB))
                values3.add(Entry(listOfXValues[listOfXValue], valueC))
            }

        }

        if (hasToRemove) {
            binding.sunriseRampLayout.visibility = View.GONE
        } else {
            binding.sunriseRampLayout.visibility = View.VISIBLE
        }


        val location = IntArray(2)
        binding.previewScheduleChart.getLocationOnScreen(location)

        val screenWidth = binding.parentLayout.width

        val chartWidth = screenWidth - (0.14) * screenWidth
        val startedGap = 0.07 * screenWidth
        val singleValue = chartWidth / (listOfXValues[listOfXValues.size - 1])
        val sunRiseRampX = (startedGap + (singleValue * listOfXValues[1])).toFloat()
        val sunSetX = (startedGap + (singleValue * listOfXValues[2])).toFloat()




        binding.sunriseRampLayout.x = sunRiseRampX - 22f

        if (hasToRemove) {
            binding.sunsetLayout.x =
                startedGap.toFloat() + (chartWidth / 2).toFloat() - 22f
        } else
            binding.sunsetLayout.x = sunSetX - 22f


        var set2: LineDataSet? = null
        var set3: LineDataSet? = null
        var set1: LineDataSet? = null

        if (easyChart!!.data != null &&
            easyChart!!.data.dataSetCount > 0
        ) {

            set1 = easyChart!!.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set2 = easyChart!!.data.getDataSetByIndex(1) as LineDataSet
            set2.values = values2
            set3 = easyChart!!.data.getDataSetByIndex(2) as LineDataSet
            set3.values = values3

            set2.notifyDataSetChanged()
            set1.notifyDataSetChanged()
            set3.notifyDataSetChanged()
            easyChart!!.data.notifyDataChanged()
            easyChart!!.notifyDataSetChanged()
            easyChart!!.invalidate()

        } else {

            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)
            set2 = LineDataSet(values2, "")
            set2.setDrawIcons(false)
            set3 = LineDataSet(values3, "")
            set3.setDrawIcons(false)

            // black lines and points
            setGraphLineColors(easyChart!!, set1, set2, set3)

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

//            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                easyChart!!.axisLeft.axisMinimum
            }

//            // set the filled area
            set2.setDrawFilled(true)
            set2.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                easyChart!!.axisLeft.axisMinimum
            }

//            // set the filled area
            set3.setDrawFilled(true)
            set3.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                easyChart!!.axisLeft.axisMinimum
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets
            dataSets.add(set2) // add the data sets
            dataSets.add(set3) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)
            data.setDrawValues(false)
            // set data
            easyChart!!.data = data
            easyChart!!.invalidate();
        }

    }

    private fun setGraphLineColors(
        chart: LineChart,
        set1: LineDataSet,
        set2: LineDataSet,
        set3: LineDataSet
    ) {
        if (singleSchedule.waterType.lowercase(Locale.getDefault()) == "marine") {
            set1.color = chart.context.getColor(R.color.purple_led_color)
            set2.color = chart.context.getColor(R.color.blue_selected_led_color)
            set3.color = chart.context.getColor(R.color.bar_white_color)

            val drawable = ContextCompat.getDrawable(this, R.drawable.set1_gradient)
            set1.fillDrawable = drawable
            val drawable2 = ContextCompat.getDrawable(this, R.drawable.set2_gradient)
            set2.fillDrawable = drawable2
            val drawable3 = ContextCompat.getDrawable(this, R.drawable.set3_gradient)
            set3.fillDrawable = drawable3
        } else {
            set1.color = chart!!.context.getColor(R.color.red_led_color)
            set2.color = chart!!.context.getColor(R.color.green_led_color)
            set3.color = chart!!.context.getColor(R.color.yellow_led_color)

            val drawable = ContextCompat.getDrawable(this, R.drawable.set1_fresh_water_gradient)
            set1.fillDrawable = drawable
            val drawable2 = ContextCompat.getDrawable(this, R.drawable.set2_fresh_water_gradient)
            set2.fillDrawable = drawable2
            val drawable3 = ContextCompat.getDrawable(this, R.drawable.set3_fresh_water_gradient)
            set3.fillDrawable = drawable3
        }
    }

    private fun initChart() {

        // // Chart Style // //
        easyChart = binding.previewScheduleChart

        // background color
        easyChart!!.setBackgroundColor(Color.WHITE)

        // disable description text
        easyChart!!.description.isEnabled = false

        // enable touch gestures
        easyChart!!.setTouchEnabled(false)
        easyChart!!.minOffset = 0f
        // set listeners
        easyChart!!.setDrawGridBackground(false)

        // enable scaling and dragging
        easyChart!!.isDragEnabled = false
        easyChart!!.setScaleEnabled(false)

        // force pinch zoom along both axis
        easyChart!!.setPinchZoom(false)

        // // X-Axis Style // //
        val xAxis: XAxis = easyChart!!.xAxis
        xAxis.axisLineColor = Color.rgb(128, 128, 255);

        xAxis.setDrawGridLines(false)
        xAxis.gridLineWidth = 2f
        xAxis.gridColor = resources.getColor(R.color.white, theme)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setLabelCount(4, true)
        xAxis.setDrawLabels(false)
        xAxis.axisLineWidth = 0.1f

        // // Y-Axis Style // //
        val yAxis: YAxis = easyChart!!.axisLeft
        yAxis.gridLineWidth = 0.5f


        // disable dual axis (only use LEFT axis)
        easyChart!!.axisRight.isEnabled = false
        easyChart!!.axisLeft.isEnabled = false

        // axis range
        yAxis.axisMaximum = 130f
        yAxis.axisMinimum = 0f

        // Create Limit Lines //
        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        setData(0f, 0f, 0f)

        // draw points over time
        easyChart!!.animateX(1500, Easing.EaseInBack);
//        easyChart!!.animateX(1500, Easing.EaseInSine)
        // get the legend (only possible after setting data)
        easyChart!!.legend.isEnabled = false

    }

    private fun initChartA() {

        // // Chart Style // //
        advanceChart = binding.previewScheduleChartA

        // background color
        advanceChart!!.setBackgroundColor(Color.WHITE)

        // disable description text
        advanceChart!!.description.isEnabled = false

        // enable touch gestures
        advanceChart!!.setTouchEnabled(false)
        advanceChart!!.minOffset = 0f
        // set listeners
        advanceChart!!.setDrawGridBackground(false)

        // enable scaling and dragging
        advanceChart!!.isDragEnabled = false
        advanceChart!!.setScaleEnabled(false)

        // force pinch zoom along both axis
        advanceChart!!.setPinchZoom(false)

        // // X-Axis Style // //
        val xAxis: XAxis = advanceChart!!.xAxis
        xAxis.axisLineColor = Color.rgb(128, 128, 255);

        xAxis.setDrawGridLines(false)
        xAxis.gridLineWidth = 2f
        xAxis.gridColor = resources.getColor(R.color.white, theme)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setLabelCount(4, true)
        xAxis.setDrawLabels(false)
        xAxis.axisLineWidth = 0.1f

        // // Y-Axis Style // //
        val yAxis: YAxis = advanceChart!!.axisLeft
        yAxis.gridLineWidth = 0.5f

        // disable dual axis (only use LEFT axis)
        advanceChart!!.axisRight.isEnabled = false
        advanceChart!!.axisLeft.isEnabled = false

        // axis range
        yAxis.axisMaximum = 105f
        yAxis.axisMinimum = 0f

        // Create Limit Lines //
        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        setData(0f, 0f, 0f)

        // draw points over time
        advanceChart!!.animateX(1500, Easing.EaseInBack);
//        easyChart!!.animateX(1500, Easing.EaseInSine)
        // get the legend (only possible after setting data)
        advanceChart!!.legend.isEnabled = false

    }

    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_preview)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        getIntentData()
        viewModel.getMyScheduleApi(singleSchedule.deviceId, singleSchedule.groupId)
        myProgressDialog()
        if (singleSchedule.mode!!.contentEquals("2"))
            binding.parentLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
                OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.parentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    screenWidth = binding.parentLayout.width.toDouble() //height is ready
                    screenHeight =
                        binding.parentLayout.height.toDouble() * 0.25 //height is ready 0.21 is previous
                    threePC = binding.parentLayout.height.toDouble() * 0.1
                }
            })

        if (singleSchedule.mode!!.contentEquals("2"))
            binding.previewScheduleChart.viewTreeObserver.addOnGlobalLayoutListener(object :
                OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.previewScheduleChart.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    chartWidth = binding.previewScheduleChart.width.toDouble() //height is ready
                    if (!ifCalled) {
//                        setData(viewModel.listOfValueAndStep)
                        ifCalled = true
                    }
                }
            })

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceBroadcastReceiver, IntentFilter("AWSConnection"))

    }

    private fun getIntentData() {
        singleSchedule =
            ProjectUtil.stringToObject(intent.getStringExtra("sc"), SingleSchedule::class.java)
        singleSchedule.waterType = intent.getStringExtra("waterType")
        viewModel.configuration.value = ProjectUtil.stringToObject(
            intent.getStringExtra("configuration"),
            Configuration::class.java
        )
        if (launchFrom == "device") {
            viewModel.device.value =
                ProjectUtil.stringToObject(intent.getStringExtra("device"), Device::class.java)
        } else if (launchFrom == "group") {
            viewModel.group.value = ProjectUtil.stringToObject(
                intent.getStringExtra("group"),
                AquariumGroup::class.java
            )
        }
        if (intent.hasExtra("aquarium")) {
            viewModel.aquarium.value = ProjectUtil.stringToObject(
                intent.getStringExtra("aquarium"),
                SingleAquarium::class.java
            )
        }
    }

    fun goToListScheduleActivity(view: View) {
        if (launchFrom == "device") {
            ScheduleListActivity.launchedByDeviceDetail(
                this,
                viewModel.device.value!!,
                singleSchedule,
                singleSchedule.waterType,
                viewModel.configuration.value!!,
                viewModel.aquarium.value!!
            )
        } else if (launchFrom == "group") {
            ScheduleListActivity.launchedByGroupDetail(
                this,
                viewModel.group.value!!,
                singleSchedule,
                singleSchedule.waterType,
                viewModel.configuration.value!!,
                viewModel.aquarium.value!!
            )
        }
    }

    companion object {
        const val TAG = "SchedulePreviewActivity"
        var launchFrom = ""

        fun launchFromGroupDetail(
            context: Context,
            group: AquariumGroup,
            schedule: SingleSchedule,
            waterType: String,
            configuration: Configuration,
            singleAquarium: SingleAquarium
        ) {
            launchFrom = "group"
            context.startActivity(Intent(context, SchedulePreviewActivity::class.java).apply {
                putExtra("group", ProjectUtil.objectToString(group))
                putExtra("sc", ProjectUtil.objectToString(schedule))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
            })
        }

        fun launchFromDeviceDetail(
            context: Context,
            device: Device,
            schedule: SingleSchedule,
            waterType: String,
            configuration: Configuration,
            singleAquarium: SingleAquarium
        ) {
            launchFrom = "device"
            context.startActivity(Intent(context, SchedulePreviewActivity::class.java).apply {
                putExtra("device", ProjectUtil.objectToString(device))
                putExtra("sc", ProjectUtil.objectToString(schedule))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
            })
        }
    }

}