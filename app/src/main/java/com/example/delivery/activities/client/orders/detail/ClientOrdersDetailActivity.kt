package com.example.delivery.activities.client.orders.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.adapters.OrderProductsAdapter
import com.example.delivery.activities.client.orders.map.ClientOrdersMapActivity
import com.example.delivery.activities.delivery.home.orders.map.DeliveryOrdersMapActivity
import com.example.delivery.models.Order
import com.google.gson.Gson

class ClientOrdersDetailActivity : AppCompatActivity() {

    val TAG = "ClientOrdersDetail"
    var order: Order? = null
    val gson = Gson()

    var toolbar: Toolbar? = null
    var textViewClient: TextView? = null
    var textViewAddress: TextView? = null
    var textViewDate: TextView? = null
    var textViewTotal: TextView? = null
    var textViewStatus: TextView? = null
    var RecyclerViewProducts: RecyclerView? = null

    var buttonGoToMap: Button? = null
    var adapter: OrderProductsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_orders_detail)

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)
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
        buttonGoToMap = findViewById(R.id.btn_goToMap)

        adapter = OrderProductsAdapter(this, order?.products!!)
        RecyclerViewProducts?.adapter = adapter

        textViewClient?.text= "${order?.client?.name} ${order?.client?.lastname}"
        textViewAddress?.text= order?.address?.address
        textViewDate?.text= "${order?.timestamp}"
        textViewStatus?.text= order?.status

        Log.d(TAG, "Orden: ${order.toString()}")

        getTotal()

        if(order?.status == "EN CAMINO"){
            buttonGoToMap?.visibility = View.VISIBLE
        }
        buttonGoToMap?.setOnClickListener {
                    goToMap()

        }

    }

    private fun getTotal(){
        var total = 0.0

        for(p in order?.products!!){
            total = total + (p.price * p.quantity!!)
        }
            textViewTotal?.text = "${total}$"

    }

    private fun goToMap(){
        val i = Intent(this, ClientOrdersMapActivity::class.java)
        i.putExtra("order", order?.toJson())
        startActivity(i)
    }


}