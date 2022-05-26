package com.optic.kotlinudemydelivery.models

import com.google.gson.annotations.SerializedName

class Cardholder(
    @SerializedName("identification") val identification: Identification,
    @SerializedName("name") val name: String
) {

    override fun toString(): String {
        return "CardHolder(identification=$identification, name='$name')"
    }
}