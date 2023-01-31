
package com.dalua.app.models.GroupDetailResponse;

import java.util.List;

import com.dalua.app.models.EasySlots;
import com.dalua.app.models.schedulemodel.Location;
import com.dalua.app.models.schedulemodel.Slot;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Schedule {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("geo_location")
    @Expose
    private Integer geoLocation;
    @SerializedName("geo_location_id")
    @Expose
    private String geoLocationId;
    @SerializedName("public")
    @Expose
    private Integer _public;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("device_id")
    @Expose
    private Integer deviceId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("graph")
    @Expose
    private String graph;
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
    private Integer enabled;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("easy_slots")
    @Expose
    private EasySlots easySlots;
    @SerializedName("moonlight_enabled")
    @Expose
    private Integer moonlightEnabled;
    @SerializedName("default")
    @Expose
    private Integer _default;
    @SerializedName("requested_at")
    @Expose
    private String requestedAt;
    @SerializedName("approval")
    @Expose
    private Object approval;
    @SerializedName("schedule_type")
    @Expose
    private Object scheduleType;
    @SerializedName("sorted_slots")
    @Expose
    private List<SortedSlot> sortedSlots = null;
    @SerializedName("water_type")
    @Expose
    private String waterType;
    @SerializedName("location")
    @Expose
    private Location location;

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

    public Integer getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(Integer geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getGeoLocationId() {
        return geoLocationId;
    }

    public void setGeoLocationId(String geoLocationId) {
        this.geoLocationId = geoLocationId;
    }

    public Integer getPublic() {
        return _public;
    }

    public void setPublic(Integer _public) {
        this._public = _public;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Object getDeviceId() {
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

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
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

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public EasySlots getEasySlots() {
        return easySlots;
    }

    public void setEasySlots(EasySlots easySlots) {
        this.easySlots = easySlots;
    }

    public Integer getMoonlightEnabled() {
        return moonlightEnabled;
    }

    public void setMoonlightEnabled(Integer moonlightEnabled) {
        this.moonlightEnabled = moonlightEnabled;
    }

    public Integer getDefault() {
        return _default;
    }

    public void setDefault(Integer _default) {
        this._default = _default;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Object getApproval() {
        return approval;
    }

    public void setApproval(Object approval) {
        this.approval = approval;
    }

    public Object getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Object scheduleType) {
        this.scheduleType = scheduleType;
    }

    public List<SortedSlot> getSortedSlots() {
        return sortedSlots;
    }

    public void setSortedSlots(List<SortedSlot> sortedSlots) {
        this.sortedSlots = sortedSlots;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
