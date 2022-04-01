package com.example.delivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class Address(
    @SerializedName("id") val id: String? = null,
    @SerializedName("id_user") val idUser: String,
    @SerializedName("address") val address: String,
    @SerializedName("neighborhood") val neighborhood: String? = null,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,

) {

    fun toJson(): String {
        return Gson().toJson(this)
    }

    override fun toString(): String {
        return "Address(id=$id, idUser='$idUser', address='$address', neighborhood=$neighborhood, lat=$lat, lng=$lng)"
    }
}