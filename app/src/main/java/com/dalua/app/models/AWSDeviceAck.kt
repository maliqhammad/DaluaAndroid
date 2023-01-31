package com.dalua.app.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CheckAckMacAddressResponse {

    @SerializedName("success")
    @Expose
    var success = false

    @SerializedName("message")
    @Expose
    var message = ""

    @SerializedName("data")
    @Expose
    var data = ArrayList<SocketACKResponseModel>()
}