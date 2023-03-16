package com.consulmedics.patientdata.threads

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.consulmedics.patientdata.activities.MainActivity
import com.consulmedics.patientdata.NoUserIdActivity
import com.consulmedics.patientdata.PatientsDatabase
import com.consulmedics.patientdata.activities.LoginActivity
import com.consulmedics.patientdata.utils.AppUtils

class CheckUserThread(appContext: Context): Thread() {
    private var aContext:Context = appContext

    public override fun run() {
        var isUserIDExsist:Boolean = false
        isUserIDExsist = AppUtils.checkUserID()
        if(isUserIDExsist){
            val i = Intent(aContext, MainActivity::class.java)
            i.addFlags(FLAG_ACTIVITY_NEW_TASK)
            aContext.startActivity(i)
        }
        else{
            val patientDB by lazy { PatientsDatabase.getDatabase(aContext).patientDao() }
            val patientCount: Int = patientDB.getList().count()
            if(patientCount == 0){
                val i = Intent(aContext, LoginActivity::class.java)
                i.addFlags(FLAG_ACTIVITY_NEW_TASK)
                aContext.startActivity(i)
            }
            else{
                val i = Intent(aContext, NoUserIdActivity::class.java)
                i.addFlags(FLAG_ACTIVITY_NEW_TASK)
                aContext.startActivity(i)
            }

        }

    }
}