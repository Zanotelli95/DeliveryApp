package com.example.delivery.activities.products.list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.adapters.ProductsAdapter
import com.example.delivery.models.Product
import com.example.delivery.models.User
import com.example.delivery.providers.ProductsProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientProductListActivity : AppCompatActivity() {
    var TAG = "ProductListActivity"
    var recyclerViewProducts: RecyclerView? = null
    var adapter: ProductsAdapter? = null
    var user: User? = null
    var productsProvider: ProductsProvider? = null
    var products: ArrayList<Product> = ArrayList()

    var sharedPref: SharedPref? = null

    var idCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_product_list)

        sharedPref = SharedPref(this)
        idCategory = intent.getStringExtra("idCategory")

        getUserFromSession()

        productsProvider = ProductsProvider(user?.sessionToken!!)

        recyclerViewProducts = findViewById(R.id.recycler_products)
        recyclerViewProducts?.layoutManager = GridLayoutManager(this, 2)

        getProduct()
    }


    private fun getUserFromSession(){
        val gson = Gson()
        if(!sharedPref?.getData("user").isNullOrBlank()){
            //si el usiario exsite en sesion
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun getProduct(){
        productsProvider?.findByCategory(idCategory!!)?.enqueue(object: Callback<ArrayList<Product>>{
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if (response.body() != null){
                    products = response.body()!!
                    adapter = ProductsAdapter(this@ClientProductListActivity, products)
                    recyclerViewProducts?.adapter = adapter
                }

            }

            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                Toast.makeText(this@ClientProductListActivity, t.message, Toast.LENGTH_LONG).show()
                Log.d(TAG, "Error: ${t.message}")
            }


        })
    }


}