package com.example.delivery.providers

import android.util.Log
import com.example.delivery.api.ApiRoutes
import com.example.delivery.models.Address
import com.example.delivery.models.Category
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.routes.AddressRoutes
import com.example.delivery.routes.CategoriesRoutes
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class AddressProvider(val token: String) {

    private var addressRoutes: AddressRoutes? = null

    init {
        val api = ApiRoutes()
        addressRoutes = api.getAddressRoutes(token)

    }

   fun getAddress(idUser: String): Call<ArrayList<Address>>? {
        return addressRoutes?.getAddress(idUser, token)
    }



    fun create(address: Address): Call<ResponseHttp>?{
        return addressRoutes?.create(address, token)
    }


}