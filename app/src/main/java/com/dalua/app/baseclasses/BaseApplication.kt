package com.dalua.app.baseclasses

import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.iot.AWSIotClient
import com.dalua.app.utils.AppConstants
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {

    var mqttManager: AWSIotMqttManager? = null
    private lateinit var client: AWSIotClient
    var credentials = MutableLiveData<CognitoCachingCredentialsProvider>()
    var connectionStatus: MutableLiveData<Int> = MutableLiveData()
    private var uniqueUserIdForThisPhone: String? = null
    override fun onCreate() {
        super.onCreate()
        PRDownloader.initialize(applicationContext,PRDownloaderConfig.newBuilder().build().apply {
            isDatabaseEnabled = true
            readTimeout = 30000
            connectTimeout = 30000
        })
//        connectToAws()
    }

    //aws Connection
    fun connectToAws() {
        Log.d(TAG, "connectToAws: ")
        uniqueUserIdForThisPhone = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext,  /* get the context for the application */
            AppConstants.identityPoolID,  /* Identity Pool ID */
            Regions.US_EAST_2 /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )

        mqttManager =
            AWSIotMqttManager(
                uniqueUserIdForThisPhone,
                AppConstants.CUSTOMER_SPECIFIC_ENDPOINT
            )
        Thread {
            client = AWSIotClient(credentials.value?.credentials)
            client.setRegion(Region.getRegion(Regions.US_EAST_2))
        }.start()

        callServiceThroughCredentials(credentialsProvider)
    }

    private fun callServiceThroughCredentials(credentialsProvider: CognitoCachingCredentialsProvider) {
        mqttManager!!.setReconnectRetryLimits(5000, 10000)
        mqttManager!!.maxAutoReconnectAttempts = 1
        mqttManager?.connect(
            credentialsProvider
        ) { status: AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus, throwable: Throwable? ->
            when (status) {
                AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting -> {
                    Log.d(TAG, "Connecting...")
                    connectionStatus.postValue(1)

                }
                AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected -> {
                    connectionStatus.postValue(2)
                    Log.d(TAG, "Connection Successful.")

                }

                AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting -> {
                    connectionStatus.postValue(3)
                    if (throwable != null) {
                        Log.d(TAG, "Reconnecting...", throwable)
                    }

                }

                AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost -> {
                    Log.d(TAG, "Disconnected: ")
                    connectionStatus.postValue(4)
                    if (throwable != null) {
                        Log.d(TAG, "Aws Service Disconnected...", throwable)
                    }

                }
                else -> {
                    connectionStatus.postValue(0)
                    Log.d(TAG, "Disconnected for no reason bro..")
                }
            }
        }
    }

    companion object {
        const val TAG = "BaseApplication"
    }

}