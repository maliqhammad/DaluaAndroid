package com.dalua.app.ui.geolocation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.ItemGeoLocationBinding
import com.dalua.app.databinding.ItemGeoLocationEmptyBinding
import com.dalua.app.models.geolocation.GeoLocationResponseAll
import com.dalua.app.ui.geolocation.GeoLocationVM

class SearchGeoLocationAdapter(
    // on below line we are passing variables as course list and context
    private var locationList: MutableList<GeoLocationResponseAll.Datum>,
    var viewModel: GeoLocationVM
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isEmpty = false
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.

        return if (viewType == 0) {
            SearchGeoLocationHolderEmpty(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_geo_location_empty, parent, false
                )
            )
        } else {
            SearchGeoLocationHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_geo_location, parent, false
                )
            )
        }

        // at last we are returning our view holder
        // class with our item View File.
    }

    // method for filtering our recyclerview items.
    fun filterList(filterList: ArrayList<GeoLocationResponseAll.Datum>, isEmpty: Boolean) {
        // below line is to add our filtered
        // list in our course array list.
        this.isEmpty = isEmpty
        locationList = filterList
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (isEmpty) {
            bindingViewHolderEmpty(holder as SearchGeoLocationHolderEmpty, locationList[position])
        } else {
            bindingViewHolder(holder as SearchGeoLocationHolder, locationList[position])
        }
    }

    private fun bindingViewHolderEmpty(
        holder: SearchGeoLocationHolderEmpty,
        datum: GeoLocationResponseAll.Datum
    ) {

    }

    private fun bindingViewHolder(
        holder: SearchGeoLocationHolder,
        location: GeoLocationResponseAll.Datum
    ) {
        holder.binding.viewModel = viewModel
        holder.binding.item = location
    }

    override fun getItemViewType(position: Int): Int {
        return if (isEmpty) {
            0
        } else {
            1
        }
    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return locationList.size
    }

    class SearchGeoLocationHolder(var binding: ItemGeoLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // on below line we are initializing our course name text view and our image view.
    }

    class SearchGeoLocationHolderEmpty(var binding: ItemGeoLocationEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // on below line we are initializing our course name text view and our image view.
    }
}