package com.consulmedics.patientdata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.consulmedics.patientdata.databinding.ActivityMainBinding
import com.consulmedics.patientdata.models.Patient
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {



    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    val scardLib = SCardExt()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            run {

                var status: Long = 0
                if(!scardLib.initialized){
                    status = scardLib.SCardEstablishContext(baseContext)
                    if(0L != status){
                        Log.e("ERROR", "Establish context error")
                    }
                    else{
                        status = scardLib.SCardListReaders(baseContext)
                    }
                }
                if(scardLib.initialized){
                    status = scardLib.SCardConnect()
                    if(0L == status){
                        var patientData = scardLib.getPatientData()
                        startActivity(Intent(this, PatientDataTabActivity::class.java).apply {
                            // you can add values(if any) to pass to the next class or avoid using `.apply`
                            putExtra("patient_data", patientData)
                        })
                    }
                }
                else{
                    var patient: Patient = Patient()
                    var pdata: String = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\" standalone=\"yes\"?><vsdp:UC_PersoenlicheVersichertendatenXML CDM_VERSION=\"5.2.0\" xmlns:vsdp=\"http://ws.gematik.de/fa/vsdm/vsd/v5.2\"><vsdp:Versicherter><vsdp:Versicherten_ID>T115774582</vsdp:Versicherten_ID><vsdp:Person><vsdp:Geburtsdatum>19800713</vsdp:Geburtsdatum><vsdp:Vorname>Claudia</vsdp:Vorname><vsdp:Nachname>Burdack</vsdp:Nachname><vsdp:Geschlecht>W</vsdp:Geschlecht><vsdp:StrassenAdresse><vsdp:Postleitzahl>01187</vsdp:Postleitzahl><vsdp:Ort>Dresden</vsdp:Ort><vsdp:Land><vsdp:Wohnsitzlaendercode>D</vsdp:Wohnsitzlaendercode></vsdp:Land><vsdp:Strasse>Bayreuther Str.</vsdp:Strasse><vsdp:Hausnummer>30</vsdp:Hausnummer></vsdp:StrassenAdresse></vsdp:Person></vsdp:Versicherter></vsdp:UC_PersoenlicheVersichertendatenXML>"
                    var vdata: String = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\" standalone=\"yes\"?>\n" +
                            "<vsda:UC_AllgemeineVersicherungsdatenXML CDM_VERSION=\"5.2.0\"\n" +
                            "\txmlns:vsda=\"http://ws.gematik.de/fa/vsdm/vsd/v5.2\">\n" +
                            "\t<vsda:Versicherter>\n" +
                            "\t\t<vsda:Versicherungsschutz>\n" +
                            "\t\t\t<vsda:Beginn>20081002</vsda:Beginn>\n" +
                            "\t\t\t<vsda:Kostentraeger>\n" +
                            "\t\t\t\t<vsda:Kostentraegerkennung>104526376</vsda:Kostentraegerkennung>\n" +
                            "\t\t\t\t<vsda:Kostentraegerlaendercode>D</vsda:Kostentraegerlaendercode>\n" +
                            "\t\t\t\t<vsda:Name>VIACTIV Krankenkasse</vsda:Name>\n" +
                            "\t\t\t\t<vsda:AbrechnenderKostentraeger>\n" +
                            "\t\t\t\t\t<vsda:Kostentraegerkennung>104526376222</vsda:Kostentraegerkennung>\n" +
                            "\t\t\t\t\t<vsda:Kostentraegerlaendercode>D</vsda:Kostentraegerlaendercode>\n" +
                            "\t\t\t\t\t<vsda:Name>VIACTIV Krankenkasse222</vsda:Name>\n" +
                            "\t\t\t\t</vsda:AbrechnenderKostentraeger>\n" +
                            "\t\t\t</vsda:Kostentraeger>\n" +
                            "\t\t</vsda:Versicherungsschutz>\n" +
                            "\t\t<vsda:Zusatzinfos>\n" +
                            "\t\t\t<vsda:ZusatzinfosGKV>\n" +
                            "\t\t\t\t<vsda:Versichertenart>1</vsda:Versichertenart>\n" +
                            "\t\t\t\t<vsda:Zusatzinfos_Abrechnung_GKV>\n" +
                            "\t\t\t\t\t<vsda:WOP>98</vsda:WOP>\n" +
                            "\t\t\t\t</vsda:Zusatzinfos_Abrechnung_GKV>\n" +
                            "\t\t\t</vsda:ZusatzinfosGKV>\n" +
                            "\t\t</vsda:Zusatzinfos>\n" +
                            "\t</vsda:Versicherter>\n" +
                            "</vsda:UC_AllgemeineVersicherungsdatenXML>"
                    patient.loadFrom(pdata, vdata)
                    startActivity(Intent(this, PatientDataTabActivity::class.java).apply {
                        // you can add values(if any) to pass to the next class or avoid using `.apply`
                        putExtra("patient_data", patient)
                    })
                }

            }
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val sttus = scardLib.USBRequestPermission(applicationContext)
        Log.e("USB_CONNECTION", sttus.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
}