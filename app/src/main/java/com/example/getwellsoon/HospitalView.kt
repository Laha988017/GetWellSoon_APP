package com.example.getwellsoon

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_hospital_view.*


class HospitalView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_view)

        //Get NavigationView And Drawer Layout
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Add Back Button on toolbar
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.title = "Hospitals"

        val myWebView: WebView = findViewById(R.id.webView)
        webView.getSettings().setJavaScriptEnabled(true);
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        myWebView.loadUrl("https://laha988017.github.io/HospitalList"+intent.getStringExtra("path"))
    }

}
