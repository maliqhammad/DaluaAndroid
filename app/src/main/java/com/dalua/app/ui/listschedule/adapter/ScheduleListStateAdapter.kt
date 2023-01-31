package com.dalua.app.ui.listschedule.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dalua.app.ui.listschedule.fragments.daluaschedules.DaluaScheduleFragment
import com.dalua.app.ui.listschedule.fragments.myschedules.MyScheduleFragment
import com.dalua.app.ui.listschedule.fragments.publicschedules.PublicScheduleFragment

class ScheduleListStateAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            1 -> PublicScheduleFragment()
            0 -> DaluaScheduleFragment()
            else -> MyScheduleFragment()
        }

    }

}