package com.dalua.app.ui.listschedule.fragments.daluaschedules

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseFragment
import com.dalua.app.databinding.FragmentDaluaScheduleBinding
import com.dalua.app.models.schedulemodel.ScheduleResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.ui.createschedule.CreateScheduleActivity
import com.dalua.app.ui.listschedule.ScheduleListActivityVM
import com.dalua.app.utils.AppConstants.ApiTypes.GetDaluaSchedules
import com.dalua.app.utils.AppConstants.Companion.UpdateDaluaSchedule
import com.dalua.app.utils.PaginationScrollListener
import com.dalua.app.utils.ProjectUtil
import com.dalua.app.utils.WrapContentLinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DaluaScheduleFragment : BaseFragment() {

    lateinit var binding: FragmentDaluaScheduleBinding
    lateinit var daluaScheduleAdapter: DaluaScheduleAdapter
    val viewModel: DaluaScheduleVM by viewModels()
    private val scheduleListActivityVM: ScheduleListActivityVM by activityViewModels()

    private var isLoading = false
    private var isLastPage = false
    private var currentPage: Int = 1
    private var totalPage: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dalua_schedule, container, false)

        binding.lifecycleOwner = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObjects()
        apiResponses()
        observers()

    }

    override fun onResume() {
        super.onResume()
        if (UpdateDaluaSchedule) {
            UpdateDaluaSchedule = false
        }
        setRecyclerView()
        daluaScheduleAdapter.clearList()
        viewModel.loadFirstPage()
    }

    private fun initObjects() {

        initializeProgressDialog()
        viewModel.waterType.value = scheduleListActivityVM.waterType.value
        viewModel.configuration.value = scheduleListActivityVM.configuration.value

    }

    private fun apiResponses() {


        viewModel.apiResponses.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Error -> {
                    hideWorking()

                    when (it.api_Type) {
                        GetDaluaSchedules.name -> {

                        }
                    }

                }
                is Resource.Loading -> {
                    showWorking("Dalua")
                }
                is Resource.Success -> {
                    hideWorking()

                    when (it.api_Type) {
                        GetDaluaSchedules.name -> {
                            it.data?.let { response ->
                                val scheduleResponse = ProjectUtil.stringToObject(
                                    response.string(),
                                    ScheduleResponse::class.java
                                )
                                currentPage = scheduleResponse.data.currentPage
                                totalPage = scheduleResponse.data.lastPage
                                setAdapter(scheduleResponse.data.data)
                            }
                        }
                    }

                }
            }

        }

    }

    private fun observers() {

        viewModel.previewClicked.observe(viewLifecycleOwner) {
            if (it.mode!!.contentEquals("1")) {
                if (scheduleListActivityVM.launchFrom.value == "device") {
                    CreateScheduleActivity.launchEasyPreviewFromDevice(
                        requireContext(),
                        scheduleListActivityVM.device.value!!,
                        it,
                        viewModel.waterType.value!!,
                        viewModel.configuration.value!!,
                        0,
                        scheduleListActivityVM.aquarium.value!!
                    )
                } else if (scheduleListActivityVM.launchFrom.value == "group") {
                    CreateScheduleActivity.launchEasyPreviewFromGroup(
                        requireContext(),
                        scheduleListActivityVM.group.value!!,
                        it,
                        viewModel.waterType.value!!,
                        viewModel.configuration.value!!,
                        0,
                        scheduleListActivityVM.aquarium.value!!
                    )
                }
            } else {
                if (scheduleListActivityVM.launchFrom.value == "device") {
                    CreateScheduleActivity.launchAdvancePreviewFromDevice(
                        requireContext(),
                        scheduleListActivityVM.device.value!!,
                        it,
                        viewModel.waterType.value!!,
                        viewModel.configuration.value!!,
                        0,
                        scheduleListActivityVM.aquarium.value!!
                    )
                } else if (scheduleListActivityVM.launchFrom.value == "group") {
                    CreateScheduleActivity.launchAdvancePreviewFromGroup(
                        requireContext(),
                        scheduleListActivityVM.group.value!!,
                        it,
                        viewModel.waterType.value!!,
                        viewModel.configuration.value!!,
                        0,
                        scheduleListActivityVM.aquarium.value!!
                    )
                }
            }
        }

        scheduleListActivityVM.geoLocation.observe(viewLifecycleOwner) {
            viewModel.geoLocation.value = it
        }

    }

    private fun setAdapter(list: MutableList<SingleSchedule>) {
        Log.d(TAG, "setAdapter: currentPage: $currentPage")
        daluaScheduleAdapter.removeLoadingFooter()
        isLoading = false
        daluaScheduleAdapter.addAll(list)
        if (currentPage != totalPage) {
            daluaScheduleAdapter.addLoadingFooter()
        } else {
            isLastPage = true
        }
    }

    private fun setRecyclerView() {
        val linearLayoutManager = WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        daluaScheduleAdapter = DaluaScheduleAdapter(viewModel)
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.adapter = daluaScheduleAdapter
        binding.recyclerview.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@DaluaScheduleFragment.isLoading = true
                Log.d(TAG, "loadMoreItems: currentPage: $currentPage")
                currentPage += 1
                viewModel.loadNextPage(currentPage)
            }

            override val isLastPage: Boolean get() = this@DaluaScheduleFragment.isLastPage
            override val isLoading: Boolean get() = this@DaluaScheduleFragment.isLoading
        })
    }

    companion object {
        const val TAG = "DaluaScheduleFragment"
    }

}