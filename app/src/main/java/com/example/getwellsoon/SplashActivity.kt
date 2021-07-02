package com.example.getwellsoon

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androidisland.ezpermission.EzPermission
import com.example.getwellsoon.staticClasses.DialogCaller
import com.example.getwellsoon.staticClasses.StaticValues
import java.util.*
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreference = getSharedPreferences(StaticValues.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val newRegistration = sharedPreference.getBoolean(StaticValues.USER_PREF_NEW_REGISTRATION,true)
        val xAuthToken = sharedPreference.getString(StaticValues.USER_PREF_X_AUTH_TOKEN,null)
        val userRoleIsHospital = sharedPreference.getBoolean(StaticValues.USER_PREF_ROLE_IS_HOSPITAL,false)

        // If User Register And Isn't Approved
        if (newRegistration){
            val nextIntent = Intent(this,RollSelect::class.java)
            startActivityAfterInterval(nextIntent,300)
        }
        else if(xAuthToken!=null && userRoleIsHospital){
            val nextIntent = Intent(this,HospitalDashboardActivity::class.java)
            startActivityAfterInterval(nextIntent,600)
        }
        else if(xAuthToken!=null && !userRoleIsHospital){
            val nextIntent = Intent(this,PatientDashboardActivity::class.java)
            startActivityAfterInterval(nextIntent,600)
        }
        else if(xAuthToken==null && userRoleIsHospital){
            val nextIntent = Intent(this,HospitalRegistrationActivity::class.java) // hospital login
            startActivityAfterInterval(nextIntent,600)
        }
        else if(xAuthToken==null && !userRoleIsHospital){
            val nextIntent = Intent(this,PatientsLoginActivity::class.java)
            startActivityAfterInterval(nextIntent,600)
        }
    }

    private fun startActivityAfterInterval(nextIntent: Intent, time:Long){
        val timer = Timer()
        EzPermission.with(this)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .request{ granted, _, _ ->
                    if(granted.size == 2){
                        //Start Activity
                        timer.schedule(timerTask {
                            startActivity(nextIntent)
                            finish()
                        }, time)
                    }
                    else{
                        DialogCaller.alertDialog(this,"You Are Not Allowed Without Permission")
                    }
                }
    }
}