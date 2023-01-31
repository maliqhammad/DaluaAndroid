
package com.dalua.app.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class EasySlots {

    @SerializedName("sunrise")
    @Expose
    private String sunrise;
    @SerializedName("sunset")
    @Expose
    private String sunset;
    @SerializedName("value_a")
    @Expose
    private String valueA;
    @SerializedName("value_b")
    @Expose
    private String valueB;
    @SerializedName("value_c")
    @Expose
    private String valueC;
    @SerializedName("ramp_time")
    @Expose
    private String rampTime;

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
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

    public String getRampTime() {
        return rampTime;
    }

    public void setRampTime(String rampTime) {
        this.rampTime = rampTime;
    }

}
