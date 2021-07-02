package com.example.getwellsoon

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.afollestad.vvalidator.form
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.getwellsoon.staticClasses.DialogCaller
import com.example.getwellsoon.staticClasses.StaticValues
import org.json.JSONObject

class PatientsLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patients_login)

        // Validate Form and Perform Login
        form {
            input(R.id.emailInput) {
                isNotEmpty()
                isEmail()
            }
            input(R.id.passwordInput) {
                isNotEmpty()
                length().atLeast(6)
            }
            submitWith(R.id.loginBtn) {
                performLogin();
            }
        }

        // Create Account Text Pressed
        val createAccountText: TextView = findViewById(R.id.createAccountText)
        createAccountText.setOnClickListener {
            val nextIntent = Intent(this,PatientRegistrationActivity::class.java)
            //nextIntent.putExtra("isGuest",true)
            startActivity(nextIntent)
        }
    }

    private fun performLogin() {
        //API Endpoint
        val url = StaticValues.URL_API_SERVER+"patients/login/"

        val emailInput:EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)

        // Post parameters
        val params = HashMap<String, String>()
        params["email"] = emailInput.text.toString()
        params["password"] = passwordInput.text.toString()
        val jsonObject = JSONObject(params as Map<*, *>)

        // Show Processing Dialog
        val pd = ProgressDialog(this)
        pd.setMessage("loading")
        pd.show()

        // Perform API Request
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val jor = JsonObjectRequest(Request.Method.POST, url, jsonObject, { response ->
            // Stop Processing Dialog
            pd.hide()

            // Handle Response Data
            if(response.getInt("status") == 200){
                val data = response.getJSONObject("data")

                //Store User Information in shared Preference
                val sharedPreference =  getSharedPreferences(StaticValues.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()

                editor.putBoolean(StaticValues.USER_PREF_NEW_REGISTRATION,false)
                editor.putString(StaticValues.USER_PREF_NAME,data.getString("name"))
                editor.putString(StaticValues.USER_PREF_EMAIL_ID,data.getString("email"))
                editor.putString(StaticValues.USER_PREF_OBJECT_ID,data.getString("patientObjectId"))
                editor.putString(StaticValues.USER_PREF_X_AUTH_TOKEN,data.getString("x-auth-token"))
                editor.apply()

                // Goto DashBoard Intent
                val nextIntent = Intent(this,PatientDashboardActivity::class.java)
                startActivity(nextIntent)
                finishAffinity()
            }
            else{
                DialogCaller.positiveDialog(this,response.getString("message"))
            }
        }, { error ->
            // Stop Processing Dialog
            pd.hide()
            DialogCaller.alertDialog(this,error.message)
        })
        rq.add(jor)
    }

}