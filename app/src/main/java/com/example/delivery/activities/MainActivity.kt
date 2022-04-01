package com.example.delivery.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClienteHomeActivity
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.providers.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlin.math.log

class MainActivity : AppCompatActivity() {

    var imagenlogin: ImageView? = null
    var editTextEmail: EditText? = null
    var editTextPasword: EditText? = null
    var buttonLogin: Button? = null
    var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imagenlogin = findViewById(R.id.imagenloginboton)
        editTextEmail = findViewById(R.id.emailIniciarSesion)
        editTextPasword = findViewById(R.id.contraseñaIniciarSesion)
        buttonLogin = findViewById(R.id.buttonLogin)

        imagenlogin?.setOnClickListener {
            val i = Intent(this, registro::class.java)
            startActivity(i)
        }

        buttonLogin?.setOnClickListener {
            login()
        }

        getUserFromSession()

    }  //fin del onCreate




    private fun login() {
        val email = editTextEmail?.text.toString()
        val password = editTextPasword?.text.toString()

        if (isValidForm(email, password)) {

            usersProvider.login(email, password)?.enqueue(object: Callback<ResponseHttp> {
                    override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>
                    ) {

                        Log.d("MainActivity", "Response: ${response.body()}")

                        if (response.body()?.isSuccess == "true") {
                            Toast.makeText(this@MainActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                            saveUserInSession(response.body()?.data.toString())
                           // goToClientHome()

                        } else {
                            Toast.makeText(this@MainActivity, "Los datos no son correctos", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                        Log.d("MainActivity", "Hubo un error ${t.message}")
                        Toast.makeText(this@MainActivity, "Hubo un error $(t.message)", Toast.LENGTH_LONG).show()
                    }

                })

            //  Toast.makeText(this, "Ingresando...", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Email o contraseña no válidos", Toast.LENGTH_LONG).show()
        }

    }






    private fun getUserFromSession(){
        val sharedPref = SharedPref(this)
        val gson = Gson()

        if(!sharedPref.getData("user").isNullOrBlank()){
            //si el usuario existe en sesion
            val user = gson.fromJson(sharedPref.getData("user"), User::class.java)

            if(!sharedPref.getData("rol").isNullOrBlank()){
                //si el usuario selecciono el rol
                val rol = sharedPref.getData("rol")?.replace("\"","")
                Log.d("MainActivity", "ROL $rol")

                if(rol == "RESTAURANTE"){
                    goToRestaurantHome()
                } else if (rol == "CLIENTE"){
                    goToClientHome()
                } else if (rol == "REPARTIDOR"){
                    goToDeliveryHome()
                }
            } else {
                Log.d("MainActivity", "ROL NO EXISTE")
                goToClientHome()
            }


        }
    }





    private fun goToClientHome(){
        val i = Intent(this, ClienteHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun goToRestaurantHome(){
        val i = Intent(this, RestaurantHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun goToDeliveryHome(){
        val i = Intent(this, DeliveryHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun goToSelectRol(){
        val i = Intent(this, SelectRolesActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }



    private fun saveUserInSession(data: String){
            val sharedPref = SharedPref(this)
            val gson = Gson()
            val user = gson.fromJson(data, User::class.java)
            sharedPref.save("user", user)

        if (user.roles?.size!! > 1) { // TIENE MAS DE UN ROL
            goToSelectRol()
        }
        else { // SOLO UN ROL (CLIENTE)
            goToClientHome()
        }

    }






    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun isValidForm(email: String, password: String): Boolean {
        if (email.isBlank()){
            Toast.makeText(this, "Ingresa el email", Toast.LENGTH_LONG).show()
            return false
        }

        if(password.isBlank()){
            Toast.makeText(this, "Ingresa la contraseña", Toast.LENGTH_LONG).show()
            return false
        }

        if(!email.isEmailValid()){
            return false
        }

        return true
    }



}


