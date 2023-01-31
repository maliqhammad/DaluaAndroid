package com.dalua.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SingleAquarium implements Serializable {

    @SerializedName("test_frequency")
    @Expose
    private String testFrequency;
    @SerializedName("aquarium_type")
    @Expose
    private Integer aquariumType;
    @SerializedName("last_test")
    @Expose
    private String lastTest;
    @SerializedName("clean_frequency")
    @Expose
    private String cleanFrequency;
    @SerializedName("last_clean")
    @Expose
    private String lastClean;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("temperature")
    @Expose
    private String temperature;
    @SerializedName("ph")
    @Expose
    private String ph;
    @SerializedName("salinity")
    @Expose
    private String salinity;
    @SerializedName("alkalinity")
    @Expose
    private String alkalinity;
    @SerializedName("magnesium")
    @Expose
    private String magnesium;
    @SerializedName("nitrate")
    @Expose
    private String nitrate;
    @SerializedName("phosphate")
    @Expose
    private String phosphate;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("groups_count")
    @Expose
    private Integer groupsCount;
    @SerializedName("devices_count")
    @Expose
    private Integer devicesCount;
    @SerializedName("groups")
    @Expose
    private List<AquariumGroup> groups = new ArrayList<>();
    @SerializedName("devices")
    @Expose
    private List<Device> devices = new ArrayList<>();

    public List<AquariumGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<AquariumGroup> groups) {
        this.groups = groups;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getSalinity() {
        return salinity;
    }

    public void setSalinity(String salinity) {
        this.salinity = salinity;
    }

    public String getAlkalinity() {
        return alkalinity;
    }

    public void setAlkalinity(String alkalinity) {
        this.alkalinity = alkalinity;
    }

    public String getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(String magnesium) {
        this.magnesium = magnesium;
    }

    public String getNitrate() {
        return nitrate;
    }

    public void setNitrate(String nitrate) {
        this.nitrate = nitrate;
    }

    public String getPhosphate() {
        return phosphate;
    }

    public void setPhosphate(String phosphate) {
        this.phosphate = phosphate;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupsCount() {
        return groupsCount;
    }

    public void setGroupsCount(Integer groupsCount) {
        this.groupsCount = groupsCount;
    }

    public Integer getDevicesCount() {
        return devicesCount;
    }

    public void setDevicesCount(Integer devicesCount) {
        this.devicesCount = devicesCount;
    }

    public Integer getAquariumType() {
        return aquariumType;
    }

    public void setAquariumType(Integer aquariumType) {
        this.aquariumType = aquariumType;
    }

    public String getTestFrequency() {
        return testFrequency;
    }

    public void setTestFrequency(String testFrequency) {
        this.testFrequency = testFrequency;
    }

    public String getLastTest() {
        return lastTest;
    }

    public void setLastTest(String lastTest) {
        this.lastTest = lastTest;
    }

    public String getCleanFrequency() {
        return cleanFrequency;
    }

    public void setCleanFrequency(String cleanFrequency) {
        this.cleanFrequency = cleanFrequency;
    }

    public String getLastClean() {
        return lastClean;
    }

    public void setLastClean(String lastClean) {
        this.lastClean = lastClean;
    }

}
