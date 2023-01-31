
package com.dalua.app.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ChannelC {

    @SerializedName("light")
    @Expose
    private String light;
    @SerializedName("rgba")
    @Expose
    private String rgba;
    @SerializedName("hex")
    @Expose
    private String hex;
    @SerializedName("coral_name")
    @Expose
    private String coralName;

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getRgba() {
        return rgba;
    }

    public void setRgba(String rgba) {
        this.rgba = rgba;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getCoralName() {
        return coralName;
    }

    public void setCoralName(String coralName) {
        this.coralName = coralName;
    }

}
