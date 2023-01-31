package com.dalua.app.models

import com.dalua.app.models.schedulemodel.SingleSchedule
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Device {
    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("uid")
    @Expose
    var uid: String = ""

    @SerializedName("topic")
    @Expose
    var topic: String = ""

    @SerializedName("device_topic")
    @Expose
    var deviceTopic: String = ""

    @SerializedName("wifi")
    @Expose
    var wifi: String = ""

    @SerializedName("product_id")
    @Expose
    var productId: Int = 0

    @SerializedName("group_id")
    @Expose
    var groupId: Int = 0

    @SerializedName("created_at")
    @Expose
    var createdAt: String = ""

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String = ""

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("mac_address")
    @Expose
    var macAddress: String = ""

    @SerializedName("ip_address")
    @Expose
    var ipAddress: String = ""

    @SerializedName("configuration")
    @Expose
    var configuration: Configuration = Configuration()

    @SerializedName("status")
    @Expose
    var status: Int = 0

    @SerializedName("completed")
    @Expose
    var completed: Int = 0

    @SerializedName("water_type")
    @Expose
    var waterType: String = ""

    @SerializedName("version")
    @Expose
    var version: Int? = null

    @SerializedName("device_name")
    @Expose
    var deviceName: String = ""

    @SerializedName("esp_product_name")
    @Expose
    var espProductName: String? = null

    @SerializedName("schedule")
    @Expose
    var schedule: SingleSchedule = SingleSchedule()

    @SerializedName("aquarium")
    @Expose
    var aquarium: Aquarium = Aquarium()

    @SerializedName("settings")
    @Expose
    var settings: Aquarium = Aquarium()

    @SerializedName("product")
    @Expose
    var product: Product = Product()

    class Aquarium {
        @SerializedName("id")
        @Expose
        var id: Int = 0

        @SerializedName("name")
        @Expose
        var name: String = ""

        @SerializedName("created_at")
        @Expose
        var createdAt: String = ""

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String = ""

        @SerializedName("test_frequency")
        @Expose
        var testFrequency: Int = 0

        @SerializedName("last_test")
        @Expose
        var lastTest: String = ""

        @SerializedName("clean_frequency")
        @Expose
        var cleanFrequency: Int = 0

        @SerializedName("last_clean")
        @Expose
        var lastClean: String = ""
    }

    class Settings {
        @SerializedName("value_a")
        @Expose
        var valueA: Int = 0

        @SerializedName("value_b")
        @Expose
        var valueB: Int = 0

        @SerializedName("value_c")
        @Expose
        var valueC: Int = 0

        @SerializedName("master_control")
        @Expose
        var masterControl: Int = 0

        @SerializedName("device_id")
        @Expose
        var deviceId: Int = 0

        @SerializedName("created_at")
        @Expose
        var createdAt: String = ""

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String = ""
    }
}