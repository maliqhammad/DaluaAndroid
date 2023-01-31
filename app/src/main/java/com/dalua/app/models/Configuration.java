
package com.dalua.app.models;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Configuration {

    @SerializedName("channel_c")
    @Expose
    private List<ChannelC> channelC = null;
    @SerializedName("channel_b")
    @Expose
    private List<ChannelB> channelB = null;
    @SerializedName("channel_a")
    @Expose
    private List<ChannelA> channelA = null;

    public List<ChannelC> getChannelC() {
        return channelC;
    }

    public void setChannelC(List<ChannelC> channelC) {
        this.channelC = channelC;
    }

    public List<ChannelB> getChannelB() {
        return channelB;
    }

    public void setChannelB(List<ChannelB> channelB) {
        this.channelB = channelB;
    }

    public List<ChannelA> getChannelA() {
        return channelA;
    }

    public void setChannelA(List<ChannelA> channelA) {
        this.channelA = channelA;
    }

}
