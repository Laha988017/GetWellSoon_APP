package com.example.getwellsoon

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.Request.*
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.getwellsoon.staticClasses.DialogCaller
import com.example.getwellsoon.staticClasses.StaticValues
import com.google.android.material.navigation.NavigationView
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.activity_patient_dashboard.*

class PatientDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        // Load User Info From Shared Preference
        val sharedPreference =
                getSharedPreferences(StaticValues.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val name = sharedPreference.getString(StaticValues.USER_PREF_NAME, null)
        val userId = sharedPreference.getString(StaticValues.USER_PREF_EMAIL_ID, null)
        val imageBase64 = sharedPreference.getString(StaticValues.USER_PREF_IMAGE_BASE64,null)

        // Set Name And User Id String On Dashboard
        findViewById<TextView>(R.id.nameText).text = name
        findViewById<TextView>(R.id.userIdText).text = userId
        val userImage: CircularImageView = findViewById(R.id.userImage)

        //image load
        if (imageBase64 !=null) {
            val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            userImage.setImageBitmap(decodedImage)
        }


        //Get NavigationView And Drawer Layout
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        // Set toolbar action
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_drawer_menu)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // Set Action For Navigation Menu Option Selection
        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuMyProfile -> {
                    // Goto Login Intent
                    val nextIntent = Intent(this, PatientProfileActivity::class.java)
                    startActivity(nextIntent)
                }
                R.id.menuHelp -> {
                    // Goto Login Intent
                    //val nextIntent = Intent(this, HelpActivity::class.java)
                    //startActivity(nextIntent)
                }
                R.id.menuLogout -> {
                    // Clear SharedPreference
                    sharedPreference.edit().clear().apply()

                    // Goto Login Intent
                    val nextIntent = Intent(this, PatientsLoginActivity::class.java)
                    startActivity(nextIntent)
                    finishAffinity()
                }
            }
            drawerLayout.closeDrawers()
            return@OnNavigationItemSelectedListener true
        })

        // On Over Scroll Hide Background Dashboard Profile
        val dashboardNestedScrollview = findViewById<NestedScrollView>(R.id.dashboardNestedScrollview)
        val userInformationIndicator = findViewById<androidx.gridlayout.widget.GridLayout>(R.id.userInformationIndicator)
        dashboardNestedScrollview.viewTreeObserver.addOnScrollChangedListener {
            val scrollY: Int = dashboardNestedScrollview.scrollY
            if(scrollY/this.resources.displayMetrics.density >80){
                toolbar.title = "Get Well Soon"
                toolbar.subtitle = name
                userInformationIndicator.visibility = View.GONE
            }
            else{
                toolbar.title = ""
                toolbar.subtitle = ""
                userInformationIndicator.visibility = View.VISIBLE
            }
        }
        hospitalView.setOnClickListener{
            val nextIntent = Intent(this,HospitalView::class.java)
            nextIntent.putExtra("path","/hospitaldetails")
            startActivity(nextIntent)
        }
        callBtn.setOnClickListener{
            val nextIntent = Intent(this,HospitalView::class.java)
            nextIntent.putExtra("path","/contacts")
            startActivity(nextIntent)
        }
        noOfBeds.setOnClickListener {
            val nextIntent = Intent(this,HospitalView::class.java)
            nextIntent.putExtra("path","/bedDetails")
            startActivity(nextIntent)
        }
    }

    private fun convertString64ToImage(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}