
package com.dalua.app.models.GroupDetailResponse;

import java.util.List;

import com.dalua.app.models.Configuration;
import com.dalua.app.models.Device;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("aquarium_id")
    @Expose
    private Integer aquariumId;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("configuration")
    @Expose
    private Configuration configuration;
    @SerializedName("water_type")
    @Expose
    private String waterType;
    @SerializedName("devices")
    @Expose
    private List<Device> devices = null;
    @SerializedName("schedule")
    @Expose
    private Schedule schedule;

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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
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

    public Integer getAquariumId() {
        return aquariumId;
    }

    public void setAquariumId(Integer aquariumId) {
        this.aquariumId = aquariumId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

}
