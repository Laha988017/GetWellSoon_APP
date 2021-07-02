package com.example.getwellsoon.models

import com.google.gson.annotations.SerializedName

data class User_patient(
    @SerializedName("imageBase64")
    var imageBase64: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("contactNumber")
    var contactNumber: String? = null,
    @SerializedName("aadharNo")
    var aadharNo: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("alergicTo")
    var alergicTo: String?= null,
    @SerializedName("medicalHistory")
    var medicalHistory: String?= null,
    @SerializedName("bloodGroup")
    var bloodGroup: String?= null,
    @SerializedName("ailment")
    var ailment: String?= null,
    @SerializedName("isCovidPositive")
    var isCovidPositive: Boolean?= false
) {}