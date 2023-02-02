package com.consulmedics.patientdata.ui.register

import android.content.Intent
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.consulmedics.patientdata.activities.AdminActivity
import com.consulmedics.patientdata.databinding.ActivityRegisterBinding

import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.utils.AESEncyption
import com.consulmedics.patientdata.utils.AppConstants.CERT_FILE_FOLDER
import com.consulmedics.patientdata.utils.AppConstants.CERT_FILE_NAME
import com.consulmedics.patientdata.utils.AppConstants.CERT_FILE_PATH
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val btnRegister = binding.btnRegister
        val editUserID = binding.editUserID
        val editDeviceID = binding.editDeviceID

        btnRegister.setOnClickListener{
            Log.e(TAG_NAME, "BTN REGISTER HAS BEEN CLICKED!!!")
            Log.e(TAG_NAME, editUserID.text.toString())
            Log.e(TAG_NAME, (editUserID.text.toString() == "").toString())
            if(editUserID.text.toString() == ""){
                Toast.makeText(applicationContext, R.string.missing_user_id, Toast.LENGTH_LONG).show()
            }
            else if (editDeviceID.text.toString() == ""){
                Toast.makeText(applicationContext, R.string.missing_device_id, Toast.LENGTH_LONG).show()
            }
            else{
                var certFile:File = File(getExternalStorageDirectory(),CERT_FILE_PATH)
                try {
                    val dir = File(getExternalStorageDirectory().getAbsolutePath() + CERT_FILE_FOLDER)
                    dir.mkdirs();
                    certFile = File(dir,CERT_FILE_NAME)
                    val fileOutPutStream = FileOutputStream(certFile)
                    val encrypted: String? = AESEncyption.encrypt("${editUserID.text}@${editDeviceID.text}")
                    fileOutPutStream.write(encrypted!!.toByteArray())
                    fileOutPutStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                var isUserIDExsist:Boolean = false
                isUserIDExsist = AppUtils.checkUserID()
                if(isUserIDExsist){
                    val i = Intent(applicationContext, AdminActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                }
                else{

                }
            }
        }
    }
}

