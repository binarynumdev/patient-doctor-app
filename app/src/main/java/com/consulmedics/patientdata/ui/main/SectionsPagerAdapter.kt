package com.consulmedics.patientdata.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.fragments.addeditpatient.PatientInsurranceDetailsFragment
import com.consulmedics.patientdata.fragments.addeditpatient.PatientPersonalDetailsFragment

private val TAB_TITLES = arrayOf(
    R.string.personal_details,
    R.string.insurrance_details
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(val context: Context, fm: FragmentManager, patient: Patient) :
    FragmentPagerAdapter(fm) {
    val patient: Patient = patient;
    override fun getItem(position: Int): Fragment {

        return PlaceholderFragment.newInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}