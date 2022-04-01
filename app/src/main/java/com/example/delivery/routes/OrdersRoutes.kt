package com.example.delivery.routes

import com.example.delivery.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface OrdersRoutes {


   @GET("orders/findByStatus/{status}")
    fun getOrdersByStatus(
        @Path("status") status: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Order>>

    @GET("orders/findByClientAndStatus/{id_client}/{status}")
    fun getOrdersByClientAndStatus(
        @Path("id_client") idClient: String,
        @Path("status") status: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Order>>

    @GET("orders/findByDeliveryAndStatus/{id_delivery}/{status}")
    fun getOrdersByDeliveryAndStatus(
        @Path("id_delivery") idDeliver: String,
        @Path("status") status: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Order>>



    @POST("orders/create")
    fun create (
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>



    @PUT("orders/updateToDispatched")
    fun updateToDispatched (
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>



    @PUT("orders/updateToOnTheWay")
    fun updateToOnTheWay (
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>


    @PUT("orders/updateToDelivered")
    fun updateToDelivery (
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>


    @PUT("orders/updateLatLng")
    fun updateLatLng (
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>



}