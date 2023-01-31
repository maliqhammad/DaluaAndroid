package com.dalua.app.ui.createschedule.fragments.mastercontrol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dalua.app.R
import com.dalua.app.databinding.FragmentMasterControlBinding
import com.dalua.app.ui.createschedule.CreateScheduleActivityVM
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.ProjectUtil
import com.sdsmdg.harjot.crollerTest.Croller
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MasterControlFragment(val waterType: String) : Fragment() {

    lateinit var binding: FragmentMasterControlBinding
    val viewModel: MasterControlFragmentVM by viewModels()
    private val createScheduleActivityVM: CreateScheduleActivityVM by activityViewModels()
    var shouldIncrease = true
    var shouldDecrease = true
    var previousKnobProgress = 0
    var ifIstTimeKnob = arrayListOf("d")
    var maxValue: Int = 0
    var minValue: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_master_control, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        initObject()
    }

    override fun onResume() {
        super.onResume()

        viewModel.aValue.value = createScheduleActivityVM.croller1Progress.value.toString()

        viewModel.bValue.value =
            createScheduleActivityVM.croller2Progress.value.toString()
        viewModel.cValue.value = createScheduleActivityVM.croller3Progress.value.toString()

        setMaxMinForCircularKnobe()


    }


    private fun initObject() {
        viewModel.waterType.value = waterType
    }

    private fun observers() {

        viewModel.aValue.value = createScheduleActivityVM.croller1Progress.value.toString()
        viewModel.bValue.value = createScheduleActivityVM.croller2Progress.value.toString()
        viewModel.cValue.value = createScheduleActivityVM.croller3Progress.value.toString()



        createScheduleActivityVM.croller1Progress.observe(viewLifecycleOwner) {
            viewModel.aValue.value = it.toString()
        }

        createScheduleActivityVM.croller2Progress.observe(viewLifecycleOwner) {
            viewModel.bValue.value = it.toString()
        }

        createScheduleActivityVM.croller3Progress.observe(viewLifecycleOwner) {
            viewModel.cValue.value = it.toString()
        }


        val myset: MutableList<Int> = mutableListOf(
            createScheduleActivityVM.croller1Progress.value!!,
            createScheduleActivityVM.croller2Progress.value!!,
            createScheduleActivityVM.croller3Progress.value!!
        )

        val smallest = myset.minOrNull()
        val big = myset.maxOrNull()

        binding.croller.progress = smallest!!
        binding.croller.max = 100 - big!! + smallest

        binding.croller.setOnCrollerChangeListener(object : OnCrollerChangeListener {
            override fun onProgressChanged(croller: Croller?, progress: Int) {

                if (ifIstTimeKnob.isEmpty()) {
                    if (progress > previousKnobProgress) {
                        if (shouldIncrease) {

                            createScheduleActivityVM.croller1Progress.value =
                                createScheduleActivityVM.croller1Progress.value!!.toInt() + progress - previousKnobProgress
                            createScheduleActivityVM.croller2Progress.value =
                                createScheduleActivityVM.croller2Progress.value!!.toInt() + progress - previousKnobProgress
                            createScheduleActivityVM.croller3Progress.value =
                                createScheduleActivityVM.croller3Progress.value!!.toInt() + progress - previousKnobProgress

                            viewModel.aValue.value =
                                createScheduleActivityVM.croller1Progress.value.toString()
                            viewModel.bValue.value =
                                createScheduleActivityVM.croller2Progress.value.toString()
                            viewModel.cValue.value =
                                createScheduleActivityVM.croller3Progress.value.toString()

                        }

                    } else {
                        if (shouldDecrease) {
                            createScheduleActivityVM.croller1Progress.value =
                                createScheduleActivityVM.croller1Progress.value!!.toInt() + progress - previousKnobProgress
                            createScheduleActivityVM.croller2Progress.value =
                                createScheduleActivityVM.croller2Progress.value!!.toInt() + progress - previousKnobProgress
                            createScheduleActivityVM.croller3Progress.value =
                                createScheduleActivityVM.croller3Progress.value!!.toInt() + progress - previousKnobProgress

                            viewModel.aValue.value =
                                createScheduleActivityVM.croller1Progress.value.toString()

                            viewModel.bValue.value =
                                createScheduleActivityVM.croller2Progress.value.toString()
                            viewModel.cValue.value =
                                createScheduleActivityVM.croller3Progress.value.toString()
                        }
                    }

                    previousKnobProgress = progress
                    setMaxMinForCircularKnobe()

                    AppConstants.ISFROMMASTER = true

                    val text =
                        viewModel.aValue.value + ":" + viewModel.bValue.value + ":" + viewModel.cValue.value
                    ProjectUtil.putCrollerValues(requireContext(), text)
                } else {
                    ifIstTimeKnob.remove("d")
                }
            }

            override fun onStartTrackingTouch(croller: Croller?) {

            }

            override fun onStopTrackingTouch(croller: Croller?) {
                createScheduleActivityVM.sendDataToBleNow.value = true
            }
        })

    }

    private fun setMaxMinForCircularKnobe() {

        val list = arrayListOf(
            viewModel.aValue.value!!.toInt(),
            viewModel.bValue.value!!.toInt(),
            viewModel.cValue.value!!.toInt()
        )


        val maxObj: Int? = list.maxByOrNull { it }
        val minObj: Int? = list.minByOrNull { it }
        maxValue = 100 - maxObj!! + minObj!!
        minValue = minObj
        previousKnobProgress = minValue

        when {
            maxValue < 40 -> {
                binding.croller.progressPrimaryCircleSize = 8f
                binding.croller.progressSecondaryCircleSize = 8f
            }
            maxValue in 40..60 -> {
                binding.croller.progressPrimaryCircleSize = 5f
                binding.croller.progressSecondaryCircleSize = 5f
            }
            maxValue in 61..90 -> {
                binding.croller.progressPrimaryCircleSize = 4f
                binding.croller.progressSecondaryCircleSize = 4f
            }
            else -> {
                binding.croller.progressPrimaryCircleSize = 3f
                binding.croller.progressSecondaryCircleSize = 3f
            }
        }

        ifIstTimeKnob.add("d")
        binding.croller.max = maxValue
        binding.croller.progress = minValue
        shouldIncrease = !list.contains(100)
        shouldDecrease = !list.contains(0)
    }


}