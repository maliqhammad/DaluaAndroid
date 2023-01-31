package com.dalua.app.ui.home.fragments.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.ItemAquariumBinding
import com.dalua.app.databinding.ItemProgressBinding
import com.dalua.app.models.ListAllAquariumResponse.SharedAquariums
import java.util.*

class PaginationAdapter(private val context: Context, private val aquariumType: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var movieList: MutableList<SharedAquariums>?
    private var isLoadingAdded = false
    fun setMovieList(movieList: MutableList<SharedAquariums>?) {
        this.movieList = movieList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            ITEM -> {
                val binding: ItemAquariumBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_aquarium,
                    parent,
                    false
                )
                viewHolder = AquariumViewHolder(binding)
            }
            LOADING -> {
                val binding: ItemProgressBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_progress,
                    parent,
                    false
                )
                val viewLoading = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingViewHolder(binding)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val aquarium = movieList!![position]
        when (getItemViewType(position)) {
            ITEM -> {
                itemBinding(holder as AquariumViewHolder, aquarium)
            }
            LOADING -> {
                val loadingViewHolder = holder as LoadingViewHolder
                loadingViewHolder.binding.loadMoreProgress.visibility = View.VISIBLE
            }
        }
    }

    private fun itemBinding(
        holder: AquariumViewHolder,
        aquarium: SharedAquariums
    ) {
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
        return if (movieList == null) 0 else movieList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == movieList!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(SharedAquariums())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = movieList!!.size - 1
        val result = getItem(position)
        movieList!!.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add(movie: SharedAquariums) {
        movieList!!.add(movie)
        notifyItemInserted(movieList!!.size - 1)
    }

    fun addAll(moveResults: List<SharedAquariums>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun getItem(position: Int): SharedAquariums {
        return movieList!![position]
    }

    inner class AquariumViewHolder(itemView: ItemAquariumBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    inner class LoadingViewHolder(itemView: ItemProgressBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    companion object {
        private const val LOADING = 0
        private const val ITEM = 1
    }

    init {
        movieList = LinkedList()
    }
}