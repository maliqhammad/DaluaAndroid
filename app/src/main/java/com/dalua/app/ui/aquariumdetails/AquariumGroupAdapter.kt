package com.dalua.app.ui.aquariumdetails

import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.ItemAquariumGroupAddBinding
import com.dalua.app.databinding.ItemAquariumGroupBinding
import com.dalua.app.interfaces.OnCheckIfInArea
import com.dalua.app.models.AquariumGroup

class AquariumGroupAdapter(
    val vm: AquariumDetailsVM,
    val context: Activity,
    val binding: Int,
    val list: MutableList<AquariumGroup>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var hilight: MutableLiveData<Boolean> = MutableLiveData()
    private var areaInterface: OnCheckIfInArea? = context as OnCheckIfInArea

    class AquariumVH(itemView: ItemAquariumGroupBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val madapterbinding = itemView
    }

    class AquariumAddVH(itemView: ItemAquariumGroupAddBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val madapterbinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 0) {
            val binding: ItemAquariumGroupAddBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_aquarium_group_add,
                parent,
                false
            )
            return AquariumAddVH(binding)
        } else {

            val binding: ItemAquariumGroupBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_aquarium_group,
                parent,
                false
            )
            return AquariumVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val displaymetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displaymetrics)

        if (position == 0) {
            (holder as AquariumAddVH).madapterbinding.apply {
                viewModel = vm
//                    item=AquariumGroup()
                //if you need three fix imageview in width

                cardView.layoutParams.width = (binding / 3) - 20
                cardView.layoutParams.height = (binding / 3) + 30
                if (cardView.layoutParams is ViewGroup.MarginLayoutParams) {
                    (cardView.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                        20
                    cardView.requestLayout()
                }
            }

        } else {

            (holder as AquariumVH).madapterbinding.apply {

                viewModel = vm
                item = list[position - 1]
                itemPos = position

                //if you need three fix item in width

                cardView.layoutParams.width = (binding / 3) - 20
                cardView.layoutParams.height = (binding / 3) + 10

                if (cardView.layoutParams is ViewGroup.MarginLayoutParams) {
                    (cardView.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                        20
                    cardView.requestLayout()
                }

            }

            // drag and drop scene...
//            holder.itemView.setOnDragListener { _, event ->
//                when (event.action) {
//
//                    DragEvent.ACTION_DRAG_ENTERED -> {
//                        // area in which it can be draged...
//                        areaInterface?.onOptionFollowed(
//                            true,
//                            position.toString() + "",
//                            holder
//                        )
//                    }
//
//                    DragEvent.ACTION_DRAG_EXITED -> {
//                        // area in which it cannot be draged...
//
//                        areaInterface?.onOptionFollowed(
//                            false,
//                            position.toString() + "",
//                            holder
//                        )
//                        hilight.postValue(false)
//
//                    }
//
//                    DragEvent.ACTION_DRAG_ENDED -> {
//
//                        areaInterface?.onOptionFollowed(
//                            false,
//                            position.toString() + "",
//                            holder
//                        )
//
//                        hilight.postValue(false)
//                    }
//                    DragEvent.ACTION_DROP -> {
//                        // item is dropped...!!
//                        areaInterface?.onItemDroped(
//                            true,
//                            position.toString() + "",
//                            holder,
//                            list[position - 1]
//                        )
//                    }
//                    else -> {
//
//                    }
//                }
//                true
//            }

        }

    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}