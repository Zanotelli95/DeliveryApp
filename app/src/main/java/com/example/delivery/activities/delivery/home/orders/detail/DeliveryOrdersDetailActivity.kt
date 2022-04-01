package com.example.delivery.activities.delivery.home.orders.detail


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.adapters.OrderProductsAdapter
import com.example.delivery.activities.delivery.home.orders.map.DeliveryOrdersMapActivity
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.models.Category
import com.example.delivery.models.Order
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.providers.OrdersProvider
import com.example.delivery.providers.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersDetailActivity : AppCompatActivity() {

    val TAG = "DeliveryOrdersDetail"
    var order: Order? = null
    val gson = Gson()

    var toolbar: Toolbar? = null
    var textViewClient: TextView? = null
    var textViewAddress: TextView? = null
    var textViewDate: TextView? = null
    var textViewTotal: TextView? = null
    var textViewStatus: TextView? = null

    var RecyclerViewProducts: RecyclerView? = null



    var adapter: OrderProductsAdapter? = null

    var usersProvider: UsersProvider? = null
    var ordersProvider: OrdersProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null


    var idDelivery = ""
    var buttonUpdate: Button? = null
    var buttonMap: Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_orders_detail)

        sharedPref = SharedPref(this)

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)


        getUserFromSession()

        usersProvider = UsersProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Order #${order?.id}"



        textViewClient = findViewById(R.id.textView_clientOrdersDetail)
        textViewAddress = findViewById(R.id.textView_direccionOrdersDetail)
        textViewDate = findViewById(R.id.textView_dateOrdersDetail)
        textViewTotal = findViewById(R.id.textView_totalOrdersDetail)

        textViewStatus = findViewById(R.id.textView_statusOrdersDetail)
        RecyclerViewProducts = findViewById(R.id.recyclerView_products)
        RecyclerViewProducts?.layoutManager = LinearLayoutManager(this)

        buttonUpdate = findViewById(R.id.button_go_to_map)




        adapter = OrderProductsAdapter(this, order?.products!!)
        RecyclerViewProducts?.adapter = adapter

        textViewClient?.text= "${order?.client?.name} ${order?.client?.lastname}"
        textViewAddress?.text= order?.address?.address
        textViewDate?.text= "${order?.timestamp}"
        textViewStatus?.text= order?.status


        Log.d(TAG, "Orden: ${order.toString()}")

        getTotal()


        if(order?.status == "DESPACHADO"){
            buttonUpdate?.visibility = View.VISIBLE
        }

        if(order?.status == "EN CAMINO"){
            buttonMap?.visibility = View.VISIBLE
        }



        buttonUpdate?.setOnClickListener { updateOrder()}

        buttonMap?.setOnClickListener { goToMap() }
    }



    private fun goToMap(){
        val i = Intent(this, DeliveryOrdersMapActivity::class.java)
        i.putExtra("order", order?.toJson())
        startActivity(i)
    }

    private fun updateOrder(){


        ordersProvider?.updateToOnTheWay(order!!)?.enqueue(object: Callback<ResponseHttp>{
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if(response.body() != null){
                    if(response.body()?.isSuccess == "true"){
                        goToMap()
                        Toast.makeText(this@DeliveryOrdersDetailActivity, "Entrega iniciada", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@DeliveryOrdersDetailActivity, "No se asigno el repartidor", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this@DeliveryOrdersDetailActivity, "No hubo respuesta del servidor", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@DeliveryOrdersDetailActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }


    private fun getUserFromSession(){
        val gson = Gson()
        if(!sharedPref?.getData("user").isNullOrBlank()){
            //si el usiario exsite en sesion
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }


    private fun getTotal(){
        var total = 0.0

        for(p in order?.products!!){
            total = total + (p.price * p.quantity!!)
        }
        textViewTotal?.text = "${total}$"

    }


}