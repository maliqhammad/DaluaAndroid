package com.dalua.app.ui.customDialogs.devicesListDialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.DevicesListDialogItemBinding
import com.dalua.app.models.Device

class DevicesListAdapter(var viewModel1: DevicesListDialogVM) :
    RecyclerView.Adapter<DevicesListAdapter.ViewHolder>() {

    var devicesList: ArrayList<Device> = ArrayList()

    class ViewHolder(binding: DevicesListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val adapterBinding = binding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: DevicesListDialogItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.devices_list_dialog_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun addList(list: List<Device>) {
        devicesList.clear()
        devicesList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.adapterBinding.apply {
            viewModel = viewModel1
            device = devicesList[position]
        }
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }
}