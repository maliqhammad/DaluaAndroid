package com.dalua.app.ui.aquariumdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.ItemSharedUserBinding
import com.dalua.app.models.SharedUserResponse


class ShareUseAdapter(
    val viewModel: AquariumDetailsVM,
    val user: List<SharedUserResponse.Data.User>
) :
    RecyclerView.Adapter<ShareUseAdapter.AquariumVH>() {


    class AquariumVH(itemView: ItemSharedUserBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AquariumVH {
        val binding: ItemSharedUserBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_shared_user,
            parent,
            false
        )
        return AquariumVH(binding)
    }

    override fun onBindViewHolder(holder: AquariumVH, position: Int) {
        holder.binding.viewModel = viewModel
        holder.binding.user = user[position]
    }

    override fun getItemCount(): Int {
        return user.size
    }

}