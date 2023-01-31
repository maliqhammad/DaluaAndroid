
package com.dalua.app.models.GroupDetailResponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SortedSlot {

    @SerializedName("value_a")
    @Expose
    private String valueA;
    @SerializedName("value_b")
    @Expose
    private String valueB;
    @SerializedName("value_c")
    @Expose
    private String valueC;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("type")
    @Expose
    private String type;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
