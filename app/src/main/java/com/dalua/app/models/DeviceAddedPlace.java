package com.dalua.app.models;

import android.widget.ImageView;
import android.widget.LinearLayout;

import com.clj.fastble.data.BleDevice;

public class DeviceAddedPlace {

    public DeviceAddedPlace(Integer position, BleDevice bleDevice, ImageView imageView) {
        this.position = position;
        this.bleDevice = bleDevice;
        this.imageView = imageView;
    }
    public DeviceAddedPlace(Integer position, BleDevice bleDevice, LinearLayout linearLayout) {
        this.position = position;
        this.bleDevice = bleDevice;
        this.imageView = imageView;
    }

    public DeviceAddedPlace(Integer position, BleDevice bleDevice) {
        this.position = position;
        this.bleDevice = bleDevice;
    }

    public DeviceAddedPlace( BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    Integer position;
    BleDevice bleDevice;
    ImageView imageView;

}
