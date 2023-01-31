package com.dalua.app.models;

public class ConnectedDevices {
    String device_id, mac_address;

    public ConnectedDevices(String device_id, String mac_address) {
        this.device_id = device_id;
        this.mac_address = mac_address;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }
}
