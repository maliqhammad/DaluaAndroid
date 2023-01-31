package com.dalua.app.ui.home.fragments.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.ItemAquariumBinding
import com.dalua.app.models.ListAllAquariumResponse

class AquariumAdapter(
    private val aquariumType: Int,
    val viewModel: HomeFragmentViewModel,
    val list: List<ListAllAquariumResponse.SharedAquariums>
) :
    RecyclerView.Adapter<AquariumAdapter.AquariumVH>() {


    class AquariumVH(itemView: ItemAquariumBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AquariumVH {
        val binding: ItemAquariumBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_aquarium,
            parent,
            false
        )
        return AquariumVH(binding)
    }

    override fun onBindViewHolder(holder: AquariumVH, position: Int) {
        Log.d("TAG", "onBindViewHolder: size: "+list.size)
        holder.binding.viewModel = viewModel
        val aquarium = list[position]
        aquarium.aquariumType = aquariumType
        holder.binding.aquariumData = aquarium
        holder.binding.owner = aquarium.user
        holder.binding.textView1.text =
            "${aquarium.devicesCount} devices and ${aquarium.groupsCount} groups"
        if (aquariumType == 2) {
            if (aquarium.users[0].pivot.status.contains("0")) {
                holder.binding.buttonGroup.visibility = View.VISIBLE
                holder.binding.arrowImage.visibility = View.GONE
            } else {
                holder.binding.buttonGroup.visibility = View.GONE
                holder.binding.arrowImage.visibility = View.VISIBLE
            }
            holder.binding.onwerTextViewGroup.visibility = View.VISIBLE
            holder.binding.ownerImage.visibility = View.VISIBLE
            holder.binding.userImage.visibility = View.GONE
            holder.binding.userImage1.visibility = View.GONE
            holder.binding.userImage2.visibility = View.GONE
            holder.binding.userImage3.visibility = View.GONE
        } else if (aquariumType == 1) {
            holder.binding.buttonGroup.visibility = View.GONE
            holder.binding.arrowImage.visibility = View.VISIBLE
            holder.binding.onwerTextViewGroup.visibility = View.GONE
            holder.binding.ownerImage.visibility = View.GONE
            holder.binding.userImage.visibility = View.VISIBLE
            holder.binding.userImage1.visibility = View.VISIBLE
            holder.binding.userImage2.visibility = View.VISIBLE
            holder.binding.userImage3.visibility = View.VISIBLE
        } else {
            holder.binding.buttonGroup.visibility = View.GONE
            holder.binding.arrowImage.visibility = View.VISIBLE
            holder.binding.onwerTextViewGroup.visibility = View.GONE
            holder.binding.ownerImage.visibility = View.GONE
            holder.binding.userImage.visibility = View.GONE
            holder.binding.userImage1.visibility = View.GONE
            holder.binding.userImage2.visibility = View.GONE
            holder.binding.userImage3.visibility = View.GONE
        }
        when (aquarium.users.size) {
            1 -> {
                holder.binding.user = aquarium.users[0]
            }
            2 -> {
                holder.binding.user = aquarium.users[0]
                holder.binding.user1 = aquarium.users[1]
            }
            3 -> {
                holder.binding.user = aquarium.users[0]
                holder.binding.user1 = aquarium.users[1]
                holder.binding.user2 = aquarium.users[2]
            }
            4 -> {
                holder.binding.user = aquarium.users[0]
                holder.binding.user1 = aquarium.users[1]
                holder.binding.user2 = aquarium.users[2]
                holder.binding.user3 = aquarium.users[3]
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}