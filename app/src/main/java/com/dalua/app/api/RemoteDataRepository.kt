package com.dalua.app.api

import com.dalua.app.baseclasses.BaseApiConverter
import com.dalua.app.utils.AppConstants.ApiTypes.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Named

class RemoteDataRepository @Inject constructor(
    private val apiService: ApiService,
    @Named("uniqueID") private val uniqueId: String,
) : BaseApiConverter() {

    suspend fun getUserToken() = flow {
        emit(
            safeApiCall(
                { apiService.loginUserWithUniqueID(uniqueId) }, apiName = GetUserToken.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun getOtaFiles() = flow {
        emit(
            safeApiCall(
                { apiService.getOtaFiles() }, apiName = GetOtaFiles.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun getUserProfile() = flow {
        emit(
            safeApiCall(
                { apiService.userProfile() }, apiName = GetUserProfile.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun createAquariumApi(
        name: String,
        temperature: String,
        ph: String,
        salinity: String,
        alkalinity: String,
        magnesium: String,
        nitrate: String,
        phosphate: String,
        test_frequency: String,
        clean_frequency: String,
    ) = flow {
        emit(safeApiCall({
            apiService.createAquarium(
                name, temperature, ph, salinity, alkalinity,
                magnesium, nitrate, phosphate, test_frequency, clean_frequency
            )
        }, apiName = CreateAquariumApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun deleteDeviceApi(id: String) = flow {
        emit(safeApiCall({ apiService.deleteUserDevice(id) }, apiName = DeleteDeviceApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun updateDeviceStatusApi(
        id: String,
        completed: Int,
        wifi: String,
        water_type: String,
        ipAddress: String? = null
    ) = flow {
        emit(
            safeApiCall(
                {
                    apiService.updateUserDeviceStatus(
                        id = id,
                        completed = completed,
                        wifi = wifi,
                        water_type = water_type,
                        ipAddress = ipAddress
                    )
                },
                apiName = UpdateDeviceStatusApi.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun changeProductType(
        id: String,
        productId: Int
    ) = flow {
        emit(
            safeApiCall(
                {
                    apiService.changeProductType(
                        id = id,
                        productId = productId
                    )
                },
                apiName = ChangeProductType.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun updateDeviceStatusApi(id: String, status: Int) = flow {
        emit(
            safeApiCall(
                { apiService.updateUserDeviceStatus(id, status) },
                apiName = UpdateDeviceStatusApi.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun deleteAquariumApi(id: String) = flow {
        emit(safeApiCall({ apiService.deleteUserAquarium(id) }, DeleteAquariumApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun shareAquarium(aquarium_id: String, email: String) = flow {
        emit(safeApiCall({ apiService.shareAquarium(aquarium_id, email) }, ShareAquariumApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun removeShareAquarium(aquarium_id: String, user_id: String) = flow {
        emit(
            safeApiCall(
                { apiService.removeShareAquarium(aquarium_id, user_id) },
                RemoveShareAquariumApi.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun approveOrRejectAquarium(id: String, status: String) = flow {
        emit(
            safeApiCall(
                { apiService.approveOrRejectAquarium(id, status) },
                ApproveOrRejectAquariumApi.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun getShareAquariumWithUser(aquarium_id: String) = flow {
        emit(safeApiCall({ apiService.getShareAquariumWithUser(aquarium_id) }, ShareUserApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun getUserAquarium() = flow {
        emit(safeApiCall({ apiService.getUserAquarium() }, GetAquariumListingApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun getDeviceDetails(id: String) = flow {
        emit(safeApiCall({ apiService.getDeviceDetails(id) }, GetDeviceDetails.name))
    }.flowOn(Dispatchers.IO)

    suspend fun getUserGroups(aquariumID: Int, pageNumber: Int? = null) = flow {
        emit(
            safeApiCall(
                { apiService.getUserGroup(aquariumID, page = pageNumber) },
                GetUserGroup.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun updateDeviceApi(
        name: String,
        id: String,
        aquarium_id: String,
        group_id: String? = null,
        water_type: String? = null,
        ip_address: String? = null,
        timezone: String,

        ): Flow<Resource<ResponseBody>> = flow {
        emit(safeApiCall({
            apiService.updateUserDevice(
                name = name,
                id = id,
                aquarium_id = aquarium_id,
                water_type = water_type,
                group_id = group_id,
                timezone = timezone
            )
        }, UpdateDeviceApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun updateAquariumApi(
        name: String, id: String, temperature: String, ph: String, salinity: String,
        alkalinity: String, magnesium: String, nitrate: String, phosphate: String,
    ) = flow {
        emit(safeApiCall({
            apiService.updateAquarium(
                name, id, temperature, ph, salinity, alkalinity,
                magnesium, nitrate, phosphate
            )
        }, UpdateAquariumApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun createGroupApi(
        name: String, aquariumID: String, timezone: String
    ) = flow {
        emit(safeApiCall({
            apiService.createUserGroup(
                name, aquariumID, timezone
            )
        }, CreateGroupApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun getAquariumDetails(id: String): Flow<Resource<ResponseBody>> {

        return flow {
            emit(safeApiCall({
                apiService.getAquariumDetails(id)
            }, GetAquariumDetails.name))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun getPreviewSchedule(
        path: String,
        device_id: Int?,
        group_id: Int?,
        apiType: String
    ): Flow<Resource<ResponseBody>> {

        return flow {
            emit(safeApiCall({
                apiService.getPreviewSchedule(path, device_id, group_id)
            }, apiType))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun getMySchedules(
        device_id: Int?,
        group_id: Int?,
        page: Int?
    ): Flow<Resource<ResponseBody>> {

        return flow {
            emit(safeApiCall({
                apiService.getMySchedules(device_id, group_id, page = page)
            }, GetMySchedules.name))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun deleteSchedule(
        scheduleId: Int
    ): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.deleteSchedule(scheduleId)
            }, DeleteSchedule.name))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun renameSchedule(
        scheduleId: Int,
        name: String
    ): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.renameSchedule(scheduleId, name)
            }, RenameSchedule.name))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun getPublicSchedules(page: Int): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.getPublicSchedules(page)
            }, GetPublicSchedules.name))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun getDaluaSchedules(page: Int?): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.getDaluaSchedules(page)
            }, GetDaluaSchedules.name))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserDevice(id: Int, grpID: Int? = null, pageNumber: Int? = null) = flow {
        emit(safeApiCall({
            apiService.getUserDevices(id, grpID, pageNumber)
        }, GetUserDevicesApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun createDeviceApi(
        name: String,
        product: String,
        aquarium_id: String,
        group_id: Int?,
        mac_address: String,
        timezone: String,
    ) = flow {

        emit(safeApiCall({
            apiService.createDevice(
                name,
                product,
                aquarium_id,
                group_id,
                mac_address,
                timezone
            )
        }, CreateDeviceApi.name))
    }

    suspend fun updateDeviceApiName(
        name: String,
        id: String,
//        product_id: String,
        aquarium_id: String,
        group_id: Int? = null,
        ip_address: String? = null,
        timezone: String
    ) = flow {
        emit(safeApiCall({
            apiService.updateUserDeviceName(
                name = name,
                id = id,
//                product_id = product_id,
                aquarium_id = aquarium_id,
                group_id = group_id,
                ip_address = ip_address,
                timezone = timezone
            )
        }, UpdateDeviceApi.name))
    }.flowOn(Dispatchers.IO)


    suspend fun updateGroupApiName(
        name: String,
        id: String,
        aquarium_id: String,
        device_id: String? = null,
        timezone: String
    ) = flow {
        emit(safeApiCall({
            apiService.updateUserGroup(
                name,
                id,
                aquarium_id,
                device_id,
                timezone
            )
        }, UpdateGroupApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun deleteGroupApi(id: String) = flow {
        emit(
            safeApiCall(
                { apiService.deleteUserGroup(id) }, DeleteGroupApi.name
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun getGroupDetailApi(id: String) = flow {
        emit(
            safeApiCall(
                { apiService.getGroupDetail(id) }, GetGroupDetailApi.name
            )
        )
    }.flowOn(Dispatchers.IO)


    suspend fun unGroupDeviceApi(
        name: String,
        id: String,
        aquarium_id: String,
        group_id: String? = null,
        timezone: String,
        ip_address: String? = null,
    ) = flow {
        emit(safeApiCall({
            apiService.unGroupDevice(
                name = name,
                id = id,
                group_id = group_id,
                aquarium_id = aquarium_id,
                timezone = timezone,
                ip_address = ip_address
            )
        }, UnGroupDeviceApi.name))
    }.flowOn(Dispatchers.IO)

    suspend fun getLocation(): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({ apiService.getLocations() }, GetLocationApi.name))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun checkMacAddress(mac_address: List<String>): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({ apiService.checkMacAddress(mac_address) }, CheckMacAddressApi.name))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun checkMacAddressMulti(mac_address: List<String>): Flow<Resource<ResponseBody>> {
        return flow {
            emit(
                safeApiCall(
                    { apiService.checkMacAddressMulti(mac_address) },
                    CheckMacAddressApiMulti.name
                )
            )
        }.flowOn(Dispatchers.IO)
    }


    suspend fun checkAckMacAddress(mac_address: List<String>): Flow<Resource<ResponseBody>> {
        return flow {
            emit(
                safeApiCall(
                    { apiService.checkAckMacAddress(mac_address) },
                    CheckAckMacAddressApi.name
                )
            )
        }.flowOn(Dispatchers.IO)
    }

    suspend fun reUploadSchedule(hashMap: HashMap<String, String>): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({ apiService.reUploadSchedule(hashMap) }, ReUploadSchedule.name))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateSchedule(
        name: String,
        public: Int,
        hashmap: HashMap<String, String>,
        id: Int,
        geo_location: Int,
        geo_location_id: Int?,
        enabled: Int,
        mode: Int,
        moon_light: String,
        slot_0_start_time: String,
        slot_0_value_a: String,
        slot_0_value_b: String,
        slot_0_value_c: String,
        slot_0_gradual_or_step: String,
        slot_1_start_time: String,
        slot_1_value_a: String,
        slot_1_value_b: String,
        slot_1_value_c: String,
        slot_1_gradual_or_step: String,
        slot_2_start_time: String,
        slot_2_value_a: String,
        slot_2_value_b: String,
        slot_2_value_c: String,
        slot_2_gradual_or_step: String,
        slot_3_start_time: String,
        slot_3_value_a: String,
        slot_3_value_b: String,
        slot_3_value_c: String,
        slot_3_gradual_or_step: String,
        slot_4_start_time: String,
        slot_4_value_a: String,
        slot_4_value_b: String,
        slot_4_value_c: String,
        slot_4_gradual_or_step: String,
        slot_5_start_time: String,
        slot_5_value_a: String,
        slot_5_value_b: String,
        slot_5_value_c: String,
        slot_5_gradual_or_step: String
    ): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.updateSchedule(
                    name,
                    public,
                    hashmap,
                    id,
                    geo_location,
                    geo_location_id,
                    enabled,
                    mode,
                    moon_light,
                    slot_0_start_time,
                    slot_0_value_a,
                    slot_0_value_b,
                    slot_0_value_c,
                    slot_0_gradual_or_step,
                    slot_1_start_time,
                    slot_1_value_a,
                    slot_1_value_b,
                    slot_1_value_c,
                    slot_1_gradual_or_step,
                    slot_2_start_time,
                    slot_2_value_a,
                    slot_2_value_b,
                    slot_2_value_c,
                    slot_2_gradual_or_step,
                    slot_3_start_time,
                    slot_3_value_a,
                    slot_3_value_b,
                    slot_3_value_c,
                    slot_3_gradual_or_step,
                    slot_4_start_time,
                    slot_4_value_a,
                    slot_4_value_b,
                    slot_4_value_c,
                    slot_4_gradual_or_step,
                    slot_5_start_time,
                    slot_5_value_a,
                    slot_5_value_b,
                    slot_5_value_c,
                    slot_5_gradual_or_step
                )
            }, UpdateSchedules.name))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createSchedule(
        name: String,
        public: Int,
        id: HashMap<String, String>,
        geo_location: Int,
        geo_location_id: Int?,
        enabled: Int,
        mode: Int,
        moon_light: String,
        slot_0_start_time: String,
        slot_0_value_a: String,
        slot_0_value_b: String,
        slot_0_value_c: String,
        slot_0_gradual_or_step: String,
        slot_1_start_time: String,
        slot_1_value_a: String,
        slot_1_value_b: String,
        slot_1_value_c: String,
        slot_1_gradual_or_step: String,
        slot_2_start_time: String,
        slot_2_value_a: String,
        slot_2_value_b: String,
        slot_2_value_c: String,
        slot_2_gradual_or_step: String,
        slot_3_start_time: String,
        slot_3_value_a: String,
        slot_3_value_b: String,
        slot_3_value_c: String,
        slot_3_gradual_or_step: String,
        slot_4_start_time: String,
        slot_4_value_a: String,
        slot_4_value_b: String,
        slot_4_value_c: String,
        slot_4_gradual_or_step: String,
        slot_5_start_time: String,
        slot_5_value_a: String,
        slot_5_value_b: String,
        slot_5_value_c: String,
        slot_5_gradual_or_step: String
    ): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.createSchedule(
                    name,
                    public,
                    id,
                    geo_location,
                    geo_location_id,
                    enabled,
                    mode,
                    moon_light,
                    slot_0_start_time,
                    slot_0_value_a,
                    slot_0_value_b,
                    slot_0_value_c,
                    slot_0_gradual_or_step,
                    slot_1_start_time,
                    slot_1_value_a,
                    slot_1_value_b,
                    slot_1_value_c,
                    slot_1_gradual_or_step,
                    slot_2_start_time,
                    slot_2_value_a,
                    slot_2_value_b,
                    slot_2_value_c,
                    slot_2_gradual_or_step,
                    slot_3_start_time,
                    slot_3_value_a,
                    slot_3_value_b,
                    slot_3_value_c,
                    slot_3_gradual_or_step,
                    slot_4_start_time,
                    slot_4_value_a,
                    slot_4_value_b,
                    slot_4_value_c,
                    slot_4_gradual_or_step,
                    slot_5_start_time,
                    slot_5_value_a,
                    slot_5_value_b,
                    slot_5_value_c,
                    slot_5_gradual_or_step
                )
            }, CreateSchedules.name))
        }.flowOn(Dispatchers.IO)
    }


    suspend fun updateEasySchedule(
        name: String,
        moon_light: String,
        hashmap: HashMap<String, String>,
        public: Int,
        id: Int,
        geo_location: Int,
        geo_location_id: Int?,
        enabled: Int,
        mode: Int,
        sunrise: String,
        sunset: String,
        value_a: String,
        value_b: String,
        value_c: String,
        ramp_time: String,

        ): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.updateEasySchedule(
                    name,
                    moon_light,
                    hashmap,
                    public,
                    id,
                    geo_location,
                    geo_location_id,
                    enabled,
                    mode,
                    sunrise,
                    sunset,
                    value_a,
                    value_b,
                    value_c,
                    ramp_time
                )
            }, UpdateSchedules.name))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createEasySchedule(
        name: String,
        moon_light: String,
        public: Int,
        device_or_group_id: HashMap<String, String>,
        geo_location: Int,
        geo_location_id: Int?,
        enabled: Int,
        mode: Int,
        sunrise: String,
        sunset: String,
        value_a: String,
        value_b: String,
        value_c: String,
        ramp_time: String,

        ): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.createEasySchedule(
                    name, moon_light,
                    public,
                    device_or_group_id,
                    geo_location,
                    geo_location_id,
                    enabled,
                    mode,
                    sunrise,
                    sunset,
                    value_a,
                    value_b,
                    value_c,
                    ramp_time
                )
            }, CreateSchedules.name))
        }.flowOn(Dispatchers.IO)
    }

    fun updateProfile(
        country: String,
        phone_no: String,
        first_name: String,
        tank_size: String,
    ): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.updateUserProfile(
                    country,
                    phone_no,
                    first_name,
                    tank_size
                )
            }, UpdateUserProfile.name))
        }

    }

    fun updateProfile(
        country: RequestBody,
        phone_no: RequestBody,
        first_name: RequestBody,
        tank_size: RequestBody,
        country_code: RequestBody,
        image: MultipartBody.Part? = null
    ): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.updateUserProfile(
                    country,
                    phone_no,
                    first_name,
                    tank_size,
                    country_code,
                    image
                )
            }, UpdateUserProfile.name))
        }

    }

    fun updatePassword(oldPassword: String, password: String): Flow<Resource<ResponseBody>> {
        return flow {
            emit(safeApiCall({
                apiService.updateUserPassword(
                    oldPassword,
                    password,
                )
            }, UpdateUserPassword.name))
        }

    }

    suspend fun deleteAccount(): Flow<Resource<ResponseBody>> {
        return flow { emit(safeApiCall({ apiService.deleteAccount() }, DeleteAccount.name)) }
    }


}