package com.dalua.app.ui.aquariumdetails

import android.app.Activity
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.ItemAquariumGroupAddBinding
import com.dalua.app.databinding.ItemAquariumGroupBinding
import com.dalua.app.databinding.ItemProgressBinding
import com.dalua.app.interfaces.OnCheckIfInArea
import com.dalua.app.models.AquariumGroup
import java.util.*

class GroupPaginationAdapter(
    val vm: AquariumDetailsVM,
    val context: Activity,
    val binding: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var highlight: MutableLiveData<Boolean> = MutableLiveData()
    private var areaInterface: OnCheckIfInArea? = context as OnCheckIfInArea
    private var aquariumGroupList: MutableList<AquariumGroup>?
    private var isLoadingAdded = false

    fun setGroupList(aquariumGroupList: MutableList<AquariumGroup>?) {
        this.aquariumGroupList = aquariumGroupList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            ITEM -> {
                val binding: ItemAquariumGroupBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_aquarium_group,
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
                viewHolder = LoadingViewHolder(binding)
            }
            ADD_ITEM -> {
                val binding: ItemAquariumGroupAddBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_aquarium_group_add,
                    parent,
                    false
                )
                viewHolder = AddGroupViewHolder(binding)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                itemBinding(holder as AquariumViewHolder, position)
            }
            LOADING -> {
                val loadingViewHolder = holder as LoadingViewHolder
                loadingViewHolder.binding.loadMoreProgress.visibility = View.VISIBLE
            }
            ADD_ITEM -> {
                itemAddBinding(holder as AddGroupViewHolder)
            }
        }
    }

    private fun itemAddBinding(holder: AddGroupViewHolder) {
        holder.binding.apply {
            viewModel = vm
            item = AquariumGroup()
        }
    }

    private fun itemBinding(
        holder: AquariumViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            viewModel = vm
            item = aquariumGroupList!![position]
            itemPos = position
        }

        holder.itemView.setOnDragListener { _, event ->
            when (event.action) {

                DragEvent.ACTION_DRAG_ENTERED -> {
                    // area in which it can be dragged...
                    areaInterface?.onOptionFollowed(
                        true,
                        position.toString() + "",
                        holder
                    )
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    // area in which it cannot be dragged...

                    areaInterface?.onOptionFollowed(
                        false,
                        position.toString() + "",
                        holder
                    )
                    highlight.postValue(false)

                }

                DragEvent.ACTION_DRAG_ENDED -> {

                    areaInterface?.onOptionFollowed(
                        false,
                        position.toString() + "",
                        holder
                    )

                    highlight.postValue(false)
                }

                DragEvent.ACTION_DROP -> {
                    // item is dropped...!!
                    areaInterface?.onItemDroped(
                        true,
                        position.toString() + "",
                        holder,
                        aquariumGroupList!![position]
                    )
                }

                else -> {

                }
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return if (aquariumGroupList == null) 0 else aquariumGroupList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ADD_ITEM
        } else if (position == aquariumGroupList!!.size - 1 && isLoadingAdded) {
            LOADING
        } else {
            ITEM
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(AquariumGroup())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = aquariumGroupList!!.size - 1
        if (position != -1) {
            aquariumGroupList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun add(group: AquariumGroup) {
        aquariumGroupList!!.add(group)
        notifyItemInserted(aquariumGroupList!!.size - 1)
    }

    fun addAll(currentGroupPage: Int, moveResults: List<AquariumGroup>) {
        if (currentGroupPage == 1) {
            aquariumGroupList!!.add(AquariumGroup())
        }
        for (result in moveResults) {
            add(result)
        }
    }

    fun clearList() {
        aquariumGroupList!!.clear()
    }

    fun getItem(position: Int): AquariumGroup {
        return aquariumGroupList!![position]
    }

    inner class AquariumViewHolder(val binding: ItemAquariumGroupBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class AddGroupViewHolder(val binding: ItemAquariumGroupAddBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LoadingViewHolder(val binding: ItemProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val LOADING = 0
        private const val ITEM = 1
        private const val ADD_ITEM = 2
        const val TAG = "GroupPaginationAdapter"
    }

    init {
        aquariumGroupList = LinkedList()
    }
}
