package com.dalua.app.ui.aquariumdetails

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import com.dalua.app.R
import com.dalua.app.databinding.SharedAquariumDialogBinding
import com.dalua.app.utils.ProjectUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SharedAquariumBottomSheetDialog : BottomSheetDialogFragment() {

    lateinit var binding: SharedAquariumDialogBinding
    private val viewModel: AquariumDetailsVM by viewModels(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SharedAquariumDialogBinding.inflate(LayoutInflater.from(requireContext()))
        viewModel.shareUSerList.observe(requireActivity()) {
            binding.shareAquariumRv.adapter = ShareUseAdapter(viewModel, it)
        }
        binding.shareButton.setOnClickListener {
            val email = binding.edittext.text.toString().trim()
            if (email.isNotEmpty()) {
                if (ProjectUtil.IsConnected(context)) {
                    viewModel.shareAquarium(
                        viewModel.aquarium.value!!.id.toString(),
                        email
                    )
                    binding.edittext.text = Editable.Factory.getInstance().newEditable("")
                } else {
                    ProjectUtil.showToastMessage(
                        requireActivity(),
                        false,
                        "Check your internet connection"
                    )
                }
            } else
                ProjectUtil.showToastMessage(
                    requireActivity(),
                    false,
                    "Please enter email address"
                )

        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog: BottomSheetDialog? = dialogInterface as BottomSheetDialog?
            setupFullHeight(bottomSheetDialog!!)
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout? =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
            bottomSheet!!
        )
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
        val windowHeight: Int = getWindowHeight()
        layoutParams.height = windowHeight
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }


}