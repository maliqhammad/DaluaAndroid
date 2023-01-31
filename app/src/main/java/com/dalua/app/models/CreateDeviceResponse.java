package com.dalua.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateDeviceResponse {

    @SerializedName("success")
    @Expose
    private Boolean success = false;
    @SerializedName("message")
    @Expose
    private String message = "";
    @SerializedName("data")
    @Expose
    private Device device = new Device();

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

    public Device getData() {
        return device;
    }

    public void setData(Device data) {
        this.device = data;
    }

}
