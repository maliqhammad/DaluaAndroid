package com.dalua.app.ui.home.fragments.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseFragment
import com.dalua.app.databinding.FragmentProfileBinding
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.models.ProfileResponse
import com.dalua.app.ui.home.homeactivity.HomeActivity
import com.dalua.app.ui.home.homeactivity.HomeViewModel
import com.dalua.app.ui.welcomescrn.WelcomeActivity
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.ApiTypes.GetUserProfile
import com.dalua.app.utils.ProjectUtil
import com.ehsanmashhadi.library.model.Country
import com.ehsanmashhadi.library.view.CountryPicker
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {


    private lateinit var binding: FragmentProfileBinding
    private lateinit var countryCodePicker: CountryPicker
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeProgressDialog()
        initObjects()
        apiResponse()
        observers()
    }

    private fun apiResponse() {
        viewModel.apiResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    hideWorking()
                    Log.d(TAG, "observers: " + it.error)
                    Log.d(TAG, "ResponseError: " + it.error + " \n type: " + it.api_Type)
                    showMessage(it.error, false)
                }
                is Resource.Loading -> {
                    showWorking("Profile")
                }
                is Resource.Success -> {
                    hideWorking()
                    when (it.api_Type) {
                        GetUserProfile.name -> {
                            it.data?.let { responseBody ->
                                val profileResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    ProfileResponse::class.java
                                )
                                viewModel.user.value = profileResponse.user
                                viewModel.country.value = profileResponse.user.country
                                viewModel.userImage.value = profileResponse.user.image
                                ProjectUtil.putUserObjects(requireContext(),
                                    ProjectUtil.getUserObjects(
                                        requireContext()
                                    ).apply {
                                        data.user.image = profileResponse.user.image
                                        data.user.firstName = profileResponse.user.firstName
                                    })
                                if (profileResponse.user.country != null) {
                                    setCountryPicker(profileResponse.user.country)
                                }
                            }
                        }
                        AppConstants.ApiTypes.UpdateUserProfile.name -> {
                            it.data?.let { responseBody ->
                                val profileResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    ProfileResponse::class.java
                                )
                                viewModel.getUserProfile()
                                showMessage(profileResponse.message, profileResponse.success)
                            }
                        }
                        AppConstants.ApiTypes.UpdateUserPassword.name -> {
                            it.data?.let { responseBody ->
                                val passwordResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    ProfileResponse::class.java
                                )
                                viewModel.oldPassword.value = ""
                                viewModel.newPassword.value = ""
                                viewModel.confirmPassword.value = ""
                                showMessage(passwordResponse.message, passwordResponse.success)
                            }
                        }
                        AppConstants.ApiTypes.DeleteAccount.name -> {
                            it.data?.let { responseBody ->
                                val passwordResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    ProfileResponse::class.java
                                )
                                showMessage(passwordResponse.message, passwordResponse.success)
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
                        }
                    }
                }
            }
        }
    }

    private fun observers() {
        homeViewModel.aquariumCreated.observe(viewLifecycleOwner) {
            if (this.isVisible)
                (activity as HomeActivity).hideWorking()
        }

        viewModel.userImage.observe(viewLifecycleOwner) {
            Log.d(TAG, "observers: $it")
        }

        viewModel.profileClick.observe(viewLifecycleOwner) {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(256)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    420,
                    420
                )
                //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        viewModel.saveClick.observe(viewLifecycleOwner) {
            viewModel.updateProfile()
        }

        viewModel.showPicker.observe(viewLifecycleOwner) {
            countryCodePicker.show(requireActivity())
        }

        viewModel.selectCountry.observe(viewLifecycleOwner) {
            countryCodePicker.show(requireActivity())
        }

        viewModel.selectTankSize.observe(viewLifecycleOwner) {
            val popupMenu = PopupMenu(requireContext(), binding.root.findViewById(R.id.tvTankSize))
            popupMenu.menu.add("Nano")
            popupMenu.menu.add("3\'")
            popupMenu.menu.add("4\'")
            popupMenu.menu.add("5\'")
            popupMenu.menu.add("6\'")
            popupMenu.menu.add("Monster")
            popupMenu.gravity = Gravity.RIGHT
            popupMenu.setOnMenuItemClickListener { item ->
                val user = viewModel.user.value
                when (item?.title.toString()) {
                    "Nano" -> {
                        user!!.tankSize = "Nano"
                    }
                    "3\'" -> {
                        user!!.tankSize = "3'"
                    }
                    "4\'" -> {
                        user!!.tankSize = "4'"
                    }
                    "5\'" -> {
                        user!!.tankSize = "5'"
                    }
                    "6\'" -> {
                        user!!.tankSize = "6'"
                    }
                    "Monster" -> {
                        user!!.tankSize = "Monster"
                    }
                }
                viewModel.user.value = user
                false
            }
            popupMenu.show()
        }

        viewModel.changePasswordClick.observe(viewLifecycleOwner) {
            viewModel.updatePassword()
        }

        viewModel.showMessage.observe(viewLifecycleOwner) {
            showMessage(it.message, it.boolean)
        }

        viewModel.deleteAccount.observe(viewLifecycleOwner) {
            showAlertDialogWithListener(
                requireContext(),
                getString(R.string.delete_account_title),
                getString(R.string.delete_account_message),
                getString(R.string.delete),
                getString(R.string.cancel),
                true,
                object : AlertDialogInterface {
                    override fun onPositiveClickListener(dialog: Dialog?) {
                        dialog?.dismiss()
                        viewModel.deleteAccount()
                    }

                    override fun onNegativeClickListener(dialog: Dialog?) {
                        dialog?.dismiss()
                    }
                }
            )
        }
    }


    private fun initObjects() {
        viewModel.getUserProfile()
        viewModel.phoneNumberUtil = PhoneNumberUtil.createInstance(context)
        setCountryPicker("")
//        setCountryPicker("")
    }

    private fun setCountryPicker(country: String?) {

        countryCodePicker = CountryPicker.Builder(requireContext()).run {
            setViewType(CountryPicker.ViewType.BOTTOMSHEET)
            enablingSearch(true)
            showingFlag(true)
            if (!country.isNullOrEmpty()) {
                setPreSelectedCountry(country) {
                    setCountryData(it)
                }
            }
            setCountrySelectionListener {
                ProjectUtil.showToastMessage(
                    activity,
                    true,
                    "" + getString(R.string.selected_country, it.name)
                )
                setCountryData(it)
            }
            build()
        }
    }

    private fun setCountryData(country: Country) {
        viewModel.country.value = country.name
        viewModel.phoneCountryShortcut = country.code
        viewModel.phoneCode.value =
            "+" + viewModel.phoneNumberUtil?.getCountryCodeForRegion(viewModel.phoneCountryShortcut)
        binding.countryFlag.setImageDrawable(getDrawable(country.flagName))
    }

    companion object {
        const val TAG = "ProfileFragment"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.data!!.let {
                    viewModel.inputStream.value =
                        requireActivity().contentResolver.openInputStream(it)
                    viewModel.userImage.value = it.toString()
                    Log.d(TAG, "onActivityResult: " + it.toString())
                }
            }
            ImagePicker.RESULT_ERROR -> {
                ProjectUtil.showToastMessage(requireActivity(), false, ImagePicker.getError(data))
            }
            else -> {
                ProjectUtil.showToastMessage(requireActivity(), false, "Image Not Picked")
            }
        }
    }


}