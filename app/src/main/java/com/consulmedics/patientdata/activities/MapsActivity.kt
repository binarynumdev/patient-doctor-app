package com.consulmedics.patientdata.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.api.response.FetchLocationResponse

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.consulmedics.patientdata.databinding.ActivityMapsBinding
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import com.consulmedics.patientdata.viewmodels.LocationViewModel
import com.consulmedics.patientdata.viewmodels.LocationViewModelFactory
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.InputStream
import java.util.*

class MapsActivity : BaseActivity(), OnMapReadyCallback {

    val PERMISSION_ID = 42
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var currentLocation: Location? = null
    lateinit var locationManager: LocationManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mMarker: Marker? = null
    val location_permission_grant_request_code = 1001
    private lateinit var placesClient: PlacesClient
    private lateinit var coordinates: LatLng
    private lateinit var apiKey: String
    private var targetAddress: com.consulmedics.patientdata.data.model.Address? = null
//    private lateinit var viewModel: LocationViewModel
    private val viewModel: LocationViewModel by viewModels(){
        LocationViewModelFactory(MyApplication.addressRepository!!)
    }
    private var startAutocompleteIntentListener = View.OnClickListener { view: View ->
        view.setOnClickListener(null)
        startAutocompleteIntent()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
//
        targetAddress  = intent.getSerializableExtra("address") as com.consulmedics.patientdata.data.model.Address?
        if(targetAddress == null)
            targetAddress = com.consulmedics.patientdata.data.model.Address()
        val isAddressHotel = intent.getBooleanExtra("isHotel", true)
        targetAddress?.isHotel = isAddressHotel

        showLoading("Just a seconds", "Loading map...")
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)


        try {
            // Load the API key from the local.properties file
            val applicationInfo = packageManager.getApplicationInfo(
                packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            apiKey = bundle.getString("com.google.android.geo.API_KEY").toString()

            // Use the API key to access the Google Maps API

            Places.initialize(applicationContext, apiKey)
            placesClient = Places.createClient(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.editSearchAddress.setOnClickListener {
            Log.e(TAG_NAME, "ONCLICK")
            val fields = listOf(
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG, Place.Field.VIEWPORT, Place.Field.PLUS_CODE
            )

            // Build the autocomplete intent with field, country, and type filters applied
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("DE")
                .setTypesFilter(listOf( TypeFilter.ESTABLISHMENT.toString().lowercase()))
                .build(this)
            startAutocomplete.launch(intent)
        }

        binding.btnConfirm.setOnClickListener {
            var intent: Intent = Intent()
            intent.putExtra("address", targetAddress)
            setResult(RESULT_OK, intent)
            viewModel.saveAddress(targetAddress!!)
        }
        viewModel.saveAddressId.observe(this){
            targetAddress!!.uid = it.toInt()
            finish()
        }
        viewModel.fetchResponseResult.observe(this) {
            when (it) {
                is BaseResponse.Loading -> {
//                    showLoading()
                    showLoading("Just a sec", "We are getting address information of your target.")
                }

                is BaseResponse.Success -> {
                    stopLoading()

                    processApiResult(it.data)
//                    processLogin(it.data)
                }

                is BaseResponse.Error -> {
                    stopLoading()

                    processError(it.msg)
                }
                else -> {
                    stopLoading()

                }
            }
        }
    }

    private fun processApiResult(data: FetchLocationResponse?) {
        data?.apply {
            if(results.count() > 0){
                val result = results[0]

                result.addressComponents.forEach{
                    Log.e(it.types[0], it.longName)
                    when(it.types[0]){
                        "street_number" ->{
                            binding.textHouseNumber.text = it.longName
                            targetAddress!!.streetNumber = it.longName
                        }
                        "route" ->{
                            binding.textStreet.text = it.longName
                            targetAddress!!.streetName = it.longName
                        }
                        "locality" ->{
                            binding.textCity.text = it.longName
                            targetAddress!!.city = it.longName
                        }
                        "postal_code" ->{
                            binding.textPostCode.text = it.longName
                            targetAddress!!.postCode = it.longName
                        }
                    }

                }

                if(plusCode == null){
                    Log.e(TAG_NAME, "LOCATION: ${result.geometry.location.longitude}, ${result.geometry.location.latitude}")
                    Log.e(TAG_NAME, "LOCATION: ${result.geometry.location.longitude}, ${result.geometry.location.latitude}")
                    targetAddress!!.longitute = result.geometry.location.longitude
                    targetAddress!!.latitute = result.geometry.location.latitude
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(result.geometry.location.latitude, result.geometry.location.longitude), 13f))
                    mMarker = mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(result.geometry.location.latitude, result.geometry.location.longitude))
                            .title("Pickup Your Hotel")
                    )
                }
                else{
//                    binding.editSearchAddress.setText(result.formatedAddress)
                }
            }

        }
    }

    fun showLoading(title: String, message: String) {
//        binding.progressBar.visibility = View.VISIBLE
        showLoadingSpinner(title, message)
    }
    fun stopLoading(){
//        binding.progressBar.visibility = View.GONE
        hideLoadingSpinner()
    }
    fun processError(msg: String?) {
        showToast("Error:" + msg)
    }
    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    private fun fillInAddress(place: Place) {
        val components = place.addressComponents
        val address1 = StringBuilder()
        val postcode = StringBuilder()

        binding.textStreet.text = ""
        binding.textHouseNumber.text = ""
        binding.textCity.text = ""
        binding.textPostCode.text = ""
        // Get each component of the address from the place details,
        // and then fill-in the corresponding field on the form.
        // Possible AddressComponent types are documented at https://goo.gle/32SJPM1
        if (components != null) {
            for (component in components.asList()) {
                Log.e("COMPONETNS:", "${component.types[0]}: ${component.name}")
                when (component.types[0]) {
                    "street_number" -> {
                        address1.insert(0, component.name)
                        Log.e(TAG_NAME, component.name)
                        binding.textHouseNumber.text = component.name
                        targetAddress!!.streetNumber = component.name
                    }
                    "route" -> {
                        address1.append(" ")
                        address1.append(component.shortName)
                        Log.e(TAG_NAME, component.shortName)
                        binding.textStreet.text = component.name
                        targetAddress!!.streetName = component.name
                    }
                    "postal_code" -> {
                        postcode.insert(0, component.name)
                        Log.e(TAG_NAME, component.name)
                        targetAddress!!.postCode = component.name

                    }
                    "postal_code_suffix" -> {
                        postcode.append("-").append(component.name)
                        Log.e(TAG_NAME, component.name)

                    }
                    "locality" ->{
                        binding.textCity.text = component.name
                        targetAddress!!.city = component.name
                    }
//                    "locality" -> binding.autocompleteCity.setText(component.name)
//                    "administrative_area_level_1" -> {
//                        binding.autocompleteState.setText(component.shortName)
//                    }
//                    "country" -> binding.autocompleteCountry.setText(component.name)
                }
            }
        }
//        binding.autocompleteAddress1.setText(address1.toString())
//        binding.editSearchAddress.setText(address1.toString())
        binding.textPostCode.setText(postcode.toString())

//        binding.autocompletePostal.setText(postcode.toString())
//
//        // After filling the form with address components from the Autocomplete
//        // prediction, set cursor focus on the second address line to encourage
//        // entry of sub-premise information such as apartment, unit, or floor number.
//        binding.autocompleteAddress2.requestFocus()

        // Add a map for visual confirmation of the address
        showMap(place)
    }
    private fun showMap(place: Place) {
        coordinates = place.latLng as LatLng
        if(mMarker != null){
            mMarker!!.position = coordinates
            targetAddress!!.latitute = coordinates.latitude
            targetAddress!!.longitute = coordinates.longitude
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 13f))
    }
    // [START maps_solutions_android_autocomplete_define]
    private val startAutocomplete = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult ->
//            binding.editSearchAddress.setOnClickListener(startAutocompleteIntentListener)
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    // Write a method to read the address components from the Place
                    // and populate the form with the address components
                    Log.d(TAG_NAME, "Place: " + place.addressComponents)
                    fillInAddress(place)
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG_NAME, "User canceled autocomplete")
            }
        } as ActivityResultCallback<ActivityResult>)
    // [END maps_solutions_android_autocomplete_define]
    private fun startAutocompleteIntent() {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.

    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Log.e(TAG_NAME, location.latitude.toString())
                        Log.e(TAG_NAME, location.longitude.toString())
                        targetAddress!!.latitute = location.latitude
                        targetAddress!!.longitute = location.longitude
                        addMarker(location)
                        stopLoading()
//                        findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
//                        findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun addMarker(location: Location) {
        mMarker = mMap.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .title("Pickup Your Hotel")
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 13f))
        viewModel.getAddressFromLatLng(location.latitude, location.longitude, apiKey)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location? = locationResult.lastLocation
            if(mLastLocation != null){
                Log.e(TAG_NAME, mLastLocation!!.latitude.toString())
                Log.e(TAG_NAME, mLastLocation!!.longitude.toString())
                targetAddress!!.latitute = mLastLocation.latitude
                targetAddress!!.longitute = mLastLocation.longitude
                addMarker(mLastLocation)
                stopLoading()
            }

//            findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
//            findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        stopLoading()
        Log.e("Selected Address", "${targetAddress!!.latitute } ${targetAddress!!.longitute }")
        mMap.setOnMapLoadedCallback {

            if(targetAddress!!.latitute == 0.00 && targetAddress!!.longitute == 0.00){

                if(targetAddress!!.city.isEmpty() && targetAddress!!.postCode.isEmpty() && targetAddress!!.streetName.isEmpty() && targetAddress!!.streetNumber.isEmpty()){
                    val builder = AlertDialog.Builder(this)
                    builder.setCancelable(false)
                    builder.setTitle("Detect your location?")
                    builder.setMessage("Do you want to detect your current location?")
                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        stopLoading()
                        showLoading("Just a seconds", "We are detecting your current location")
                        getLastLocation()
                    }
                    builder.setNegativeButton(android.R.string.no) { dialog, which ->
                        mMarker = mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(51.035482, 13.7046237))
                                .title("Pickup Your Hotel")
                        )

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.035482, 13.7046237), 13f))
                        viewModel.getAddressFromLatLng(51.035482, 13.7046237, apiKey)
                        dialog.dismiss()
                        stopLoading()
                    }
                    val qDialog = builder.show()
                    qDialog.setCanceledOnTouchOutside(false)
                }
                else{
//                    binding.editSearchAddress.setText("${targetAddress!!.streetName} ${targetAddress!!.streetNumber}, ${targetAddress!!.postCode} ${targetAddress!!.city}")
                    viewModel.getAddressFromString("${targetAddress!!.streetName} ${targetAddress!!.streetNumber}, ${targetAddress!!.postCode} ${targetAddress!!.city}", apiKey)
                }
            }
            else{
                mMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(targetAddress!!.latitute, targetAddress!!.longitute))
                        .title("Pickup Your Hotel")
                )

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(targetAddress!!.latitute, targetAddress!!.longitute), 13f))
//                viewModel.getAddressFromLatLng(targetAddress!!.latitute, targetAddress!!.longitute, apiKey)
                binding.textCity.setText(targetAddress?.city)
                binding.textStreet.setText(targetAddress?.streetName)
                binding.textHouseNumber.setText(targetAddress?.streetNumber)
                binding.textPostCode.setText(targetAddress?.postCode)

            }



        }
        mMap.setOnMapClickListener {
            Log.e(TAG_NAME, "Lat:${it.latitude}, Long:${it.longitude}")
            if(mMarker != null){
                mMarker!!.position = it
            }
            showLoading("Just a sec", "We are getting address information of your target.")
            targetAddress!!.latitute = it.latitude
            targetAddress!!.longitute = it.longitude
            viewModel.getAddressFromLatLng(it.latitude, it.longitude, apiKey)
//            viewModel.getAddressUsingGeoCode(it.latitude, it.longitude)
        }

    }
    fun getAddressFromLatLng(latitude: Double, longitude: Double): String {

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            val sb = StringBuilder()
            for (i in 0..address.maxAddressLineIndex) {
                Log.e(TAG_NAME,address.getAddressLine(i) )
                binding.textCity.text = address.locality
                binding.textHouseNumber.text = address.featureName
                binding.textStreet.text = address.thoroughfare
                binding.textPostCode.text = address.postalCode
                Log.e(TAG_NAME, address.adminArea)
                Log.e(TAG_NAME, address.locality) // city
                Log.e(TAG_NAME, address.featureName) // house number
                Log.e(TAG_NAME, address.postalCode)
                Log.e(TAG_NAME, address.thoroughfare)



                sb.append(address.getAddressLine(i)).append("\n")
            }

            return sb.toString().trim()
        }
        return ""
    }

}