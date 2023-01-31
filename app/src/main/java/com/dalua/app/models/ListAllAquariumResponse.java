package com.dalua.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListAllAquariumResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        @SerializedName("aquariums")
        @Expose
        private List<SharedAquariums> aquariums = null;
        @SerializedName("shared_aquariums")
        @Expose
        private List<SharedAquariums> sharedAquariums = null;
        @SerializedName("shared_aquariums_user")
        @Expose
        private List<SharedAquariums> sharedAquariumsUser = null;

        public List<SharedAquariums> getAquariums() {
            return aquariums;
        }

        public void setAquariums(List<SharedAquariums> aquariums) {
            this.aquariums = aquariums;
        }

        public List<SharedAquariums> getSharedAquariums() {
            return sharedAquariums;
        }

        public void setSharedAquariums(List<SharedAquariums> sharedAquariums) {
            this.sharedAquariums = sharedAquariums;
        }

        public List<SharedAquariums> getSharedAquariumsUser() {
            return sharedAquariumsUser;
        }

        public void setSharedAquariumsUser(List<SharedAquariums> sharedAquariumsUser) {
            this.sharedAquariumsUser = sharedAquariumsUser;
        }


    }

    public static class SharedAquariums {

        @SerializedName("aquariumType")
        @Expose
        private Integer aquariumType;

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
        private Integer testFrequency;
        @SerializedName("last_test")
        @Expose
        private String lastTest;
        @SerializedName("clean_frequency")
        @Expose
        private Integer cleanFrequency;
        @SerializedName("last_clean")
        @Expose
        private String lastClean;
        @SerializedName("groups_count")
        @Expose
        private Integer groupsCount;
        @SerializedName("devices_count")
        @Expose
        private Integer devicesCount;
        @SerializedName("users")
        @Expose
        private List<User> users;
        @SerializedName("user")
        @Expose
        private User user;

        public Integer getAquariumType() {
            return aquariumType;
        }

        public void setAquariumType(Integer aquariumType) {
            this.aquariumType = aquariumType;
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

        public Integer getTestFrequency() {
            return testFrequency;
        }

        public void setTestFrequency(Integer testFrequency) {
            this.testFrequency = testFrequency;
        }

        public String getLastTest() {
            return lastTest;
        }

        public void setLastTest(String lastTest) {
            this.lastTest = lastTest;
        }

        public Integer getCleanFrequency() {
            return cleanFrequency;
        }

        public void setCleanFrequency(Integer cleanFrequency) {
            this.cleanFrequency = cleanFrequency;
        }

        public String getLastClean() {
            return lastClean;
        }

        public void setLastClean(String lastClean) {
            this.lastClean = lastClean;
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

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public static class User {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("middle_name")
        @Expose
        private String middleName;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("phone_no")
        @Expose
        private String phoneNo;
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("unique_id")
        @Expose
        private String uniqueId;
        @SerializedName("tank_size")
        @Expose
        private String tankSize;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("country_code")
        @Expose
        private String countryCode;
        @SerializedName("pivot")
        @Expose
        private Pivot pivot;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
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

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public String getTankSize() {
            return tankSize;
        }

        public void setTankSize(String tankSize) {
            this.tankSize = tankSize;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public Pivot getPivot() {
            return pivot;
        }

        public void setPivot(Pivot pivot) {
            this.pivot = pivot;
        }

    }

    public static class Pivot {

        @SerializedName("aquarium_id")
        @Expose
        private Integer aquariumId;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("status")
        @Expose
        private String status;

        public Integer getAquariumId() {
            return aquariumId;
        }

        public void setAquariumId(Integer aquariumId) {
            this.aquariumId = aquariumId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }


}

