package com.dalua.app.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseOtaFiles {
    @SerializedName("success")
    @Expose
    var success = false

    @SerializedName("message")
    @Expose
    var message = ""

    @SerializedName("data")
    @Expose
    var data: MutableList<OtaFile> = mutableListOf()

    class OtaFile {
        @SerializedName("id")
        @Expose
        var id = 0

        @SerializedName("name")
        @Expose
        var name = ""

        @SerializedName("location")
        @Expose
        var location = ""

        @SerializedName("version")
        @Expose
        var version = 0

        @SerializedName("token")
        @Expose
        var token = ""

        @SerializedName("created_at")
        @Expose
        var createdAt = ""

        @SerializedName("updated_at")
        @Expose
        var updatedAt = ""

        @SerializedName("product_id")
        @Expose
        var productId = 0

        @SerializedName("url")
        @Expose
        var url = ""

        @SerializedName("product")
        @Expose
        var product = Product()

        @SerializedName("selected")
        @Expose
        var selected = false


        @SerializedName("bytesArray")
        @Expose
        var bytesArray: ByteArray? = null


    }
}