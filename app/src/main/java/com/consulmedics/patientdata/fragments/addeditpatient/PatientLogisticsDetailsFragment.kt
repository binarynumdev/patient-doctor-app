package com.consulmedics.patientdata.fragments.addeditpatient

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.Converters
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.activities.MapsActivity
import com.consulmedics.patientdata.adapters.AddressDialogAdapter
import com.consulmedics.patientdata.components.SelectBottomSheetDialogFragment
import com.consulmedics.patientdata.data.model.Address
import com.consulmedics.patientdata.databinding.FragmentPatientLogisticsDetailsBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppConstants.HOTEL_TEXT
import com.consulmedics.patientdata.utils.AppConstants.NO_TEXT
import com.consulmedics.patientdata.utils.AppConstants.PREV_PATIENT_TEXT
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppConstants.YES_TEXT
import com.consulmedics.patientdata.utils.AppUtils.Companion.isOnline
import com.consulmedics.patientdata.viewmodels.PatientViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PatientLogisticsDetailsFragment : BaseAddEditPatientFragment() {
    private var _binding: FragmentPatientLogisticsDetailsBinding? = null
    val binding get() = _binding!!
    var hotelList = emptyList<Address>()

    private  val patientListViewModel: PatientViewModel by viewModels()
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
            editDateOfVisit.setOnClickListener {
                var c = Calendar.getInstance()
                val converter: Converters = Converters()
                if(sharedViewModel.patientData.value?.startVisitDate != null){
                    c.time = sharedViewModel.patientData.value?.startVisitDate!!
                }
                var year = c.get(Calendar.YEAR)
                var month = c.get(Calendar.MONTH)
                var day = c.get(Calendar.DAY_OF_MONTH)


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
                    sharedViewModel.setStartVisitDate(c.time)
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
                sharedViewModel.previousPatients.value?.also {
                    if(it.isNotEmpty()){
                        val prevPatient = it[0]
                        if (prevPatient.visitAddress != null) {
                            sharedViewModel.viewModelScope.launch {
                                sharedViewModel.setStartAddress(prevPatient.visitAddress)
                            }
                        } else {
                            sharedViewModel.viewModelScope.launch {

                            }
                        }
                    }
                }
//                binding.startAddressForm.formRoot.visibility = GONE
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
                sharedViewModel.setCurrentAddressSame(false)
                addressFormLayout.visibility = VISIBLE
                val radioOptions = arrayOf("Fill from patient address", "Choose new address from map", "Fill address manually")
                val bottomSheet = SelectBottomSheetDialogFragment(radioOptions, false)
                bottomSheet.setOnItemClickListener {
                    bottomSheet.dismiss()
                    when(it){
                        0 ->{
                            importAddressFromPatientData()
                        }
                        1 ->{
                            showNewAddressMapScreen()
                        }
                        2 ->{
                            fillVisitAddressManually()
                        }
                    }
                }
                activity?.supportFragmentManager?.let { it1 -> bottomSheet.show(it1, "SelectBottomSheet") }

            }
            radioCurrentAddressSameYes.setOnClickListener {
                sharedViewModel.setCurrentAddressSame(true)
                sharedViewModel.previousPatients.value?.also {
                    Log.e("Previous", it.toString())
                    if(it.isNotEmpty()){
                        Log.e("Number of Patients", it.size.toString())
                        val prevPatient = it[0]
                        Log.e("Street", prevPatient.street)
                        Log.e("House Number", prevPatient.houseNumber)
                        Log.e("Post Code", prevPatient.postCode)
                        Log.e("City", prevPatient.city)
                        Log.e("Visit Address", prevPatient.visitAddress.toString())
                        if (prevPatient.visitAddress != null) {
                            addressFormLayout.visibility = GONE
                            sharedViewModel.viewModelScope.launch {
                                sharedViewModel.setStartAddress(prevPatient.visitAddress)
                                sharedViewModel.setVisitAddress(prevPatient.visitAddress)
                            }
                        } else {
                            sharedViewModel.viewModelScope.launch {

                            }
                        }
                    }
                }
            }
            radioCurrentPatientVisitThisShiftYes.setOnClickListener{
                sharedViewModel.setCurrentPatientAlreadyVisited(true)
            }
            radioCurrentPatientVisitThisShiftNo.setOnClickListener {
                sharedViewModel.setCurrentPatientAlreadyVisited(false)
            }
            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.action_patientLogisticsDetailsFragment_to_patientDoctorDocumentFragment)
            }
            btnBack.setOnClickListener {
                activity?.onBackPressed()
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

                editStreet.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        // Not needed for this implementation
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if(!s.isNullOrEmpty() && s[0].isLowerCase() && s.length == 1) {
                            val capitalizedText = s.toString().capitalize()
                            editStreet.setText(capitalizedText)
                            editStreet.setSelection(capitalizedText.length)
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // Not needed for this implementation
                    }
                })

                editCity.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        // Not needed for this implementation
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if(!s.isNullOrEmpty() && s[0].isLowerCase() && s.length == 1) {
                            val capitalizedText = s.toString().capitalize()
                            editCity.setText(capitalizedText)
                            editCity.setSelection(capitalizedText.length)
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // Not needed for this implementation
                    }
                })
//                editHouseNumber.doAfterTextChanged {
//                    sharedViewModel.visitAddress.value?.streetNumber = it.toString()
//                }
                editPostalCode.doAfterTextChanged {
                    sharedViewModel.visitAddress.value?.postCode = it.toString()
                }
                btnConfirmMap.setOnClickListener {

                    if(isOnline(requireContext())){
                        val intent = Intent(requireActivity(), MapsActivity::class.java)
                        intent.putExtra("isHotel", false)
                        intent.putExtra("address", sharedViewModel.visitAddress.value)
                        resultLauncher.launch(intent)
                    }
                    else{
                        Toast.makeText(requireContext(), "Sorry! You are offline now. Please try again later.", Toast.LENGTH_LONG).show();
                    }

                }
            }
            startAddressForm.apply {
                editCity.doAfterTextChanged {
                    sharedViewModel.startAddress.value?.city = it.toString()
                }
                editStreet.doAfterTextChanged {
                    sharedViewModel.startAddress.value?.streetName = it.toString()
                }
//                editHouseNumber.doAfterTextChanged {
//                    sharedViewModel.startAddress.value?.streetNumber = it.toString()
//                }
                editPostalCode.doAfterTextChanged {
                    sharedViewModel.startAddress.value?.postCode = it.toString()
                }

                btnSyncVisitAddress.setOnClickListener {
                    importAddressFromPatientData()
                }
                btnVisitAddressFromMap.setOnClickListener {
                    showNewAddressMapScreen()
                }
                btnFillVIsitAddressManually.setOnClickListener {
                    fillVisitAddressManually()
                }
                btnConfirmMap.setOnClickListener {
                    if(isOnline(requireContext())){
                        val intent = Intent(requireActivity(), MapsActivity::class.java)
                        intent.putExtra("isHotel", true)
                        intent.putExtra("address", sharedViewModel.startAddress.value)
                        resultLauncher.launch(intent)
                    }
                    else{
                        Toast.makeText(requireContext(), "Sorry! You are offline now. Please try again later.", Toast.LENGTH_LONG).show();
                    }

                }

            }
        }
        return binding.root
    }

    private fun fillVisitAddressManually() {
        checkVisitAddress()
        sharedViewModel.setVisitAddressFromPatientData(false)
        binding.currentAddressForm.apply {

            editStreet.setText("")
//            editHouseNumber.setText("")
            editCity.setText("")
            editPostalCode.setText("")
            editStreet.isEnabled = true
//            editHouseNumber.isEnabled = true
            editCity.isEnabled = true
            editPostalCode.isEnabled = true
        }
    }

    private fun importAddressFromPatientData() {

        val patient = sharedViewModel.patientData.value
        if(patient?.postCode?.trim()?.isEmpty() == true && patient?.houseNumber?.trim()?.isEmpty() == true && patient?.postCode?.trim()?.isEmpty() == true && patient?.city?.trim()?.isEmpty() == true){

        }

        sharedViewModel.setVisitAddressFromPatientData(true)
        checkVisitAddress()
        sharedViewModel.viewModelScope.launch {
            var visitAddress = sharedViewModel.visitAddress.value
            visitAddress?.apply {
                city = patient?.city.toString()
                streetName = patient?.street.toString()
                streetNumber = patient?.houseNumber.toString()
                postCode = patient?.postCode.toString()
            }
            if(visitAddress != null){
                sharedViewModel.setVisitAddress(visitAddress!!)
            }
        }



    }

    fun checkVisitAddress() {
        if(sharedViewModel.visitAddress.value == null){
            sharedViewModel.setVisitAddress(Address())
        }
    }

    private fun showVisitAddressFromPatientData() {
        binding.currentAddressForm.apply {
            editStreet.setText(sharedViewModel.patientData.value?.street)
//            editHouseNumber.setText(sharedViewModel.patientData.value?.houseNumber)
            editCity.setText(sharedViewModel.patientData.value?.city)
            editPostalCode.setText(sharedViewModel.patientData.value?.postCode)
            editStreet.isEnabled = false
//            editHouseNumber.isEnabled = false
            editCity.isEnabled = false
            editPostalCode.isEnabled = false
        }
    }


    fun showChooseHotelModal(){
        val radioOptions = arrayOf("Fill from patient address", "Choose new address from map", "Fill address manually")
        val bottomSheet = SelectBottomSheetDialogFragment(radioOptions, true, "Select an item")
        val dialogAddressList = ArrayList(hotelList)
        if(isOnline(requireContext())){
            dialogAddressList.add(Address())
        }

        dialogAddressList.add(Address(-99))
        val adapter = AddressDialogAdapter(requireContext(), dialogAddressList)
        adapter.setOnItemClickListener {
            bottomSheet.dismiss()
            val address = dialogAddressList.get(it)
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
        bottomSheet.setAdapter(adapter)
        activity?.supportFragmentManager?.let { it1 ->
            bottomSheet.show(it1, "SelectBottomSheet")
        }

    }
    fun showNewAddressMapScreen(){
        Log.e(TAG_NAME, "SHOW CREATE HOTEL MODAL")

        if(isOnline(requireContext())){
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            intent.putExtra("isHotel", false)
            intent.putExtra("address", sharedViewModel.visitAddress.value)

//        startActivityForResult(intent, 1100)
            resultLauncher.launch(intent)
        }
        else{
            Toast.makeText(requireContext(), "Sorry! You are offline now. Please try again later.", Toast.LENGTH_LONG).show();
        }

    }

    fun confirmAddressFromMapScreen(){
        if(isOnline(requireContext())){
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            intent.putExtra("isHotel", true)
            intent.putExtra("address", sharedViewModel.startAddress.value)

//        startActivityForResult(intent, 1100)
            resultLauncher.launch(intent)
        }
        else{
            Toast.makeText(requireContext(), "Sorry! You are offline now. Please try again later.", Toast.LENGTH_LONG).show();
        }

    }
    fun showCreateHotelModal(){
        if(isOnline(requireContext())){
            Log.e(TAG_NAME, "SHOW CREATE HOTEL MODAL")
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            intent.putExtra("isHotel", true)

//        startActivityForResult(intent, 1100)
            resultLauncher.launch(intent)
        }
        else{
            Toast.makeText(requireContext(), "Sorry! You are offline now. Please try again later.", Toast.LENGTH_LONG).show();
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
        val converters: Converters = Converters()
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {


            binding.editTimeOfVisit.setText(sharedViewModel.patientData.value?.startVisitTime)
            if(sharedViewModel.patientData.value?.startVisitDate !=  null) {
                binding.editDateOfVisit.setText(
                    birthDateFormat.format(
                        sharedViewModel.patientData.value?.startVisitDate
                    )
                )
            }
            if(sharedViewModel.patientData.value?.startPoint == HOTEL_TEXT){
                binding.radioStartPointIsHotel.isChecked = true
            }
            else if(sharedViewModel.patientData.value?.startPoint == PREV_PATIENT_TEXT){
                binding.radioStartPointIsPrevPatient.isChecked = true
            }

            if(sharedViewModel.patientData.value?.sameAddAsPrev == true){
                binding.radioCurrentAddressSameYes.isChecked = true
                binding.addressFormLayout.visibility = GONE
            }
            else{
                binding.radioCurrentAddressSameNo.isChecked = true
                binding.addressFormLayout.visibility = VISIBLE
            }

//            binding.radioCurrentPatientVisitThisShiftYes.isChecked = sharedViewModel.patientData.value?.alreadyVisitedDuringThisShift == true
            if(sharedViewModel.patientData.value?.alreadyVisitedDuringThisShift == true) {
                binding.radioCurrentPatientVisitThisShiftYes.isChecked = true
            } else {
                binding.radioCurrentPatientVisitThisShiftNo.isChecked = true
            }

            if(it.distance > 0.00){
                Log.e("DISTANCE", "${it.distance}")
                binding.textDistance.text = "Total Distance: ${(it.distance/1000).toInt()}Km"
                binding.textDistance.visibility = VISIBLE
            }
            else{
                binding.textDistance.visibility = GONE
            }
            if(it.sincVisitAddress){
                binding.btnSyncVisitAddress.isChecked = true
                showVisitAddressFromPatientData()
            }
            else{
                binding.currentAddressForm.apply {
                    if(sharedViewModel.visitAddress.value?.latitute == 0.00 && sharedViewModel.visitAddress.value?.longitute == 0.00){
                        editStreet.isEnabled = true
//                        editHouseNumber.isEnabled = true
                        editCity.isEnabled = true
                        editPostalCode.isEnabled = true
                        btnConfirmMap.visibility = VISIBLE
                    }
                    else{
                        editStreet.isEnabled = false
//                        editHouseNumber.isEnabled = false
                        editCity.isEnabled = false
                        editPostalCode.isEnabled = false
                        btnConfirmMap.visibility = GONE
                    }
                }
            }
        })
        sharedViewModel.hotelList.observe(viewLifecycleOwner, Observer {
            hotelList = it
        })

        sharedViewModel.startAddress.observe(viewLifecycleOwner, Observer {
            if(it != null){
                Log.e(TAG_NAME, "Collect start address: ${it.uid}")
                showStartPointAddressForm(it)
            }


        })
        sharedViewModel.visitAddress.observe(viewLifecycleOwner, Observer {
            showCurrentAddressForm(it)
        })

        sharedViewModel.previousPatients.observe(viewLifecycleOwner, Observer {
            if(it.count() > 0){
                binding.layoutVisitAddressIsPrv.visibility = VISIBLE
                binding.layoutVisitAddressIsNew.visibility = GONE
                binding.radioStartPointIsPrevPatient.visibility = VISIBLE
            }
            else{
                binding.layoutVisitAddressIsPrv.visibility = GONE
                binding.layoutVisitAddressIsNew.visibility = VISIBLE
                binding.currentAddressForm.formRoot.visibility = VISIBLE

                binding.radioStartPointIsPrevPatient.visibility = GONE
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
        sharedViewModel.viewModelScope.launch {
//            sharedViewModel.setStartAddress(address.uid)
            sharedViewModel.setStartAddress(address)
        }
//        showStartPointAddressForm(address)
    }
    private fun showStartPointAddressForm(address: Address) {
        binding.startAddressForm.apply {

            if(address.latitute != 0.0 && address.longitute != 0.0){
                editStreet.isEnabled = false
//                editHouseNumber.isEnabled = false
                editCity.isEnabled = false
                editPostalCode.isEnabled = false
//                binding.btnConfirmStartAddress.isEnabled = false
                btnConfirmMap.visibility = GONE
                Log.e(TAG_NAME, "${address.latitute}")
            }
            else{
                editStreet.isEnabled = true
//                editHouseNumber.isEnabled = true
                editCity.isEnabled = true
                editPostalCode.isEnabled = true
                btnConfirmMap.visibility = VISIBLE
            }
            formRoot.visibility = VISIBLE
            editStreet.setText(address.streetName)
//            editHouseNumber.setText(address.streetNumber)
            editCity.setText(address.city)
            editPostalCode.setText(address.postCode)

        }
    }
    private fun setVisitAddress(address: Address){
        sharedViewModel.viewModelScope.launch {
//            sharedViewModel.setCurrentAddress(address.uid)
            sharedViewModel.setCurrentAddress(address)
            if(sharedViewModel.patientData.value?.sincVisitAddress == true){
                sharedViewModel.setCity(address.city)
                sharedViewModel.setPostCode(address.postCode)
                sharedViewModel.setHouseNumber(address.streetNumber)
                sharedViewModel.setStreet(address.streetName)
            }
        }
//        showCurrentAddressForm(address)
    }
    private fun showCurrentAddressForm(address: Address){
        binding.currentAddressForm.apply {
            formRoot.visibility = VISIBLE
            Log.e(TAG_NAME, address.longitute.toString());
            Log.e(TAG_NAME, address.latitute.toString());
            if(sharedViewModel.patientData.value?.sincVisitAddress == true){
                editStreet.isEnabled = false
//                editHouseNumber.isEnabled = false
                editCity.isEnabled = false
                editPostalCode.isEnabled = false
                if(address.latitute != 0.0 && address.longitute != 0.0){
                    this.btnConfirmMap.visibility = GONE
                }
                else{
                    this.btnConfirmMap.visibility = VISIBLE
                }
            }
            else if(address.latitute != 0.0 && address.longitute != 0.0){
                editStreet.isEnabled = false
//                editHouseNumber.isEnabled = false
                editCity.isEnabled = false
                editPostalCode.isEnabled = false
                this.btnConfirmMap.visibility = GONE
            }
            else{
                editStreet.isEnabled = true
//                editHouseNumber.isEnabled = true
                editCity.isEnabled = true
                editPostalCode.isEnabled = true
                this.btnConfirmMap.visibility = VISIBLE
            }
            editStreet.setText(address.streetName)
//            editHouseNumber.setText(address.streetNumber)
            editCity.setText(address.city)
            editPostalCode.setText(address.postCode)


        }
    }
}