package com.consulmedics.patientdata.utils

import android.os.Environment
import android.util.Log
import java.io.*

class AppUtils {
    companion object {
        fun checkUserID(): Boolean {
            var certFile: File = File(Environment.getExternalStorageDirectory(),"Consulmedics/userinfo.cert")
            var isUserIDExsist:Boolean = false
            if(certFile.exists()){
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
                            Log.e(AppConstants.TAG_NAME, "UserID:$userID , DeviceID:$deviceID")
                            isUserIDExsist = true
                        }
                    }
                }
            }
            return isUserIDExsist
        }
    }
}