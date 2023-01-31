package com.dalua.app.ui.createschedule.fragments.instantcontrol

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dalua.app.R
import com.dalua.app.baseclasses.BaseFragment
import com.dalua.app.databinding.FragmentInstantControlBinding
import com.dalua.app.models.Configuration
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.ui.createschedule.CreateScheduleActivityVM
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.ProjectUtil
import com.sdsmdg.harjot.crollerTest.Croller
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.abs


@AndroidEntryPoint
class InstantControlFragment(val waterType: String, val configuration: Configuration) :
    BaseFragment() {

    lateinit var binding: FragmentInstantControlBinding
    val viewModel: InstantControlFragmentVM by viewModels()
    private val activityVM: CreateScheduleActivityVM by activityViewModels()
    var crollerOneIstTime = true
    var crollerTwoIstTime = true
    var singleSchedule: SingleSchedule? = null
    var crollerThreeIstTime = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_instant_control, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.croller.progress = activityVM.croller1Progress.value!!
        binding.croller1.progress = activityVM.croller2Progress.value!!
        binding.croller2.progress = activityVM.croller3Progress.value!!
        viewModel.aValue.value = activityVM.croller1Progress.value.toString()
        viewModel.bValue.value = activityVM.croller2Progress.value.toString()
        viewModel.cValue.value = activityVM.croller3Progress.value.toString()
        viewModel.waterType.value = waterType
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeners()
        initObjects()
        setData()

    }

    private fun setData() {

        if (requireActivity().intent.hasExtra("schedule")) {

            singleSchedule =
                ProjectUtil.stringToObject(
                    requireActivity().intent.getStringExtra("schedule"),
                    SingleSchedule::class.java
                )

//            if (!singleSchedule?.name.contentEquals("Default Schedule"))
            if (!singleSchedule?.name.contentEquals("Default Schedule")) {
                if (singleSchedule?.mode.contentEquals("1"))
                    setDefaultScheduleData()
            } else {
                binding.croller.progress = 2
                binding.croller1.progress = 2
                binding.croller2.progress = 2
            }
        }
    }

    private fun setDefaultScheduleData() {
        binding.croller.progress = singleSchedule!!.easySlots.valueA!!.toInt()
        binding.croller1.progress = singleSchedule!!.easySlots.valueB!!.toInt()
        binding.croller2.progress = singleSchedule!!.easySlots.valueC!!.toInt()

    }

    private fun initObjects() {
        setValuesCroller1(0)
        setValuesCroller2(0)
        setValuesCroller3(0)
    }

    private fun listeners() {

        binding.croller.setOnCrollerChangeListener(object : OnCrollerChangeListener {
            override fun onProgressChanged(croller: Croller?, progress: Int) {
                if (!crollerOneIstTime || singleSchedule != null) {
                    viewModel.aValue.value = progress.toString()
                    activityVM.croller1Progress.value = progress
                    setValuesCroller1(progress)
                } else
                    crollerOneIstTime = false
            }

            override fun onStartTrackingTouch(croller: Croller?) {
            }

            override fun onStopTrackingTouch(croller: Croller?) {
                activityVM.sendDataToBleNow.value = true
                Log.d("vvvvvv", "onStopTrackingTouch: " + croller?.progress)
            }
        })

        binding.croller1.setOnCrollerChangeListener(object : OnCrollerChangeListener {
            override fun onProgressChanged(croller: Croller?, progress: Int) {
                if (!crollerTwoIstTime || singleSchedule != null) {
                    viewModel.bValue.value = progress.toString()
                    activityVM.croller2Progress.value = progress
                    setValuesCroller2(progress)
                } else
                    crollerTwoIstTime = false
            }

            override fun onStartTrackingTouch(croller: Croller?) {
            }

            override fun onStopTrackingTouch(croller: Croller?) {
                activityVM.sendDataToBleNow.value = true
            }
        })

        binding.croller2.setOnCrollerChangeListener(object : OnCrollerChangeListener {
            override fun onProgressChanged(croller: Croller?, progress: Int) {
                if (!crollerThreeIstTime || singleSchedule != null) {
                    viewModel.cValue.value = progress.toString()
                    activityVM.croller3Progress.value = progress
                    setValuesCroller3(progress)
                } else
                    crollerThreeIstTime = false
            }

            override fun onStartTrackingTouch(croller: Croller?) {
            }

            override fun onStopTrackingTouch(croller: Croller?) {
                activityVM.sendDataToBleNow.value = true
            }
        })

    }

    private fun setValuesCroller1(value: Int) {
        if (waterType.lowercase(Locale.getDefault()) == "marine") {
            val k = ColorUtils.setAlphaComponent(Color.argb(106, 102, 0, 204), abs(value))
            binding.image1.setColorFilter(k, PorterDuff.Mode.ADD)
            binding.image1.setImageResource(R.drawable.picture_one)
        } else {
            val k = ColorUtils.setAlphaComponent(Color.argb(106, 102, 0, 204), abs(value))
            binding.image1.setColorFilter(k, PorterDuff.Mode.ADD)
            binding.image1.setImageResource(R.drawable.picture_four)
        }
        binding.ledImage1.setImageDrawable(setSmallLedColor(configuration.channelA[0].hex))
        binding.ledImage2.setImageDrawable(setSmallLedColor(configuration.channelA[1].hex))
        binding.ledImage3.setImageDrawable(setSmallLedColor(configuration.channelA[2].hex))
        binding.ledImage4.setImageDrawable(setSmallLedColor(configuration.channelA[3].hex))
        if (value < 100) {
            binding.gradientImage1.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelA[0].hex
                )
            )
            binding.gradientImage2.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelA[1].hex
                )
            )
            binding.gradientImage3.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelA[2].hex
                )
            )
            binding.gradientImage4.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelA[3].hex
                )
            )
        }
    }

    private fun setValuesCroller2(value: Int) {
        if (waterType.lowercase(Locale.getDefault()) == "marine") {
            val k = ColorUtils.setAlphaComponent(Color.argb(13, 48, 191, 255), abs(value))
            binding.image2.setColorFilter(k, PorterDuff.Mode.ADD)
            binding.image2.setImageResource(R.drawable.picture_two)
        } else {
            val k = ColorUtils.setAlphaComponent(Color.argb(13, 48, 191, 255), abs(value))
            binding.image2.setColorFilter(k, PorterDuff.Mode.ADD)
            binding.image2.setImageResource(R.drawable.picture_five)
        }
        binding.ledImage5.setImageDrawable(setSmallLedColor(configuration.channelB[0].hex))
        binding.ledImage6.setImageDrawable(setSmallLedColor(configuration.channelB[1].hex))
        binding.ledImage7.setImageDrawable(setSmallLedColor(configuration.channelB[2].hex))
        binding.ledImage8.setImageDrawable(setSmallLedColor(configuration.channelB[3].hex))
        if (value < 100) {
            binding.gradientImage5.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelB[0].hex
                )
            )
            binding.gradientImage6.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelB[1].hex
                )
            )
            binding.gradientImage7.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelB[2].hex
                )
            )
            binding.gradientImage8.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelB[3].hex
                )
            )
        }
    }

    private fun setValuesCroller3(value: Int) {
        if (waterType.lowercase(Locale.getDefault()) == "marine") {
            val k = ColorUtils.setAlphaComponent(
                Color.argb(255, 255, 255, 255),
                abs(value * 0.8).toInt()
            )
            binding.image3.setColorFilter(k, PorterDuff.Mode.ADD)
            binding.image3.setImageResource(R.drawable.picture_three)
        } else {
            val k = ColorUtils.setAlphaComponent(
                Color.argb(255, 255, 255, 255),
                abs(value * 0.8).toInt()
            )
            binding.image3.setColorFilter(k, PorterDuff.Mode.ADD)
            binding.image3.setImageResource(R.drawable.picture_six)
        }
        binding.ledImage9.setImageDrawable(setSmallLedColor(configuration.channelC[0].hex))
        binding.ledImage10.setImageDrawable(setSmallLedColor(configuration.channelC[1].hex))
        binding.ledImage11.setImageDrawable(setSmallLedColor(configuration.channelC[2].hex))
        binding.ledImage12.setImageDrawable(setSmallLedColor(configuration.channelC[3].hex))
        if (value < 100) {
            binding.gradientImage9.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelC[0].hex
                )
            )
            binding.gradientImage10.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelC[1].hex
                )
            )
            binding.gradientImage11.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelC[2].hex
                )
            )
            binding.gradientImage12.setImageDrawable(
                setLedGradientColor(
                    value,
                    configuration.channelC[3].hex
                )
            )

        }
    }

    override fun onResume() {
        super.onResume()
        val x = ProjectUtil.getCrollerValues(requireContext(), null)
        if (AppConstants.ISFROMMASTER && x != null) {
            binding.croller.progress = x.split(":")[0].toInt()
            binding.croller1.progress = x.split(":")[1].toInt()
            binding.croller2.progress = x.split(":")[2].toInt()
            AppConstants.ISFROMMASTER = false
        }

    }

}