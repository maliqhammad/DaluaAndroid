package com.dalua.app.ui.geolocation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityGeoLocationBinding
import com.dalua.app.models.geolocation.GeoLocationResponseAll
import com.dalua.app.ui.geolocation.adapter.SearchGeoLocationAdapter
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.Companion.GEOLOCATIONID
import com.dalua.app.utils.AppConstants.Companion.GEOLOCATIONIDEasy
import com.dalua.app.utils.ProjectUtil
import com.paulrybitskyi.persistentsearchview.PersistentSearchView
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener
import com.paulrybitskyi.persistentsearchview.utils.VoiceRecognitionDelegate
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class GeoLocationActivity : BaseActivity() {

    companion object {
        const val TAG = "GeoLocationActivity"

        fun launchGeoLocationActivity(context: Context, intent: Intent) {
            context.startActivity(intent.putExtra("What", "ADVANCE"))
        }

    }

    lateinit var binding: ActivityGeoLocationBinding
    val viewModel: GeoLocationVM by viewModels()
    lateinit var persistentSearchView: PersistentSearchView
    lateinit var searchGeoLocationAdapter: SearchGeoLocationAdapter
    var locationList: MutableList<GeoLocationResponseAll.Datum>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObjects()
        observers()
        apiResponses()

        persistentSearchView = PersistentSearchView(this)
        PersistentSearchView(this).apply {
            setOnLeftBtnClickListener {
                // Handle the left button click
                Log.d(TAG, "onCreate: ")
            }
            setOnClearInputBtnClickListener {
                Log.d(TAG, "onCreate: ")
                // Handle the clear input button click
            }
            // Setting a delegate for the voice recognition input
            setVoiceRecognitionDelegate(VoiceRecognitionDelegate(this@GeoLocationActivity))
            setOnSearchConfirmedListener { searchView, query ->
                Log.d(TAG, "onCreate: ")
                // Handle a search confirmation. This is the place where you'd
                // want to save a new query and perform a search against your
                // data provider.
            }
            setOnSearchQueryChangeListener { searchView, oldQuery, newQuery ->
                Log.d(TAG, "onCreate: ")
                // Handle a search query change. This is the place where you'd
                // want load new suggestions based on the newQuery parameter.
            }
            setOnSuggestionChangeListener(object : OnSuggestionChangeListener {

                override fun onSuggestionPicked(suggestion: SuggestionItem) {
                    Log.d(TAG, "onSuggestionPicked: ")
                    // Handle a suggestion pick event. This is the place where you'd
                    // want to perform a search against your data provider.
                }

                override fun onSuggestionRemoved(suggestion: SuggestionItem) {
                    Log.d(TAG, "onSuggestionRemoved: ")
                    // Handle a suggestion remove event. This is the place where
                    // you'd want to remove the suggestion from your data provider.
                }

            })
        }
    }

    private fun observers() {

        viewModel.goBackButton.observe(this) {
            GEOLOCATIONIDEasy = 0
            GEOLOCATIONID = 0
            finish()
        }

        viewModel.itemClickedButton.observe(this) {
            GEOLOCATIONID = it
            GEOLOCATIONIDEasy = it
            showMessage("Location Selected successfully", true)
            finish()
        }

        binding.searchView.apply {
            findViewById<EditText>(androidx.appcompat.R.id.search_src_text).apply {
                setTextColor(resources.getColor(R.color.selected_blue_dot_color))
                setHintTextColor(resources.getColor(R.color.selected_blue_dot_color))
            }
            setOnClickListener {
                binding.searchView.setIconifiedByDefault(false)
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(msg: String): Boolean {
                    // inside on query text change method we are
                    // calling a method to filter our recycler view.
                    Log.d(TAG, "onQueryTextChange: $msg")
                    filter(msg)
                    return false
                }
            })
        }

    }

    private fun apiResponses() {

        viewModel.apiResponses.observe(this) {
            when (it) {
                is Resource.Error -> {
                    hideWorking()

                    when (it.api_Type) {
                        AppConstants.ApiTypes.GetLocationApi.name -> {

                        }
                    }

                }
                is Resource.Loading -> {
                    showWorking()
                }
                is Resource.Success -> {
                    hideWorking()

                    when (it.api_Type) {
                        AppConstants.ApiTypes.GetLocationApi.name -> {

                            it.data?.let { response ->

                                val geolocation = ProjectUtil.stringToObject(
                                    response.string(),
                                    GeoLocationResponseAll::class.java
                                )

                                if (geolocation.data != null) {
                                    searchGeoLocationAdapter =
                                        SearchGeoLocationAdapter(geolocation.data, viewModel)
                                    binding.recyclerView.adapter = searchGeoLocationAdapter
                                    locationList = geolocation.data
                                    searchGeoLocationAdapter.notifyDataSetChanged()
                                }

                            }


                        }
                    }

                }
            }
        }

    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<GeoLocationResponseAll.Datum> = ArrayList()

        // running a for loop to compare elements.
        if (locationList == null || locationList!!.isEmpty()) {
            Toast.makeText(this, "No Data Found...", Toast.LENGTH_SHORT).show()
            return
        }
        for (item in locationList!!) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.name.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            filteredlist.add(locationList!![0])
            searchGeoLocationAdapter.filterList(filteredlist, true)
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            searchGeoLocationAdapter.filterList(filteredlist, false)
        }
    }

    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_geo_location)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        AppConstants.IsFromChangeValue = true
        locationList = mutableListOf()
        myProgressDialog()

    }

}