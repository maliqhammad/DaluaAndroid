package com.dalua.app.api

import com.dalua.app.models.apiparameter.ChangePassword
import com.dalua.app.models.apiparameter.SignupUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @FormUrlEncoded
    @POST("auth/new-user")
    suspend fun loginUserWithUniqueID(
        @Field("unique_id") unique_id: String
    ): Response<ResponseBody>

    @GET("profile/detail")
    suspend fun userProfile(): Response<ResponseBody>

    @GET("ota/get/files")
    suspend fun getOtaFiles(): Response<ResponseBody>


    @POST("auth/register")
    suspend fun signUpNewUser(
        @Body unique_id: SignupUser
    ): Response<ResponseBody>

    @POST("user/delete-account")
    suspend fun deleteAccount(): Response<ResponseBody>

    @FormUrlEncoded
    @POST("auth/reset-password-mail")
    suspend fun resetPasswordRequest(
        @Field("email") email: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("auth/verify-email")
    suspend fun verifyCode(
        @Field("email") email: String,
        @Field("verification_code") verification_code: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("auth/verify-reset-code")
    suspend fun verifyPasswordCode(
        @Field("email") email: String,
        @Field("verification_code") verification_code: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginUser(
        @Field("username") email: String,
        @Field("password") password: String,
        @Field("login_type") login_type: String
    ): Response<ResponseBody>

    @POST("auth/reset-password")
    suspend fun changeUserPassword(
        @Body changePassword: ChangePassword
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("auth/verify-code-resend")
    suspend fun resendVerificationCode(
        @Field("email") email: String,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("aquariums/store")
    suspend fun createAquarium(
        @Field("name") name: String,
        @Field("temperature") temperature: String,
        @Field("ph") ph: String,
        @Field("salinity") salinity: String,
        @Field("alkalinity") alkalinity: String,
        @Field("magnesium") magnesium: String,
        @Field("nitrate") nitrate: String,
        @Field("phosphate") phosphate: String,
        @Field("test_frequency") test_frequency: String,
        @Field("clean_frequency") clean_frequency: String,

        ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("devices/delete")
    suspend fun deleteUserDevice(
        @Field("id") id: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("devices/status-update")
    suspend fun updateUserDeviceStatus(
        @Field("id") id: String,
        @Field("completed") completed: Int,
        @Field("wifi") wifi: String,
        @Field("water_type") water_type: String,
        @Field("status") status: Int? = null,
        @Field("ip_address") ipAddress: String? = null,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("devices/change-product")
    suspend fun changeProductType(
        @Field("id") id: String,
        @Field("product_id") productId: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("devices/status-update")
    suspend fun updateUserDeviceStatus(
        @Field("id") id: String,
        @Field("status") status: Int? = null,
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("devices/details")
    suspend fun getDeviceDetails(
        @Field("id") id: String
    ): Response<ResponseBody>

    @POST("aquariums/listing")
    suspend fun getUserAquarium(): Response<ResponseBody>


    @FormUrlEncoded
    @POST("devices/update")
    suspend fun updateUserDevice(
        @Field("name") name: String,
        @Field("id") id: String,
        @Field("aquarium_id") aquarium_id: String,
        @Field("water_type") water_type: String? = null,
        @Field("group_id") group_id: String? = null,
        @Field("timezone") timezone: String,
        @Field("ip_address") ip_address: String? = null
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("aquariums/update")
    suspend fun updateAquarium(
        @Field("name") name: String,
        @Field("id") id: String,
        @Field("temperature") temperature: String,
        @Field("ph") ph: String,
        @Field("salinity") salinity: String,
        @Field("alkalinity") alkalinity: String,
        @Field("magnesium") magnesium: String,
        @Field("nitrate") nitrate: String,
        @Field("phosphate") phosphate: String,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("aquariums/delete")
    suspend fun deleteUserAquarium(
        @Field("id") id: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("share/aquarium")
    suspend fun shareAquarium(
        @Field("aquarium_id") aquarium_id: String,
        @Field("email") email: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("aquariums/shared-users")
    suspend fun getShareAquariumWithUser(
        @Field("aquarium_id") aquarium_id: String,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("share/remove-aquarium")
    suspend fun removeShareAquarium(
        @Field("aquarium_id") aquarium_id: String,
        @Field("user_id") user_id: String
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("aquariums/approve-aquarium")
    suspend fun approveOrRejectAquarium(
        @Field("id") id: String,
        @Field("status") status: String,
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("groups/store")
    suspend fun createUserGroup(
        @Field("name") name: String,
        @Field("aquarium_id") aquarium_id: String,
        @Field("timezone") timezone: String
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("groups/listing")
    suspend fun getUserGroup(
        @Field("aquarium_id") id: Int,
        @Query("page") page: Int? = null
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("devices/listing")
    suspend fun getUserDevices(
        @Field("aquarium_id") aquarium_id: Int,
        @Field("group_id") group_id: Int? = null,
        @Query("page") page: Int? = null
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("aquariums/details")
    suspend fun getAquariumDetails(
        @Field("id") name: String
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("devices/store")
    suspend fun createDevice(
        @Field("name") name: String,
        @Field("product") product_id: String,
        @Field("aquarium_id") aquarium_id: String,
        @Field("group_id") group_id: Int?,
        @Field("mac_address") mac_address: String,
        @Field("timezone") timezone: String
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("devices/update")
    suspend fun updateUserDeviceName(
        @Field("name") name: String,
        @Field("id") id: String,
//        @Field("product_id") product_id: String,
        @Field("aquarium_id") aquarium_id: String,
        @Field("group_id") group_id: Int? = null,
        @Field("timezone") timezone: String,
        @Field("ip_address") ip_address: String? = null
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("groups/update")
    suspend fun updateUserGroup(
        @Field("name") name: String,
        @Field("id") id: String,
        @Field("aquarium_id") aquarium_id: String,
        @Field("devices[0]") device_id: String? = null,
        @Field("timezone") timezone: String
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("groups/delete")
    suspend fun deleteUserGroup(
        @Field("id") id: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("groups/detail")
    suspend fun getGroupDetail(
        @Field("id") id: String
    ): Response<ResponseBody>

    //multi
    @FormUrlEncoded
    @POST("devices/check-mac-addresses")
    suspend fun checkMacAddress(@Field("mac_addresses[]") macAddress: List<String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST("devices/check-mac-addresses-multi")
    suspend fun checkMacAddressMulti(@Field("mac_addresses[]") macAddress: List<String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST("devices/check-ack-mac-addresses-multi")
    suspend fun checkAckMacAddress(@Field("mac_addresses[]") macAddress: List<String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST("devices/update")
    suspend fun unGroupDevice(
        @Field("name") name: String,
        @Field("id") id: String,
        @Field("aquarium_id") aquarium_id: String,
        @Field("timezone") timezone: String,
        @Field("ip_address") ip_address: String? = null,
        @Field("group_id") group_id: String? = null
    ): Response<ResponseBody>


    @GET("schedules/{url}")
    suspend fun getPreviewSchedule(
        @Path("url") path: String,
        @Query("device_id") device_id: Int?,
        @Query("group_id") group_id: Int?
    ): Response<ResponseBody>

    @GET("schedules/listing")
    suspend fun getMySchedules(
        @Query("device_id") device_id: Int?,
        @Query("group_id") group_id: Int?,
        @Query("page") page: Int?
    ): Response<ResponseBody>

    @POST("schedules/delete")
    suspend fun deleteSchedule(
        @Query("id") ScheduleId: Int
    ): Response<ResponseBody>

    @POST("schedules/update-name")
    suspend fun renameSchedule(
        @Query("id") ScheduleId: Int,
        @Query("name") name: String
    ): Response<ResponseBody>

    @GET("schedules/public")
    suspend fun getPublicSchedules(@Query("page") page: Int): Response<ResponseBody>

    @GET("schedules/dalua")
    suspend fun getDaluaSchedules(@Query("page") page: Int?): Response<ResponseBody>


    @FormUrlEncoded
    @POST("schedules/resend")
    suspend fun reUploadSchedule(@FieldMap hashmap: HashMap<String, String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST("schedules/resend")
    fun reUploadSchedule1(@FieldMap hashmap: HashMap<String, String>): Response<ResponseBody>


    @GET("locations/listing")
    suspend fun getLocations(): Response<ResponseBody>

    @FormUrlEncoded
    @POST("schedules/update")
    suspend fun updateSchedule(

        @Field("name") name: String,
        @Field("public") public: Int,
        @FieldMap hashmap: HashMap<String, String>,
        @Field("id") id: Int,
        @Field("geo_location") geo_location: Int,
        @Field("geo_location_id") geo_location_id: Int? = null,
        @Field("enabled") enabled: Int,
        @Field("mode") mode: Int,

        @Field("moonlight_enabled") moon_light: String,
        @Field("slots[0][start_time]") slot_0_start_time: String,
        @Field("slots[0][value_a]") slot_0_value_a: String,
        @Field("slots[0][value_b]") slot_0_value_b: String,
        @Field("slots[0][value_c]") slot_0_value_c: String,

        @Field("slots[0][type]") slot_0_gradual_or_step: String,
        @Field("slots[1][start_time]") slot_1_start_time: String,
        @Field("slots[1][value_a]") slot_1_value_a: String,
        @Field("slots[1][value_b]") slot_1_value_b: String,
        @Field("slots[1][value_c]") slot_1_value_c: String,

        @Field("slots[1][type]") slot_1_gradual_or_step: String,
        @Field("slots[2][start_time]") slot_2_start_time: String,
        @Field("slots[2][value_a]") slot_2_value_a: String,
        @Field("slots[2][value_b]") slot_2_value_b: String,
        @Field("slots[2][value_c]") slot_2_value_c: String,

        @Field("slots[2][type]") slot_2_gradual_or_step: String,
        @Field("slots[3][start_time]") slot_3_start_time: String,
        @Field("slots[3][value_a]") slot_3_value_a: String,
        @Field("slots[3][value_b]") slot_3_value_b: String,
        @Field("slots[3][value_c]") slot_3_value_c: String,

        @Field("slots[3][type]") slot_3_gradual_or_step: String,
        @Field("slots[4][start_time]") slot_4_start_time: String,
        @Field("slots[4][value_a]") slot_4_value_a: String,
        @Field("slots[4][value_b]") slot_4_value_b: String,
        @Field("slots[4][value_c]") slot_4_value_c: String,

        @Field("slots[4][type]") slot_4_gradual_or_step: String,
        @Field("slots[5][start_time]") slot_5_start_time: String,
        @Field("slots[5][value_a]") slot_5_value_a: String,
        @Field("slots[5][value_b]") slot_5_value_b: String,
        @Field("slots[5][value_c]") slot_5_value_c: String,
        @Field("slots[5][type]") slot_5_gradual_or_step: String,


        ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("schedules/store")
    suspend fun createSchedule(

        @Field("name") name: String,
        @Field("public") public: Int,
        @FieldMap hashmap: HashMap<String, String>,
        @Field("geo_location") geo_location: Int,
        @Field("geo_location_id") geo_location_id: Int? = null,
        @Field("enabled") enabled: Int,
        @Field("mode") mode: Int,
        @Field("moonlight_enabled") moon_light: String,

        @Field("slots[0][start_time]") slot_0_start_time: String,
        @Field("slots[0][value_a]") slot_0_value_a: String,
        @Field("slots[0][value_b]") slot_0_value_b: String,
        @Field("slots[0][value_c]") slot_0_value_c: String,
        @Field("slots[0][type]") slot_0_gradual_or_step: String,

        @Field("slots[1][start_time]") slot_1_start_time: String,
        @Field("slots[1][value_a]") slot_1_value_a: String,
        @Field("slots[1][value_b]") slot_1_value_b: String,
        @Field("slots[1][value_c]") slot_1_value_c: String,
        @Field("slots[1][type]") slot_1_gradual_or_step: String,

        @Field("slots[2][start_time]") slot_2_start_time: String,
        @Field("slots[2][value_a]") slot_2_value_a: String,
        @Field("slots[2][value_b]") slot_2_value_b: String,
        @Field("slots[2][value_c]") slot_2_value_c: String,
        @Field("slots[2][type]") slot_2_gradual_or_step: String,

        @Field("slots[3][start_time]") slot_3_start_time: String,
        @Field("slots[3][value_a]") slot_3_value_a: String,
        @Field("slots[3][value_b]") slot_3_value_b: String,
        @Field("slots[3][value_c]") slot_3_value_c: String,
        @Field("slots[3][type]") slot_3_gradual_or_step: String,

        @Field("slots[4][start_time]") slot_4_start_time: String,
        @Field("slots[4][value_a]") slot_4_value_a: String,
        @Field("slots[4][value_b]") slot_4_value_b: String,
        @Field("slots[4][value_c]") slot_4_value_c: String,
        @Field("slots[4][type]") slot_4_gradual_or_step: String,

        @Field("slots[5][start_time]") slot_5_start_time: String,
        @Field("slots[5][value_a]") slot_5_value_a: String,
        @Field("slots[5][value_b]") slot_5_value_b: String,
        @Field("slots[5][value_c]") slot_5_value_c: String,
        @Field("slots[5][type]") slot_5_gradual_or_step: String,


        ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("schedules/update-easy")
    suspend fun updateEasySchedule(
        @Field("name") name: String,
        @Field("moonlight_enabled") moon_light: String,
        @FieldMap hashmap: HashMap<String, String>,
        @Field("public") public: Int,
        @Field("id") id: Int,
        @Field("geo_location") geo_location: Int,
        @Field("geo_location_id") geo_location_id: Int? = null,
        @Field("enabled") enabled: Int,
        @Field("mode") mode: Int,
        @Field("sunrise") sunrise: String,
        @Field("sunset") sunset: String,
        @Field("value_a") value_a: String,
        @Field("value_b") value_b: String,
        @Field("value_c") value_c: String,
        @Field("ramp_time") ramp_time: String,
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("schedules/store")
    suspend fun createEasySchedule(
        @Field("name") name: String,
        @Field("moonlight_enabled") moon_light: String,
        @Field("public") public: Int,
        @FieldMap hashmap: HashMap<String, String>,
        @Field("geo_location") geo_location: Int,
        @Field("geo_location_id") geo_location_id: Int? = null,
        @Field("enabled") enabled: Int,
        @Field("mode") mode: Int,
        @Field("sunrise") sunrise: String,
        @Field("sunset") sunset: String,
        @Field("value_a") value_a: String,
        @Field("value_b") value_b: String,
        @Field("value_c") value_c: String,
        @Field("ramp_time") ramp_time: String,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("profile/update-profile")
    suspend fun updateUserProfile(
        @Field("country") country: String,
        @Field("phone_no") phone_no: String,
        @Field("first_name") first_name: String,
        @Field("tank_size") tank_size: String,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("profile/update-password")
    suspend fun updateUserPassword(
        @Field("old_password") old_password: String,
        @Field("password") password: String,
    ): Response<ResponseBody>

    @Multipart
    @POST("profile/update-profile")
    suspend fun updateUserProfile(
        @Part("country") country: RequestBody,
        @Part("phone_no") phone_no: RequestBody,
        @Part("first_name") first_name: RequestBody,
        @Part("tank_size") tank_size: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part image: MultipartBody.Part? = null,
    ): Response<ResponseBody>

    @Multipart
    @POST("update/")
    suspend fun updateOta(
        @Part image: MultipartBody.Part? = null
    ): Response<ResponseBody>

}