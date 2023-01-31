
package com.dalua.app.models;

import com.dalua.app.models.schedulemodel.SingleSchedule;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateScheduleResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private SingleSchedule data;

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

    public SingleSchedule getData() {
        return data;
    }

    public void setData(SingleSchedule data) {
        this.data = data;
    }

}
