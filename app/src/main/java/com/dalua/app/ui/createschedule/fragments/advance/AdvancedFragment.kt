package com.dalua.app.ui.createschedule.fragments.advance

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseFragment
import com.dalua.app.databinding.FragmentAdvancedBinding
import com.dalua.app.models.*
import com.dalua.app.models.schedulemodel.ScheduleResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.ui.changeadvance.ChangeAdvanceValuesActivity
import com.dalua.app.ui.createschedule.CreateScheduleActivityVM
import com.dalua.app.ui.customDialogs.devicesListDialog.DevicesListDialog
import com.dalua.app.ui.geolocation.GeoLocationActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.ApiTypes.CreateSchedules
import com.dalua.app.utils.AppConstants.ApiTypes.CheckAckMacAddressApi
import com.dalua.app.utils.AppConstants.ApiTypes.UpdateSchedules
import com.dalua.app.utils.AppConstants.Companion.DeviceOrGroupID
import com.dalua.app.utils.AppConstants.Companion.GEOLOCATIONID
import com.dalua.app.utils.AppConstants.Companion.ISGroupOrDevice
import com.dalua.app.utils.AppConstants.Companion.IsEditOrPreviewOrCreate
import com.dalua.app.utils.AppConstants.Companion.IsFromChangeValue
import com.dalua.app.utils.AppConstants.Companion.differenceTime
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
import com.ozcanalasalvar.library.view.popup.TimePickerPopup
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.sql.Time
import java.util.*

@AndroidEntryPoint
class AdvancedFragment : BaseFragment(), DevicesListDialog.DevicesListCallback {

    companion object {
        const val TAG = "AdvancedFragment"
    }

    private var resendSchedule = false
    lateinit var binding: FragmentAdvancedBinding
    val viewModel: AdvanceFragmentVM by viewModels()
    var singleSchedule: SingleSchedule? = null
    private var isPublic = 0
    private var scheduleName = ""
    private var geoLocationId: Int? = null
    private val createScheduleActivityVM: CreateScheduleActivityVM by activityViewModels()
    private var isCancel = false
    private var graphPC = 20f
    private val listOfXValues: MutableList<Float> = mutableListOf()
    private var startTimeStep1 = 0
    var animate: ObjectAnimator? = null
    private var stepOrGradualValues = 0   //0=step, 1=gradual
    val values = ArrayList<Entry>()
    private val values2 = ArrayList<Entry>()
    private val values3 = ArrayList<Entry>()
    private var goToLocationActivity = 0
    private var secondNumber = 0
    private var isProgressReset = true
    var arrayList: MutableList<ABCValuesModel> = arrayListOf()
    var chartWidth = 0.0
    var screenWidth = 0.0
    var screenHeight = 0.0
    var threePC = 0.0
    var yellowTextWidth = 10.0
    var ifCalled = false
    private var isGoneAbove = false
    private var seventhStepWidth = 0f
    private var countDownTimer = object : CountDownTimer(15000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick: " + (millisUntilFinished / 1000))
            if (createScheduleActivityVM.launchFrom.value == "device") {
                createScheduleActivityVM.device.value!!.apply {
                    if (status == 1) {
                        cancel()
                        createScheduleActivityVM.device.value = this
                        createScheduleActivityVM.progress.value = false
                    }
                }
            } else if (createScheduleActivityVM.launchFrom.value == "group") {
                createScheduleActivityVM.group.value!!.apply {
                    if (createScheduleActivityVM.group.value!!.devices.size == devices.filter { it.status == 1 }.size) {
                        cancel()
                        createScheduleActivityVM.progress.value = false
                        createScheduleActivityVM.group.value =
                            createScheduleActivityVM.group.value.apply { devices = this!!.devices }
                    }
                }
            }
        }

        override fun onFinish() {
            Log.d(TAG, "onFinish: ")
            if (createScheduleActivityVM.launchFrom.value == "device") {
                viewModel.checkAckMacAddress(ArrayList<String>().apply {
                    add(createScheduleActivityVM.device.value!!.macAddress)
                })
                createScheduleActivityVM.progress.value = true
            } else if (createScheduleActivityVM.launchFrom.value == "group") {
                val macList: ArrayList<String> = ArrayList()
                createScheduleActivityVM.group.value!!.devices.forEach {
                    macList.add(it.macAddress)
                }
                Log.d(TAG, "onFinish: list: " + macList.size)
                viewModel.checkAckMacAddress(macList)
                createScheduleActivityVM.progress.value = true
            }
        }
    }

    override
    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_advanced, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.startTime1.value = true
        viewModel.endTime1.value = true
        myProgressDialog()
        viewModel.geoLocationList = ProjectUtil.getLocationObjects(requireContext())
        IsFromChangeValue = false
        binding.chart1.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                binding.chart1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                chartWidth = binding.chart1.width.toDouble() //height is ready
                if (!ifCalled) {
                    setData(viewModel.listOfValueAndStep)
                    ifCalled = true
                }
            }
        })

        binding.parentLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.parentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                screenWidth = binding.parentLayout.width.toDouble() //width is ready
                screenHeight = binding.parentLayout.height.toDouble() * 0.21 //height is ready
                threePC = binding.parentLayout.height.toDouble() * 0.1
                yellowTextWidth = 0.07 * screenWidth

                animateMoon("0")
            }
        })
        //added for back button
        if (!IsEditOrPreviewOrCreate.contentEquals("create")) {
            createScheduleActivityVM.killActivity = false
            createScheduleActivityVM.backPressed.value = true
        }
        if (requireActivity().intent.hasExtra("sc")) {
            goToLocationActivity = 0
            singleSchedule = ProjectUtil.stringToObject(
                requireActivity().intent.getStringExtra("sc"),
                SingleSchedule::class.java
            )

            if (singleSchedule?.mode.contentEquals("2")) {
                setDefaultScheduleData(singleSchedule!!)
                //\Changed this click button to view starts advance fragment from edit screen
//                viewModel.button1Clicked()
//                viewModel.button7Clicked()
            }

            if (singleSchedule!!.geoLocation == 1) {
                GEOLOCATIONID = singleSchedule!!.geoLocationId.toInt()
                geoLocationId = GEOLOCATIONID
                viewModel.stayOnEasyFragment = true

                for (datum in viewModel.geoLocationList.data) {
                    if (datum.id.toString().contentEquals(singleSchedule?.geoLocationId)) {
                        binding.geolocationtv.text = datum.name
                    }
                }

            } else {
                GEOLOCATIONID = 0
                binding.geolocationtv.text = "Geo Location"
                viewModel.stayOnEasyFragment = false
            }

            viewModel.moonLightEnable.value = singleSchedule!!.moonlightEnable == 1

        } else {
            goToLocationActivity = 1
            viewModel.listOfTimeValues.add(AdvanceTimeModel("08:00", "12:00"))
            viewModel.listOfTimeValues.add(AdvanceTimeModel("12:00", "16:00"))
            viewModel.listOfTimeValues.add(AdvanceTimeModel("16:00", "20:00"))
            viewModel.listOfTimeValues.add(AdvanceTimeModel("20:00", "00:00"))
            viewModel.listOfTimeValues.add(AdvanceTimeModel("00:00", "04:00"))
            viewModel.listOfTimeValues.add(AdvanceTimeModel("04:00", "08:00"))

            for (i in 0..5) {
                viewModel.listOfValueAndStep.add(
                    i,
                    AdvanceValuesModel(
                        ABCValuesModel(
                            "0",
                            "0",
                            "0"
                        ), ABCValuesModel(
                            "0",
                            "0",
                            "0"
                        ), 2
                    )
                )
            }

            viewModel.startTime1.value = true
            viewModel.startTime2.value = true
            viewModel.startTime3.value = true
            viewModel.startTime4.value = true
            viewModel.startTime5.value = true
            viewModel.startTime6.value = true
            viewModel.endTime1.value = true
            viewModel.endTime2.value = true
            viewModel.endTime3.value = true
            viewModel.endTime4.value = true
            viewModel.endTime5.value = true
            viewModel.endTime6.value = true
//            viewModel.button7Clicked()
//            viewModel.button1Clicked()
            GEOLOCATIONID = 0
            binding.geolocationtv.text = "Geo Location"
            viewModel.stayOnEasyFragment = false

        }
        return binding.root

    }

    private fun setDefaultScheduleData(singleSchedule: SingleSchedule) {
        viewModel.singleSchedule.value = singleSchedule

        createScheduleActivityVM.scheduleNameText.value = "Preview(" + singleSchedule.name + ")"
        scheduleName = singleSchedule.name
        viewModel.listOfValueAndStep.clear()
        viewModel.listOfTimeValues.clear()

        for (i in 0..5) {

            val slot = singleSchedule.slots[i]

            if (i < 5) {
                if (i == 4) {
                    viewModel.listOfValueAndStep.add(
                        i,
                        AdvanceValuesModel(
                            ABCValuesModel(
                                slot.valueA.toString(),
                                slot.valueB.toString(),
                                slot.valueC.toString()
                            ), ABCValuesModel(
                                "0",
                                "0",
                                "0"
                            ), slot.type.toInt()
                        )
                    )
                } else
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

        viewModel.startTime1.value = true
        viewModel.startTime2.value = true
        viewModel.startTime3.value = true
        viewModel.startTime4.value = true
        viewModel.startTime5.value = true
        viewModel.startTime6.value = true
        viewModel.endTime1.value = true
        viewModel.endTime2.value = true
        viewModel.endTime3.value = true
        viewModel.endTime4.value = true
        viewModel.endTime5.value = true
        viewModel.endTime6.value = true

    }

    private fun animateMoon(b: String?) {

        if (screenHeight.toInt() == 0)
            return
        var animate: ObjectAnimator? = null
        if (b.contentEquals("1")) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObject()
        initChart()
        observers()
        listeners()
        apiResponses()

    }

    private fun initObject() {
        //remove and rename update and upload button
        if (!IsEditOrPreviewOrCreate.contentEquals("create")) {

            if (ISGroupOrDevice.contentEquals("D")) {
                // device
                if (singleSchedule?.deviceId == DeviceOrGroupID) {
                    if (createScheduleActivityVM.scheduleType.value == 1) {
                        binding.saveButton.text = getString(R.string.update)
                        binding.saveAndUploadBtn.text = getString(R.string.update_and_upload)
                    } else {
                        binding.saveButton.text = getString(R.string.save)
                        binding.saveAndUploadBtn.text = getString(R.string.save_amp_upload)
                    }
                    if (singleSchedule!!.enabled!!.contentEquals("1")) {
                        viewModel.isDialogNameShow = false
                        binding.cardView11.visibility = View.GONE

                    } else {
                        binding.cardView11.visibility = View.VISIBLE
                        viewModel.isDialogNameShow = true
                    }


                } else {
                    if (createScheduleActivityVM.scheduleType.value == 1) {
                        binding.saveButton.text = getString(R.string.update)
                        binding.saveAndUploadBtn.text = getString(R.string.update_and_upload)
                    } else {
                        binding.saveButton.text = getString(R.string.save)
                        binding.saveAndUploadBtn.text = getString(R.string.save_amp_upload)
                    }
                    binding.cardView11.visibility = View.VISIBLE
                    viewModel.isDialogNameShow = true

                }

            } else {
                // group

                if (singleSchedule?.groupId == DeviceOrGroupID) {
                    if (createScheduleActivityVM.scheduleType.value == 1) {
                        binding.saveButton.text = getString(R.string.update)
                        binding.saveAndUploadBtn.text = getString(R.string.update_and_upload)
                    } else {
                        binding.saveButton.text = getString(R.string.save)
                        binding.saveAndUploadBtn.text = getString(R.string.save_amp_upload)
                    }
                    viewModel.isDialogNameShow = singleSchedule!!.enabled!!.contentEquals("1")

                    if (singleSchedule!!.enabled!!.contentEquals("1")) {
                        viewModel.isDialogNameShow = false
                        binding.cardView11.visibility = View.GONE

                    } else {
                        binding.cardView11.visibility = View.VISIBLE
                        viewModel.isDialogNameShow = true
                    }

                } else {
                    if (createScheduleActivityVM.scheduleType.value == 1) {
                        binding.saveButton.text = getString(R.string.update)
                        binding.saveAndUploadBtn.text = getString(R.string.update_and_upload)
                    } else {
                        binding.saveButton.text = getString(R.string.save)
                        binding.saveAndUploadBtn.text = getString(R.string.save_amp_upload)
                    }
                    binding.cardView11.visibility = View.VISIBLE
                    viewModel.isDialogNameShow = true
                }


            }
        } else {
            binding.cardView11.visibility = View.VISIBLE
            viewModel.isDialogNameShow = true
        }
        Log.d(TAG, "initObject: selectedTab: " + viewModel.selectedTab)

        viewModel.waterType.value = createScheduleActivityVM.waterType.value
        setLedColors()

    }

    private fun observers() {
        createScheduleActivityVM.socketResponseModel.observe(viewLifecycleOwner) {
            Log.d(TAG, "observers: aWSDeviceAck: " + ProjectUtil.objectToString(it))
            findMacAddress(it)
        }

        createScheduleActivityVM.saveClicked.observe(viewLifecycleOwner) {
            binding.nextTv.performClick()
        }

        viewModel.gradualButtonClicked.observe(viewLifecycleOwner) {
            stepOrGradualValues = when (it) {
                true -> 1
                else -> 2
            }

            when {
                viewModel.button1Clicked.value == true -> {
                    if (viewModel.selectedTab == 6)
                        viewModel.selectedTab = 0
                }
                viewModel.button2Clicked.value == true -> {
                    if (viewModel.selectedTab == 6)
                        viewModel.selectedTab = 1
                }
                viewModel.button3Clicked.value == true -> {
                    if (viewModel.selectedTab == 6)
                        viewModel.selectedTab = 2
                }
                viewModel.button4Clicked.value == true -> {
                    if (viewModel.selectedTab == 6)
                        viewModel.selectedTab = 3
                }
                viewModel.button5Clicked.value == true -> {
                    if (viewModel.selectedTab == 6)
                        viewModel.selectedTab = 4
                }
                viewModel.button6Clicked.value == true -> {
                    if (viewModel.selectedTab == 6)
                        viewModel.selectedTab = 5
                }
            }

            if (viewModel.selectedTab != 6)
                viewModel.listOfValueAndStep[viewModel.selectedTab].stepGradual =
                    stepOrGradualValues
            setData(viewModel.listOfValueAndStep)

        }

        viewModel.changeActivity.observe(viewLifecycleOwner) {
            if (it) {
                startActivityForResult(
                    Intent(requireContext(), ChangeAdvanceValuesActivity::class.java).apply {
                        putExtra("w", "start")
                            .putExtra("a", binding.startAv.text.toString())
                            .putExtra("b", binding.startBv.text.toString())
                            .putExtra("c", binding.startCv.text.toString())
                            .putExtra("waterType", createScheduleActivityVM.waterType.value)
                            .putExtra(
                                "configuration",
                                ProjectUtil.objectToString(createScheduleActivityVM.configuration.value)
                            )
                    }, 1221
                )
            } else {
                if (viewModel.selectedTab == 4)
                    return@observe
                startActivityForResult(
                    Intent(requireContext(), ChangeAdvanceValuesActivity::class.java).apply {
                        putExtra("w", "end")
                            .putExtra("a", binding.endAv.text.toString())
                            .putExtra("b", binding.endBv.text.toString())
                            .putExtra("c", binding.endCv.text.toString())
                            .putExtra("waterType", createScheduleActivityVM.waterType.value)
                            .putExtra(
                                "configuration",
                                ProjectUtil.objectToString(createScheduleActivityVM.configuration.value)
                            )
                    }, 1221
                )
            }
        }

        viewModel.showDialog.observe(viewLifecycleOwner) {
            if (createScheduleActivityVM.launchFrom.value == "device") {
                if (createScheduleActivityVM.device.value!!.status == 0 || createScheduleActivityVM.device.value!!.status == 2) {
                    if (it) {
                        stopAnimation()
                    }
                    DevicesListDialog.launchActivityForDevice(
                        createScheduleActivityVM.aquarium.value!!,
                        requireContext(),
                        createScheduleActivityVM,
                        this
                    )
                } else {
                    showCreateDialog()
                }
            } else if (createScheduleActivityVM.launchFrom.value == "group") {
                if (createScheduleActivityVM.isAllGroupDevicesConnect.value!!) {
                    showCreateDialog()
                } else {
                    if (it) {
                        stopAnimation()
                    }
                    DevicesListDialog.launchActivityForGroup(
                        createScheduleActivityVM.aquarium.value!!,
                        requireContext(),
                        createScheduleActivityVM,
                        this
                    )
                }
            }
        }

        viewModel.startTimeDialog.observe(viewLifecycleOwner) {
            startTimeDialog()
        }

        viewModel.moonLightEnable.observe(viewLifecycleOwner) {
            Log.d(TAG, "observers: moon light: $it")
            if (it)
                animateMoon("1")
            else
                animateMoon("0")

        }

        viewModel.showMessage.observe(viewLifecycleOwner) {
            showMessage(it, true)
        }

        viewModel.endTimeDialog.observe(viewLifecycleOwner) {
            endTimeDialog()
        }

        viewModel.animationStarted.observe(viewLifecycleOwner) {
            if (!it) {
                binding.startAnimation.visibility = View.VISIBLE
                binding.stopAnimation.visibility = View.GONE
            } else {
                binding.startAnimation.visibility = View.GONE
                binding.stopAnimation.visibility = View.VISIBLE
            }
        }

        viewModel.quickPlay.observe(viewLifecycleOwner) {
            if (viewModel.animationStarted.value!!) {
                stopAnimation()
            } else {
                startQuickPlayApi()
            }
        }

    }

    private fun showCreateDialog() {
        if (IsEditOrPreviewOrCreate.contentEquals("create")) {
            showSaveDialog()
        } else {
            if (ISGroupOrDevice.contentEquals("D")) {
                if (singleSchedule!!.deviceId == DeviceOrGroupID)
                    callRespectiveApi(viewModel.saveAndUpload)
                else
                    showSaveDialog()
            } else {
                if (singleSchedule!!.groupId == DeviceOrGroupID)
                    callRespectiveApi(viewModel.saveAndUpload)
                else
                    showSaveDialog()
            }
        }
    }

    private fun listeners() {

        binding.previousTv.setOnClickListener {
            when (viewModel.selectedTab) {
                0 -> viewModel.button7Clicked()
                1 -> viewModel.button1Clicked()
                2 -> viewModel.button2Clicked()
                3 -> viewModel.button3Clicked()
                4 -> viewModel.button4Clicked()
                5 -> viewModel.button5Clicked()
                6 -> viewModel.button6Clicked()
            }
        }

        binding.nextTv.setOnClickListener {
            when (viewModel.selectedTab) {
                0 -> viewModel.button2Clicked()
                1 -> viewModel.button3Clicked()
                2 -> viewModel.button4Clicked()
                3 -> viewModel.button5Clicked()
                4 -> viewModel.button6Clicked()
                5 -> viewModel.button7Clicked()
                6 -> viewModel.button1Clicked()
            }
        }

        viewModel.apply {

            finalStepButton.observe(viewLifecycleOwner) {
                selectedTab = 6
                changeToPreviewTV()
                switchVisibility(it)
            }

            button1Clicked.observe(viewLifecycleOwner) {
                if (it) {
                    selectedTab = 0
                    if (finalStepButton.value == false)
                        finalStepButton.value = true
                    setCurrentValuesOfCurrentStep(0)
                    changeToEditTV()
                    switchVisibility(true)
                }
            }

            button2Clicked.observe(viewLifecycleOwner) {
                Log.d(TAG, "listeners: $it")
                if (it) {
                    selectedTab = 1
                    if (finalStepButton.value == false)
                        finalStepButton.value = true
                    setCurrentValuesOfCurrentStep(1)
                    switchVisibility(true)
                    changeToEditTV()
                }
            }

            button3Clicked.observe(viewLifecycleOwner) {
                if (it) {
                    selectedTab = 2
                    if (finalStepButton.value == false)
                        finalStepButton.value = true
                    setCurrentValuesOfCurrentStep(2)
                    switchVisibility(true)
                    changeToEditTV()
                }
            }

            button4Clicked.observe(viewLifecycleOwner) {
                if (it) {
                    selectedTab = 3
                    if (finalStepButton.value == false)
                        finalStepButton.value = true
                    setCurrentValuesOfCurrentStep(3)
                    switchVisibility(true)
                    changeToEditTV()
                }
            }

            button5Clicked.observe(viewLifecycleOwner) {
                if (it) {
                    selectedTab = 4
                    if (finalStepButton.value == false)
                        finalStepButton.value = true
                    setCurrentValuesOfCurrentStep(4)
                    switchVisibility(true)
                    changeToEditTV()
                }
            }

            button6Clicked.observe(viewLifecycleOwner) {
                if (it) {
                    selectedTab = 5
                    if (finalStepButton.value == false)
                        finalStepButton.value = true
                    step6Visibility()
                    setCurrentValuesOfCurrentStep(5)
                    changeToEditTV()
                }
            }

            button7Clicked.observe(viewLifecycleOwner) {
                if (it) {
                    setData(viewModel.listOfValueAndStep)
                }
            }

            geoLocationEnabled.observe(viewLifecycleOwner) {

                if (it) {
                    GeoLocationActivity.launchGeoLocationActivity(
                        requireContext(),
                        Intent(requireContext(), GeoLocationActivity::class.java)
                    )
                } else {
                    GEOLOCATIONID = 0
                    geoLocationId = null
                }
            }

        }
    }

    private fun apiResponses() {

        viewModel.apiResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Error -> {
                    hideWorking()
                    when (it.api_Type) {
                        CreateSchedules.name,
                        UpdateSchedules.name -> {
                            hideWorking()
                            showMessage(it.error, false)
                            Log.d(TAG, "ResponseError: " + it.error + " \n type: " + it.api_Type)
                        }
                        CheckAckMacAddressApi.name -> {
                            Log.d(TAG, "apiResponses: check ack: false: ")
                            createScheduleActivityVM.progress.value = false
                        }
                    }

                }
                is Resource.Loading -> {
                    if (viewModel.saveAndUpload == 0) {
                        showWorking()
                    }
                }
                is Resource.Success -> {
                    hideWorking()

                    when (it.api_Type) {

                        UpdateSchedules.name -> {
                            it.data?.let { res ->
                                val listDevicesResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    ScheduleResponse::class.java
                                )
                                AppConstants.apply {
                                    UpdateDaluaSchedule = true
                                    UpdateMySchedule = true
                                    UpdatePublicSchedule = true
                                    RefreshSchedulePreview = true
                                }
                                showMessage(
                                    listDevicesResponse.message,
                                    listDevicesResponse.success
                                )
                                if (viewModel.saveAndUpload == 0) {
                                    Handler(Looper.getMainLooper()).postDelayed(
                                        { requireActivity().finish() },
                                        500
                                    )
                                }
                            }
                        }
                        CreateSchedules.name -> {
                            it.data?.let { res ->
                                val createScheduleResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    CreateScheduleResponse::class.java
                                )
                                AppConstants.apply {
                                    UpdateDaluaSchedule = true
                                    UpdateMySchedule = true
                                    UpdatePublicSchedule = true
                                    RefreshSchedulePreview = true
                                }
                                showMessage(
                                    createScheduleResponse.message,
                                    createScheduleResponse.success
                                )
                                singleSchedule = createScheduleResponse.data
                                viewModel.singleSchedule.value = createScheduleResponse.data
                                if (viewModel.saveAndUpload == 0) {
                                    Handler(Looper.getMainLooper()).postDelayed(
                                        { requireActivity().finish() },
                                        500
                                    )
                                }
                            }
                        }
                        CheckAckMacAddressApi.name -> {
                            it.data?.let { res ->
                                ProjectUtil.stringToObject(
                                    res.string(),
                                    CheckAckMacAddressResponse::class.java
                                ).apply {
                                    this.data.forEach { findAckMacAddress(it) }
                                }
                            }
                        }
                    }

                }
            }
        }

    }

    private fun findMacAddress(awsDeviceAck: SocketACKResponseModel?) {
        Log.d(TAG, "findMacAddress: " + awsDeviceAck!!.macAddress)
        if (createScheduleActivityVM.launchFrom.value == "device") {
            if (createScheduleActivityVM.device.value!!.macAddress == awsDeviceAck.macAddress && awsDeviceAck.status == 1) {
                createScheduleActivityVM.device.value =
                    createScheduleActivityVM.device.value!!.apply {
                        status = 1
                    }
                countDownTimer.cancel()
            }
            createScheduleActivityVM.progress.value = false
        } else if (createScheduleActivityVM.launchFrom.value == "group") {
            createScheduleActivityVM.group.value = createScheduleActivityVM.group.value!!.apply {
                devices.filter {
                    countDownTimer.cancel()
                    countDownTimer.start()
                    it.macAddress == awsDeviceAck.macAddress && awsDeviceAck.status == 1
                }.forEach {
                    it.status = 1
                }
            }
            createScheduleActivityVM.progress.value = false
        }
    }

    private fun findAckMacAddress(socketAckResponse: SocketACKResponseModel?) {
        Log.d(TAG, "findMacAddress: " + socketAckResponse!!.macAddress)
        if (createScheduleActivityVM.launchFrom.value == "device") {
            if (createScheduleActivityVM.device.value!!.macAddress == socketAckResponse.macAddress) {
                createScheduleActivityVM.device.value =
                    createScheduleActivityVM.device.value!!.apply {
                        status = socketAckResponse.status
                    }
            }
            createScheduleActivityVM.progress.value = false
        } else if (createScheduleActivityVM.launchFrom.value == "group") {
            createScheduleActivityVM.group.value = createScheduleActivityVM.group.value!!.apply {
                devices.filter {
                    it.macAddress == socketAckResponse.macAddress
                }.forEach {
                    it.status = socketAckResponse.status
                }
            }
            createScheduleActivityVM.progress.value = false
        }
    }

    private fun showDialogForAckReceiving() {
        createScheduleActivityVM.isDialogOpen.value = true
        createScheduleActivityVM.resendSchedule.value = false
        countDownTimer.start()
        createScheduleActivityVM.progress.value = true
        if (createScheduleActivityVM.launchFrom.value == "device") {
            createScheduleActivityVM.device.value!!.apply { status = -1 }
            DevicesListDialog.launchActivityForDeviceAck(
                createScheduleActivityVM.aquarium.value!!,
                requireContext(),
                createScheduleActivityVM,
                this
            )
        } else if (createScheduleActivityVM.launchFrom.value == "group") {
            createScheduleActivityVM.group.value!!.apply { devices.forEach { it.status = -1 } }
            DevicesListDialog.launchActivityForGroupAck(
                createScheduleActivityVM.aquarium.value!!,
                requireContext(),
                createScheduleActivityVM,
                this
            )
        }
    }

    private fun setLedColors() {
        if (createScheduleActivityVM.waterType.value!!.lowercase(Locale.getDefault()) == "marine") {
            binding.intervalBar.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.yellow_gradient,
                    null
                )
            )
            binding.intervalBarBottom.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.yellow_gradient_above,
                    null
                )
            )
            binding.ledImage1.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_puple_db,
                    null
                )
            )
            binding.ledImage2.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_blue_db,
                    null
                )
            )
            binding.ledImage3.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_white_db,
                    null
                )
            )
            binding.ledImage4.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_puple_db,
                    null
                )
            )
            binding.ledImage5.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_blue_db,
                    null
                )
            )
            binding.ledImage6.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_white_db,
                    null
                )
            )
        } else {
            binding.intervalBar.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.gray_gradient,
                    null
                )
            )
            binding.intervalBarBottom.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.gray_gradient_above,
                    null
                )
            )
            binding.ledImage1.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_red_db,
                    null
                )
            )
            binding.ledImage2.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_green_db,
                    null
                )
            )
            binding.ledImage3.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_yellow_db,
                    null
                )
            )
            binding.ledImage4.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_red_db,
                    null
                )
            )
            binding.ledImage5.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_green_db,
                    null
                )
            )
            binding.ledImage6.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.circle_yellow_db,
                    null
                )
            )
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        stopAnimation()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    private fun disableButtonsOnQuickPlay(enable: Boolean) {
        binding.cardView11.isEnabled = enable
        binding.cardView12.isEnabled = enable
        binding.saveAndUploadBtn.isEnabled = enable
        binding.geolocationSwitch.isClickable = false
        if (enable) {
            binding.cardView11.setCardBackgroundColor(requireContext().getColor(R.color.dark_blue_color))
            binding.cardView12.setCardBackgroundColor(requireContext().getColor(R.color.white))
            binding.saveAndUploadBtn.setBackgroundResource(R.drawable.btn_blu_white_txt)
        } else {
            binding.cardView11.setCardBackgroundColor(requireContext().getColor(R.color.dark_gray_color))
            binding.cardView12.setCardBackgroundColor(requireContext().getColor(R.color.dark_gray_color))
            binding.saveAndUploadBtn.setBackgroundResource(R.drawable.btn_gray_white_txt)
        }
    }

    private fun startQuickPlayApi() {

        arrayList = mutableListOf()

        if (createScheduleActivityVM.isAwsConnected.value!!) {
            disableButtonsOnQuickPlay(false)
            viewModel.apply {

                if (viewModel.listOfValueAndStep.isNotEmpty() || viewModel.listOfTimeValues.isNotEmpty()) {

                    val zeroST = viewModel.listOfTimeValues[0].startTime.split(":")
                    val oneST = viewModel.listOfTimeValues[1].startTime.split(":")
                    val twoST = viewModel.listOfTimeValues[2].startTime.split(":")
                    val threeST = viewModel.listOfTimeValues[3].startTime.split(":")
                    val fourST = viewModel.listOfTimeValues[4].startTime.split(":")
                    val fiveST = viewModel.listOfTimeValues[5].startTime.split(":")


                    val step2Difference = differenceTime(
                        Time(oneST[0].toInt(), oneST[1].toInt(), 0),
                        Time(zeroST[0].toInt(), zeroST[1].toInt(), 0)
                    )
                    val step3Difference = differenceTime(
                        Time(twoST[0].toInt(), twoST[1].toInt(), 0),
                        Time(oneST[0].toInt(), oneST[1].toInt(), 0)
                    )
                    val step4Difference = differenceTime(
                        Time(threeST[0].toInt(), threeST[1].toInt(), 0),
                        Time(twoST[0].toInt(), twoST[1].toInt(), 0)
                    )
                    val step5Difference = differenceTime(
                        Time(fourST[0].toInt(), fourST[1].toInt(), 0),
                        Time(threeST[0].toInt(), threeST[1].toInt(), 0)
                    )
                    val step6Difference = differenceTime(
                        Time(fiveST[0].toInt(), fiveST[1].toInt(), 0),
                        Time(fourST[0].toInt(), fourST[1].toInt(), 0)
                    )

                    val step7Difference = differenceTime(
                        Time(zeroST[0].toInt(), zeroST[1].toInt(), 0),
                        Time(fiveST[0].toInt(), fiveST[1].toInt(), 0)
                    )

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
                    listOfXValues.add(
                        listOfXValues[5] + step7Difference.hours + getMinutes(
                            step7Difference.minutes
                        )
                    )

                    val listNumberOfSteps: MutableList<Float> = mutableListOf()
                    listNumberOfSteps.add(listOfXValues[1] - listOfXValues[0])
                    listNumberOfSteps.add(listOfXValues[2] - listOfXValues[1])
                    listNumberOfSteps.add(listOfXValues[3] - listOfXValues[2])
                    listNumberOfSteps.add(listOfXValues[4] - listOfXValues[3])
                    listNumberOfSteps.add(listOfXValues[5] - listOfXValues[4])
                    listNumberOfSteps.add(listOfXValues[6] - listOfXValues[5])

                    for (stp in 0 until listNumberOfSteps.size) {

                        var avalue = listOfValueAndStep[stp].startValues.a.toInt()
                        var bvalue = listOfValueAndStep[stp].startValues.b.toInt()
                        var cvalue = listOfValueAndStep[stp].startValues.c.toInt()

                        for (i in 0 until listNumberOfSteps[stp].toInt()) {

                            if (listOfValueAndStep[stp].stepGradual == 1) {

                                //gradual

                                if (stp == listNumberOfSteps.size - 1) {

                                    val a =
                                        (listOfValueAndStep[0].startValues.a.toInt() - listOfValueAndStep[stp].startValues.a.toInt()) / listNumberOfSteps[stp].toInt()
                                    val b =
                                        (listOfValueAndStep[0].startValues.b.toInt() - listOfValueAndStep[stp].startValues.b.toInt()) / listNumberOfSteps[stp].toInt()
                                    val c =
                                        (listOfValueAndStep[0].startValues.c.toInt() - listOfValueAndStep[stp].startValues.c.toInt()) / listNumberOfSteps[stp].toInt()

                                    avalue += a
                                    bvalue += b
                                    cvalue += c

                                } else {

                                    val a =
                                        (listOfValueAndStep[stp + 1].startValues.a.toInt() - listOfValueAndStep[stp].startValues.a.toInt()) / listNumberOfSteps[stp].toInt()
                                    val b =
                                        (listOfValueAndStep[stp + 1].startValues.b.toInt() - listOfValueAndStep[stp].startValues.b.toInt()) / listNumberOfSteps[stp].toInt()
                                    val c =
                                        (listOfValueAndStep[stp + 1].startValues.c.toInt() - listOfValueAndStep[stp].startValues.c.toInt()) / listNumberOfSteps[stp].toInt()

                                    avalue += a
                                    bvalue += b
                                    cvalue += c

                                }

                                arrayList.add(
                                    ABCValuesModel(
                                        avalue.toString(),
                                        bvalue.toString(),
                                        cvalue.toString()
                                    )
                                )

                            } else {

                                //step

                                arrayList.add(
                                    ABCValuesModel(
                                        listOfValueAndStep[stp].startValues.a,
                                        listOfValueAndStep[stp].startValues.b,
                                        listOfValueAndStep[stp].startValues.c
                                    )
                                )

                            }
                        }

                    }

                    arrayList.add(
                        ABCValuesModel(
                            "0",
                            "0",
                            "0"
                        )
                    )
                    val kha = arrayList

//                val moonLightDuration=24-arrayList.size
//                for (i in 1 .. moonLightDuration) {
//                    arrayList.add(
//                        ABCValuesModel(
//                            "0",
//                            "0",
//                            "0"
//                        )
//                    )
//                }

                    for (abcValuesModel in kha) {
                        Log.d(
                            TAG,
                            "a: ${abcValuesModel.a} b: ${abcValuesModel.b} c: ${abcValuesModel.c}"
                        )
                    }

                } else {
                    showMessage("Create a valid Schedule", false)
                    return
                }

//            startAnimation((24000 + 2000).toLong())
//            binding.fillL.setDuration((24 * 1000 + 2000).toLong())
//            startAnimation((24000).toLong())
//            binding.fillL.setDuration((24 * 1000).toLong())
                startAnimation((arrayList.size * 1000).toLong())
                binding.fillL.setDuration((arrayList.size * 1000).toLong())
                binding.fillL.setProgress(100, true)
                binding.fillL.setAnimationInterpolator(LinearInterpolator())
                binding.fillL.setDoOnProgressEnd {

                    if (isProgressReset) {
                        isProgressReset = false
                        binding.fillL.setProgress(0)

                    } else {
                        isProgressReset = true
                    }

                    binding.fillL.setBackgroundColor(
                        resources.getColor(
                            R.color.blue_selected_led_color,
                            activity?.theme
                        )
                    )
                    binding.fillL.clearAnimation()
                }

                viewModel.animationStarted.value = true
                secondNumber = 0
//            sendZeros()
                Handler(Looper.myLooper()!!).postDelayed({
                    sendDataToAwsMessage(1)
                }, 200)

            }
        } else {
            stopAnimation()
        }
    }

    private fun startAnimation(seconds: Long) {

        animate = ObjectAnimator.ofFloat(
            binding.animationView,
            "translationX",
            binding.chart1.width.toFloat() + seventhStepWidth
        ).apply {
            duration = seconds
            interpolator = LinearInterpolator()
            start()
        }

        animate?.doOnEnd {

            if (!isCancel) {
                stopAnimation()
                Handler(Looper.myLooper()!!).postDelayed(
                    {
                        binding.cardView10.isEnabled = false
                        binding.viewOverImage.visibility = View.GONE
                        binding.animationView.animate().translationX(0f).withEndAction {
                            binding.viewOverImage.visibility = View.GONE
                            binding.cardView10.isEnabled = true
                        }.duration = 1000
                        viewModel.animationStarted.value = false
                    }, 1000
                )
            } else {

                binding.cardView10.isEnabled = false
                binding.viewOverImage.visibility = View.GONE
                binding.animationView.animate().translationX(0f).withEndAction {
                    binding.viewOverImage.visibility = View.GONE
                    binding.cardView10.isEnabled = true
                }.duration = 1000
                viewModel.animationStarted.value = false
                isCancel = false

            }

        }

    }

    private fun stopAnimation() {
        if (viewModel.animationStarted.value!!) {
            viewModel.animationStarted.value = false
            isCancel = true
            animate?.cancel()
            binding.fillL.setDuration(1000)
            binding.fillL.setProgress(0, false)
            Log.d(TAG, "stopAnimation: ")
        }
        disableButtonsOnQuickPlay(true)
//        if (ISGroupOrDevice.contentEquals("D"))
//            createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "device_id")
//        else
//            createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "group_id")
    }

    private fun sendDataToAwsMessage(totalScheduleTime: Int) {

//        if (totalScheduleTime < totalScheduleTime) {
        if (totalScheduleTime < arrayList.size) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (viewModel.animationStarted.value == true)
                    sendDataToAwsMessage(totalScheduleTime + 1)
            }, 1000)
        } else {


            resendSchedule = false
            if (ISGroupOrDevice.contentEquals("D"))
                createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "device_id")
            else
                createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "group_id")
            return
        }

        if (createScheduleActivityVM.isAwsConnected.value!!) {
            val jsonObject = JSONObject()
            jsonObject.put("commandID", "3")
            jsonObject.put("deviceID", "1")
            jsonObject.put("macAddress", "1")// no colon + lower case
            jsonObject.put("isGroup", true)// if specific device in group then false
            jsonObject.put("timestamp", System.currentTimeMillis().toString())
            jsonObject.put("a_value", arrayList[totalScheduleTime].c)
            jsonObject.put("b_value", arrayList[totalScheduleTime].b)
            jsonObject.put("c_value", arrayList[totalScheduleTime].a)

            val data = jsonObject.toString()
            if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                publishToTopic(data)

        } else
            showMessage("aws not connected", false)
    }

    private fun changeToPreviewTV() {
        binding.previousTv.visibility = View.GONE
        binding.nextTv.visibility = View.GONE
        binding.nextTv.text = "Next"
        when {
            IsEditOrPreviewOrCreate.contentEquals("edit") || IsEditOrPreviewOrCreate.contentEquals(
                "preview"
            ) -> {
                createScheduleActivityVM.scheduleNameText.value =
                    "Preview(" + singleSchedule!!.name + ")"
            }
            else -> {
                createScheduleActivityVM.scheduleNameText.value = "Preview Schedule"
            }
        }
        if (IsEditOrPreviewOrCreate.contentEquals("create"))
            createScheduleActivityVM.tabVisibility.value = false

    }

    private fun changeToEditTV() {
        if (viewModel.selectedTab != 0)
            binding.previousTv.visibility = View.VISIBLE
        else
            binding.previousTv.visibility = View.INVISIBLE
        binding.nextTv.visibility = View.VISIBLE
        when {
            IsEditOrPreviewOrCreate.contentEquals("edit") ||
                    IsEditOrPreviewOrCreate.contentEquals("preview") -> {
                createScheduleActivityVM.scheduleNameText.value =
                    "Edit(" + singleSchedule!!.name + ")"
            }
            else -> {
                createScheduleActivityVM.scheduleNameText.value = "New Schedule"
            }
        }
        if (IsEditOrPreviewOrCreate.contentEquals("create"))
            createScheduleActivityVM.tabVisibility.value = true
//        putLocationValue()


    }

    private fun step6Visibility() {

        binding.animationView.visibility = View.GONE
        binding.viewPager.visibility = View.GONE
        binding.saveAndUploadGroup.visibility = View.GONE
        binding.moonLightGroup.visibility = View.VISIBLE
        initObject()

    }

    private fun switchVisibility(it: Boolean) {
        Log.d(TAG, "switchVisibility: $it")
        binding.apply {
            if (it) {
                viewPager.visibility = View.VISIBLE
                saveAndUploadGroup.visibility = View.GONE
                cardView11.visibility = View.GONE
                animationView.visibility = View.GONE
                moonLightGroup.visibility = View.GONE
                createScheduleActivityVM.saveText.value = "Next"
            } else {
                animationView.visibility = View.VISIBLE
                viewPager.visibility = View.GONE
                saveAndUploadGroup.visibility = View.VISIBLE
                initObject()
                moonLightGroup.visibility = View.GONE
                createScheduleActivityVM.saveText.value = ""
            }
        }

        viewModel.cardBackGround.value = viewModel.selectedTab != 4
    }

    @SuppressLint("SetTextI18n")
    private fun setCurrentValuesOfCurrentStep(i: Int) {

        if (viewModel.listOfTimeValues.isNotEmpty()) {
            if (viewModel.listOfTimeValues.size - 1 >= i) {
                if (i == 5) {

                    val startTime = viewModel.listOfTimeValues[i].startTime.split(":")
//                    val endTime = viewModel.listOfTimeValues[0].endTime.split(":")
                    val endTime = viewModel.listOfTimeValues[0].startTime.split(":")
                    binding.startTime.text = getTimeWithZero(startTime)
                    binding.endTime.text = getTimeWithZero(endTime)

                } else {

                    val startTime = viewModel.listOfTimeValues[i].startTime.split(":")
                    val endTime = when {
                        viewModel.listOfTimeValues.size - 1 > i ->
                            viewModel.listOfTimeValues[i + 1].startTime.split(":")
                        else ->
                            viewModel.listOfTimeValues[i].endTime.split(":")
                    }

                    binding.startTime.text = getTimeWithZero(startTime)
                    binding.endTime.text = getTimeWithZero(endTime)

                }
            } else {

                val startTime = viewModel.listOfTimeValues[i - 1].endTime.split(":")
                binding.startTime.text = getTimeWithZero(startTime)

                setStartTime(startTime[0].toInt(), startTime[1].toInt())

                val t = startTime[0].toInt() * 60 + startTime[1].toInt()

                if (t + 60 > 1440) {

                    val s = 1440 - (t + 60)

                    binding.endTime.text = String.format(
                        "%02d",
                        s / 60
                    ) + ":" + String.format(
                        "%02d",
                        s % 60
                    )
                    setEndTimeD(s / 60, s % 60)

                } else {

                    val s = t + 60
                    binding.endTime.text = String.format(
                        "%02d",
                        s / 60
                    ) + ":" + String.format(
                        "%02d",
                        s % 60
                    )
                    setEndTimeD(s / 60, s % 60)

                }

            }
        }

        if (viewModel.listOfValueAndStep.isNotEmpty()) {
            if (viewModel.listOfValueAndStep.size - 1 >= i) {
                binding.startAv.text = viewModel.listOfValueAndStep[i].startValues.a
                binding.startBv.text = viewModel.listOfValueAndStep[i].startValues.b
                binding.startCv.text = viewModel.listOfValueAndStep[i].startValues.c
                binding.endAv.text = viewModel.listOfValueAndStep[i].endValues.a
                binding.endBv.text = viewModel.listOfValueAndStep[i].endValues.b
                binding.endCv.text = viewModel.listOfValueAndStep[i].endValues.c

                if (viewModel.listOfValueAndStep[i].stepGradual == 1) {
                    viewModel.gradualButtonClicked.value = true
                    viewModel.stepButtonClicked.value = false
                } else {
                    viewModel.stepButtonClicked.value = true
                    viewModel.gradualButtonClicked.value = false
                }

            } else {

                binding.startAv.text = viewModel.listOfValueAndStep[i - 1].endValues.a
                binding.startBv.text = viewModel.listOfValueAndStep[i - 1].endValues.b
                binding.startCv.text = viewModel.listOfValueAndStep[i - 1].endValues.c

            }
        }

        // set end values for the graph
        setValuesOfABC(i)

        val time = viewModel.listOfTimeValues
        val value = viewModel.listOfValueAndStep
        Log.d(TAG, "setCurrentValuesOfCurrentStep: ")
    }

    private fun setValuesOfABC(i: Int) {

        val start = ABCValuesModel(
            binding.startAv.text.toString(),
            binding.startBv.text.toString(),
            binding.startCv.text.toString(),
        )
        val end = ABCValuesModel(
            binding.endAv.text.toString(),
            binding.endBv.text.toString(),
            binding.endCv.text.toString(),
        )

        stepOrGradualValues = when (viewModel.gradualButtonClicked.value) {
            true -> 1
            else -> 2
        }


        if (viewModel.listOfValueAndStep.size - 1 >= i)
            viewModel.listOfValueAndStep[i] = AdvanceValuesModel(start, end, stepOrGradualValues)
        else
            viewModel.listOfValueAndStep.add(i, AdvanceValuesModel(start, end, stepOrGradualValues))

        if (viewModel.listOfTimeValues.size - 1 >= viewModel.selectedTab)
            setData(viewModel.listOfValueAndStep)


    }

    private fun getTimeWithZero(time: List<String>): String {
        return String.format(
            "%02d",
            time[0].toInt()
        ) + ":" + String.format(
            "%02d",
            time[1].toInt()
        )
    }

    private fun sendZeros() {

        val jsonObject = JSONObject()
        jsonObject.put("commandID", "3")
        jsonObject.put("deviceID", "1")
        jsonObject.put("macAddress", "1")// no colon + lower case
        jsonObject.put("isGroup", true)// if specific device in group then false
        jsonObject.put("timestamp", System.currentTimeMillis().toString())
        jsonObject.put("a_value", 0)
        jsonObject.put("b_value", 0)
        jsonObject.put("c_value", 0)

        val data = jsonObject.toString()
        publishToTopic(data)
    }

    private fun initChart() {

        // // Chart Style // //
        val chart = binding.chart1

        // background color
        chart.setBackgroundColor(Color.WHITE)

        // disable description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(true)
        chart.minOffset = 0f
        // set listeners
        chart.setDrawGridBackground(false)

        // enable scaling and dragging
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)

        // force pinch zoom along both axis
        chart.setPinchZoom(false)
        chart.setTouchEnabled(false)

        // // X-Axis Style // //
        val xAxis: XAxis = chart.xAxis
        xAxis.axisLineColor = Color.rgb(128, 128, 255)

        xAxis.setDrawGridLines(true)
        xAxis.gridLineWidth = 2f
        xAxis.gridColor = resources.getColor(R.color.white, activity?.theme)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setLabelCount(6, true)
        xAxis.setLabelCount(5, true)
        xAxis.setDrawLabels(false)
        xAxis.axisLineWidth = 0.1f

        // // Y-Axis Style // //
        val yAxis: YAxis = chart.axisLeft
        yAxis.gridLineWidth = 0.5f

        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false

        // axis range
        yAxis.axisMaximum = 130f
        yAxis.axisMinimum = 0f

        // Create Limit Lines //
        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        // draw points over time
        chart.animateX(1500, Easing.EaseInBack)
//        chart.animateX(1500, Easing.EaseInSine)

        // get the legend (only possible after setting data)
        chart.legend.isEnabled = false

    }

    private fun setData(count: MutableList<AdvanceValuesModel>) {

        if (screenWidth.toInt() != 0) {
            graphPC = ((screenWidth * (AppConstants.screenPC)) / 100).toFloat()
        }

        binding.linearLayout6
        val chart = binding.chart1
        values.clear()
        values2.clear()
        values3.clear()


        if (listOfXValues.isNotEmpty())
            listOfXValues.clear()


        val location = IntArray(2)
        binding.chart1.getLocationOnScreen(location)


        val chartWidth = screenWidth - (0.20) * screenWidth
        val startedGap = 0.05 * screenWidth

        when (viewModel.selectedTab) {

            0, 1, 2, 3, 4, 5, 6 -> {

                if (listOfXValues.isNotEmpty())
                    listOfXValues.clear()

                val zeroST = viewModel.listOfTimeValues[0].startTime.split(":")
                val oneST = viewModel.listOfTimeValues[1].startTime.split(":")
                val twoST = viewModel.listOfTimeValues[2].startTime.split(":")
                val threeST = viewModel.listOfTimeValues[3].startTime.split(":")
                val fourST = viewModel.listOfTimeValues[4].startTime.split(":")
                val fiveST = viewModel.listOfTimeValues[5].startTime.split(":")

                val step2Difference = differenceTime(
                    Time(oneST[0].toInt(), oneST[1].toInt(), 0),
                    Time(zeroST[0].toInt(), zeroST[1].toInt(), 0)
                )
                val step3Difference = differenceTime(
                    Time(twoST[0].toInt(), twoST[1].toInt(), 0),
                    Time(oneST[0].toInt(), oneST[1].toInt(), 0)
                )
                val step4Difference = differenceTime(
                    Time(threeST[0].toInt(), threeST[1].toInt(), 0),
                    Time(twoST[0].toInt(), twoST[1].toInt(), 0)
                )
                val step5Difference = differenceTime(
                    Time(fourST[0].toInt(), fourST[1].toInt(), 0),
                    Time(threeST[0].toInt(), threeST[1].toInt(), 0)
                )
                val step6Difference = differenceTime(
                    Time(fiveST[0].toInt(), fiveST[1].toInt(), 0),
                    Time(fourST[0].toInt(), fourST[1].toInt(), 0)
                )

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

                val x = listOfXValues

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


//                divide it by total no of hours
                val kk = chartWidth.toFloat() / x[5]
                hideAllTextviews()
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
                binding.one.x = startedGap.toFloat() - graphPC

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
                    binding.three.x = startedGap.toFloat() + kk * x[2] - graphPC
                    binding.three.y = binding.three.y - 20
                } else
                    binding.three.x = startedGap.toFloat() + kk * x[2] - graphPC
                position.add(binding.three.x)
                var isDistance3Up = false
                val distance3 =
                    startedGap.toFloat() + kk * x[3] - graphPC - (startedGap.toFloat() + kk * x[2] - graphPC)
                if (distance3 < 35 && !isDistance2Up) {
                    isDistance3Up = true
                    binding.four.x = startedGap.toFloat() + kk * x[3] - graphPC
                    binding.four.y = binding.four.y - 20
                } else
                    binding.four.x = startedGap.toFloat() + kk * x[3] - graphPC
                position.add(binding.four.x)
                var isDistance4Up = false
                val distance4 =
                    startedGap.toFloat() + kk * x[4] - graphPC - (startedGap.toFloat() + kk * x[3] - graphPC)
                if (distance4 < 35 && !isDistance3Up) {
                    binding.five.x = startedGap.toFloat() + kk * x[4] - graphPC
                    isDistance4Up = true
                    binding.five.y = binding.five.y - 20
                } else
                    binding.five.x = startedGap.toFloat() + kk * x[4] - graphPC

                val distance5 =
                    startedGap.toFloat() + kk * x[5] - graphPC - (startedGap.toFloat() + kk * x[4] - graphPC)

                if (distance5 < 35 && !isDistance3Up) {
                    isDistance2Up = true
                    binding.six.x = startedGap.toFloat() + kk * x[5] - graphPC
                    binding.six.y = binding.six.y - 20
                } else
                    binding.six.x = startedGap.toFloat() + kk * x[5] - graphPC

                position.add(binding.five.x)
                position.add(binding.six.x)
                position.add(binding.seven.x)
                seventhStepWidth = binding.seven.x - binding.six.x
                setYellowViewOnGraph(position)

            }

        }

        var set2: LineDataSet? = null
        var set3: LineDataSet? = null
        var set1: LineDataSet? = null


        chart.xAxis.setLabelCount(4, false)
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {

            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
//            set1.values = mvalues1
            set1.values = values
            set1.notifyDataSetChanged()
            set2 = chart.data.getDataSetByIndex(1) as LineDataSet
//            set2.values = mvalues2
            set2.values = values2
            set2.notifyDataSetChanged()
            set3 = chart.data.getDataSetByIndex(2) as LineDataSet
//            set3.values = mvalues3
            set3.values = values3
            set3.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
            chart.invalidate()

        } else {

            // create a dataset and give it a type
//            set1 = LineDataSet(mvalues1, "")
//            set1.setDrawIcons(false)
//            set2 = LineDataSet(mvalues2, "")
//            set2.setDrawIcons(false)
//            set3 = LineDataSet(mvalues3, "")
//            set3.setDrawIcons(false)
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)
            set2 = LineDataSet(values2, "")
            set2.setDrawIcons(false)
            set3 = LineDataSet(values3, "")
            set3.setDrawIcons(false)

            // black lines and points
            setGraphLinesColor(chart, set1, set2, set3)

            // line thickness and point size
            set1.fillFormatter = MyFillFormatter(set2)
            set2.fillFormatter = MyFillFormatter(set3)

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
//            chart.renderer = MyLineLegendRenderer(
//                chart,
//                chart.animator,
//                chart.viewPortHandler
//            )

//            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter = IFillFormatter { _, _ ->
                chart.axisLeft.axisMinimum
            }

//            // set the filled area
            set2.setDrawFilled(true)
            set2.fillFormatter = IFillFormatter { _, _ ->
                chart.axisLeft.axisMinimum
            }

//            // set the filled area
            set3.setDrawFilled(true)
            set3.fillFormatter = IFillFormatter { _, _ ->
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
            chart.invalidate()
        }

    }

    private fun setGraphLinesColor(
        chart: LineChart,
        set1: LineDataSet,
        set2: LineDataSet,
        set3: LineDataSet
    ) {
        if (createScheduleActivityVM.waterType.value!!.lowercase(Locale.getDefault()) == "marine") {
            set1.color = chart.context.getColor(R.color.purple_led_color)
            set2.color = chart.context.getColor(R.color.blue_selected_led_color)
            set3.color = chart.context.getColor(R.color.bar_white_color)
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.set1_gradient)
            set1.fillDrawable = drawable
            val drawable2 = ContextCompat.getDrawable(requireContext(), R.drawable.set2_gradient)
            set2.fillDrawable = drawable2
            val drawable3 = ContextCompat.getDrawable(requireContext(), R.drawable.set3_gradient)
            set3.fillDrawable = drawable3
        } else {
            set1.color = chart.context.getColor(R.color.red_led_color)
            set2.color = chart.context.getColor(R.color.green_led_color)
            set3.color = chart.context.getColor(R.color.yellow_led_color)
            val drawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.set1_fresh_water_gradient)
            set1.fillDrawable = drawable
            val drawable2 =
                ContextCompat.getDrawable(requireContext(), R.drawable.set2_fresh_water_gradient)
            set2.fillDrawable = drawable2
            val drawable3 =
                ContextCompat.getDrawable(requireContext(), R.drawable.set3_fresh_water_gradient)
            set3.fillDrawable = drawable3
        }
    }

    private fun setYellowViewOnGraph(position: MutableList<Float>) {

        Log.d(TAG, "setYellowViewOnGraph: ${viewModel.selectedTab}")
        binding.yellowLayout.visibility = View.VISIBLE
        binding.one.setTextColor(resources.getColor(R.color.text_secondary_two, activity?.theme))
        binding.two.setTextColor(resources.getColor(R.color.text_secondary_two, activity?.theme))
        binding.three.setTextColor(resources.getColor(R.color.text_secondary_two, activity?.theme))
        binding.four.setTextColor(resources.getColor(R.color.text_secondary_two, activity?.theme))
        binding.five.setTextColor(resources.getColor(R.color.text_secondary_two, activity?.theme))
        binding.six.setTextColor(resources.getColor(R.color.text_secondary_two, activity?.theme))
        binding.seven.setTextColor(resources.getColor(R.color.text_secondary_two, activity?.theme))

        when (viewModel.selectedTab) {
            0 -> {
                binding.yellowLayout.x = position[0] - 6
                binding.yellowLayout.layoutParams.width =
                    position[1].toInt() - position[0].toInt() + yellowTextWidth.toInt()
            }
            1 -> {
                binding.yellowLayout.x = position[1] - 6
                binding.yellowLayout.layoutParams.width =
                    position[2].toInt() - position[1].toInt() + yellowTextWidth.toInt()
            }
            2 -> {
                binding.yellowLayout.x = position[2] - 6
                binding.yellowLayout.layoutParams.width =
                    position[3].toInt() - position[2].toInt() + yellowTextWidth.toInt()
            }
            3 -> {
                binding.yellowLayout.x = position[3] - 6
                binding.yellowLayout.layoutParams.width =
                    position[4].toInt() - position[3].toInt() + yellowTextWidth.toInt()
            }
            4 -> {
                binding.yellowLayout.x = position[4] - 6
                binding.yellowLayout.layoutParams.width =
                    position[5].toInt() - position[4].toInt() + yellowTextWidth.toInt()
            }
            5, 6 -> {
                binding.yellowLayout.visibility = View.GONE
            }


        }
        Log.d(TAG, "setYellowViewOnGraph: ${position.size}")

    }

    private fun hideAllTextviews() {

        binding.one.visibility = View.GONE
        binding.two.visibility = View.GONE
        binding.three.visibility = View.GONE
        binding.four.visibility = View.GONE
        binding.five.visibility = View.GONE
        binding.six.visibility = View.GONE
        binding.seven.visibility = View.GONE

        Log.d(TAG, "hideAllTextviews: ${binding.one.y}")
        binding.one.y = 40f
        binding.two.y = 40f
        binding.three.y = 40f
        binding.four.y = 40f
        binding.five.y = 40f
        binding.six.y = 40f
        binding.seven.y = 40f


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

    private fun showSaveDialog() {

        val showScheduleDialog = Dialog(requireContext())
        if (showScheduleDialog.isShowing) showScheduleDialog.cancel()
        showScheduleDialog.apply {
            setContentView(R.layout.item_save_schedule)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(true)
//            window?.setWindowAnimations(R.style.DialogAnimation)
            findViewById<Button>(R.id.button1).setOnClickListener {

                if (findViewById<EditText>(R.id.edittext).text.toString().isNotEmpty()) {
                    showScheduleDialog.dismiss()

                    scheduleName = findViewById<EditText>(R.id.edittext).text.toString()
                    isPublic = if (findViewById<SwitchCompat>(R.id.isPublicSwitch).isChecked)
                        1
                    else 0

                    callRespectiveApi(viewModel.saveAndUpload)

                } else
                    showMessage("Please select schedule name.", false)
            }
            findViewById<Button>(R.id.button).setOnClickListener {
                showScheduleDialog.dismiss()
            }

            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnCancelListener {
                dismiss()
            }
            setOnDismissListener {
                cancel()
            }
            show()
        }

    }

    private fun callRespectiveApi(saveAndUpload: Int) {

        if (IsEditOrPreviewOrCreate.contentEquals("create"))
            makeCreateApiCall(saveAndUpload)
        else {

            if (ISGroupOrDevice.contentEquals("D")) {
                if (singleSchedule!!.deviceId == DeviceOrGroupID)
                    makeUpdateApiCall(saveAndUpload)
                else
                    makeCreateApiCall(saveAndUpload)
            } else {
                if (singleSchedule!!.groupId == DeviceOrGroupID)
                    makeUpdateApiCall(saveAndUpload)
                else
                    makeCreateApiCall(saveAndUpload)
            }

        }

    }

    private fun makeCreateApiCall(enable: Int) {

        val list = viewModel.listOfValueAndStep.distinct()
        val listTime = viewModel.listOfTimeValues

        val geoLocationEnabled = if (binding.geolocationSwitch.isChecked)
            1 else 0

        val hashmap: HashMap<String, String> = HashMap()
        if (ISGroupOrDevice.contentEquals("D"))
            hashmap["device_id"] = DeviceOrGroupID.toString()
        else
            hashmap["group_id"] = DeviceOrGroupID.toString()

        viewModel.apply {

            createScheduleApi(
                scheduleName,
                isPublic,
                hashmap, //should be the id of schedule with the device
                geoLocationEnabled,
                geoLocationId,
                enable,
                2,
                when (viewModel.moonLightEnable.value) {
                    true -> {
                        "1"
                    }
                    else -> {
                        "0"
                    }
                },
                addOtherZeros(listTime, 0),
                list[0].startValues.a,
                list[0].startValues.b,
                list[0].startValues.c,
                list[0].stepGradual.toString(),
                addOtherZeros(listTime, 1),
                list[1].startValues.a,
                list[1].startValues.b,
                list[1].startValues.c,
                list[1].stepGradual.toString(),
                addOtherZeros(listTime, 2),
                list[2].startValues.a,
                list[2].startValues.b,
                list[2].startValues.c,
                list[2].stepGradual.toString(),
                addOtherZeros(listTime, 3),
                list[3].startValues.a,
                list[3].startValues.b,
                list[3].startValues.c,
                list[3].stepGradual.toString(),
                addOtherZeros(listTime, 4),
                list[4].startValues.a,
                list[4].startValues.b,
                list[4].startValues.c,
                list[4].stepGradual.toString(),
                addOtherZeros(listTime, 5),
                "0",
                "0",
                "0",
                "2"

            )
            if (enable == 1)
                showDialogForAckReceiving()

        }
    }

    private fun publishToTopic(data: String) {

        var topic = ""
        topic = when {
            ISGroupOrDevice.contentEquals("G") -> AppConstants.GroupTopic
            else -> AppConstants.DeviceTopic
        }


        try {
            createScheduleActivityVM.mqttManager?.publishString(
                data,
                topic,
                AWSIotMqttQos.QOS0,
                { _, _ ->
                    activity?.runOnUiThread {
//                        publish_confirm_dialog.dismiss()
                        resendSchedule = true
                        Log.d(TAG, "Advance Publish data.$data")
                    }
                },
                "Check if data is given"
            )
        } catch (e: java.lang.Exception) {
            publishToTopic(data)
            Log.d(TAG, "Publish error." + e.localizedMessage)
        }

    }

    private fun endTimeDialog() {

        val x = binding.endTime.text.toString().split(":")
        val timePickerPopupSunSet = TimePickerPopup.Builder()
            .from(requireContext())
            .offset(3)
            .textSize(17)
            .setTime(x[0].toInt(), x[1].toInt())
            .listener { _, hour, minutee ->

                val minute =
                    when (minutee) {
                        0 -> 0
                        1 -> 15
                        2 -> 30
                        else -> 45
                    }


                if (!checkEndTimeStep(hour, minute)) {
                    showMessage(
//                        "End time must be between the Current Step \nStart Time and Next Step End Time",
                        "Schedule must not exceed 24hr limit",
                        false
                    )
                    return@listener
                }
                setEndTimeD(hour, minute)

            }.build()

        if (!timePickerPopupSunSet.isShowing)
            timePickerPopupSunSet.show()

    }

    private fun startTimeDialog() {

        val x = binding.startTime.text.toString().split(":")

        val timePickerPopupSunRise: TimePickerPopup = TimePickerPopup.Builder()
            .from(requireContext())
            .offset(3)
            .textSize(17)
            .setTime(x[0].toInt(), x[1].toInt())
            .listener { _, hour, minutee ->

                val minute =
                    when (minutee) {
                        0 -> 0
                        1 -> 15
                        2 -> 30
                        else -> 45
                    }


                if (!checkStartTimeStep(hour, minute)) {
                    showMessage("Schedule must not exceed 24hr limit", false)
//                    showMessage("Start time must be between the previous and Next Step ", true)
                    return@listener
                }
                setStartTime(hour, minute)

            }.build()
        if (!timePickerPopupSunRise.isShowing)
            timePickerPopupSunRise.show()

    }

    private fun checkStartTimeStep(hour: Int, minute: Int): Boolean {
        if (viewModel.listOfTimeValues.size <= viewModel.selectedTab) {
            return true
        }

        val csst = hour * 60 + minute
        val istLimit = when (viewModel.selectedTab) {
            0 -> {
                val limitIst = viewModel.listOfTimeValues[5].startTime.split(":")
                limitIst[0].toInt() * 60 + limitIst[1].toInt()
            }
            else -> {
                val limitIst =
                    viewModel.listOfTimeValues[viewModel.selectedTab - 1].startTime.split(":")
                limitIst[0].toInt() * 60 + limitIst[1].toInt()
            }
        }

        val currentTimeObj = viewModel.listOfTimeValues[viewModel.selectedTab]
        val endTime = currentTimeObj.endTime.split(":")
        val lastLimit = endTime[0].toInt() * 60 + endTime[1].toInt()

        return if (lastLimit > istLimit) {
            csst in (istLimit + 1) until lastLimit
        } else {
            (csst in (istLimit + 1)..1440) || (csst in 0 until lastLimit)
        }

    }

    private fun checkEndTimeStep(hour: Int, minute: Int): Boolean {

        //there is an crash at list size is 6
        if (viewModel.listOfTimeValues.size <= viewModel.selectedTab) {
            return true
        }
        Log.d(TAG, "checkEndTimeStep: " + viewModel.listOfTimeValues.size)
        Log.d(TAG, "checkEndTimeStep: " + viewModel.selectedTab)

        val currentTimeObj = viewModel.listOfTimeValues[viewModel.selectedTab]
        val startTime = currentTimeObj.startTime.split(":")
        val csst = hour * 60 + minute
        val istLimit = startTime[0].toInt() * 60 + startTime[1].toInt()

        val lastLimit = when (viewModel.selectedTab) {
            5 -> {
                val limitIst = viewModel.listOfTimeValues[0].endTime.split(":")
                limitIst[0].toInt() * 60 + limitIst[1].toInt()
            }
            else -> {
                val limitIst =
                    viewModel.listOfTimeValues[viewModel.selectedTab + 1].endTime.split(":")
                limitIst[0].toInt() * 60 + limitIst[1].toInt()
            }
        }

        return if (lastLimit > istLimit) {
            csst in (istLimit + 1) until lastLimit
        } else {
            (csst in (istLimit + 1)..1440) || (csst in 0 until lastLimit)
        }

    }

    private fun setEndTimeD(hour: Int, minute: Int) {

        viewModel.apply {

            when (selectedTab) {
                0 -> {

                    listOfTimeValues[selectedTab].endTime =
                        getTimeWithZero(mutableListOf("$hour", "$minute"))
                    binding.endTime.text = setEndTime(hour, minute)

                    endTime1.value = true
//                    addStartTimeToList(0)
                    addEndTimeToList(0)
                    setData(viewModel.listOfValueAndStep)
                }

                1 -> {


                    listOfTimeValues[selectedTab].endTime =
                        getTimeWithZero(mutableListOf("$hour", "$minute"))
                    binding.endTime.text = setEndTime(hour, minute)
                    endTime2.value = true
//                    addStartTimeToList(1)
                    addEndTimeToList(1)
                    setData(viewModel.listOfValueAndStep)

                }

                2 -> {

//                    addStartTimeToList(2)
                    listOfTimeValues[selectedTab].endTime =
                        getTimeWithZero(mutableListOf("$hour", "$minute"))
                    binding.endTime.text = setEndTime(hour, minute)
                    endTime3.value = true
                    addEndTimeToList(2)
                    setData(viewModel.listOfValueAndStep)

                }

                3 -> {


                    listOfTimeValues[selectedTab].endTime =
                        getTimeWithZero(mutableListOf("$hour", "$minute"))
                    binding.endTime.text = setEndTime(hour, minute)
                    endTime4.value = true
//                    addStartTimeToList(3)
                    addEndTimeToList(3)
                    setData(viewModel.listOfValueAndStep)


                }

                4 -> {

                    listOfTimeValues[selectedTab].endTime =
                        getTimeWithZero(mutableListOf("$hour", "$minute"))
                    binding.endTime.text = setEndTime(hour, minute)
                    endTime5.value = true
//                    addStartTimeToList(4)
                    addEndTimeToList(4)
                    setData(viewModel.listOfValueAndStep)

                }

                5 -> {

                    listOfTimeValues[selectedTab].endTime =
                        getTimeWithZero(mutableListOf("$hour", "$minute"))
                    binding.endTime.text = setEndTime(hour, minute)
                    endTime6.value = true
//                    addStartTimeToList(5)

                    addEndTimeToList(5)
                    setData(viewModel.listOfValueAndStep)

                }

                6 -> {


                }
            }

        }


    }

    @SuppressLint("SetTextI18n")
    private fun setStartTime(hour: Int, minute: Int) {
        val time = setEndTime(hour, minute)

        viewModel.apply {

            when (selectedTab) {
                0 -> {

                    startTimeStep1 = hour * 60 + minute

                    startTime1.value = true
                    binding.startTime.text = setEndTime(hour, minute)

                    addStartTimeToList(0)

                }
                1 -> {


                    listOfTimeValues[0].endTime = time
                    startTime2.value = true
                    binding.startTime.text = setEndTime(hour, minute)


                    addStartTimeToList(1)
                }

                2 -> {

                    listOfTimeValues[1].endTime = time
                    startTime3.value = true
                    binding.startTime.text = setEndTime(hour, minute)
                    addStartTimeToList(2)

                }
                3 -> {

                    listOfTimeValues[2].endTime = time
                    startTime4.value = true
                    binding.startTime.text = setEndTime(hour, minute)
                    addStartTimeToList(3)


                }
                4 -> {

                    listOfTimeValues[3].endTime = time
                    startTime5.value = true
                    binding.startTime.text = setEndTime(hour, minute)
                    addStartTimeToList(4)

                }
                5 -> {
                    listOfTimeValues[4].endTime = time
                    startTime6.value = true
                    binding.startTime.text = setEndTime(hour, minute)
                    addStartTimeToList(5)

                }
                6 -> {

                }

            }

        }

    }

    private fun addStartTimeToList(i: Int) {

        if (viewModel.listOfTimeValues.size - 1 >= i)
            viewModel.listOfTimeValues[i] =
                AdvanceTimeModel(binding.startTime.text.toString(), binding.endTime.text.toString())
        else
            viewModel.listOfTimeValues.add(
                i,
                AdvanceTimeModel(binding.startTime.text.toString(), binding.endTime.text.toString())
            )

        setData(viewModel.listOfValueAndStep)
    }

    private fun addEndTimeToList(i: Int) {

        val timee = binding.endTime.text.toString().split(":")

        val t = timee[0].toInt() * 60 + timee[1].toInt()
        val timeEnd = if (t + 60 > 1440) {
            1440 - (t + 60)
        } else {
            t + 60
        }

        if (viewModel.listOfTimeValues.size - 1 > i)
            viewModel.listOfTimeValues[i + 1] = AdvanceTimeModel(
                binding.endTime.text.toString(),
                viewModel.listOfTimeValues[i + 1].endTime
            )
        else {
            viewModel.listOfTimeValues[0] = AdvanceTimeModel(
                binding.endTime.text.toString(),
                viewModel.listOfTimeValues[0].endTime
            )

            val end = ABCValuesModel(
                binding.endAv.text.toString(),
                binding.endBv.text.toString(),
                binding.endCv.text.toString(),
            )
            val start = ABCValuesModel(
                binding.endAv.text.toString(),
                binding.endBv.text.toString(),
                binding.endCv.text.toString(),
            )
            viewModel.listOfValueAndStep.add(i, AdvanceValuesModel(start, end, 2))

        }

    }

    private fun setEndTime(hour: Int, minute: Int): String {

        var hr = hour.toString()
        if (hour < 10) {
            hr = "0$hour"
        }
        var min = minute.toString()
        if (minute < 10) {
            min = "0$minute"
        }
        return "$hr:$min"

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1221 && resultCode == Activity.RESULT_OK) {
            val abcValuesObject: ABCValuesModel = data!!.getParcelableExtra("data")!!

            stepOrGradualValues = when (viewModel.gradualButtonClicked.value) {
                true -> 1
                else -> 2
            }

            if (data.getStringExtra("mdata").contentEquals("start")) {

                binding.startAv.text = abcValuesObject.a
                binding.startBv.text = abcValuesObject.b
                binding.startCv.text = abcValuesObject.c

                setChangedData(abcValuesObject, true)

            } else {

                binding.endAv.text = abcValuesObject.a
                binding.endBv.text = abcValuesObject.b
                binding.endCv.text = abcValuesObject.c
                setChangedData(
                    abcValuesObject,
                    false
                )
            }

        }

    }

    private fun setChangedData(abcValuesObject: ABCValuesModel, b: Boolean) {

        if (viewModel.listOfValueAndStep.size - 1 >= viewModel.selectedTab) {
            // update the list
            if (b) {
                viewModel.listOfValueAndStep[viewModel.selectedTab] = AdvanceValuesModel(
                    abcValuesObject, ABCValuesModel(
                        binding.endAv.text.toString(),
                        binding.endBv.text.toString(),
                        binding.endCv.text.toString()
                    ), stepOrGradualValues
                )

                if (viewModel.selectedTab > 0) {
                    viewModel.listOfValueAndStep[viewModel.selectedTab - 1] = AdvanceValuesModel(
                        viewModel.listOfValueAndStep[viewModel.selectedTab - 1].startValues,
                        abcValuesObject,
                        viewModel.listOfValueAndStep[viewModel.selectedTab - 1].stepGradual
                    )
                } else {
                    if (viewModel.listOfValueAndStep.size == 6) {
                        viewModel.listOfValueAndStep[5] =
                            AdvanceValuesModel(
                                viewModel.listOfValueAndStep[5].startValues,
                                abcValuesObject,
                                stepOrGradualValues
                            )
                    }
                }


            } else {

                viewModel.listOfValueAndStep[viewModel.selectedTab] = AdvanceValuesModel(
                    ABCValuesModel(
                        binding.startAv.text.toString(),
                        binding.startBv.text.toString(),
                        binding.startCv.text.toString()
                    ), abcValuesObject, stepOrGradualValues
                )

                if (viewModel.selectedTab == 5) {
                    viewModel.listOfValueAndStep[0] = AdvanceValuesModel(
                        abcValuesObject,
                        viewModel.listOfValueAndStep[0].endValues,
                        viewModel.listOfValueAndStep[0].stepGradual
                    )
                } else {
                    if (viewModel.listOfValueAndStep.size > viewModel.selectedTab + 1)
                        viewModel.listOfValueAndStep[viewModel.selectedTab + 1] =
                            AdvanceValuesModel(
                                abcValuesObject,
                                viewModel.listOfValueAndStep[viewModel.selectedTab + 1].endValues,
                                viewModel.listOfValueAndStep[viewModel.selectedTab + 1].stepGradual
                            )

                }
            }


        } else {
            if (b) {
                viewModel.listOfValueAndStep.add(
                    AdvanceValuesModel(
                        abcValuesObject, ABCValuesModel(
                            binding.endAv.text.toString(),
                            binding.endBv.text.toString(),
                            binding.endCv.text.toString()
                        ), stepOrGradualValues
                    )
                )
            } else {
                viewModel.listOfValueAndStep.add(
                    AdvanceValuesModel(
                        ABCValuesModel(
                            binding.startAv.text.toString(),
                            binding.startBv.text.toString(),
                            binding.startCv.text.toString()
                        ), abcValuesObject, stepOrGradualValues
                    )
                )
            }
        }

        setData(viewModel.listOfValueAndStep)
    }

    override fun onStart() {
        super.onStart()
        putLocationValue()
    }

    private fun putLocationValue() {


        if (GEOLOCATIONID != 0) {
            geoLocationId = GEOLOCATIONID
            for (datum in viewModel.geoLocationList.data) {
                if (datum.id.toString().contentEquals(geoLocationId.toString())) {
                    binding.geolocationtv.text = datum.name
                    viewModel.stayOnEasyFragment = true
                    binding.geolocationSwitch.isChecked = true
                }
            }
        } else {

            if (!IsEditOrPreviewOrCreate.contentEquals("create")) {
                if (singleSchedule!!.geoLocation == 1) {
                    for (datum in viewModel.geoLocationList.data) {
                        if (datum.id.toString().contentEquals(singleSchedule?.geoLocationId)) {
                            binding.geolocationtv.text = datum.name
                        }
                    }
                } else {
                    geoLocationId = null
                    binding.geolocationSwitch.isChecked = false
                    binding.geolocationtv.text = "Geo Location"
                }
            } else {
                geoLocationId = null
                binding.geolocationSwitch.isChecked = false
                binding.geolocationtv.text = "Geo Location"
            }


        }

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        createScheduleActivityVM.saveVisibility.value = true

        if (!IsFromChangeValue) {
            binding.moonLightSwitch.isChecked = (singleSchedule?.moonlightEnable == 1)
        }

        if (isVisible)
            if (!IsEditOrPreviewOrCreate.contentEquals("preview") && !IsFromChangeValue) {
                viewModel.button1Clicked()
                IsFromChangeValue = false
            }


    }

    private fun makeUpdateApiCall(enable: Int) {

        val list = viewModel.listOfValueAndStep.distinct()
        val listTime = viewModel.listOfTimeValues

        val geoLocationEnabled = if (binding.geolocationSwitch.isChecked)
            1 else 0

        val hashmap: HashMap<String, String> = HashMap()
        if (ISGroupOrDevice.contentEquals("D"))
            hashmap["device_id"] = DeviceOrGroupID.toString()
        else
            hashmap["group_id"] = DeviceOrGroupID.toString()

        viewModel.apply {

            updateScheduleApi(
                scheduleName,
                isPublic,
                hashmap,
                singleSchedule.value!!.id,
                geoLocationEnabled,
                geoLocationId,
                enable,
                2,
                when (viewModel.moonLightEnable.value) {
                    true -> {
                        "1"
                    }
                    else -> {
                        "0"
                    }
                },
                addOtherZeros(listTime, 0),
                list[0].startValues.a,
                list[0].startValues.b,
                list[0].startValues.c,
                list[0].stepGradual.toString(),
                addOtherZeros(listTime, 1),
                list[1].startValues.a,
                list[1].startValues.b,
                list[1].startValues.c,
                list[1].stepGradual.toString(),
                addOtherZeros(listTime, 2),
                list[2].startValues.a,
                list[2].startValues.b,
                list[2].startValues.c,
                list[2].stepGradual.toString(),
                addOtherZeros(listTime, 3),
                list[3].startValues.a,
                list[3].startValues.b,
                list[3].startValues.c,
                list[3].stepGradual.toString(),
                addOtherZeros(listTime, 4),
                list[4].startValues.a,
                list[4].startValues.b,
                list[4].startValues.c,
                list[4].stepGradual.toString(),
                addOtherZeros(listTime, 5),
                "0",
                "0",
                "0",
                "2"
            )

            if (enable == 1)
                showDialogForAckReceiving()

        }

    }

    private fun addOtherZeros(listTime: MutableList<AdvanceTimeModel>, index: Int): String {

        val time = listTime[index].startTime.split(":").toMutableList()
        return String.format("%02d", time[0].toInt()) + ":" + String.format(
            "%02d",
            time[1].toInt()
        ) + ":00"
    }

    override fun onUploadSchedule() {
        showCreateDialog()
    }

    override fun onReUploadSchedule() {
        makeUpdateApiCall(viewModel.saveAndUpload)
    }

    override fun onFinish(finishBack: Boolean) {
        if (finishBack) {
//            createScheduleActivityVM.isDialogOpen.value = false
            Handler(Looper.myLooper()!!).postDelayed({ activity?.finish() }, 100)
        }
    }

}

