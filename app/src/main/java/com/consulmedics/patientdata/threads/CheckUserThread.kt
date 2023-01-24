package com.consulmedics.patientdata.threads

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Environment.*
import android.util.Log
import com.consulmedics.patientdata.MainActivity
import com.consulmedics.patientdata.ui.login.RegisterActivity
import com.consulmedics.patientdata.utils.AESEncyption
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import java.io.*

class CheckUserThread(appContext: Context): Thread() {
    private var aContext:Context = appContext
    private var certFile:File = File(getExternalStorageDirectory(),"Consulmedics/userinfo.cert")
    public override fun run() {
        var isUserIDExsist:Boolean = false
        if(!certFile.exists()){

            Log.e(TAG_NAME, "No Certificate File")
            Log.e(TAG_NAME, certFile.path)
            try {
                val dir = File(getExternalStorageDirectory().getAbsolutePath() + "/Consulmedics")
                dir.mkdirs();
                certFile = File(dir,"userinfo.cert")
                val fileOutPutStream = FileOutputStream(certFile)
                val encrypted: String? = AESEncyption.encrypt("This is sample text")
                fileOutPutStream.write(encrypted!!.toByteArray())
                fileOutPutStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        else{
            var fileInputStream = FileInputStream(certFile)
            var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while ({ text = bufferedReader.readLine(); text }() != null) {
                stringBuilder.append(text)
            }

            val decrypted:String? = AESEncyption.decrypt(stringBuilder.toString())

            fileInputStream.close()
            if(decrypted != null){
                val userInfo: List<String> = decrypted!!.split("@")
                if(userInfo.count() == 2){
                    val userID: String = userInfo[0]
                    val deviceID: String = userInfo[1]
                    if(userID != "" && deviceID != ""){
                        Log.e(TAG_NAME, "UserID:$userID , DeviceID:$deviceID")
                        isUserIDExsist = true
                    }
                }
            }
        }
        if(isUserIDExsist){
            val i = Intent(aContext, MainActivity::class.java)
            i.addFlags(FLAG_ACTIVITY_NEW_TASK)
            aContext.startActivity(i)
        }
        else{
            val i = Intent(aContext, RegisterActivity::class.java)
            i.addFlags(FLAG_ACTIVITY_NEW_TASK)
            aContext.startActivity(i)
        }

    }
}