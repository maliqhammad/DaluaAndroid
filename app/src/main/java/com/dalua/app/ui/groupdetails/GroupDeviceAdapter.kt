package com.dalua.app.ui.groupdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.ItemDeviceGroupBinding
import com.dalua.app.databinding.ItemProgressBinding
import com.dalua.app.models.Device
import java.util.*

class GroupDeviceAdapter(
    val vm: GroupDetailsVM
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isLoadingAdded = false
    private var aquariumDeviceList: MutableList<Device>? = LinkedList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            ITEM -> {
                val binding: ItemDeviceGroupBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_device_group,
                    parent,
                    false
                )
                viewHolder = AquariumVH(binding)
            }
            LOADING -> {
                val binding: ItemProgressBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_progress,
                    parent,
                    false
                )
                viewHolder = LoadingViewHolder(binding)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                itemBinding(holder as AquariumVH, position)
            }
            LOADING -> {
                val loadingViewHolder = holder as LoadingViewHolder
                loadingViewHolder.binding.loadMoreProgress.visibility = View.VISIBLE
            }
        }
    }

    private fun itemBinding(holder: GroupDeviceAdapter.AquariumVH, position: Int) {
        holder.binding.apply {
            viewModel = vm
            item = aquariumDeviceList!![position]
        }
    }

    override fun getItemCount(): Int {
        return vm.deviceList.value!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == aquariumDeviceList!!.size - 1 && isLoadingAdded) {
            LOADING
        } else {
            ITEM
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Device())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = aquariumDeviceList!!.size - 1
        if (position != -1) {
            aquariumDeviceList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun add(group: Device) {
        aquariumDeviceList!!.add(group)
        notifyItemInserted(aquariumDeviceList!!.size - 1)
    }

    fun addAll(moveResults: List<Device>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun clearList() {
        aquariumDeviceList!!.clear()
    }

    fun getItem(position: Int): Device {
        return aquariumDeviceList!![position]
    }

    inner class AquariumVH(val binding: ItemDeviceGroupBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LoadingViewHolder(val binding: ItemProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val LOADING = 0
        private const val ITEM = 1
    }
}