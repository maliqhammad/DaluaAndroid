package com.dalua.app.interfaces

import com.dalua.app.models.Device

interface TroubleshootCallback {
    fun showTroubleshoot(device: Device)
}