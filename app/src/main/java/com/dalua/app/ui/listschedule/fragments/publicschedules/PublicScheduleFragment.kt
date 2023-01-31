package com.dalua.app.ui.listschedule.fragments.publicschedules

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
import com.dalua.app.databinding.FragmentPublicScheduleBinding
import com.dalua.app.models.schedulemodel.ScheduleResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.ui.createschedule.CreateScheduleActivity
import com.dalua.app.ui.listschedule.ScheduleListActivityVM
import com.dalua.app.utils.AppConstants.ApiTypes.GetPublicSchedules
import com.dalua.app.utils.AppConstants.Companion.UpdatePublicSchedule
import com.dalua.app.utils.PaginationScrollListener
import com.dalua.app.utils.ProjectUtil
import com.dalua.app.utils.WrapContentLinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublicScheduleFragment : BaseFragment() {

    lateinit var binding: FragmentPublicScheduleBinding
    lateinit var adapter: PublicScheduleAdapter
    val viewModel: PublicScheduleVM by viewModels()
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_public_schedule, container, false)
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
        if (UpdatePublicSchedule) {
            UpdatePublicSchedule = false
        }
        setRecyclerView()
        adapter.clearList()
        viewModel.loadFirstPage()
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

    private fun apiResponses() {

        viewModel.apiResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Error -> {
                    hideWorking()

                    when (it.api_Type) {
                        GetPublicSchedules.name -> {

                        }
                    }
                }

                is Resource.Loading -> {
                    showWorking("Public")
                }

                is Resource.Success -> {
                    hideWorking()
                    when (it.api_Type) {
                        GetPublicSchedules.name -> {
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

    private fun setAdapter(list: List<SingleSchedule>) {
        Log.d(TAG, "setAdapter: currentPage: $currentPage")
        adapter.removeLoadingFooter()
        isLoading = false
        adapter.addAll(list)
        if (currentPage != totalPage) {
            adapter.addLoadingFooter()
        } else {
            isLastPage = true
        }
    }

    private fun initObjects() {
        initializeProgressDialog()
        viewModel.waterType.value = scheduleListActivityVM.waterType.value
        viewModel.configuration.value = scheduleListActivityVM.configuration.value
    }


    private fun setRecyclerView() {
        val linearLayoutManager =
            WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = PublicScheduleAdapter(viewModel)
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.adapter = adapter
        binding.recyclerview.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@PublicScheduleFragment.isLoading = true
                Log.d(TAG, "loadMoreItems: currentPage: $currentPage")
                currentPage += 1
                viewModel.loadNextPage(currentPage)
            }

            override val isLastPage: Boolean get() = this@PublicScheduleFragment.isLastPage
            override val isLoading: Boolean get() = this@PublicScheduleFragment.isLoading
        })
    }

    companion object {
        const val TAG = "PublicScheduleFragment"
    }


}