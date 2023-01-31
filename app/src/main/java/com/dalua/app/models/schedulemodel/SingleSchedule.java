package com.dalua.app.models.schedulemodel;

import com.dalua.app.models.EasySlots;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SingleSchedule {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("geo_location")
    @Expose
    private int geoLocation;
    @SerializedName("geo_location_id")
    @Expose
    private String geoLocationId;
    @SerializedName("public")
    @Expose
    private String _public;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("device_id")
    @Expose
    private Integer deviceId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("slots")
    @Expose
    private List<Slot> slots = null;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("enabled")
    @Expose
    private String enabled;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("easy_slots")
    @Expose
    private EasySlots easySlots;
    @SerializedName("moonlight_enabled")
    @Expose
    private int moonlightEnabled;
    @SerializedName("water_type")
    @Expose
    private String waterType;
    @SerializedName("location")
    @Expose
    private Location location;

    public int getMoonlightEnable() {
        return moonlightEnabled;
    }

    public void setMoonlightEnable(int moonlightEnabled) {
        this.moonlightEnabled = moonlightEnabled;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public EasySlots getEasySlots() {
        return easySlots;
    }

    public void setEasySlots(EasySlots easySlots) {
        this.easySlots = easySlots;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(int geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getGeoLocationId() {
        return geoLocationId;
    }

    public void setGeoLocationId(String geoLocationId) {
        this.geoLocationId = geoLocationId;
    }

    public String getPublic() {
        return _public;
    }

    public void setPublic(String _public) {
        this._public = _public;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}