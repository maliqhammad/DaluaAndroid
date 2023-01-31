package com.dalua.app.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SocketResponseModel {
    @SerializedName("mac_address")
    @Expose
    var macAddress= ""

    @SerializedName("status")
    @Expose
    var status= -1


}