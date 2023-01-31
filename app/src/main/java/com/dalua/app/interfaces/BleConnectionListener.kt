package com.dalua.app.interfaces

import android.bluetooth.BluetoothGatt
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException

interface BleConnectionListener {
    fun onStartConnect()
    fun onConnectFail(bleDevice: BleDevice?, exception: BleException?)
    fun onConnectSuccess(
        bleDevice: BleDevice?,
        gatt: BluetoothGatt?,
        status: Int
    )

    fun onDisConnected(
        isActiveDisConnected: Boolean,
        device: BleDevice?,
        gatt: BluetoothGatt?,
        status: Int
    )
}