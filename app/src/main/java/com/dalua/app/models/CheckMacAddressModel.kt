package com.dalua.app.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CheckMacAddressModel {

    @SerializedName("success")
    @Expose
    var success: Boolean = false

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("data")
    @Expose
    var data: List<Data> = listOf()

    class Data {

        @SerializedName("mac")
        @Expose
        var mac: String = ""

        @SerializedName("status")
        @Expose
        var status: Boolean = false
    }
}