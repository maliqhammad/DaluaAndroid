package com.dalua.app.ui.createschedule.fragments.mastercontrol

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dalua.app.api.RemoteDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MasterControlFragmentVM @Inject constructor(val repository: RemoteDataRepository) :
    ViewModel() {
    val aValue: MutableLiveData<String> = MutableLiveData()
    val bValue: MutableLiveData<String> = MutableLiveData()
    val cValue: MutableLiveData<String> = MutableLiveData()
    val waterType: MutableLiveData<String> = MutableLiveData("")


}