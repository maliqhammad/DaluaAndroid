package com.dalua.app.ui.createschedule.fragments.instantcontrol

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dalua.app.api.RemoteDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InstantControlFragmentVM @Inject constructor(val repository: RemoteDataRepository): ViewModel() {

    val aValue:MutableLiveData<String> = MutableLiveData("0")
    val bValue:MutableLiveData<String> = MutableLiveData("0")
    val cValue:MutableLiveData<String> = MutableLiveData("0")
    val waterType: MutableLiveData<String> = MutableLiveData("")

}