package com.consulmedics.patientdata.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.consulmedics.patientdata.fragments.SubShiftListFragment
import com.consulmedics.patientdata.utils.AppConstants.PAST_TABS
import com.consulmedics.patientdata.utils.AppConstants.UPCOMING_TABS
import com.consulmedics.patientdata.utils.AppConstants.UPLOADED_TABS

class ShiftPageAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3  // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubShiftListFragment.newInstance(PAST_TABS)  // Replace with your first tab fragment
            1 -> SubShiftListFragment.newInstance(UPCOMING_TABS)
            2 -> SubShiftListFragment.newInstance(UPLOADED_TABS)
            else -> Fragment()
        }
    }
}