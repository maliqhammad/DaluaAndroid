package com.dalua.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AquariumNewModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("test_frequency")
    @Expose
    private String testFrequency;
    @SerializedName("last_test")
    @Expose
    private String lastTest;
    @SerializedName("clean_frequency")
    @Expose
    private String cleanFrequency;
    @SerializedName("last_clean")
    @Expose
    private String lastClean;
    @SerializedName("groups_count")
    @Expose
    private String groupsCount;
    @SerializedName("devices_count")
    @Expose
    private String devicesCount;
    @SerializedName("users")
    @Expose
    private List<User> users = null;

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

    public String getGroupsCount() {
        return groupsCount;
    }

    public void setGroupsCount(String groupsCount) {
        this.groupsCount = groupsCount;
    }

    public String getDevicesCount() {
        return devicesCount;
    }

    public void setDevicesCount(String devicesCount) {
        this.devicesCount = devicesCount;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
