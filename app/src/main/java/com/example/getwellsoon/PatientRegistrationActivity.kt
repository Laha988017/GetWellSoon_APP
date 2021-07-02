package com.example.getwellsoon

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afollestad.vvalidator.form
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.getwellsoon.models.User_patient
import com.example.getwellsoon.models.User_patientSimple
import com.example.getwellsoon.staticClasses.DialogCaller
import com.example.getwellsoon.staticClasses.StaticValues
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor

class PatientRegistrationActivity : AppCompatActivity() {

    private var bitmap: Bitmap?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_registration)

        // Add Back Button on toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val passwordInput:EditText = findViewById(R.id.passwordInput)
        val passwordConfirmInput: EditText = findViewById(R.id.passwordConfirmInput)

        // Select Image
        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.imagePickerBtn).setOnClickListener {
            ImagePicker.with(this)
                .galleryMimeTypes(mimeTypes = arrayOf("image/jpg","image/jpeg"))
                .compress(50)
                .maxResultSize(360, 640)
                .start()
        }

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.title = "Patient Registration"

        // Register Button Click Form Submit
        form {
            input(R.id.nameInput) {
                isNotEmpty()
                length().atLeast(3)
            }
            input(R.id.contactInput) {
                isNotEmpty()
                length().exactly(10)
            }
            input(R.id.emailInput) {
                isNotEmpty()
                isEmail()
            }
            input(R.id.aadharNoInput) {
                isNotEmpty()
                length().exactly(12)
            }
            input(R.id.passwordInput) {
                isNotEmpty()
                length().atLeast(6)
            }
            input(R.id.passwordConfirmInput) {
                isNotEmpty()
                length().atLeast(6)
            }
            submitWith(R.id.registerBtn) {
                // Check Is Both Passwords Are Same
                if (passwordInput.text.toString() != passwordConfirmInput.text.toString()) {
                    DialogCaller.alertDialog(this@PatientRegistrationActivity, "Passwords Do Not Match")
                } else {
                    performRegistration()
                }
            }
        }
    }

    private fun performRegistration() {
        val userPatient = User_patientSimple()
        val patient = User_patient()

        //import form activity id
        val imageView = findViewById<ImageView>(R.id.imageView)
        val nameInput:EditText = findViewById(R.id.nameInput)
        val emailInput:EditText = findViewById(R.id.emailInput)
        val contactInput:EditText = findViewById(R.id.contactInput)
        val aadharNo:EditText = findViewById(R.id.aadharNoInput)
        val passwordInput:EditText = findViewById(R.id.passwordInput)

        //add imageBase64 to sharedPref
        val sharedPreference =  getSharedPreferences(StaticValues.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString(StaticValues.USER_PREF_IMAGE_BASE64,bitmap?.let{ bitmapToBase64(it) })
        editor.apply()

        //API Endpoint
        val url = StaticValues.URL_API_SERVER+"patients/"

        // Post parameters
        patient.imageBase64 = bitmap?.let{ bitmapToBase64(it) }
        patient.name = nameInput.text.toString()
        patient.email = emailInput.text.toString()
        patient.contactNumber = contactInput.text.toString()
        patient.aadharNo = aadharNo.text.toString()
        patient.password = passwordInput.text.toString()

        userPatient.imageBase64 = bitmap?.let{ bitmapToBase64(it) }
        userPatient.name = nameInput.text.toString()
        userPatient.email = emailInput.text.toString()
        userPatient.contactNumber = contactInput.text.toString()
        userPatient.aadharNo = aadharNo.text.toString()
        userPatient.password = passwordInput.text.toString()
        val requestJsonString = Gson().toJson(userPatient)



        // Show Processing Dialog
        val pd = ProgressDialog(this)
        pd.setMessage("loading")
        pd.show()

        val rq: RequestQueue = Volley.newRequestQueue(this)
        val jor = JsonObjectRequest(Request.Method.POST, url, JSONObject(requestJsonString), Response.Listener { response ->
            // Stop Processing Dialog
            pd.hide()

            // Handle Response Data
            if(response.getInt("status") == 200) {
                val data = response.getJSONObject("data")

                //Store User Information in shared Preference
                val sharedPreference =  getSharedPreferences(StaticValues.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putString(StaticValues.USER_PREF_NAME,data.getString("name"))
                editor.putString(StaticValues.USER_PREF_EMAIL_ID,data.getString("email"))
                editor.putBoolean(StaticValues.USER_PREF_NEW_REGISTRATION,false)
                editor.apply()

                // Goto Next Intent
                val nextIntent = Intent(this,PatientsLoginActivity::class.java)
                startActivity(nextIntent)

                finishAffinity()
            }
            else{
                DialogCaller.positiveDialog(this,response.getString("message"))
            }
        }, Response.ErrorListener { error ->
            // Stop Processing Dialog
            pd.hide()
            DialogCaller.alertDialog(this,error.message)
        })
        rq.add(jor)
    }

    private fun bitmapToBase64(image:Bitmap):String{
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            val qrCodeString = data?.extras?.getString("qrCodeString")
            if (qrCodeString != null){
                // Match QR With Regex Pattern
                val matched = Regex("([A-Za-z0-9]+)").matches(qrCodeString)

                if(matched){
                    Toast.makeText(this, qrCodeString, Toast.LENGTH_SHORT).show()
                }
                else{
                    DialogCaller.positiveDialog(this,"Scanned QR Is Not For Product!")
                }
            }
        }
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
                findViewById<ImageView>(R.id.imageView).setImageURI(fileUri)

                // Convert Image To Bitmap
                val parcelFileDescriptor = fileUri?.let { contentResolver.openFileDescriptor(it, "r") }
                val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}