package com.dalua.app.models;

import com.clj.fastble.data.BleDevice;

public class MacBle {
    BleDevice bleDevice;
    String macAddress;

    public MacBle(BleDevice bleDevice, String macAddress, Boolean alreadyAdded) {
        this.bleDevice = bleDevice;
        this.macAddress = macAddress;
        this.alreadyAdded = alreadyAdded;
    }

    Boolean alreadyAdded;

    public Boolean getAlreadyAdded() {
        return alreadyAdded;
    }

    public void setAlreadyAdded(Boolean alreadyAdded) {
        this.alreadyAdded = alreadyAdded;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
