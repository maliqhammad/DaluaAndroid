package com.dalua.app.ui.customDialogs.shareAquariumDialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.databinding.ShareAquariumDialogBinding
import com.dalua.app.models.CreateAquariumResponse
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.ProjectUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareAquariumDialog(var aquariumId: Int, var listener: ShareAquariumCallback) :
    DialogFragment() {
    lateinit var viewModel: ShareAquariumDialogVM
    lateinit var binding: ShareAquariumDialogBinding

    companion object {
        const val TAG = "DevicesListDialog"
        var from: String = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.share_aquarium_dialog,
            container,
            false
        )
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[ShareAquariumDialogVM::class.java]
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        Log.d(TAG, "onViewCreated: ")
        initObjects()
        setListener()
        observers()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
//        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        dialog!!.window!!.setStatusBarColor(ResourcesCompat.getColor(resources, R.color.white, null))
    }

    private fun initObjects() {

    }

    private fun setListener() {
        binding.button.setOnClickListener {
            val email = binding.edittext.text.trim().toString()
            Log.d(TAG, "setListener: ")
            if (email.isNotEmpty()) {
                if (ProjectUtil.IsConnected(requireContext())) {
                    viewModel.shareAquarium(
                        aquariumId.toString(),
                        email
                    )
                } else {
                    ProjectUtil.showToastMessage(
                        requireActivity(),
                        false,
                        "Check your internet connection"
                    )
                }
            } else {
                viewModel.progressBar.value = false
                viewModel.isSuccess.value = false
                viewModel.isError.value = true
                viewModel.message.value = "Please enter email address"
            }
        }
        binding.buttonDone.setOnClickListener {
            dialog?.cancel()
            dialog?.dismiss()
        }
        binding.close.setOnClickListener {
            dialog?.cancel()
            dialog?.dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Log.d(TAG, "onCancel: ")
        listener.onFinish()
    }

    private fun observers() {

        viewModel.apiResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    when (it.api_Type) {
                        AppConstants.ApiTypes.ShareAquariumApi.name -> {
                            viewModel.progressBar.value = false
                            viewModel.isSuccess.value = false
                            viewModel.isError.value = true
                            viewModel.message.value = it.error
                        }
                    }
                }
                is Resource.Loading -> {
                    when (it.api_Type) {
                        AppConstants.ApiTypes.ShareAquariumApi.name -> {
                            viewModel.progressBar.value = true
                            viewModel.isSuccess.value = false
                            viewModel.isError.value = false
                            viewModel.message.value = ""
                        }
                    }
                }
                is Resource.Success -> {
                    when (it.api_Type) {
                        AppConstants.ApiTypes.ShareAquariumApi.name -> {
                            it.data?.let { responseBody ->
                                val aquariumResponse = ProjectUtil.stringToObject(
                                    responseBody.string(),
                                    CreateAquariumResponse::class.java
                                )
                                viewModel.progressBar.value = false
                                viewModel.isSuccess.value = true
                                viewModel.isError.value = false
                                viewModel.message.value = aquariumResponse.message
                            }
                        }
                    }
                }

            }
        }
        viewModel.message.observe(viewLifecycleOwner) {
            Log.d(TAG, "observers: $it")
            binding.successMessage.text = it
            binding.errorMessage.text = it
        }
        viewModel.isSuccess.observe(viewLifecycleOwner) {

        }
        viewModel.isError.observe(viewLifecycleOwner) {

        }
        viewModel.progressBar.observe(viewLifecycleOwner) {

        }
    }

    interface ShareAquariumCallback {
        fun onClose()
        fun onFinish()
    }

}