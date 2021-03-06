package com.example.delivery.activities.delivery.home


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.delivery.R
import com.example.delivery.activities.MainActivity
import com.example.delivery.fragments.client.ClientCategoriesFragment
import com.example.delivery.fragments.client.ClientOrdersFragment
import com.example.delivery.fragments.client.ClienteProfileFragment
import com.example.delivery.fragments.delivery.DeliveryOrdersFragment
import com.example.delivery.fragments.restaurant.RestaurantCategoryFragment
import com.example.delivery.fragments.restaurant.RestaurantOrdersFragment
import com.example.delivery.fragments.restaurant.RestaurantProductFragment
import com.example.delivery.models.User
import com.example.delivery.utils.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.gson.Gson

class DeliveryHomeActivity : AppCompatActivity() {

    private val TAG = "DeliveryHomeActivity"
    var buttonLogout: Button? = null
    var sharedPref: SharedPref? = null

    var bottomNavigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_home)

        openFragment(DeliveryOrdersFragment())

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation?.setOnItemSelectedListener {

            when(it.itemId){
                R.id.item_orders -> {
                    openFragment(DeliveryOrdersFragment())
                    true
                }

                R.id.item_profile -> {
                    openFragment(ClienteProfileFragment())
                    true
                }


                else -> false
            }



        }

        sharedPref = SharedPref(this)

        //       buttonLogout = findViewById(R.id.buttonCerrarSesion)
        //    buttonLogout?.setOnClickListener{
        //       logout()
        //   }

        getUserFromSession()
    }

    private fun openFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun logout(){
        sharedPref?.remove("user")
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun getUserFromSession(){

        val gson = Gson()

        if(!sharedPref?.getData("user").isNullOrBlank()){
            //si el usiario exsite en sesion
            val user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
            Log.d(TAG, "Usuario: $user")
        }
    }
}