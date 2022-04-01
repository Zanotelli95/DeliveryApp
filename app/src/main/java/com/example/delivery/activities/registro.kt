package com.example.delivery.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClienteHomeActivity
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.providers.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class registro : AppCompatActivity() {

    val TAG = "registro"

    var imagenRegistro: ImageView? = null
    var nombre: EditText? = null
    var apellido: EditText? = null
    var email: EditText? = null
    var telefono: EditText? = null
    var contraseña: EditText? = null
    var confirmar: EditText? = null
    var botonRegistro: Button? = null

    var usersProvider = UsersProvider()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        imagenRegistro = findViewById(R.id.imagenRegistro)
        nombre = findViewById(R.id.nombreFormulario)
        apellido = findViewById(R.id.apellidoFormulario)
        email = findViewById(R.id.EmailFormulario)
        telefono = findViewById(R.id.TelefonoFormulario)
        contraseña = findViewById(R.id.ContraseñaFormulario)
        confirmar = findViewById(R.id.ConfirmarContraseñaFormulario)
        botonRegistro = findViewById(R.id.buttonRegistro)
        

        imagenRegistro?.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        botonRegistro?.setOnClickListener{
            registro()
        }

    } //fin del onCreate


   private fun registro() {
        val email = email?.text.toString()
        val contraseña = contraseña?.text.toString()
       val confirmar = confirmar?.text.toString()
       val telefono = telefono?.text.toString()
       val apellido = apellido?.text.toString()
       val nombre = nombre?.text.toString()


       if(isValidForm(nombre = nombre, apellido = apellido, telefono = telefono, confirmar = confirmar, contraseña = contraseña, email = email)){

           val user = User(
               name = nombre,
               lastname = apellido,
               phone = telefono,
               password = contraseña,
               email = email,
           )

           usersProvider.register(user)?.enqueue(object: Callback<ResponseHttp> {
               override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                if(response.body()?.isSuccess == "true"){
                    saveUserInSession(response.body()?.data.toString())
                    goToClientHome()

                }


               }

               override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Se produjo un error")
                   Toast.makeText(applicationContext,"Error", Toast.LENGTH_LONG).show()
               }

           })
       }




    }


    private fun saveUserInSession(data: String){
        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }



    private fun goToClientHome(){
        val i = Intent(this,  SaveImageActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }




    private fun isValidForm(email: String, contraseña: String, nombre: String, apellido: String, telefono: String, confirmar: String): Boolean {
        if (email.isBlank()){
            Toast.makeText(this, "Completa todos los datos correctamente", Toast.LENGTH_LONG).show()
            return false
        }

        if(contraseña.isBlank()){
            Toast.makeText(this, "Completa todos los datos correctamente", Toast.LENGTH_LONG).show()
            return false
        }

        if(nombre.isBlank()){
            Toast.makeText(this, "Completa todos los datos correctamente", Toast.LENGTH_LONG).show()
            return false
        }

        if(apellido.isBlank()){
            Toast.makeText(this, "Completa todos los datos correctamente", Toast.LENGTH_LONG).show()
            return false
        }

        if(telefono.isBlank()){
            Toast.makeText(this, "Completa todos los datos correctamente", Toast.LENGTH_LONG).show()
            return false
        }

        if(confirmar.isBlank()){
            Toast.makeText(this, "Completa todos los datos correctamente", Toast.LENGTH_LONG).show()
            return false
        }

        if (contraseña != confirmar){
            Toast.makeText(this, "Completa todos los datos correctamente", Toast.LENGTH_LONG).show()
            return false
        }

        if(!email.isEmailValid()){
            Toast.makeText(this, "Ingresa un email válido", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }



}