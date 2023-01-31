package com.dalua.app.models;

import com.dalua.app.models.schedulemodel.SingleSchedule;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PublicScheduleResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<SingleSchedule> data = null;

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

    public List<SingleSchedule> getData() {
        return data;
    }

    public void setData(List<SingleSchedule> data) {
        this.data = data;
    }

}
