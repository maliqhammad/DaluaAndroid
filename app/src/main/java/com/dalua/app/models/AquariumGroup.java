package com.dalua.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalua.app.models.schedulemodel.SingleSchedule;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AquariumGroup implements Parcelable {

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
    private SingleSchedule schedule;

    @SerializedName("user")
    @Expose
    private User user;

    public Integer getAquariumId() {
        return aquariumId;
    }

    public void setAquariumId(Integer aquariumId) {
        this.aquariumId = aquariumId;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public AquariumGroup createFromParcel(Parcel in) {
            return new AquariumGroup(in);
        }

        @Override
        public AquariumGroup[] newArray(int size) {
            return new AquariumGroup[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    public AquariumGroup() {
    }

    public AquariumGroup(Parcel in) {
        this.id = in.readInt();
        this.aquariumId = in.readInt();
        this.uid = in.readString();
        this.name = in.readString();
        this.topic = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.id);
        dest.writeInt(this.aquariumId);
        dest.writeString(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.topic);
        dest.writeString(this.updatedAt);
        dest.writeString(this.createdAt);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SingleSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(SingleSchedule schedule) {
        this.schedule = schedule;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
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
}
