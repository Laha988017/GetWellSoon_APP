package com.example.getwellsoon

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import androidx.drawerlayout.widget.DrawerLayout
import com.afollestad.vvalidator.form
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.getwellsoon.models.User_patient
import com.example.getwellsoon.staticClasses.DialogCaller
import com.example.getwellsoon.staticClasses.StaticValues
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.activity_patient_profile.*
import org.json.JSONObject

class PatientProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        //Get NavigationView And Drawer Layout
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        // Set toolbar action
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        // Add Back Button on toolbar
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val sharedPreference =
                getSharedPreferences(StaticValues.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val imageBase64 = sharedPreference.getString(StaticValues.USER_PREF_IMAGE_BASE64,null)

        val userImage: CircularImageView = findViewById(R.id.userImage)

        //image load
        if (imageBase64 !=null) {
            val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            userImage.setImageBitmap(decodedImage)
        }


        // get profile and attach
        // Show Processing Dialog
        val pd = ProgressDialog(this)
        pd.setMessage("loading")
        pd.show()

        val url = StaticValues.URL_API_SERVER+"patients/myProfile/"
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val jor = object: JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
            // Stop Processing Dialog
            pd.hide()

            if(response.getInt("status") == 200) {
                val userJson = response.getJSONObject("data");
                val userObject = Gson().fromJson(userJson.toString(), User_patient::class.java)

                // Display Values
                nameText.text = userObject.name;
                emailText.text = userObject.email;
                contactNumberText.text = userObject.contactNumber.toString();
                allergicTo.text = userObject.alergicTo;
                medicalHistory.text = userObject.medicalHistory;
                bloodGroup.text = userObject.bloodGroup;
                ailment.text = userObject.ailment;
            }
            else{
                DialogCaller.positiveDialog(this,response.getString("message"))
            }
        }, Response.ErrorListener { error ->
            // Stop Processing Dialog
            pd.hide()

            DialogCaller.alertActionDialog(this,error.message,
                    DialogInterface.OnClickListener { _, _ ->
                        finish()
                    })
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val sharedPreference = getSharedPreferences(StaticValues.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                val xAuthToken = sharedPreference.getString(StaticValues.USER_PREF_X_AUTH_TOKEN,null)
                val headers = HashMap<String, String>()
                headers["x-auth-token"] = xAuthToken!!
                return headers
            }
        }
        rq.add(jor)

        // Onclick on password change btn
        changePasswordBtn.setOnClickListener {
            // Show Popup Of QR Scan or Manually Enter
            val dialog: Dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_password_change)
            dialog.show()

            val currentPassword = dialog.findViewById<EditText>(R.id.currentPasswordInput)
            val newPasswordInput = dialog.findViewById<EditText>(R.id.newPasswordInput)
            val newPasswordConfirmInput = dialog.findViewById<EditText>(R.id.newPasswordConfirmInput)
            val passwordSubmitBtn = dialog.findViewById<Button>(R.id.passwordSubmitBtn)

            form {
                input(currentPassword){
                    isNotEmpty()
                    length().atLeast(6)
                }
                input(newPasswordInput){
                    isNotEmpty()
                    length().atLeast(6)
                }
                input(newPasswordConfirmInput){
                    isNotEmpty()
                    length().atLeast(6)
                }
                submitWith(passwordSubmitBtn){
                    if(newPasswordInput.text.toString() != newPasswordConfirmInput.text.toString()){
                        DialogCaller.alertDialog(this@PatientProfileActivity,"Password Does Not matched!")
                    }
                    else{
                        dialog.hide()
                        changePassword(currentPassword.text.toString() ,newPasswordInput.text.toString())
                    }
                }
            }

        }

        addDetail.setOnClickListener {
            val dialog: Dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_add_details)
            dialog.show()
            val pd = ProgressDialog(this)
            pd.setMessage("loading")
            pd.show()

            val alergicTo = dialog.findViewById<EditText>(R.id.alergicTo)
            val medicalHistory = dialog.findViewById<EditText>(R.id.medicalHistory)
            val bloodGroup = dialog.findViewById<EditText>(R.id.bloodGroup)
            val bloodType = dialog.findViewById<EditText>(R.id.bloodtype)
            val isCovidPositive = dialog.findViewById<EditText>(R.id.isCovidPositive)
            val addDetailsBtn = dialog.findViewById<Button>(R.id.addDetailsBtn)


            form {
                input(alergicTo) {
                    isNotEmpty()
                    length().atLeast(4)
                }
                input(medicalHistory) {
                    isNotEmpty()
                    length().atLeast(4)
                }
                input(bloodGroup) {
                    isNotEmpty()
                    length().atLeast(1)
                }
                input(bloodType) {
                    isNotEmpty()
                    length().atLeast(1)
                }
                input(isCovidPositive) {
                    isNotEmpty()
                    length().atLeast(1)
                }
                submitWith(addDetailsBtn) {
                    dialog.hide()
                    addDetails(alergicTo.text.toString(), medicalHistory.text.toString(), bloodGroup.text.toString(), bloodGroup.text.toString(), isCovidPositive.text.toString())
                }
            }
        }
            }


    private fun addDetails(alergicTo: String, medicalHistory: String, bloodGroup: String, bloodType: String, isCovidPositive: String) {
        val pd = ProgressDialog(this)
        pd.setMessage("loading")
        pd.show()

        // Post parameters
        val params = HashMap<String, String>()

        val paramsBlood = HashMap<String, String>()
        paramsBlood["rhFactor"] = bloodGroup
        paramsBlood["bloodType"] = bloodType
        val jsonObjectBlood = JSONObject(paramsBlood as Map<*, *>)

        params["currentPassword"] = alergicTo
        params["medicalHistory"] = medicalHistory
        params["bloodGroup"] = jsonObjectBlood.toString()
        params["isCovidPositive"] = isCovidPositive


        val jsonObject = JSONObject(params as Map<*, *>)
    }





    private fun changePassword(currentPassword:String,newPassword:String){
        // get profile and attach
        // Show Processing Dialog
        val pd = ProgressDialog(this)
        pd.setMessage("loading")
        pd.show()

        // Post parameters
        val params = HashMap<String, String>()
        params["currentPassword"] = currentPassword
        params["newPassword"] = newPassword
        val jsonObject = JSONObject(params as Map<*, *>)

        val url = StaticValues.URL_API_SERVER+"patients/changePassword/"
        val rq: RequestQueue = Volley.newRequestQueue(this)
        val jor = object: JsonObjectRequest(Request.Method.PUT, url, jsonObject, Response.Listener { response ->
            // Stop Processing Dialog
            pd.hide()

            if(response.getInt("status") == 200) {
                DialogCaller.positiveDialog(this,"Password Successfully Changed")
            }
            else{
                DialogCaller.positiveDialog(this,response.getString("message"))
            }
        }, Response.ErrorListener { error ->
            // Stop Processing Dialog
            pd.hide()

            DialogCaller.alertActionDialog(this,error.message,
                    DialogInterface.OnClickListener { _, _ ->
                        finish()
                    })
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val sharedPreference = getSharedPreferences(StaticValues.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                val xAuthToken = sharedPreference.getString(StaticValues.USER_PREF_X_AUTH_TOKEN,null)
                val headers = HashMap<String, String>()
                headers["x-auth-token"] = xAuthToken!!
                return headers
            }
        }
        rq.add(jor)
    }
}
