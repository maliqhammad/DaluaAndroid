package com.dalua.app.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SocketACKResponseModel {

    @SerializedName("fromDevice")
    @Expose
    var fromDevice: Boolean= false

    @SerializedName("macAddress")
    @Expose
    var macAddress: String = ""

    @SerializedName("commandID")
    @Expose
    var commandID: Int = 0

    @SerializedName("status")
    @Expose
    var status: Int = 0

    @SerializedName("timestamp")
    @Expose
    var timestamp: String = ""

}
