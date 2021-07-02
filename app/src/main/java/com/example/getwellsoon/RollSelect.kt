package com.example.getwellsoon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class RollSelect() : AppCompatActivity() ,  AdapterView.OnItemSelectedListener{

    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll_select)

        spinner = findViewById<Spinner>(R.id.spinner)
        ArrayAdapter.createFromResource(
                this,
                R.array.role,
                android.R.layout.simple_spinner_item
        ).also{
            adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val select: String = parent?.getItemAtPosition(position).toString()
        val nextBtn: Button = findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener {
            if (select == "Patient") {
                val intent = Intent(this, PatientsLoginActivity::class.java)
                startActivity(intent)
            } else if (select == "Hospital") {
                val intent = Intent(this, HospitalRegistrationActivity::class.java)
                startActivity(intent)
            }
        }
    }

}