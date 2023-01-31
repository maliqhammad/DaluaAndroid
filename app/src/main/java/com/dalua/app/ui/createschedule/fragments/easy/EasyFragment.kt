package com.dalua.app.ui.createschedule.fragments.easy


import android.animation.ObjectAnimator
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
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseFragment
import com.dalua.app.databinding.FragmentEasyBinding
import com.dalua.app.databinding.ItemRamptimeRvBinding
import com.dalua.app.interfaces.TimeRampListner
import com.dalua.app.models.ABCValuesModel
import com.dalua.app.models.CheckAckMacAddressResponse
import com.dalua.app.models.CreateScheduleResponse
import com.dalua.app.models.SocketACKResponseModel
import com.dalua.app.models.schedulemodel.ScheduleResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.ui.changeadvance.ChangeAdvanceValuesActivity
import com.dalua.app.ui.createschedule.CreateScheduleActivityVM
import com.dalua.app.ui.createschedule.adapters.MasterControlStateAdapter
import com.dalua.app.ui.customDialogs.devicesListDialog.DevicesListDialog
import com.dalua.app.ui.geolocation.GeoLocationActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.ApiTypes.CreateSchedules
import com.dalua.app.utils.AppConstants.ApiTypes.CheckAckMacAddressApi
import com.dalua.app.utils.AppConstants.ApiTypes.UpdateSchedules
import com.dalua.app.utils.AppConstants.Companion.DeviceOrGroupID
import com.dalua.app.utils.AppConstants.Companion.DeviceTopic
import com.dalua.app.utils.AppConstants.Companion.GEOLOCATIONIDEasy
import com.dalua.app.utils.AppConstants.Companion.GroupTopic
import com.dalua.app.utils.AppConstants.Companion.ISGroupOrDevice
import com.dalua.app.utils.AppConstants.Companion.IsEditOrPreviewOrCreate
import com.dalua.app.utils.AppConstants.Companion.difference
import com.dalua.app.utils.AppConstants.Companion.differenceTime
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
import kotlin.collections.ArrayList
import kotlin.math.abs


@AndroidEntryPoint
class EasyFragment : BaseFragment(), TimeRampListner, DevicesListDialog.DevicesListCallback {

    private var graphPC: Float = 25f
    private lateinit var fragmentStateAdapter: MasterControlStateAdapter
    lateinit var binding: FragmentEasyBinding
    private val createScheduleActivityVM: CreateScheduleActivityVM by activityViewModels()
    private val viewModel: EasyFragmentVM by viewModels()
    var singleSchedule: SingleSchedule? = null
    var sunRise: String = "08:00"
    private var defaultScData: Boolean = false
    private var isProgressReset = true
    private var shouldSave = true
    private val listOfXValues: MutableList<Float> = mutableListOf()
    var aValue = 25
    var bValue = 25
    var cValue = 25
    var screenWidth = 0
    private var isPublic = 0
    private var istTime = 0
    private var isCancel = false
    private var scheduleName = ""
    private var geoLocationId: Int? = null
    private var rampTime = 240
    private var sunsetSec = 17 * 60
    var arrayList: MutableList<ABCValuesModel> = arrayListOf()
    private var secondNumber = 0
    private var sunriseSec = 8 * 60
    var animate: ObjectAnimator? = null
    private var goToLocationActivity = 0
    lateinit var chart: LineChart
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
            if (createScheduleActivityVM.launchFrom.value == "device") {
                viewModel.checkAckMacAddress(ArrayList<String>().apply {
                    add(
                        createScheduleActivityVM.device.value!!.macAddress
                    )
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
            Log.d(TAG, "onFinish: ")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_easy, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        myProgressDialog()
        //added below 2 lines to make sure that easy fragment starts with edit screen
        if (!IsEditOrPreviewOrCreate.contentEquals("create")) {
            createScheduleActivityVM.killActivity = false
            createScheduleActivityVM.backPressed.value = true
        }
        viewModel.geoLocationList = ProjectUtil.getLocationObjects(requireContext())

        if (requireActivity().intent.hasExtra("sc")) {
            Log.d(TAG, "onCreateView: getIntent: ")
            goToLocationActivity = 0
            singleSchedule =
                ProjectUtil.stringToObject(
                    requireActivity().intent.getStringExtra("sc"),
                    SingleSchedule::class.java
                )

            if (!singleSchedule?.name.contentEquals("Default Schedule"))
                if (singleSchedule?.mode.contentEquals("1"))
                    setDefaultScheduleData()

        } else {
            goToLocationActivity = 1
            viewModel.stayOnEasyFragment = false
        }

        return binding.root

    }

    private fun setDefaultScheduleData() {

        binding.parentLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.parentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                screenWidth = binding.parentLayout.width //height is ready
                setData(
                    createScheduleActivityVM.croller1Progress.value!!.toFloat(),
                    createScheduleActivityVM.croller2Progress.value!!.toFloat(),
                    createScheduleActivityVM.croller3Progress.value!!.toFloat()
                )
            }
        })

        defaultScData = true
        val rap = singleSchedule?.easySlots?.rampTime!!.split(":")

        scheduleName = singleSchedule!!.name

        createScheduleActivityVM.scheduleNameText.value = singleSchedule?.name


        rampTime = when (rap.size) {
            2 -> rap[0].toInt() * 60 + rap[1].toInt()
            else -> rap[0].toInt() * 60
        }

        val xy = singleSchedule?.easySlots?.sunrise!!.split(":")
        val yz = singleSchedule?.easySlots?.sunset!!.split(":")
        viewModel.sunRise.value = xy[0] + ":" + xy[1]
        viewModel.sunSet.value = yz[0] + ":" + yz[1]

        sunriseSec = xy[0].toInt() * 60 + xy[1].toInt()
        sunsetSec = yz[0].toInt() * 60 + yz[1].toInt()

        val sCurrentTime: Int = if ((sunriseSec + rampTime) >= 1440) {
            sunriseSec + rampTime - 1440
        } else
            sunriseSec + rampTime

        val shou: String = if ((sCurrentTime / 60) < 10)
            "0" + (sCurrentTime / 60).toString()
        else
            (sCurrentTime / 60).toString()

        val sMin: String = if ((sCurrentTime % 60) < 10)
            "0" + (sCurrentTime % 60).toString()
        else
            (sCurrentTime % 60).toString()

        viewModel.rampStart.value = "$shou:$sMin"

        val currentTime = yz[0].toInt() * 60 + yz[1].toInt()

        val currtime: Int = if ((currentTime + rampTime) >= 1440) {
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
        viewModel.ramp.value = singleSchedule!!.easySlots.rampTime

        createScheduleActivityVM.croller1Progress.value =
            singleSchedule!!.easySlots.valueA!!.toInt()
        createScheduleActivityVM.croller2Progress.value =
            singleSchedule!!.easySlots.valueB!!.toInt()
        createScheduleActivityVM.croller3Progress.value =
            singleSchedule!!.easySlots.valueC!!.toInt()

        Log.d(TAG, "setDefaultScheduleData: $IsEditOrPreviewOrCreate")
        if (IsEditOrPreviewOrCreate.contentEquals("preview")) {
//            createScheduleActivityVM.saveText.value = "Next"
            createScheduleActivityVM.saveText.value = ""
            createScheduleActivityVM.saveScheduleClicked()
//            createScheduleActivityVM.killActivity = true
        } else {
            createScheduleActivityVM.saveText.value = ""
            createScheduleActivityVM.saveScheduleClicked()
        }

        if (singleSchedule!!.geoLocation == 1) {
            GEOLOCATIONIDEasy = singleSchedule!!.geoLocationId.toInt()
            geoLocationId = GEOLOCATIONIDEasy
            viewModel.stayOnEasyFragment = true
//           viewModel.geoLocationEnabled.value = true

            for (datum in viewModel.geoLocationList.data) {
                if (datum.id.toString().contentEquals(singleSchedule?.geoLocationId)) {
                    binding.geolocationtv.text = datum.name
                }
            }

        } else {
            viewModel.stayOnEasyFragment = false
            GEOLOCATIONIDEasy = 0
            binding.geolocationtv.text = "Geo Location"
        }
    }

    private fun changeToPreviewTV() {

        binding.masterShow.visibility = View.GONE
        binding.instantShow.visibility = View.GONE

        when {
            IsEditOrPreviewOrCreate.contentEquals("edit") ||
                    IsEditOrPreviewOrCreate.contentEquals("preview") -> {
                createScheduleActivityVM.scheduleNameText.value =
                    "Preview(" + singleSchedule!!.name + ")"
            }
            else -> {
                createScheduleActivityVM.scheduleNameText.value = "Preview Schedule"
            }
        }
    }

    private fun changeToEditTV() {

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObjects()
        initChart()
        observers()
        listeners()
        apiResponses()

    }

    private fun initObjects() {
        fragmentStateAdapter = MasterControlStateAdapter(
            childFragmentManager,
            lifecycle,
            createScheduleActivityVM.waterType.value!!,
            createScheduleActivityVM.configuration.value!!
        )
        binding.viewPager.adapter = fragmentStateAdapter
        binding.viewPager.isUserInputEnabled = false

        viewModel.singleSchedule.value = singleSchedule
        createScheduleActivityVM.saveVisibility.value = true

        //Removed and rename update and upload button for different conditions
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
                    viewModel.isDialogNameShow = !singleSchedule!!.enabled!!.contentEquals("1")

                } else {
                    if (createScheduleActivityVM.scheduleType.value == 1) {
                        binding.saveButton.text = getString(R.string.update)
                        binding.saveAndUploadBtn.text = getString(R.string.update_and_upload)
                    } else {
                        binding.saveButton.text = getString(R.string.save)
                        binding.saveAndUploadBtn.text = getString(R.string.save_amp_upload)
                    }
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
                    viewModel.isDialogNameShow = !singleSchedule!!.enabled!!.contentEquals("1")

                } else {
                    if (createScheduleActivityVM.scheduleType.value == 1) {
                        binding.saveButton.text = getString(R.string.update)
                        binding.saveAndUploadBtn.text = getString(R.string.update_and_upload)
                    } else {
                        binding.saveButton.text = getString(R.string.save)
                        binding.saveAndUploadBtn.text = getString(R.string.save_amp_upload)
                    }
                }
            }


        } else
            viewModel.changeControl.value = "instant"

    }

    private fun observers() {
        createScheduleActivityVM.socketResponseModel.observe(viewLifecycleOwner) {
            Log.d(TAG, "observers: DeviceAck: " + ProjectUtil.objectToString(it))
            findMacAddress(it)
        }

        createScheduleActivityVM.saveClicked.observe(viewLifecycleOwner) {

            if (isVisible)
                if (createScheduleActivityVM.saveText.value.contentEquals("")) {
                    binding.viewPager.visibility = View.VISIBLE
                    binding.linearLayout6.visibility = View.VISIBLE
                    binding.saveAndUploadGroup.visibility = View.GONE
                    createScheduleActivityVM.killActivity = true
                    createScheduleActivityVM.saveText.value = "Next"
                    binding.animationView.visibility = View.GONE
                    binding.cardView11.visibility = View.GONE
                    viewModel.changeControl.value = "instant"
                    changeToEditTV()
                    createScheduleActivityVM.tabVisibility.value =
                        IsEditOrPreviewOrCreate.contentEquals("create")

                } else {
                    changeToPreviewTV()
                    if (IsEditOrPreviewOrCreate.contentEquals("create"))
                        createScheduleActivityVM.tabVisibility.value = false

                    createScheduleActivityVM.saveText.value = ""
                    binding.animationView.visibility = View.VISIBLE
                    createScheduleActivityVM.killActivity = false
                    binding.linearLayout6.visibility = View.INVISIBLE
                    binding.viewPager.visibility = View.GONE
                    binding.saveAndUploadGroup.visibility = View.VISIBLE
                    if (singleSchedule != null) {
                        if (singleSchedule!!.enabled!!.contentEquals("1"))
                            binding.cardView11.visibility = View.INVISIBLE
                        else
                            binding.cardView11.visibility = View.VISIBLE
                    } else
                        binding.cardView11.visibility = View.VISIBLE

                }

        }

        createScheduleActivityVM.sendDataToBleNow.observe(viewLifecycleOwner) {
            codeDataToAwsFormat()
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

        viewModel.endTimeDialog.observe(viewLifecycleOwner) {
            endTimeDialog()
        }

        viewModel.quickPlay.observe(viewLifecycleOwner) {
            if (shouldSave) {
                if (viewModel.animationStarted.value!!) {
                    stopAnimation()
                } else {
                    startQuickPlayApi()
                }
            } else
                showMessage("Select valid timeline", false)

        }

        viewModel.rampTimePopUp.observe(viewLifecycleOwner) {
            showRampPopUp()
        }

        createScheduleActivityVM.croller1Progress.observe(viewLifecycleOwner) {
            aValue = it
            if (istTime > 2) {
                val x =
                    it.toString() + ":" + createScheduleActivityVM.croller2Progress.value + ":" + createScheduleActivityVM.croller3Progress.value
                Log.d(TAG, "observers: $x")
                setData(
                    it.toFloat(),
                    createScheduleActivityVM.croller2Progress.value!!.toFloat(),
                    createScheduleActivityVM.croller3Progress.value!!.toFloat()
                )
            } else
                istTime++
        }
        createScheduleActivityVM.croller2Progress.observe(viewLifecycleOwner) {
            bValue = it
            if (istTime > 2) {
                val x =
                    createScheduleActivityVM.croller1Progress.value.toString() + ":" + it.toString() + ":" + createScheduleActivityVM.croller3Progress.value
                Log.d(TAG, "observers: $x")
                setData(
                    createScheduleActivityVM.croller1Progress.value!!.toFloat(),
                    it.toFloat(),
                    createScheduleActivityVM.croller3Progress.value!!.toFloat()
                )
            } else
                istTime++
        }
        createScheduleActivityVM.croller3Progress.observe(viewLifecycleOwner) {
            cValue = it
            if (istTime > 2) {
                val x =
                    createScheduleActivityVM.croller1Progress.value.toString() + ":" + createScheduleActivityVM.croller2Progress.value + ":" + it.toString()
                Log.d(TAG, "observers: $x")
                setData(
                    createScheduleActivityVM.croller1Progress.value!!.toFloat(),
                    createScheduleActivityVM.croller2Progress.value!!.toFloat(),
                    it.toFloat()
                )

            } else
                istTime++
        }

        viewModel.geoLocationEnabled.observe(viewLifecycleOwner) {
            Log.d(TAG, "observers: GEOLOCATIONIDEasy: $it")
            if (it) {
                GeoLocationActivity.launchGeoLocationActivity(
                    requireContext(),
                    Intent(requireContext(), GeoLocationActivity::class.java)
                )
            } else {
                GEOLOCATIONIDEasy = 0
                geoLocationId = null
            }
            Log.d(TAG, "observers: GEOLOCATIONIDEasy: $GEOLOCATIONIDEasy")

        }

        viewModel.changeControl.observe(viewLifecycleOwner) {
            if (it!!.contentEquals("instant")) {
                binding.viewPager.setCurrentItem(0, true)
                binding.instantShow.visibility = View.VISIBLE
                binding.masterShow.visibility = View.GONE
            } else {
                binding.instantShow.visibility = View.GONE
                binding.masterShow.visibility = View.VISIBLE
                binding.viewPager.setCurrentItem(1, true)
            }

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

    }

    private fun listeners() {

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
                    if (viewModel.saveAndUpload == 0)
                        showWorking()
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
                                if (viewModel.saveAndUpload == 0)
                                    Handler(Looper.getMainLooper()).postDelayed(
                                        { requireActivity().finish() },
                                        500
                                    )
                            }
                        }

                        CreateSchedules.name -> {
                            it.data?.let { res ->
                                val createScheduleResponse = ProjectUtil.stringToObject(
                                    res.string(),
                                    CreateScheduleResponse::class.java
                                )
                                AppConstants.apply {
                                    RefreshSchedulePreview = true
                                    UpdateDaluaSchedule = true
                                    UpdateMySchedule = true
                                    UpdatePublicSchedule = true
                                }
                                showMessage(
                                    createScheduleResponse.message,
                                    createScheduleResponse.success
                                )
                                singleSchedule = createScheduleResponse.data
                                viewModel.singleSchedule.value = createScheduleResponse.data
                                if (viewModel.saveAndUpload == 0)
                                    Handler(Looper.getMainLooper()).postDelayed(
                                        { requireActivity().finish() },
                                        500
                                    )
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

    private fun findMacAddress(socketAckResponse: SocketACKResponseModel?) {
        Log.d(
            TAG,
            "findMacAddress: socket: " + socketAckResponse!!.macAddress + " | status: " + socketAckResponse.status
        )
        if (createScheduleActivityVM.launchFrom.value == "device") {
            if (createScheduleActivityVM.device.value!!.macAddress == socketAckResponse.macAddress && socketAckResponse.status == 1) {
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
                    it.macAddress == socketAckResponse.macAddress && socketAckResponse.status == 1
                }.forEach {
                    it.status = 1
                }
            }
            createScheduleActivityVM.progress.value = false
        }
    }

    private fun findAckMacAddress(socketAckResponse: SocketACKResponseModel?) {
        Log.d(
            TAG,
            "findMacAddress: API: " + socketAckResponse!!.macAddress + " | status" + socketAckResponse.status
        )
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
        createScheduleActivityVM.resendSchedule.value = false
        createScheduleActivityVM.isDialogOpen.value = true
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

    var showRampDialog: Dialog? = null

    private fun showRampPopUp() {

        val array: Array<String> = resources.getStringArray(R.array.timeArray)
        showRampDialog = Dialog(requireContext())
        if (showRampDialog!!.isShowing) showRampDialog!!.cancel()
        var recyclerView: RecyclerView?
        val adapter = ShowRampPopUpAdapter(array, this)
        showRampDialog!!.apply {
            setContentView(R.layout.item_select_ramptime)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(true)
//            window?.setWindowAnimations(R.style.DialogAnimation)
            val textview: TextView = findViewById(R.id.textView2)
            textview.text = getString(R.string.select_ramptime)
            recyclerView = findViewById(R.id.recyclerview)
            recyclerView?.adapter = adapter
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            show()

        }
    }

    override fun onStart() {
        super.onStart()

        if (GEOLOCATIONIDEasy != 0) {
            geoLocationId = GEOLOCATIONIDEasy
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

    private fun showCreateDialog() {
        if (shouldSave) {
            if (IsEditOrPreviewOrCreate.contentEquals("create")) {
                showSaveDialog()
            } else {
                if (ISGroupOrDevice.contentEquals("D")) {
                    if (singleSchedule!!.deviceId == DeviceOrGroupID) {
                        checkIfValidTimeLine(viewModel.saveAndUpload)
                    } else {
                        showSaveDialog()
                    }
                } else {
                    if (singleSchedule!!.groupId == DeviceOrGroupID) {
                        checkIfValidTimeLine(viewModel.saveAndUpload)
                    } else {
                        showSaveDialog()
                    }
                }
            }
        } else {
            showMessage("Select valid timeline", false)
        }
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
        val totalScheduleTime = if (listOfXValues[listOfXValues.size - 1] > 0)
            listOfXValues[listOfXValues.size - 1] + 1
        else
            listOfXValues[listOfXValues.size - 1]

        Log.d(TAG, "startQuickPlayApi: $totalScheduleTime")
        if (createScheduleActivityVM.isAwsConnected.value!!) {
            disableButtonsOnQuickPlay(false)
            viewModel.apply {

                val mRampTime = rampTime / 60
                if (rampTime == 0)
                    rampTime = 1
                val startValue = rampStart.value!!.split(":")[0].toInt()
                val endValue = rampEnd.value!!.split(":")[0].toInt()

                var totalScheduleTime = if (endValue - startValue > 0) {
                    endValue - startValue
                } else {
                    endValue - startValue + 24
                }
                totalScheduleTime += mRampTime

                arrayList = mutableListOf()

                val a = createScheduleActivityVM.croller1Progress.value!! / mRampTime
                val b = createScheduleActivityVM.croller2Progress.value!! / mRampTime
                val c = createScheduleActivityVM.croller3Progress.value!! / mRampTime

                var avalue = 0
                var bvalue = 0
                var cvalue = 0

                var rLimit = mRampTime - 1


                for (i in 0..23) {

                    when {
                        i < mRampTime -> {

                            avalue += a
                            bvalue += b
                            cvalue += c

                            arrayList.add(
                                ABCValuesModel(
                                    avalue.toString(),
                                    bvalue.toString(),
                                    cvalue.toString()
                                )
                            )

                        }
                        i <= (totalScheduleTime - mRampTime) -> {
                            arrayList.add(
                                ABCValuesModel(
                                    createScheduleActivityVM.croller1Progress.value!!.toString(),
                                    createScheduleActivityVM.croller2Progress.value!!.toString(),
                                    createScheduleActivityVM.croller3Progress.value!!.toString()
                                )
                            )

                        }
                        i > totalScheduleTime - mRampTime && i <= totalScheduleTime -> {
                            arrayList.add(arrayList[rLimit])
                            rLimit--
                            if (i > (totalScheduleTime - mRampTime) && i == totalScheduleTime)
                                arrayList.add(
                                    ABCValuesModel(
                                        "0", "0", "0"
                                    )
                                )
                        }
                        else -> {
                            arrayList.add(
                                ABCValuesModel(
                                    "0", "0", "0"
                                )
                            )
                        }
                    }
                }


                startAnimation((totalScheduleTime * 1000).toLong())

                binding.fillL.setDuration((totalScheduleTime * 1000).toLong())
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
                sendZeros()
                Handler(Looper.myLooper()!!).postDelayed(
                    {
                        sendDataToAwsMessage(totalScheduleTime + 1)
                    }, 10
                )

            }
        } else {
            showMessage("Wait till aws connected...", false)
            stopAnimation()
        }

    }

    private fun startAnimation(seconds: Long) {

        animate = ObjectAnimator.ofFloat(
            binding.animationView,
            "translationX",
            binding.chart1.width.toFloat()
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
                        binding.animationView.animate().translationX(0f).withEndAction {
                            binding.cardView10.isEnabled = true
                        }.duration = 1000
                        viewModel.animationStarted.value = false
                    }, 1000
                )
            } else {
                Log.d(TAG, "startAnimation: stopAnimation: else ")
                binding.cardView10.isEnabled = false
                binding.animationView.animate().translationX(0f).withEndAction {
                    binding.cardView10.isEnabled = true
                }.duration = 1000
                viewModel.animationStarted.value = false
                isCancel = false
            }

        }

    }

    private fun stopAnimation() {
        if (viewModel.animationStarted.value!!) {
            Log.d(TAG, "stopAnimation: true: ")
            viewModel.animationStarted.value = false
            isCancel = true
            animate?.cancel()
            binding.fillL.setDuration(1000)
            binding.fillL.setProgress(0, false)
        } else {
            Log.d(TAG, "stopAnimation: false: ")
        }
        disableButtonsOnQuickPlay(true)
//        if (ISGroupOrDevice.contentEquals("D"))
//            createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "device_id")
//        else
//            createScheduleActivityVM.reSendSchedule(DeviceOrGroupID, "group_id")
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

    private fun sendDataToAwsMessage(totalScheduleTime: Int) {

        if (secondNumber <= totalScheduleTime) {
            secondNumber++
            Handler(Looper.getMainLooper()).postDelayed({
                if (viewModel.animationStarted.value!!)
                    sendDataToAwsMessage(totalScheduleTime)
            }, 1000)
        } else {
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
            if (secondNumber - 1 < 24) {
                jsonObject.put("a_value", arrayList[secondNumber - 1].c)
                jsonObject.put("b_value", arrayList[secondNumber - 1].b)
                jsonObject.put("c_value", arrayList[secondNumber - 1].a)
            } else {
                jsonObject.put("a_value", "0")
                jsonObject.put("b_value", "0")
                jsonObject.put("c_value", "0")
            }
            val data = jsonObject.toString()
            if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                publishToTopic(data)
        } else {
            showMessage("Wait till aws connected...", false)
        }
    }

    private fun codeDataToAwsFormat() {
        Log.d(ChangeAdvanceValuesActivity.TAG, "codeDataToAwsFormate: ")
        if (createScheduleActivityVM.isAwsConnected.value!!) {
            val jsonObject = JSONObject()
            jsonObject.put("commandID", "3")
            jsonObject.put("deviceID", "1")

            if (ISGroupOrDevice.contentEquals("D"))
                jsonObject.put("macAddress", AppConstants.DeviceMacAdress)// no colon + lower case
            else
                jsonObject.put("macAddress", "1")// no colon + lower case

            jsonObject.put("isGroup", true)// if specific device in group then false
            jsonObject.put("timestamp", System.currentTimeMillis().toString())

            jsonObject.put("a_value", cValue)
            jsonObject.put("b_value", bValue)
            jsonObject.put("c_value", aValue)

            val data = jsonObject.toString()
            publishToTopic(data)
            Log.d(TAG, "a= " + aValue + "b= " + bValue + " c= " + cValue)
        } else {
            showMessage("Wait till aws connected...", false)
        }
    }

    private fun calculateStartRampValue(time: String) {

        val k = time.split(":")

        rampTime = k[0].toInt() * 60 + k[1].toInt()

        val rampStart = when {
            sunriseSec + rampTime >= 1440 -> sunriseSec + rampTime - 1440
            else -> sunriseSec + rampTime
        }

        if (rampStart > sunsetSec) {

            val sunSetTime = when {
                sunriseSec + rampTime >= 1440 -> sunriseSec + rampTime - 1440
                else -> sunriseSec + rampTime
            }
            viewModel.sunSet.value = String.format("%02d", sunSetTime / 60) + ":" + String.format(
                "%02d",
                sunSetTime % 60
            )
            sunsetSec = sunSetTime

        }


        val rampEnd = when {
            sunsetSec + rampTime >= 1440 -> sunsetSec + rampTime - 1440
            else -> sunsetSec + rampTime
        }


        viewModel.rampEnd.value =
            String.format("%02d", rampEnd / 60) + ":" + String.format("%02d", rampEnd % 60)

        viewModel.rampStart.value =
            String.format("%02d", rampStart / 60) + ":" + String.format("%02d", (rampStart % 60))

        viewModel.sunSetEnable.value = true
        viewModel.ramp.value = time

    }

    private fun endTimeDialog() {


        val timePickerPopupSunSet = TimePickerPopup.Builder()
            .from(requireContext())
            .offset(3)
            .textSize(20)
            .setTime(
                binding.sunsetTextview.text.toString().split(":")[0].toInt(),
                binding.sunsetTextview.text.toString().split(":")[1].toInt()
            )
            .listener { _, hour, minutee ->

                val minute =
                    when (minutee) {
                        0 -> 0
                        1 -> 15
                        2 -> 30
                        else -> 45
                    }


                val currentTime = hour * 60 + minute
                val mSunRiseTime = sunriseSec
                val sunRiseRamp: Int
                val sunSetRamp: Int

                // Checking the up limit for ramp
                when {
                    (mSunRiseTime + rampTime) >= 1440 -> {
                        sunRiseRamp = mSunRiseTime + rampTime - 1440
                        // when sunriseRamp is greater than 1440 then || cuz two limits
                        if (currentTime < sunRiseRamp || currentTime >= mSunRiseTime) {
                            showMessage("current time must be greater than sunrise + ramp", false)
                            return@listener
                        }
                    }
                    else -> {
                        // when its less than 1440 then and cuz single limits
                        sunRiseRamp = mSunRiseTime + rampTime
                        if (currentTime in (mSunRiseTime) until sunRiseRamp) {
                            showMessage("current time must be greater than sunrise + ramp.", false)
                            return@listener
                        }
                    }
                }

                //checking the down limit
                when {
                    mSunRiseTime - rampTime > 0 -> {
                        // when its greater than zero then and cuz single limits
                        sunSetRamp = mSunRiseTime - rampTime
                        if (currentTime in (sunSetRamp + 1) until mSunRiseTime) {
                            showMessage("current time must be Less than sunrise - ramp", false)
                            return@listener
                        }
                    }
                    else -> {
                        // when its less than zero then or cuz two limits
                        sunSetRamp = mSunRiseTime - rampTime + 1440
                        if (currentTime > sunSetRamp || currentTime < mSunRiseTime) {
                            showMessage("current time must be Less than sunrise - ramp", false)
                            return@listener
                        }
                    }
                }


                val currtime: Int = if ((currentTime + rampTime) >= 1440) {
                    currentTime + rampTime - 1440
                } else
                    currentTime + rampTime

                viewModel.rampEnd.value =
                    String.format("%02d", currtime / 60) + ":" + String.format(
                        "%02d",
                        currtime % 60
                    )

                viewModel.sunSet.value =
                    String.format("%02d", hour) + ":" + String.format("%02d", minute)

                sunsetSec = hour * 60 + minute
                shouldSave = true
                setData(
                    createScheduleActivityVM.croller1Progress.value!!.toFloat(),
                    createScheduleActivityVM.croller2Progress.value!!.toFloat(),
                    createScheduleActivityVM.croller3Progress.value!!.toFloat()
                )


                Log.d(TAG, "endTimeDialog: $sunriseSec  $sunsetSec  $rampTime")

            }
            .build()

        if (!timePickerPopupSunSet.isShowing)
            timePickerPopupSunSet.show()

    }

    private fun startTimeDialog() {

        val timePickerPopupSunRise: TimePickerPopup = TimePickerPopup.Builder()
            .from(requireContext())
            .offset(3)
            .textSize(20)
            .setTime(
                binding.sunRiseTextview.text.toString().split(":")[0].toInt(),
                binding.sunRiseTextview.text.toString().split(":")[1].toInt()
            )
            .listener { _, hour, minutee ->

                val minute =
                    when (minutee) {
                        0 -> 0
                        1 -> 15
                        2 -> 30
                        else -> 45
                    }


                sunriseSec = hour * 60 + minute

                sunRise = "$hour:$minute"
                viewModel.rampTimeEnable.value = true

                viewModel.sunRise.value =
                    String.format("%02d", hour) + ":" + String.format("%02d", minute)

                val rampStart = when {
                    sunriseSec + rampTime >= 1440 -> {
                        sunriseSec + rampTime - 1440
                    }
                    else -> sunriseSec + rampTime
                }

                viewModel.rampStart.value =
                    String.format("%02d", rampStart / 60) + ":" + String.format(
                        "%02d",
                        rampStart % 60
                    )

                Log.d(TAG, "startTimeDialog: $sunriseSec  $rampTime $sunsetSec")


                if (abs(sunriseSec - sunsetSec) < rampTime) {

                    if (sunriseSec + rampTime >= sunsetSec) {

                        val sunSetTime = when {
                            sunriseSec + rampTime >= 1440 -> {
                                sunriseSec + rampTime - 1440
                            }
                            else -> sunriseSec + rampTime
                        }
                        viewModel.sunSet.value = String.format("%02d", sunSetTime / 60) + ":" +
                                String.format(
                                    "%02d",
                                    sunSetTime % 60
                                )
                        sunsetSec = sunSetTime


                        val currtime: Int = if ((sunsetSec + rampTime) >= 1440) {
                            sunsetSec + rampTime - 1440
                        } else
                            sunsetSec + rampTime

                        viewModel.rampEnd.value =
                            String.format("%02d", currtime / 60) + ":" + String.format(
                                "%02d",
                                currtime % 60
                            )
                    }
                }
                setData(
                    createScheduleActivityVM.croller1Progress.value!!.toFloat(),
                    createScheduleActivityVM.croller2Progress.value!!.toFloat(),
                    createScheduleActivityVM.croller3Progress.value!!.toFloat()
                )
                Log.d(TAG, "endTimeDialog: $sunriseSec  $sunsetSec  $rampTime")


            }.build()
        if (!timePickerPopupSunRise.isShowing)
            timePickerPopupSunRise.show()

    }

    private fun initChart() {

        // // Chart Style // //
        chart = binding.chart1

        // background color
        chart.setBackgroundColor(Color.WHITE)

        // disable description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(false)
        chart.minOffset = 0f
        // set listeners
        chart.setDrawGridBackground(false)

        // enable scaling and dragging
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)

        // force pinch zoom along both axis
        chart.setPinchZoom(false)

        // // X-Axis Style // //
        val xAxis: XAxis = chart.xAxis
        xAxis.axisLineColor = Color.rgb(128, 128, 255)

        xAxis.setDrawGridLines(false)
        xAxis.gridLineWidth = 2f
        xAxis.gridColor = resources.getColor(R.color.white, activity?.theme)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setLabelCount(4, true)
        xAxis.setDrawLabels(false)
        xAxis.axisLineWidth = 0.1f

        // // Y-Axis Style // //
        val yAxis: YAxis = chart.axisLeft
        yAxis.gridLineWidth = 0.5f

        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false

        // axis range
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f
        yAxis.setLabelCount(6, true)

        // Create Limit Lines //
        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        if (defaultScData)
            setData(
                singleSchedule!!.easySlots.valueA!!.toFloat(),
                singleSchedule!!.easySlots.valueB!!.toFloat(),
                singleSchedule!!.easySlots.valueC!!.toFloat()
            )
        else
            setData(0f, 0f, 0f)

        // draw points over time
        chart.animateX(1500, Easing.EaseInBack)
//        chart.animateX(1500, Easing.EaseInSine)
        // get the legend (only possible after setting data)
        chart.legend.isEnabled = false

    }

    private fun setData(valueA: Float, valueB: Float, valueC: Float) {

        if (screenWidth != 0) {
            graphPC = ((screenWidth * (AppConstants.screenPC)) / 100).toFloat()
        }


        val k = viewModel.sunRise.value!!.split(":")
        val n = viewModel.rampEnd.value!!.split(":")
        val l = viewModel.sunSet.value!!.split(":")
        val m = viewModel.rampStart.value!!.split(":")

        if (listOfXValues.isNotEmpty())
            listOfXValues.clear()

        val sunSetAndSunRise =
            differenceTime(Time(l[0].toInt(), l[1].toInt(), 0), Time(k[0].toInt(), k[1].toInt(), 0))
        val sunRiseAndRampStart =
            differenceTime(Time(m[0].toInt(), m[1].toInt(), 0), Time(k[0].toInt(), k[1].toInt(), 0))
        val completeScheduleTime =
            differenceTime(Time(n[0].toInt(), n[1].toInt(), 0), Time(k[0].toInt(), k[1].toInt(), 0))

        listOfXValues.add(0f)

        val srrM = getMinutes(sunRiseAndRampStart.minutes)

        listOfXValues.add(sunRiseAndRampStart.hours + srrM)

        val ssrM = getMinutes(sunSetAndSunRise.minutes)

//        if (sunSetAndSunRise.hours == 23)
//            listOfXValues.add(22 + ssrM)
//        else
        listOfXValues.add(sunSetAndSunRise.hours + ssrM)
        val lastIndex = getMinutes(completeScheduleTime.minutes)
        if (completeScheduleTime.hours == 0)
            listOfXValues.add(24 + lastIndex)
        else
            listOfXValues.add(completeScheduleTime.hours + lastIndex)

        var hasToRemove = false
        if (listOfXValues[1] == listOfXValues[2]) {
            hasToRemove = true
        }

        binding.chart1.xAxis.setLabelCount(4, true)

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
        binding.chart1.getLocationOnScreen(location)

        screenWidth = binding.parentLayout.width

        val chartWidth = screenWidth - (0.14) * screenWidth
        val startedGap = 0.07 * screenWidth
        val singleValue = chartWidth / (listOfXValues[listOfXValues.size - 1])
        val sunRiseRampX = (startedGap + (singleValue * listOfXValues[1])).toFloat()
        val sunSetX = (startedGap + (singleValue * listOfXValues[2])).toFloat()
//        22f

        binding.sunriseRampLayout.x = sunRiseRampX - graphPC

        if (hasToRemove) {
            binding.sunsetLayout.x =
                startedGap.toFloat() + (chartWidth / 2).toFloat() - graphPC
        } else
            binding.sunsetLayout.x = sunSetX - graphPC


        val set2: LineDataSet?
        val set3: LineDataSet?
        val set1: LineDataSet?

        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {

            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set2 = chart.data.getDataSetByIndex(1) as LineDataSet
            set2.values = values2
            set3 = chart.data.getDataSetByIndex(2) as LineDataSet
            set3.values = values3

            set2.notifyDataSetChanged()
            set1.notifyDataSetChanged()
            set3.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
            chart.invalidate()

        } else {

            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)
            set2 = LineDataSet(values2, "")
            set2.setDrawIcons(false)
            set3 = LineDataSet(values3, "")
            set3.setDrawIcons(false)

            // black lines and points
            setGraphLinesColor(chart, set1, set2, set3)

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
            val drawable2 =
                ContextCompat.getDrawable(requireContext(), R.drawable.set2_gradient)
            set2.fillDrawable = drawable2
            val drawable3 =
                ContextCompat.getDrawable(requireContext(), R.drawable.set3_gradient)
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
        Log.d(TAG, "showSaveDialog: ")
        val showScheduleDialog = Dialog(requireContext())
        if (showScheduleDialog.isShowing) showScheduleDialog.dismiss()
        showScheduleDialog.apply {
            setContentView(R.layout.item_save_schedule)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(true)
//            window?.setWindowAnimations(R.style.DialogAnimation)
            findViewById<Button>(R.id.button1).setOnClickListener {
                isPublic = if (findViewById<SwitchCompat>(R.id.isPublicSwitch).isChecked) {
                    1
                } else {
                    0
                }
                if (findViewById<EditText>(R.id.edittext).text.toString().isNotEmpty()) {
                    showScheduleDialog.dismiss()
                    scheduleName = findViewById<EditText>(R.id.edittext).text.toString()
                    checkIfValidTimeLine(viewModel.saveAndUpload)

                } else {
                    showMessage("Please enter schedule name.", false)
                }
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
        }
        showScheduleDialog.show()

    }

    private fun checkIfValidTimeLine(saveAndUpload: Int) {

        val k = viewModel.sunRise.value!!.split(":")
        val l = viewModel.sunSet.value!!.split(":")
        val m = viewModel.rampStart.value!!.split(":")
        val n = viewModel.rampEnd.value!!.split(":")

        val sunRiseAndRampStart =
            difference(Time(m[0].toInt(), m[1].toInt(), 0), Time(k[0].toInt(), k[1].toInt(), 0))
        val sunSetAndRampEnd =
            difference(Time(n[0].toInt(), n[1].toInt(), 0), Time(l[0].toInt(), l[1].toInt(), 0))
        val allScheduleTime =
            difference(Time(n[0].toInt(), n[1].toInt(), 0), Time(k[0].toInt(), k[1].toInt(), 0))

        if (allScheduleTime.toInt() > 1440) {
            showMessage("Schedule must be less than 24hr", false)
            return
        } else if (sunRiseAndRampStart == sunSetAndRampEnd) {

            if (IsEditOrPreviewOrCreate.contentEquals("create"))
                makeCreateApiCall(saveAndUpload)
            else {
                if (ISGroupOrDevice.contentEquals("D")) {
                    if (singleSchedule!!.deviceId == DeviceOrGroupID) {
                        makeUpdateApiCall(saveAndUpload)
                    } else {
                        makeCreateApiCall(saveAndUpload)
                    }
                } else {
                    if (singleSchedule!!.groupId == DeviceOrGroupID) {
                        makeUpdateApiCall(saveAndUpload)
                    } else {
                        makeCreateApiCall(saveAndUpload)
                    }
                }

            }

        }

    }

    private fun checkIfValidTimeLine(saveAndUpload: Int, name: String) {

        val k = viewModel.sunRise.value!!.split(":")
        val l = viewModel.sunSet.value!!.split(":")
        val m = viewModel.rampStart.value!!.split(":")
        val n = viewModel.rampEnd.value!!.split(":")

        val sunRiseAndRampStart =
            difference(Time(m[0].toInt(), m[1].toInt(), 0), Time(k[0].toInt(), k[1].toInt(), 0))
        val sunSetAndRampEnd =
            difference(Time(n[0].toInt(), n[1].toInt(), 0), Time(l[0].toInt(), l[1].toInt(), 0))
        val allScheduleTime =
            difference(Time(n[0].toInt(), n[1].toInt(), 0), Time(k[0].toInt(), k[1].toInt(), 0))

        if (allScheduleTime.toInt() > 1440) {
            showMessage("Schedule must be less than 24hr", false)
            return
        } else if (sunRiseAndRampStart == sunSetAndRampEnd) {

            if (IsEditOrPreviewOrCreate.contentEquals("create")) {
                if (name.isEmpty()) {
                    makeCreateApiCall(saveAndUpload)
                } else {
                    if (ISGroupOrDevice.contentEquals("D")) {
                        if (singleSchedule != null) {
                            if (singleSchedule!!.deviceId == DeviceOrGroupID) {
                                makeUpdateApiCall(saveAndUpload)
                            } else {
                                makeCreateApiCall(saveAndUpload)
                            }
                        } else {
                            makeCreateApiCall(saveAndUpload)
                        }
                    } else {
                        if (singleSchedule != null) {
                            if (singleSchedule!!.groupId == DeviceOrGroupID) {
                                makeUpdateApiCall(saveAndUpload)
                            } else {
                                makeCreateApiCall(saveAndUpload)
                            }
                        } else {
                            makeUpdateApiCall(saveAndUpload)
                        }
                    }
                }
            } else {
                if (ISGroupOrDevice.contentEquals("D")) {
                    if (singleSchedule!!.deviceId == DeviceOrGroupID) {
                        makeUpdateApiCall(saveAndUpload)
                    } else {
                        makeCreateApiCall(saveAndUpload)
                    }
                } else {
                    if (singleSchedule!!.groupId == DeviceOrGroupID) {
                        makeUpdateApiCall(saveAndUpload)
                    } else {
                        makeCreateApiCall(saveAndUpload)
                    }
                }

            }

        }

    }

    // create new api call
    private fun makeCreateApiCall(enable: Int) {

        val geoLocationEnabled = if (binding.geolocationSwitch.isChecked)
            1 else 0

//        val geoLocationEnabled = if (viewModel.geoLocationEnabled.value!!)
//            1 else 0

        val ramptime = String.format("%02d", (rampTime) / 60) + ":" + String.format(
            "%02d",
            (rampTime) % 60
        )
        val hashmap: HashMap<String, String> = HashMap()
        if (ISGroupOrDevice.contentEquals("D"))
            hashmap["device_id"] = DeviceOrGroupID.toString()
        else
            hashmap["group_id"] = DeviceOrGroupID.toString()


        val x =
            createScheduleActivityVM.croller1Progress.value.toString() + ":" + createScheduleActivityVM.croller2Progress.value + ":" + createScheduleActivityVM.croller3Progress.value
        Log.d(TAG, "api call before: $x")

        viewModel.apply {

            createScheduleApi(
                scheduleName,
                "0",
                isPublic,
                hashmap, //should be the id of schedule with the device
                geoLocationEnabled,
                geoLocationId,
                enable,
                1,
                sunRise.value + ":00",
                sunSet.value + ":00",
                createScheduleActivityVM.croller1Progress.value.toString(),
                createScheduleActivityVM.croller2Progress.value.toString(),
                createScheduleActivityVM.croller3Progress.value.toString(),
                ramptime
            )

            if (enable == 1)
                showDialogForAckReceiving()

        }
    }

    private fun makeUpdateApiCall(enable: Int) {

        val geoLocationEnabled = if (binding.geolocationSwitch.isChecked)
            1 else 0

//        val geoLocationEnabled = if (viewModel.geoLocationEnabled.value!!)
//            1 else 0

        val ramptime = String.format("%02d", (rampTime) / 60) + ":" + String.format(
            "%02d",
            (rampTime) % 60
        )
        val hashmap: HashMap<String, String> = HashMap()
        if (ISGroupOrDevice.contentEquals("D"))
            hashmap["device_id"] = DeviceOrGroupID.toString()
        else
            hashmap["group_id"] = DeviceOrGroupID.toString()

        val x =
            createScheduleActivityVM.croller1Progress.value.toString() + ":" + createScheduleActivityVM.croller2Progress.value + ":" + createScheduleActivityVM.croller3Progress.value
        Log.d(TAG, "api call before: $x")


        viewModel.apply {

            updateScheduleApi(
                scheduleName,
                "0",
                hashmap,
                isPublic,
                singleSchedule.value!!.id,
//                ScheDuleID, //should be the id of schedule with the device
                geoLocationEnabled,
                geoLocationId,
                enable,
                1,
                sunRise.value + ":00",
                sunSet.value + ":00",
                createScheduleActivityVM.croller1Progress.value.toString(),
                createScheduleActivityVM.croller2Progress.value.toString(),
                createScheduleActivityVM.croller3Progress.value.toString(),
                ramptime
            )
            if (enable == 1)
                showDialogForAckReceiving()

        }
    }

    private fun publishToTopic(data: String) {

        val topic: String = when {
            ISGroupOrDevice.contentEquals("G") -> GroupTopic
            else -> DeviceTopic
        }


        try {
            createScheduleActivityVM.mqttManager!!.publishString(
                data,
                topic,
                AWSIotMqttQos.QOS0,
                { _, _ ->
                    activity?.runOnUiThread {
                        Log.d(TAG, "Easy Publish data.$data")
                    }
                },
                "Check if data is given"
            )
        } catch (e: java.lang.Exception) {
            publishToTopic(data)
            Log.d(TAG, "Publish error." + e.localizedMessage)
        }

    }

    class ShowRampPopUpAdapter(val array: Array<String>, private val mInterface: EasyFragment) :
        RecyclerView.Adapter<ShowRampPopUpAdapter.PopUpViewHolder>() {

        class PopUpViewHolder(val item: ItemRamptimeRvBinding) :
            RecyclerView.ViewHolder(item.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpViewHolder {
            val itemBinding: ItemRamptimeRvBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_ramptime_rv,
                parent,
                false
            )
            return PopUpViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: PopUpViewHolder, position: Int) {

            holder.item.textView1.text = array[position]
            holder.itemView.setOnClickListener {
                (mInterface as TimeRampListner).onRampTimeClicked(array[position])

            }
        }

        override fun getItemCount(): Int {
            return array.size
        }

    }

    override fun onRampTimeClicked(time: String) {
        if (showRampDialog != null)
            showRampDialog!!.dismiss()
        calculateStartRampValue(time)
        setData(
            createScheduleActivityVM.croller1Progress.value!!.toFloat(),
            createScheduleActivityVM.croller2Progress.value!!.toFloat(),
            createScheduleActivityVM.croller3Progress.value!!.toFloat()
        )
    }

    override fun onUploadSchedule() {
        Log.d(TAG, "onUploadSchedule: ")
        showCreateDialog()
    }

    override fun onReUploadSchedule() {
        Log.d(TAG, "onReUploadSchedule: ")
        makeUpdateApiCall(viewModel.saveAndUpload)
    }

    override fun onFinish(finishBack: Boolean) {
        if (finishBack) {
//            createScheduleActivityVM.isDialogOpen.value = false
            Handler(Looper.myLooper()!!).postDelayed({ activity?.finish() }, 100)
        }
    }

    companion object {
        const val TAG = "EasyFragment"
    }

}

