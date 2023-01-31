package com.dalua.app.models.schedulemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slot {

    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("value_a")
    @Expose
    private String valueA;
    @SerializedName("value_b")
    @Expose
    private String valueB;
    @SerializedName("value_c")
    @Expose
    private String valueC;
    @SerializedName("type")
    @Expose
    private String type;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getValueA() {
        return valueA;
    }

    public void setValueA(String valueA) {
        this.valueA = valueA;
    }

    public String getValueB() {
        return valueB;
    }

    public void setValueB(String valueB) {
        this.valueB = valueB;
    }

    public String getValueC() {
        return valueC;
    }

    public void setValueC(String valueC) {
        this.valueC = valueC;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}