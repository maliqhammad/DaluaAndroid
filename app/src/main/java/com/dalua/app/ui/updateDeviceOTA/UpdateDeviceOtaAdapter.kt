package com.dalua.app.ui.updateDeviceOTA

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dalua.app.databinding.ItemUpdateDeviceOtaBinding
import com.dalua.app.models.ResponseOtaFiles

class UpdateDeviceOtaAdapter(
    var context: Context,
    var otaFiles: ArrayList<ResponseOtaFiles.OtaFile>,
    var listener: UpdateDeviceOtaAdapterCallback
) : RecyclerView.Adapter<UpdateDeviceOtaAdapter.ViewHolder>() {
    var lastItemSelectedPos = -1

    class ViewHolder(item: ItemUpdateDeviceOtaBinding) :
        RecyclerView.ViewHolder(item.root) {
        val binding = item
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemUpdateDeviceOtaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            Glide.with(root.context).load(otaFiles[position].product.imageId).into(deviceImage)
            checkButton.text = otaFiles[position].product.name
            checkButton.isChecked = otaFiles[position].selected
            checkButton.setOnClickListener {
                Log.d(
                    "TAG",
                    "onBindViewHolder: UpdateDeviceOtaActivity: " + otaFiles[position].product.name
                )
                if (lastItemSelectedPos == -1) {
                    lastItemSelectedPos = position
                    otaFiles[position].selected = true
                    notifyDataSetChanged()
                } else {
                    otaFiles[lastItemSelectedPos].selected = false
                    otaFiles[position].selected = true
                    lastItemSelectedPos = position
                    notifyDataSetChanged()
                }
                listener.onDeviceClick(otaFiles[position])
            }
            cardView.setOnClickListener {
                checkButton.performClick()
            }
        }
    }

    interface UpdateDeviceOtaAdapterCallback {
        fun onDeviceClick(otaFile: ResponseOtaFiles.OtaFile)
    }

    override fun getItemCount(): Int {
        return otaFiles.size
    }
}

