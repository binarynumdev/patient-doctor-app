package com.consulmedics.patientdata.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.consulmedics.patientdata.fragments.SubShiftListFragment
import com.consulmedics.patientdata.utils.AppConstants.PAST_TABS
import com.consulmedics.patientdata.utils.AppConstants.UPCOMING_TABS

class ShiftPageAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 2  // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubShiftListFragment(PAST_TABS)  // Replace with your first tab fragment
            1 -> SubShiftListFragment(UPCOMING_TABS)  // Replace with your second tab fragment
            else -> Fragment()
        }
    }
}