package com.dalua.app.ui.aquariumdetails

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalua.app.api.RemoteDataRepository
import com.dalua.app.api.Resource
import com.dalua.app.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AquariumDetailsVM @Inject constructor(private val repository: RemoteDataRepository) :
    ViewModel() {


    val addDeviceClicked: MutableLiveData<Boolean> = MutableLiveData()
    val groupList: MutableLiveData<MutableList<AquariumGroup>> = MutableLiveData()
    val deviceList: MutableLiveData<MutableList<Device>> = MutableLiveData()
    val deviceClick: MutableLiveData<Device> = MutableLiveData()
    val device: MutableLiveData<Device> = MutableLiveData()
    val sharedUserClick: MutableLiveData<SharedUserResponse.Data.User> = MutableLiveData()
    val shareUSerList: MutableLiveData<List<SharedUserResponse.Data.User>> = MutableLiveData()
    val refreshShareUsers: MutableLiveData<Boolean> = MutableLiveData()
    val aquarium: MutableLiveData<SingleAquarium> = MutableLiveData()
    val aquariumType: MutableLiveData<Int> = MutableLiveData()
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val apiResponse1: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()
    val groupTheDevice: MutableLiveData<Boolean> = MutableLiveData()
    val finishThisActivity: MutableLiveData<Boolean> = MutableLiveData()
    val menuItemClicked: MutableLiveData<Boolean> = MutableLiveData()
    val aquariumGroup: MutableLiveData<AquariumGroup> = MutableLiveData()
    val addGroup: MutableLiveData<Boolean> = MutableLiveData()
    lateinit var view: View

    fun getAquariumDetails(id: String) {

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.getAquariumDetails(id).collect {
                apiResponse.value = it
            }
        }
    }

    fun getShareAquariumWithUser(aquariumID: String) {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.getShareAquariumWithUser(aquariumID).collect {
                apiResponse.value = it
            }
        }
    }

    fun backPressed() {
        finishThisActivity.value = true
    }

    fun onAddDevice() {
        addDeviceClicked.value = true
    }

    fun onAddGroupClicked() {
        addGroup.value = true
    }

    fun onGroupClicked(aquariumGroup: AquariumGroup) {
        this.aquariumGroup.value = aquariumGroup
    }

    fun onUnShareClick(user: SharedUserResponse.Data.User) {
        sharedUserClick.value = user
    }

    fun onMenuObjectClicked() {
        menuItemClicked.value = true
    }

    fun createGroupApi(name: String, aquariumID: String, timezone: String) {

        viewModelScope.launch {

            apiResponse.value = Resource.Loading()
            repository.createGroupApi(name, aquariumID, timezone).collect {
                apiResponse.value = it
            }
        }

    }

    fun deleteAquariumApi(id: String) {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.deleteAquariumApi(id).collect {
                apiResponse.value = it
            }
        }
    }

    fun shareAquarium(aquarium_id: String, email: String) {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.shareAquarium(aquarium_id, email).collect {
                apiResponse.value = it
            }
        }
    }

    fun removeShareAquarium(aquarium_id: String, user_id: String) {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.removeShareAquarium(aquarium_id, user_id).collect {
                apiResponse.value = it
            }
        }
    }

    fun updateAquariumApi(name: String) {

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.updateAquariumApi(
                name, aquarium.value?.id.toString(), "25",
                "7", "23", "45", "33", "87", "21"
            ).collect {
                apiResponse.value = it
            }

        }

    }

    fun showDeviceMenuBu(device: Device, view: View) {
        this.view = view
        this.device.value = device
    }

    fun onDeviceClick(device: Device) {
        this.deviceClick.value = device
    }

    fun updateDeviceApi(groupId: String, device: Device, timezone: String) {

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.updateDeviceApi(
                device.name,
                device.id.toString(),
                aquarium.value!!.id.toString(),
                group_id = groupId,
                timezone = timezone
            ).collect {
                apiResponse.value = it
            }
        }

    }

    fun updateDeviceWaterType(device: Device, waterType: String) {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.updateDeviceApi(
                id = device.id.toString(),
                name = device.name,
                group_id = "",
                water_type = waterType,
                aquarium_id = aquarium.value!!.id.toString(),
                ip_address = "",
                timezone = TimeZone.getDefault().id
            ).collect {
                apiResponse.value = it
            }
        }
    }

    fun renameDeviceApi(device: Device) {

        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.updateDeviceApi(
                device.name,
                device.id.toString(),
                aquarium_id = "" + aquarium.value!!.id,
                group_id = "",
                ip_address = "" + device.ipAddress,
                timezone = "" + TimeZone.getDefault().id

            ).collect {
                apiResponse.value = it
            }
        }

    }

    fun deleteDeviceApi() {

        apiResponse.value = Resource.Loading()
        viewModelScope.launch {
            repository.deleteDeviceApi(device.value!!.id.toString()).collect {
                apiResponse.value = it
            }
        }

    }

    fun initializedAquarium(id: Int) {
        viewModelScope.launch {

            val job1 = launch {
                repository.getAquariumDetails(id.toString()).collect {
                    apiResponse.value = it
                }
            }

            job1.join()

//            val job2 = launch {
//                repository.getUserDevice(id, pageNumber = 1).collect {
//                    apiResponse1.value = it
//                }
//            }
//
//            job2.join()

//            val job3 = launch {
//                repository.getUserGroups(id, pageNumber = 1).collect {
//                    apiResponse1.value = it
//                }
//            }
//
//            job3.join()

            val job4 = launch {
                repository.getShareAquariumWithUser(id.toString()).collect {
                    apiResponse.value = it
                }
            }

            job4.join()

        }

    }

    fun loadDevicesFirstPage(id: Int) {
        Log.d("AquariumDetailsVM", "loadDevicesFirstPage: ")
        apiResponse1.value = Resource.Loading()
        viewModelScope.launch {
            repository.getUserDevice(id, pageNumber = 1).collect {
                apiResponse1.value = it
            }
        }
    }

    fun loadDevicesNextPage(id: Int, page: Int) {
        Log.d("AquariumDetailsVM", "loadDevicesNextPage: ")
        apiResponse1.value = Resource.Loading()
        viewModelScope.launch {
            repository.getUserDevice(id, pageNumber = page).collect {
                apiResponse1.value = it
            }
        }
    }

    fun loadGroupsFirstPage(id: Int) {
        Log.d("AquariumDetailsVM", "loadFirstPage: ")
        apiResponse1.value = Resource.Loading()
        viewModelScope.launch {
            repository.getUserGroups(id, 1).collect {
                apiResponse1.value = it
            }
        }
    }

    fun loadGroupsNextPage(id: Int, page: Int) {
        Log.d("AquariumDetailsVM", "loadNextPage: ")
//        apiResponse1.value = Resource.Loading()
        viewModelScope.launch {
            repository.getUserGroups(id, pageNumber = page).collect {
                apiResponse1.value = it
            }
        }
    }


}