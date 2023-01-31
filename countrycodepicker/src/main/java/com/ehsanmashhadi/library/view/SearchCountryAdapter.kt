package com.ehsanmashhadi.library.view

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ehsanmashhadi.library.R
import com.ehsanmashhadi.library.databinding.RowCountryBinding
import com.ehsanmashhadi.library.databinding.RowCountryEmptyBinding
import com.ehsanmashhadi.library.model.Country
import java.util.*

class SearchCountryAdapter(
    private var locationList: List<Country>,
    private var showingFlag: Boolean,
    private var showingDialCode: Boolean,
    private var preselectCountry: String?,
    private var listener: OnCountryClickListener
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
                    R.layout.row_country_empty, parent, false
                )
            )
        } else {
            SearchGeoLocationHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_country, parent, false
                )
            )
        }

        // at last we are returning our view holder
        // class with our item View File.
    }

    // method for filtering our recyclerview items.
    fun filterList(filterList: List<Country>, isEmpty: Boolean) {
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
        datum: Country
    ) {

    }

    private fun bindingViewHolder(
        holder: SearchGeoLocationHolder,
        country: Country
    ) {
        holder.binding.textviewName.text = country.name
        holder.binding.textviewCode.text = country.dialCode

        if (showingFlag) {
            holder.binding.imageviewFlag.visibility = View.GONE
        }
        if (!showingDialCode) {
            holder.binding.imageviewFlag.visibility = View.GONE
        }
        val resourceId = holder.itemView.context.resources.getIdentifier(
            country.flagName,
            "drawable",
            holder.itemView.context.packageName
        )
        holder.binding.imageviewFlag.setImageResource(resourceId)
        if (preselectCountry != null) if (country.name
                .lowercase(
                    Locale.getDefault()
                ) == preselectCountry?.lowercase(Locale.getDefault())
        ) {
            holder.itemView.isSelected = true
            //Not setting background in XML because of not supporting reference attributes in pre-lollipop Android version
            val typedValue = TypedValue()
            val theme = holder.itemView.context.theme
            theme.resolveAttribute(R.attr.rowBackgroundSelectedColor, typedValue, true)
            @ColorInt val color = typedValue.data
            holder.itemView.setBackgroundColor(color)
        } else {
            holder.itemView.isSelected = false
            val typedValue = TypedValue()
            val theme = holder.itemView.context.theme
            theme.resolveAttribute(R.attr.rowBackgroundColor, typedValue, true)
            @ColorInt val color = typedValue.data
            holder.itemView.setBackgroundColor(color)
        }
        if (showingFlag)
            holder.binding.imageviewFlag.visibility = View.VISIBLE
        else
            holder.binding.imageviewFlag.visibility = View.GONE

        holder.binding.root.setOnClickListener {
            listener.onCountrySelected(country)
        }

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

    class SearchGeoLocationHolder(var binding: RowCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // on below line we are initializing our course name text view and our image view.
    }

    class SearchGeoLocationHolderEmpty(var binding: RowCountryEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // on below line we are initializing our course name text view and our image view.
    }

    interface OnCountryClickListener {
        fun onCountrySelected(country: Country?)
    }
}