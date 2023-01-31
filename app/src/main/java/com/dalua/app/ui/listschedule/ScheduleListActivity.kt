package com.dalua.app.ui.listschedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dalua.app.R
import com.dalua.app.api.Resource
import com.dalua.app.baseclasses.BaseActivity
import com.dalua.app.databinding.ActivityScheduleListBinding
import com.dalua.app.models.*
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.ui.createschedule.CreateScheduleActivity
import com.dalua.app.ui.listschedule.fragments.daluaschedules.DaluaScheduleFragment
import com.dalua.app.ui.listschedule.fragments.myschedules.MyScheduleFragment
import com.dalua.app.ui.listschedule.fragments.publicschedules.PublicScheduleFragment
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.AppConstants.Companion.IsEditOrPreviewOrCreate
import com.dalua.app.utils.ProjectUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleListActivity : BaseActivity() {

    lateinit var binding: ActivityScheduleListBinding
    val viewModel: ScheduleListActivityVM by viewModels()

    private val deviceBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("DevicesList")) {
                val socketDevice = ProjectUtil.stringToObject(
                    intent.getStringExtra("DevicesList"),
                    SocketResponseModel::class.java
                )
                if (viewModel.launchFrom.value == "group") {
                    Log.d(TAG, "onReceive: group mac: " + socketDevice.macAddress)
                    viewModel.group.value!!.devices.filter { it.macAddress == socketDevice.macAddress }
                        .forEach { it.status = socketDevice.status }
                } else if (viewModel.launchFrom.value == "device") {
                    Log.d(TAG, "onReceive: device mac: " + socketDevice.macAddress)
                    if (socketDevice.macAddress == viewModel.device.value!!.macAddress)
                        viewModel.device.value =
                            viewModel.device.value!!.apply { status = socketDevice.status }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObjects()
        observer()
        apiResponses()
    }

    private fun initObjects() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_list)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        getIntentData()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceBroadcastReceiver, IntentFilter("AWSConnection"))
    }

    private fun observer() {
        viewModel.selectedTab.observe(this) {
            Log.d(TAG, "observer: $it")
            when (it) {
                0 -> {
                    addFragmentToActivity(DaluaScheduleFragment())
                }
                1 -> {
                    addFragmentToActivity(PublicScheduleFragment())
                }
                2 -> {
                    addFragmentToActivity(MyScheduleFragment())
                }
            }
        }
    }

    private fun addFragmentToActivity(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val topFragment = fragmentManager.findFragmentById(R.id.container)
        if (topFragment != null) {
            transaction.remove(topFragment)
            transaction.add(R.id.container, fragment, "FA")
            transaction.commit()
        } else {
            transaction.add(R.id.container, fragment, "FA")
            transaction.commit()
        }
    }

    private fun apiResponses() {

        viewModel.apiResponse.observe(this) {
            when (it) {
                is Resource.Error -> {

                    when (it.api_Type) {
                        AppConstants.ApiTypes.GetLocationApi.name -> {

                        }
                    }

                }
                is Resource.Loading -> {
                }
                is Resource.Success -> {

                    when (it.api_Type) {
                        AppConstants.ApiTypes.GetLocationApi.name -> {

                            it.data?.let { response ->

                                val geolocation = ProjectUtil.stringToObject(
                                    response.string(),
                                    GeoLocationResponse::class.java
                                )
                                viewModel.geoLocation.value = geolocation
                                ProjectUtil.putLocationObjects(this, geolocation)

                            }

                        }
                    }

                }
            }
        }

    }

    private fun getIntentData() {
        viewModel.schedule.value =
            ProjectUtil.stringToObject(intent.getStringExtra("sc"), SingleSchedule::class.java)
        viewModel.waterType.value = intent.getStringExtra("waterType")
        viewModel.configuration.value = ProjectUtil.stringToObject(
            intent.getStringExtra("configuration"),
            Configuration::class.java
        )
        viewModel.launchFrom.value = intent.getStringExtra("launchFrom")
        if (viewModel.launchFrom.value == "device") {
            viewModel.device.value =
                ProjectUtil.stringToObject(intent.getStringExtra("device"), Device::class.java)
        } else if (viewModel.launchFrom.value == "group") {
            viewModel.group.value = ProjectUtil.stringToObject(
                intent.getStringExtra("group"),
                AquariumGroup::class.java
            )
        }

        if (intent.hasExtra("aquarium")) {
            viewModel.aquarium.value = ProjectUtil.stringToObject(
                intent.getStringExtra("aquarium"),
                SingleAquarium::class.java
            )
        }
    }

    fun goToCreateScheduleActivity(view: View) {

        // here we have to check for the values...
        IsEditOrPreviewOrCreate = "create"
        if (viewModel.launchFrom.value == "device") {
            CreateScheduleActivity.launchEasyFromDevice(
                this,
                viewModel.device.value!!,
                viewModel.waterType.value!!,
                viewModel.configuration.value!!,
                0,
                viewModel.aquarium.value!!
            )
        } else if (viewModel.launchFrom.value == "group") {
            CreateScheduleActivity.launchEasyFromGroup(
                this,
                viewModel.group.value!!,
                viewModel.waterType.value!!,
                viewModel.configuration.value!!,
                0,
                viewModel.aquarium.value!!
            )
        }
    }

    fun goBack(view: View) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceBroadcastReceiver)
    }

    companion object {
        const val TAG = "ScheduleListActivity"
        fun launchedByGroupDetail(
            context: Context,
            group: AquariumGroup,
            schedule: SingleSchedule,
            waterType: String,
            configuration: Configuration,
            singleAquarium: SingleAquarium
        ) {
            context.startActivity(Intent(context, ScheduleListActivity::class.java).apply {
                putExtra("group", ProjectUtil.objectToString(group))
                putExtra("sc", ProjectUtil.objectToString(schedule))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
                putExtra("launchFrom", "group")
            })
        }

        fun launchedByDeviceDetail(
            context: Context,
            device: Device,
            schedule: SingleSchedule,
            waterType: String,
            configuration: Configuration,
            singleAquarium: SingleAquarium
        ) {
            context.startActivity(Intent(context, ScheduleListActivity::class.java).apply {
                putExtra("device", ProjectUtil.objectToString(device))
                putExtra("sc", ProjectUtil.objectToString(schedule))
                putExtra("waterType", waterType)
                putExtra("configuration", ProjectUtil.objectToString(configuration))
                putExtra("aquarium", ProjectUtil.objectToString(singleAquarium))
                putExtra("launchFrom", "device")
            })
        }

    }

}