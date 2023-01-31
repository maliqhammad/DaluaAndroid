package com.dalua.app.ui.listschedule.fragments.myschedules

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseFragment
import com.dalua.app.databinding.FragmentMyScheduleBinding
import com.dalua.app.databinding.ItemCreateDeviceBinding
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.models.CreateScheduleResponse
import com.dalua.app.models.schedulemodel.ScheduleResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.ui.createschedule.CreateScheduleActivity
import com.dalua.app.ui.listschedule.ScheduleListActivityVM
import com.dalua.app.utils.AppConstants.ApiTypes.GetMySchedules
import com.dalua.app.utils.AppConstants.ApiTypes.DeleteSchedule
import com.dalua.app.utils.AppConstants.ApiTypes.RenameSchedule
import com.dalua.app.utils.AppConstants.Companion.UpdateMySchedule
import com.dalua.app.utils.PaginationScrollListener
import com.dalua.app.utils.ProjectUtil
import com.dalua.app.utils.WrapContentLinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyScheduleFragment : BaseFragment() {

    lateinit var binding: FragmentMyScheduleBinding
    lateinit var adapter: MyScheduleAdapter
    val viewModel: MyScheduleVM by viewModels()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_schedule, container, false)
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
        Log.d(TAG, "onResume: ")
        if (UpdateMySchedule) {
            UpdateMySchedule = false
        }
        setRecyclerView()
        adapter.clearList()
        viewModel.loadFirstPage(
            scheduleListActivityVM.schedule.value!!.deviceId,
            scheduleListActivityVM.schedule.value!!.groupId
        )
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
                        1,
                        scheduleListActivityVM.aquarium.value!!
                    )
                } else if (scheduleListActivityVM.launchFrom.value == "group") {
                    CreateScheduleActivity.launchEasyPreviewFromGroup(
                        requireContext(),
                        scheduleListActivityVM.group.value!!,
                        it,
                        viewModel.waterType.value!!,
                        viewModel.configuration.value!!,
                        1,
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
                        1,
                        scheduleListActivityVM.aquarium.value!!
                    )
                } else if (scheduleListActivityVM.launchFrom.value == "group") {
                    CreateScheduleActivity.launchAdvancePreviewFromGroup(
                        requireContext(),
                        scheduleListActivityVM.group.value!!,
                        it,
                        viewModel.waterType.value!!,
                        viewModel.configuration.value!!,
                        1,
                        scheduleListActivityVM.aquarium.value!!
                    )
                }
            }
        }

        scheduleListActivityVM.geoLocation.observe(viewLifecycleOwner) {
            viewModel.geoLocation.value = it
        }

        viewModel.selectedSchedule.observe(viewLifecycleOwner) {
            onDeviceMenuClicked(it)
        }

    }

    private fun apiResponses() {

        viewModel.apiResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Error -> {
                    hideWorking()

                    when (it.api_Type) {
                        GetMySchedules.name -> {

                        }
                        DeleteSchedule.name -> {

                        }
                        RenameSchedule.name -> {

                        }
                    }

                }
                is Resource.Loading -> {
                    showWorking("My Schedules")
                }
                is Resource.Success -> {
                    hideWorking()
                    when (it.api_Type) {
                        GetMySchedules.name -> {
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
                        DeleteSchedule.name -> {
                            it.data?.let { responseBody ->
                                Log.d(TAG, "apiResponses: ")
                                adapter.clearList()
                                viewModel.loadFirstPage(
                                    scheduleListActivityVM.schedule.value!!.deviceId,
                                    scheduleListActivityVM.schedule.value!!.groupId
                                )
                                val createScheduleResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateScheduleResponse::class.java
                                )
                                showMessage(
                                    createScheduleResponse.message,
                                    createScheduleResponse.success
                                )
                            }
                        }
                        RenameSchedule.name -> {
                            it.data?.let { responseBody ->
                                Log.d(TAG, "apiResponses: ")
                                adapter.clearList()
                                viewModel.loadFirstPage(
                                    scheduleListActivityVM.schedule.value!!.deviceId,
                                    scheduleListActivityVM.schedule.value!!.groupId
                                )
                                val createScheduleResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateScheduleResponse::class.java
                                )
                                showMessage(
                                    createScheduleResponse.message,
                                    createScheduleResponse.success
                                )
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
//        adapter.clearList()
//        viewModel.loadFirstPage(
//            scheduleListActivityVM.schedule.value!!.deviceId,
//            scheduleListActivityVM.schedule.value!!.groupId
//        )
    }

    private fun setRecyclerView() {
        val linearLayoutManager =
            WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = MyScheduleAdapter(viewModel)
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.adapter = adapter
        binding.recyclerview.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@MyScheduleFragment.isLoading = true
                Log.d(TAG, "loadMoreItems: currentPage: $currentPage")
                currentPage += 1
                viewModel.loadNextPage(
                    scheduleListActivityVM.schedule.value!!.deviceId,
                    scheduleListActivityVM.schedule.value!!.groupId,
                    currentPage
                )
            }

            override val isLastPage: Boolean get() = this@MyScheduleFragment.isLastPage
            override val isLoading: Boolean get() = this@MyScheduleFragment.isLoading
        })
    }

    private fun onDeviceMenuClicked(singleSchedule: SingleSchedule) {
        val popupMenu = PopupMenu(requireContext(), viewModel.view)
        popupMenu.menu.add("Edit Schedule")
        popupMenu.menu.add("Rename")
        if (!viewModel.isEnable.value!!) {
            if (singleSchedule.userId.toInt() == ProjectUtil.getUserObjects(requireContext()).data.user.id) {
                popupMenu.menu.add("Delete")
            }
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.title.toString()) {
                "Edit Schedule" -> {
                    viewModel.previewClicked(singleSchedule)
                }
                "Rename" -> {
                    val dialog = Dialog(requireContext())
                    if (dialog.isShowing) dialog.cancel()
                    dialog.apply {
                        with(ItemCreateDeviceBinding.inflate(layoutInflater)) {
                            setContentView(root)
                            window?.setBackgroundDrawableResource(R.color.transparent)
                            setCancelable(false)
//                            window?.setWindowAnimations(R.style.DialogAnimation)
                            title.text = getString(R.string.schedule_name)
                            description.text =
                                getString(R.string.please_choose_a_unique_name_for_the_schedule)
                            edittext.text =
                                Editable.Factory.getInstance().newEditable(singleSchedule.name)
                            button1.text = getString(R.string.rename)
                            button1.setOnClickListener {
                                val name = edittext.text.toString().trim()
                                if (name.isNotEmpty()) {
                                    viewModel.renameScheduleApi(singleSchedule.id, name)
                                    dismiss()
                                } else
                                    ProjectUtil.showToastMessage(
                                        requireActivity(),
                                        false,
                                        "Please enter schedule name"
                                    )
                            }
                            button.setOnClickListener {
                                dismiss()
                            }
                            show()
                        }
                    }
                }
                "Delete" -> {
                    showAlertDialogWithListener(
                        requireContext(),
                        getString(R.string.delete_schedule),
                        getString(R.string.delete_schedule_message),
                        getString(R.string.yes),
                        getString(R.string.cancel),
                        true,
                        object : AlertDialogInterface {
                            override fun onPositiveClickListener(dialog: Dialog?) {
                                viewModel.deleteSchedule(singleSchedule)
                                dialog!!.dismiss()
                            }

                            override fun onNegativeClickListener(dialog: Dialog?) {
                                dialog!!.dismiss()
                            }
                        }
                    )
                }
            }
            false
        }
        popupMenu.show()
    }

    companion object {
        const val TAG = "MyScheduleFragment"
    }

}