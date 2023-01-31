package com.dalua.app.ui.wifiactivity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.dalua.app.R
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityShowWifiConnectionBinding
import com.dalua.app.utils.RevealAnimation
import com.thanosfisherman.wifiutils.WifiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowWifiConnectionActivity : BaseActivity() {

    lateinit var uniqueID: String
    private val broadCastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {


            val wifiStateExtra = intent!!.getIntExtra(
                WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN
            )

            when (wifiStateExtra) {

                WifiManager.WIFI_STATE_ENABLED -> {
                    wifiConnection.value = true
                }
                WifiManager.WIFI_STATE_DISABLED -> {
                    wifiConnection.value = false
                }
                WifiManager.WIFI_STATE_ENABLING -> {

                }
                WifiManager.WIFI_STATE_DISABLING -> {

                }
                WifiManager.WIFI_STATE_UNKNOWN -> {

                }
            }

        }
    }
    private lateinit var turnOnWifiDialog: Dialog
    lateinit var binding: ActivityShowWifiConnectionBinding
    private lateinit var mRevealAnimation: RevealAnimation
    val viewModel: ShowWifiConnectionVM by viewModels()
    private var wifiConnection: MutableLiveData<Boolean> = MutableLiveData()
    private var countDownTimer: CountDownTimer? = null
    var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        initObjects()
        observers()
        listeners()

    }

    private fun listeners() {

        binding.refresh.setOnClickListener {
            WifiUtils.withContext(this).enableWifi(this::checkResult)
        }

    }

    private fun observers() {

        wifiConnection.observe(this) {
            if (!it) {
                checkResult(false)
            } else {
                checkResult(true)
            }
        }

        viewModel.backPress.observe(this) {
            if (it) {
                onBackPressed()
            }
        }

    }

    @SuppressLint("HardwareIds")
    private fun initObjects() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_wifi_connection)
        mRevealAnimation = RevealAnimation(binding.rootWifi, intent, this)
        uniqueID = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initDialog()
    }

    private fun checkResult(isSuccess: Boolean) {

        if (isSuccess) {
            Log.d(TAG, "checkResult: calling wifi")
            showWorking()
            if (!isTimerRunning)
                countDownTimer()
            WifiUtils.withContext(this).scanWifi(this::getScanResults).start()
        } else {
            turnOnWifiDialog.show()
        }

    }

    private fun initDialog() {
        turnOnWifiDialog = Dialog(this)
        if (turnOnWifiDialog.isShowing) turnOnWifiDialog.cancel()
        turnOnWifiDialog.setContentView(R.layout.item_turn_wifi_on)
        turnOnWifiDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        turnOnWifiDialog.setCancelable(true)
//        turnOnWifiDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        turnOnWifiDialog.findViewById<Button>(R.id.button1).setOnClickListener {
            turnOnWifiDialog.dismiss()
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(broadCastReceiver, intentFilter)

        turnOnWifiDialog.findViewById<Button>(R.id.button).setOnClickListener {
            turnOnWifiDialog.dismiss()
            finish()
        }


    }

    private fun getScanResults(results: List<ScanResult>) {

        if (results.isEmpty()) {
            Log.i(TAG, "SCAN RESULTS IT'S EMPTY")
            binding.refresh.visibility = View.INVISIBLE
            binding.textViewss7.visibility = View.VISIBLE
            binding.textViessw7.visibility = View.VISIBLE
            hideWorking()
            if (!isTimerRunning) {
                countDownTimer()
            }

            return
        }

        hideWorking()

        val list: MutableList<ScanResult> = mutableListOf()
        for (result in results) {
            if (result.frequency < 2500)
                list.add(result)
        }

        binding.recyclerView.adapter = WifiAdapterRV(list, this)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastReceiver)
        if (isTimerRunning)
            countDownTimer?.cancel()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
    }

    private fun countDownTimer() {

        countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                isTimerRunning = true


                var seconds = (millisUntilFinished / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60
                viewModel.timerTime.value =
                    String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
                viewModel.enableNow.value = false

            }

            override fun onFinish() {
                isTimerRunning = false
                viewModel.enableNow.value = true
                viewModel.timerTime.value = "00:00"
                binding.textViewss7.visibility = View.GONE
                binding.textViessw7.visibility = View.GONE
                binding.refresh.visibility = View.VISIBLE
            }
        }.start()

    }

    companion object {
        const val TAG = "ShowWifiConnectionActivtiy"
    }

}