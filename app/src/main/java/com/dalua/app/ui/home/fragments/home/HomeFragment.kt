package com.dalua.app.ui.home.fragments.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseFragment
import com.dalua.app.databinding.FragmentHomeBinding
import com.dalua.app.models.ListAllAquariumResponse
import com.dalua.app.models.SharedUserResponse
import com.dalua.app.ui.aquariumdetails.AquariumDetailsActivity
import com.dalua.app.ui.home.homeactivity.HomeActivity
import com.dalua.app.ui.home.homeactivity.HomeViewModel
import com.dalua.app.ui.welcomescrn.WelcomeActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.ApiTypes.GetAquariumListingApi
import com.dalua.app.utils.AppConstants.ApiTypes.ApproveOrRejectAquariumApi
import com.dalua.app.utils.ProjectUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: HomeFragmentViewModel
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: view: ")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this)[HomeFragmentViewModel::class.java]
        binding.swifeRefreshLayout.setOnRefreshListener(this)
        binding.swifeRefreshLayout.setProgressViewEndTarget(false, 0)
        binding.viewModel = viewModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observers()
        initObjects()
    }

    private fun initObjects() {
        viewModel.loginResponse.value = ProjectUtil.getUserObjects(requireContext())
    }

    private fun observers() {

        homeViewModel.callUserAquarium.observe(viewLifecycleOwner) {
            if (it) {
                if (ProjectUtil.IsConnected(activity))
                    viewModel.getUserAquariums()
                else {
                    ProjectUtil.showToastMessage(activity, false, "Check Your Internet Connection")
                }
            } else {
                binding.aquariums.visibility = View.GONE
                binding.aquariumsShered.visibility = View.GONE
                binding.aquariumsSheredWithMe.visibility = View.GONE
                binding.noAquariaum.visibility = View.VISIBLE
            }

        }

        AppConstants.refresh_aquarium.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getUserAquariums()
                AppConstants.refresh_aquarium.value = false
            }
        }

        viewModel.showCreateAquariumDialog.observe(viewLifecycleOwner) {
            homeViewModel.showAquariumDialog.value = true
        }

        viewModel.apiResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Error -> {
                    (activity as HomeActivity).hideWorking()
                    when (it.api_Type) {
                        ApproveOrRejectAquariumApi.name,
                        GetAquariumListingApi.name -> {
                            showMessage(it.error, false)
                            Log.d(TAG, "ResponseError: " + it.error + " \n type: " + it.api_Type)
                        }
                    }
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).showWorking()

                }
                is Resource.Success -> {
                    (activity as HomeActivity).hideWorking()
                    when (it.api_Type) {
                        ApproveOrRejectAquariumApi.name -> {
                            it.data?.let { responseBody ->
                                val sharedUserResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    SharedUserResponse::class.java
                                )
                                showMessage(sharedUserResponse.message, sharedUserResponse.success)
                                AppConstants.refresh_aquarium.value = true
                            }
                        }

                        GetAquariumListingApi.name -> {
                            it.data?.let { responseBody ->
                                val listDevicesResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    ListAllAquariumResponse::class.java
                                )
                                viewModel.aquariumList.value = listDevicesResponse
                                Log.d(
                                    TAG,
                                    "observers: aquariums: " + listDevicesResponse.data.aquariums.size
                                )
                                Log.d(
                                    TAG,
                                    "observers: sharedAquariums: " + listDevicesResponse.data.sharedAquariums.size
                                )
                                Log.d(
                                    TAG,
                                    "observers: sharedAquariumsUser: " + listDevicesResponse.data.sharedAquariumsUser.size
                                )
                            }
                        }
                    }
                }
            }

        }

        viewModel.aquariumClicked.observe(viewLifecycleOwner) {
            startActivity(
                Intent(context, AquariumDetailsActivity::class.java).apply {
                    putExtra("aquarium", ProjectUtil.objectToString(it))
                    putExtra("type", it.aquariumType)
                }
            )
        }

        viewModel.aquariumList.observe(viewLifecycleOwner) {
            setVisibilityOnResponse(it)
        }

        viewModel.logout.observe(viewLifecycleOwner) {
            Log.d(TAG, "observers: logout: $it")
            if (it) {
                logoutDialog()
            }
        }


    }

    private fun logoutDialog() {

        val logoutDialog = Dialog(requireContext())
        if (logoutDialog.isShowing) logoutDialog.cancel()
        logoutDialog.apply {
            setContentView(R.layout.item_logout_dialog)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(true)
//            window?.setWindowAnimations(R.style.DialogAnimation)
            findViewById<Button>(R.id.button1).setOnClickListener {
                logoutDialog.dismiss()
                ProjectUtil.putUserObjects(requireContext(), null)
                ProjectUtil.putApiTokenBearer(requireContext(), null)
                startActivity(
                    Intent(
                        requireContext(),
                        WelcomeActivity::class.java
                    ).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
                activity?.finish()

            }
            setOnCancelListener {
                dismiss()
                viewModel.logout.value = false
            }
            setOnDismissListener {
                cancel()
                viewModel.logout.value = false
            }
            findViewById<Button>(R.id.button).setOnClickListener {
                logoutDialog.dismiss()
            }

            show()
        }

    }

    private fun setVisibilityOnResponse(listAllAquariumResponse: ListAllAquariumResponse) {

        if (listAllAquariumResponse.data.aquariums.isEmpty() && listAllAquariumResponse.data.sharedAquariums.isEmpty() && listAllAquariumResponse.data.sharedAquariumsUser.isEmpty()) {
            binding.aquariums.visibility = View.GONE
            binding.aquariumsShered.visibility = View.GONE
            binding.aquariumsSheredWithMe.visibility = View.GONE
            binding.noAquariaum.visibility = View.VISIBLE
        } else {
            binding.aquariums.visibility = View.VISIBLE
            binding.aquariumsShered.visibility = View.VISIBLE
            binding.aquariumsSheredWithMe.visibility = View.VISIBLE
            binding.noAquariaum.visibility = View.GONE
            binding.swifeRefreshLayout.isRefreshing = false
        }
        setRecyclers(listAllAquariumResponse)
    }

    private fun setRecyclers(listAllAquariumResponse: ListAllAquariumResponse) {

        if (listAllAquariumResponse.data.aquariums.isNotEmpty()) {
            binding.aquariums.visibility = View.VISIBLE
            binding.aquariumRv.adapter =
                AquariumAdapter(0, viewModel, listAllAquariumResponse.data.aquariums)
        } else {
            binding.aquariums.visibility = View.GONE
        }
        if (listAllAquariumResponse.data.sharedAquariums.isNotEmpty()) {
            binding.aquariumsShered.visibility = View.VISIBLE
            binding.sharedAquariumRv.adapter =
                AquariumAdapter(1, viewModel, listAllAquariumResponse.data.sharedAquariums)
        } else {
            binding.aquariumsShered.visibility = View.GONE
        }
        if (listAllAquariumResponse.data.sharedAquariumsUser.isNotEmpty()) {
            binding.aquariumsSheredWithMe.visibility = View.VISIBLE
            binding.sharedAquariumWithMeRv.adapter =
                AquariumAdapter(2, viewModel, listAllAquariumResponse.data.sharedAquariumsUser)
        } else {
            binding.aquariumsSheredWithMe.visibility = View.GONE
        }
    }

    override fun onRefresh() {
        viewModel.getUserAquariums()
    }

    companion object {
        const val TAG = "HomeFragment"
    }

}