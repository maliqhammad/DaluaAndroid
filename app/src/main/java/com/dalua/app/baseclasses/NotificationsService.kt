package com.dalua.app.baseclasses

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.ProjectUtil
import io.socket.client.IO
import io.socket.client.Socket

class NotificationsService : Service() {
    var broadcaster: LocalBroadcastManager? = null
    var aquariumID: Int? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        try {
            socket = IO.socket(AppConstants.SOCKET_URL)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                if (socket != null && socket!!.connected()) {
                    socket!!.disconnect()
                }
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }
        try {
            if (socket != null) {
                socket!!.on(Socket.EVENT_CONNECT) { args: Array<Any?>? ->
                    Log.d(TAG, "call: EVENT_CONNECT | " + ProjectUtil.objectToString(args))
                }
                socket!!.on(Socket.EVENT_DISCONNECT) { args: Array<Any?>? ->
                    Log.d(TAG, "call: EVENT_DISCONNECT | " + ProjectUtil.objectToString(args))
                }
                socket!!.on(Socket.EVENT_CONNECT_ERROR) { args: Array<Any> ->
                    Log.d(TAG, "call: EVENT_CONNECT_ERROR | " + ProjectUtil.objectToString(args))
                }
                socket!!.connect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        if (socket == null) {
            return START_STICKY
        }
        if (intent != null) {
            if (intent.hasExtra("aquariumID")) {
                aquariumID = intent.getIntExtra("aquariumID", 0)
            }
            socket!!.on("AWSConnection-$aquariumID") { args: Array<Any> ->
                Log.d(TAG, "onStartCommand: Device Connection: " + args[0].toString())
                broadcaster = LocalBroadcastManager.getInstance(baseContext)
                val getChatUsersIntent = Intent("AWSConnection")
                getChatUsersIntent.putExtra("DevicesList", args[0].toString())
                broadcaster!!.sendBroadcast(getChatUsersIntent)
            }
            socket!!.on("AWSConnection-ack-$aquariumID") { args: Array<Any> ->
                Log.d(TAG, "onStartCommand: ACK: " + args[0].toString())
                broadcaster = LocalBroadcastManager.getInstance(baseContext)
                val getChatUsersIntent = Intent("ACK")
                getChatUsersIntent.putExtra("DeviceAck", args[0].toString())
                broadcaster!!.sendBroadcast(getChatUsersIntent)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        try {
            if (socket != null && socket!!.connected()) {
                Log.d(TAG, "onDestroy: socket disconnected")
                socket!!.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "NotificationService"
        var socket: Socket? = null
    }
}