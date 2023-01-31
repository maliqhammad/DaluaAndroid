package com.dalua.app.utils

import androidx.lifecycle.MutableLiveData
import com.dalua.app.models.GeoLocationResponse
import java.sql.Time
import java.util.regex.Pattern

class AppConstants {

    companion object {

        var DeviceMacAdress: String = ""
        var IsEditOrPreviewOrCreate: String = ""
        var IsFromChangeValue: Boolean = false
        var UpdatePublicSchedule: Boolean = false
        var UpdateMySchedule: Boolean = false
        var UpdateDaluaSchedule: Boolean = false
        var RefreshSchedulePreview: Boolean = false
        var deviceorgroup: String = ""
        var ScheDuleID: Int = 0

        //        val screenPC = 2.5
        val screenPC = 3.4722222222222223
        val screenPC2 = 6.4722222222222223
        var GEOLOCATIONIDEasy: Int = 0
        var GEOLOCATIONID: Int = 0
        val refresh_group_device: MutableLiveData<Boolean> = MutableLiveData()

        /*Live url*/
        const val BASE_URL = "https://daluawholesale.site/api/"
        const val SOCKET_URL = "https://daluawholesale.site:3000"

        /*Development url*/
//        const val BASE_URL = "https://dev.dalua.com/api/"
//        const val SOCKET_URL = "https://dev.dalua.com:3000"
        const val GROUP = "group"
        var GroupTopic = "groupTopic"

        //        const val GoogleClientID = "634084437275-e9gjaae0cqj22mgaap18u4af1f3eufm1.apps.googleusercontent.com"
//        const val GoogleClientID = "918021133918-d15ruomm36dbqsogs0a4qisqfuslgmni.apps.googleusercontent.com"
//        const val GoogleClientID = "854114577529-15805t4qkqei9au5s3mevdjliu5i6gm5.apps.googleusercontent.com"
//        const val GoogleClientID = "918021133918-41vfq778uro83b8jlu1t4048gh87b22l.apps.googleusercontent.com"
        const val GoogleClientID =
            "854114577529-nq4qv3jddr10cajvv25okfn05d66nflm.apps.googleusercontent.com"
        var geoLocation: GeoLocationResponse = GeoLocationResponse()
        var DeviceTopic = "groupTopdfdic"
        var ISGroupOrDevice = ""
        var DeviceOrGroupID: Int? = null
        val refresh_group: MutableLiveData<Boolean> = MutableLiveData()
        val refresh_device: MutableLiveData<Boolean> = MutableLiveData()
        var device_added_successfully = false
        val refresh_aquarium: MutableLiveData<Boolean> = MutableLiveData()
        var SSID = ""
        var PASSWORD = ""
        var ISFROMMASTER = false
        var StartConnectionNow = false
        const val COUNTDOWNTIMERTOREADSTATUS: Long = 40000
        const val INTENT_BLE_GRP = "intent_group_or_ble"
        const val DEVICE_FOUND_IN_PROGRESS = 5550
        const val DEVICE_NOT_FOUND = 6660
        const val DEVICE_FOUND_SUCCESSFUL = 7770
        const val DEVICE_CONNECTION_IN_PROGRESS = 1110
        const val DEVICE_CONNECTION_SUCCESS = 2220
        const val DEVICE_CONNECTION_FAILED = 3330
        const val DEVICE_DISCONNECTION_FAILED = 4440
        const val STUCK_IN_WIFI = -40000
        const val DEVICE_RESET = 0
        const val WiFi_SSID_NOT_AVAILABLE = 1
        const val WiFi_WRONG_CREDENTIALS = 2
        const val WiFi_CONNECTED_BUT_NO_INTERNET_ACCESS = 3
        const val AWS_CONNECTION_FAILED = 4
        const val AWS_SUBSCRIBE_FAILED = 5
        const val WIFI_CONNECTION_IN_PROGRESS = 6
        const val WiFi_CONNECTED = 7
        const val AWS_CONNECTION_IN_PROGRESS = 8
        const val AWS_CONNECTED = 9
        const val AWS_SUBSCRIBE_SUCCESSFUL = 10
        const val REWRITE_TOPIC = 123
        const val identityPoolID =
            "us-east-2:14d23770-63c7-4d88-b2ed-c0ba06ae8d94" // where the user temporary identities are placed...
        const val CUSTOMER_SPECIFIC_ENDPOINT =
            "a22m9v9u8tngpf-ats.iot.us-east-2.amazonaws.com" // end point of our iot devices data...


        fun difference(start: Time, stop: Time): String {

            val diff = Time(0, 0, 0)

            if (stop.seconds > start.seconds) {
                --start.minutes
                start.seconds += 60
            }

            diff.seconds = start.seconds - stop.seconds
            if (stop.minutes > start.minutes) {
                --start.hours
                start.minutes += 60
            }

            diff.minutes = start.minutes - stop.minutes
            diff.hours = start.hours - stop.hours

            return (diff.hours * 60 + diff.minutes).toString()

        }

        fun differenceTime(start: Time, stop: Time): Time {

            val diff = Time(0, 0, 0)

            if (stop.seconds > start.seconds) {
                --start.minutes
                start.seconds += 60
            }

            diff.seconds = start.seconds - stop.seconds
            if (stop.minutes > start.minutes) {
                --start.hours
                start.minutes += 60
            }

            diff.minutes = start.minutes - stop.minutes
            diff.hours = start.hours - stop.hours

            return diff

        }

        fun passwordValidation(password: String): Boolean {
            return true
//            return if (password.length >= 8) {
//                val letter: Pattern = Pattern.compile("[a-z]")
//                val capLetter: Pattern = Pattern.compile("[A-Z]")
//                val digit: Pattern = Pattern.compile("[0-9]")
//                val special: Pattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]")
//                val hasLetter: Matcher = capLetter.matcher(password)
//                val capitalLetter: Matcher = letter.matcher(password)
//                val hasDigit: Matcher = digit.matcher(password)
//                val hasSpecial: Matcher = special.matcher(password)
//                (capitalLetter.find() || hasLetter.find()) && hasDigit.find() && hasSpecial.find() && password.length >= 8
//            } else false
        }

        fun validatePasswrod(password: String?): Boolean {
            val COMPLEX_PASSWORD_REGEX = "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|" +
                    "(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|" +
                    "(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|" +
                    "(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})" +
                    "[A-Za-z0-9!~<>,;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]" +
                    "{8,32}$"
            val PASSWORD_PATTERN = Pattern.compile(COMPLEX_PASSWORD_REGEX)
            return PASSWORD_PATTERN.matcher(password).matches()
        }

        fun isValidName(name: String): Boolean {
            val symbols = "0123456789/?!:;%"
            if (name.any { it in symbols }) {
                return false
            }
            return true
        }

    }

    enum class CurrentActivity {
        PasswordVerification,
        EmailVerification,
        IntentKey1
    }

    enum class IsDeviceOrGroup {
        DEVICE,
        GROUP
    }

    enum class RegistrationType {
        Email {
            override fun toString(): String {
                return "1"
            }
        },
        Google {
            override fun toString(): String {
                return "2"
            }
        },
        Facebook {
            override fun toString(): String {
                return "3"
            }
        },
        Apple {
            override fun toString(): String {
                return "4"
            }
        }
    }

    enum class ApiTypes {
        GetOtaFiles,
        UPDATEOTA,
        GetAquariumListingApi,
        CreateAquariumApi,
        UpdateAquariumApi,
        DeleteAquariumApi,
        ShareAquariumApi,
        RemoveShareAquariumApi,
        ApproveOrRejectAquariumApi,
        ShareUserApi,
        CreateGroupApi,
        GetGroupDetailApi,
        UpdateGroupApi,
        DeleteGroupApi,
        CreateDeviceApi,
        UpdateDeviceApi,
        DeleteDeviceApi,
        UpdateDeviceStatusApi,
        ChangeProductType,
        UnGroupDeviceApi,
        GetUserToken,
        GetUserProfile,
        UpdateUserProfile,
        ResetPasswordRequest,
        VerifyUserCode,
        VerifyPasswordCode,
        LoginApi,
        ResendUserCode,
        UpdateUserPassword,
        DeleteAccount,
        GetUserDevicesApi,
        GetAquariumDetails,
        GetUserGroup,
        GetMySchedules,
        GetDaluaSchedules,
        GetPublicSchedules,
        UpdateSchedules,
        CreateSchedules,
        GetLocationApi,
        CheckMacAddressApi,
        CheckMacAddressApiMulti,
        CheckAckMacAddressApi,
        ReUploadSchedule,
        DeleteSchedule,
        RenameSchedule,
        GetDeviceDetails
    }


}