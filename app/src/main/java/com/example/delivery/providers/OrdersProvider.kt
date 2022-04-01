package com.example.delivery.providers

import android.util.Log
import com.example.delivery.api.ApiRoutes
import com.example.delivery.models.*
import com.example.delivery.routes.AddressRoutes
import com.example.delivery.routes.CategoriesRoutes
import com.example.delivery.routes.OrdersRoutes
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class OrdersProvider(val token: String) {

    private var ordersRoutes: OrdersRoutes? = null

    init {
        val api = ApiRoutes()
        ordersRoutes = api.getOrdersRoutes(token)
    }

   fun getOrdersByStatus(status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByStatus(status, token)
    }

    fun getOrdersByClientAndStatus(idClient: String, status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByClientAndStatus(idClient, status, token)
    }

    fun getOrdersByDeliveryAndStatus(idDelivery: String, status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByDeliveryAndStatus(idDelivery, status, token)
    }



    fun create(order: Order): Call<ResponseHttp>?{
        return ordersRoutes?.create(order, token)
    }

    fun updateToDispatched(order: Order): Call<ResponseHttp>?{
        return ordersRoutes?.updateToDispatched(order, token)
    }

    fun updateToOnTheWay(order: Order): Call<ResponseHttp>?{
        return ordersRoutes?.updateToOnTheWay(order, token)
    }

    fun updateToDelivery(order: Order): Call<ResponseHttp>?{
        return ordersRoutes?.updateToDelivery(order, token)
    }

    fun updateLatLng(order: Order): Call<ResponseHttp>?{
        return ordersRoutes?.updateLatLng(order, token)
    }


}