package com.dalua.app.ui.createschedule.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dalua.app.models.Configuration
import com.dalua.app.ui.createschedule.fragments.instantcontrol.InstantControlFragment
import com.dalua.app.ui.createschedule.fragments.mastercontrol.MasterControlFragment

class MasterControlStateAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val waterType: String,
    val configuration: Configuration
) :

    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            InstantControlFragment(waterType, configuration)
        else
            MasterControlFragment(waterType)

    }
}