package com.consulmedics.patientdata.fragments.addeditpatient

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.Converters
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.activities.MapsActivity
import com.consulmedics.patientdata.adapters.AddressDialogAdapter
import com.consulmedics.patientdata.data.model.Address
import com.consulmedics.patientdata.databinding.FragmentPatientLogisticsDetailsBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppConstants.HOTEL_TEXT
import com.consulmedics.patientdata.utils.AppConstants.NO_TEXT
import com.consulmedics.patientdata.utils.AppConstants.PREV_PATIENT_TEXT
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppConstants.YES_TEXT
import com.consulmedics.patientdata.utils.AppUtils.Companion.isOnline
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PatientLogisticsDetailsFragment : Fragment() {
    private var _binding: FragmentPatientLogisticsDetailsBinding? = null
    val binding get() = _binding!!
    var hotelList = emptyList<Address>()
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels(){
        AddEditPatientViewModelFactory(MyApplication.patientRepository!!, MyApplication.hotelRepository!!, MyApplication.addressRepository!!)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientLogisticsDetailsBinding.inflate(inflater, container, false)
        binding.apply {
            topBar.buttonRight1.visibility = GONE
            editDateOfVisit.setOnClickListener {
                var c = Calendar.getInstance()
                val converter: Converters = Converters()
                if(!sharedViewModel.patientData.value?.startVisitDate.isNullOrEmpty()){
                    c.time = converter.stringToDate(sharedViewModel.patientData.value?.startVisitDate!!)
                }
                var year = c.get(Calendar.YEAR)
                var month = c.get(Calendar.MONTH)
                var day = c.get(Calendar.DAY_OF_MONTH)


//                var datePicker = DatePickerDialog(requireActivity(),{ view, year, monthOfYear, dayOfMonth ->
//
//                    Log.e(AppConstants.TAG_NAME, "$year $monthOfYear $dayOfMonth")
//                    c.set(Calendar.YEAR, year)
//                    c.set(Calendar.MONTH, monthOfYear)
//                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                    sharedViewModel.setStartVisitDate(converter.dateToString(c.time)!!)
//                    val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
//                    binding.editDateOfVisit.setText(birthDateFormat.format(c.time))
//                },year , month, day)
//                var layoutParams = datePicker.window?.attributes
//                if (layoutParams != null) {
//                    Log.e(TAG_NAME, "Apply new size to datepicker")
//                    layoutParams.width = layoutParams.width * 2
//                    layoutParams.height =layoutParams.height * 2
//                    datePicker.window?.setLayout(layoutParams.width, layoutParams.height)
////                    datePicker.window?.attributes = layoutParams
//                }

//                datePicker.show()
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.custom_date_picker)
                val btnTimeOk = dialog.findViewById<Button>(R.id.btnOk)
                val btnTimeCancel = dialog.findViewById<Button>(R.id.btnCancel)
                var datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)
                datePicker.init(year, month, day, DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->  })
                btnTimeCancel.setOnClickListener {
                    dialog.dismiss()
                }
                btnTimeOk.setOnClickListener {
                    c.set(Calendar.YEAR, datePicker.year)
                    c.set(Calendar.MONTH, datePicker.month)
                    c.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
                    sharedViewModel.setStartVisitDate(converter.dateToString(c.time)!!)
                    val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                    binding.editDateOfVisit.setText(birthDateFormat.format(c.time))
                    dialog.dismiss()
                }
                dialog.show()
            }
            editTimeOfVisit.setOnClickListener {
                var c = Calendar.getInstance()
                val converter: Converters = Converters()
                if(!sharedViewModel.patientData.value?.startVisitTime.isNullOrEmpty()){
                    c.time = converter.stringToTime(sharedViewModel.patientData.value?.startVisitTime!!)
                }
                val hourOfDay: Int = c.get(Calendar.HOUR_OF_DAY)
                val minute: Int = c.get(Calendar.MINUTE)
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.custom_time_picker)
                val timePicker = dialog.findViewById<TimePicker>(R.id.time_picker)
                val btnTimeOk = dialog.findViewById<Button>(R.id.btnOk)
                val btnTimeCancel = dialog.findViewById<Button>(R.id.btnCancel)
                timePicker.setIs24HourView(true)
                timePicker.hour = hourOfDay
                timePicker.minute = minute
                btnTimeCancel.setOnClickListener {
                    dialog.dismiss()
                }
                btnTimeOk.setOnClickListener {
                    c.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    c.set(Calendar.MINUTE, timePicker.minute)
                    editTimeOfVisit.setText(converter.timeToString(c.time))
                    sharedViewModel.setStartVisitTime(converter.timeToString(c.time)!!)
                    dialog.dismiss()
                }
                dialog.show()
            }

            radioStartPointIsPrevPatient.setOnClickListener {
                sharedViewModel.setStartPoint(PREV_PATIENT_TEXT);
                binding.startAddressForm.formRoot.visibility = GONE
            }
            radioStartPointIsHotel.setOnClickListener {
                sharedViewModel.setStartPoint(HOTEL_TEXT)

                    if(hotelList.count() == 0){
                        if(isOnline(requireContext())) {
                            showCreateHotelModal()
                        }
                        else{
                            showStartPointAddressForm(Address())
                        }
                    }
                    else{
                        showChooseHotelModal()
                    }


            }
            radioCurrentAddressSameNo.setOnClickListener {
                sharedViewModel.setCurrentAddressSame(NO_TEXT)
                addressFormLayout.visibility = VISIBLE
                if(sharedViewModel.visitAddress.value?.uid == null){
                    if(isOnline(requireContext())){
                        showNewAddressMapScreen()
                    }
                    else{
                        showCurrentAddressForm(Address())
                    }

                }
                else{
                    if(isOnline(requireContext())){
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Choose New Address?")
                        builder.setMessage("Do you want to choose new address from Google Map?")
                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            showNewAddressMapScreen()
                        }

                        builder.setNegativeButton(android.R.string.no) { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                    else{
                        Toast.makeText(context, "Please try again when you have internet connection", Toast.LENGTH_LONG).show()
                    }

                }

            }
            radioCurrentAddressSameYes.setOnClickListener {
                sharedViewModel.setCurrentAddressSame(YES_TEXT)
                addressFormLayout.visibility = GONE
            }
            radioCurrentPatientVisitThisShiftYes.setOnClickListener{
                sharedViewModel.setCurrentPatientAlreadyVisited(YES_TEXT)
            }
            radioCurrentPatientVisitThisShiftNo.setOnClickListener {
                sharedViewModel.setCurrentPatientAlreadyVisited(NO_TEXT)
            }
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_patientLogisticsDetailsFragment_to_patientDoctorDocumentFragment)
            }
            btnPrev.setOnClickListener {
                activity?.onBackPressed()
            }
            btnSave.setOnClickListener {
                sharedViewModel.patientData.value?.let { it1 ->
                    it.isEnabled = false

                    sharedViewModel.viewModelScope.launch {
                        sharedViewModel.savePatient(it1)
                    }
                    activity?.finish()
                }
            }
            startAddressForm.apply {

            }
            currentAddressForm.apply {
                editCity.doAfterTextChanged {
                    sharedViewModel.visitAddress.value?.city = it.toString()
                }
                editStreet.doAfterTextChanged {
                    sharedViewModel.visitAddress.value?.streetName = it.toString()
                }
                editHouseNumber.doAfterTextChanged {
                    sharedViewModel.visitAddress.value?.streetNumber = it.toString()
                }
                editPostalCode.doAfterTextChanged {
                    sharedViewModel.visitAddress.value?.postCode = it.toString()
                }
            }
            startAddressForm.apply {
                editCity.doAfterTextChanged {
                    sharedViewModel.startAddress.value?.city = it.toString()
                }
                editStreet.doAfterTextChanged {
                    sharedViewModel.startAddress.value?.streetName = it.toString()
                }
                editHouseNumber.doAfterTextChanged {
                    sharedViewModel.startAddress.value?.streetNumber = it.toString()
                }
                editPostalCode.doAfterTextChanged {
                    sharedViewModel.startAddress.value?.postCode = it.toString()
                }
            }
        }
        return binding.root
    }

    fun showChooseHotelModal(){
        Log.e(TAG_NAME, "SHOW CHOOSE HOTEL MODAL")
        val builder = AlertDialog.Builder(requireContext())

        val dialogAddressList = ArrayList(hotelList)
        if(isOnline(requireContext())){
            dialogAddressList.add(Address())
        }

        dialogAddressList.add(Address(-99))
        val adapter = AddressDialogAdapter(requireContext(), dialogAddressList)

        builder.setTitle("Select an item")
        builder.setAdapter(adapter) { dialog, which ->
            // Handle item selection
            val address = dialogAddressList.get(which)
            if(address.uid == null){
                showCreateHotelModal()
            }
            else if (address.uid == -99){
                setStartPointAddress(Address())
            }
            else{
                setStartPointAddress(address)
            }
        }
        builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i ->
            binding.startAddressForm.formRoot.visibility = VISIBLE
        })
        val dialog = builder.create()
        dialog.show()
    }
    fun showNewAddressMapScreen(){
        Log.e(TAG_NAME, "SHOW CREATE HOTEL MODAL")
        val intent = Intent(requireActivity(), MapsActivity::class.java)
        intent.putExtra("isHotel", false)

//        startActivityForResult(intent, 1100)
        resultLauncher.launch(intent)
    }
    fun showCreateHotelModal(){
        Log.e(TAG_NAME, "SHOW CREATE HOTEL MODAL")
        val intent = Intent(requireActivity(), MapsActivity::class.java)
        intent.putExtra("isHotel", true)

//        startActivityForResult(intent, 1100)
        resultLauncher.launch(intent)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
        val converters: Converters = Converters()
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
            Log.e(AppConstants.TAG_NAME, "Shared Vide Model Data Changed in Insurance fragment")
            if(sharedViewModel.patientData.value?.birthDate != null){
                val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                val cal = Calendar.getInstance()
                cal.time = sharedViewModel.patientData.value?.birthDate
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                binding.topBar.textViewLeft.setText("${it.lastName},${it.firstName}($day.${month + 1}.$year)")
            }
            else{
                binding.topBar.textViewLeft.setText("${it.lastName},${it.firstName} ")
            }

            binding.editTimeOfVisit.setText(sharedViewModel.patientData.value?.startVisitTime)
            if(!sharedViewModel.patientData.value?.startVisitDate.isNullOrEmpty()) {
                binding.editDateOfVisit.setText(
                    birthDateFormat.format(
                        converters.stringToDate(
                            sharedViewModel.patientData.value?.startVisitDate
                        )
                    )
                )
            }
            if(sharedViewModel.patientData.value?.startPoint == HOTEL_TEXT){
                binding.radioStartPointIsHotel.isChecked = true
            }
            else if(sharedViewModel.patientData.value?.startPoint == PREV_PATIENT_TEXT){
                binding.radioStartPointIsPrevPatient.isChecked = true
            }

            if(sharedViewModel.patientData.value?.sameAddAsPrev == YES_TEXT){
                binding.radioCurrentAddressSameYes.isChecked = true
                binding.addressFormLayout.visibility = GONE
            }
            else{
                binding.radioCurrentAddressSameNo.isChecked = true
                binding.addressFormLayout.visibility = VISIBLE
            }

            if(sharedViewModel.patientData.value?.alreadyVisitedDuringThisShift == YES_TEXT){
                binding.radioCurrentPatientVisitThisShiftYes.isChecked = true
            }
            else{
                binding.radioCurrentPatientVisitThisShiftNo.isChecked = true
            }
        })
        sharedViewModel.hotelList.observe(viewLifecycleOwner, Observer {
            hotelList = it
        })

        sharedViewModel.startAddress.observe(viewLifecycleOwner, Observer {
            if(it != null){
                Log.e(TAG_NAME, "Collect start address: ${it.uid}")
                if(it.uid != null){
                    showStartPointAddressForm(it)
                }
            }

        })
        sharedViewModel.visitAddress.observe(viewLifecycleOwner, Observer {
            if(it != null){
                if(it.uid != null){
                    showCurrentAddressForm(it)
                }
            }
        })
    }

    var resultLauncher = registerForActivityResult(StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val data:Intent? = it.data
            var address: Address? = data?.getSerializableExtra("address") as Address
            if(address != null){
                if(address.isHotel == true){
                    setStartPointAddress(address)
                }
                else{
                    setVisitAddress(address)
                }

            }
        }
    }
    private fun setStartPointAddress(address: Address){
        sharedViewModel.setStartAddress(address.uid)
        sharedViewModel.setStartAddress(address)
        showStartPointAddressForm(address)
    }
    private fun showStartPointAddressForm(address: Address) {
        binding.startAddressForm.apply {
            if(address.latitute != 0.0 && address.longitute != 0.0){
                editStreet.isEnabled = false
                editHouseNumber.isEnabled = false
                editCity.isEnabled = false
                editPostalCode.isEnabled = false
            }
            formRoot.visibility = VISIBLE
            editStreet.setText(address.streetName)
            editHouseNumber.setText(address.streetNumber)
            editCity.setText(address.city)
            editPostalCode.setText(address.postCode)

        }
    }
    private fun setVisitAddress(address: Address){
        sharedViewModel.setCurrentAddress(address.uid)
        sharedViewModel.setCurrentAddress(address)
        showCurrentAddressForm(address)
    }
    private fun showCurrentAddressForm(address: Address){
        binding.currentAddressForm.apply {
            formRoot.visibility = VISIBLE
            if(address.latitute != 0.0 && address.longitute != 0.0){
                editStreet.isEnabled = false
                editHouseNumber.isEnabled = false
                editCity.isEnabled = false
                editPostalCode.isEnabled = false
            }
            editStreet.setText(address.streetName)
            editHouseNumber.setText(address.streetNumber)
            editCity.setText(address.city)
            editPostalCode.setText(address.postCode)


        }
    }
}