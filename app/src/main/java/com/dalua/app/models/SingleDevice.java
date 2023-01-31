package com.dalua.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalua.app.models.schedulemodel.SingleSchedule;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleDevice implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("group")
    @Expose
    private Object group;

    @SerializedName("mac_address")
    @Expose
    private String macAddress;

    @SerializedName("ip_address")
    @Expose
    private String ipAddress = null;

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("schedule")
    @Expose
    private SingleSchedule schedule;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getGroup() {
        return group;
    }

    public void setGroup(Object group) {
        this.group = group;
    }

    protected SingleDevice(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        uid = in.readString();
        topic = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        macAddress = in.readString();
        ipAddress = in.readString();
    }

    public static final Creator<SingleDevice> CREATOR = new Creator<SingleDevice>() {
        @Override
        public SingleDevice createFromParcel(Parcel in) {
            return new SingleDevice(in);
        }

        @Override
        public SingleDevice[] newArray(int size) {
            return new SingleDevice[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.topic);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
        dest.writeString(this.macAddress);
        dest.writeString(this.ipAddress);

    }

    @Override
    public String toString() {
        return "SingleDevice{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", topic='" + topic + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", name='" + name + '\'' +
                ", group=" + group +
                ", macAddress='" + macAddress + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
