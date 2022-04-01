package com.example.delivery.activities.client.update

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.delivery.R
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.providers.UsersProvider
import com.example.delivery.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ClientUpdateActivity : AppCompatActivity() {

    var TAG = "ClientUpdateActivity"
    var circleImageUser: ImageView? = null
    var editTextName: EditText? = null
    var editTextLastname: EditText? = null
    var editTextPhone: EditText? = null
    var buttonUpdate: Button? = null

    private var imageFile: File? = null
    var usersProvider: UsersProvider? = null

    var sharedPref: SharedPref? = null
    var user: User? = null

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_update)

        sharedPref = SharedPref(this)

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Editar perfil"
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    circleImageUser = findViewById(R.id.imagenCircularPerfilUpdate)
        editTextLastname = findViewById(R.id.apellidoFormularioUpdate)
        editTextName = findViewById(R.id.nombreFormularioUpdate)
        buttonUpdate = findViewById(R.id.buttonRegistroUpdate)
        editTextPhone = findViewById(R.id.TelefonoFormularioUpdate)

        buttonUpdate?.setOnClickListener { updateData() }
        circleImageUser?.setOnClickListener { selectImage() }

        getUserFromSession()

        usersProvider = UsersProvider(user?.sessionToken)

        editTextName?.setText(user?.name)
        editTextLastname?.setText(user?.lastname)
        editTextPhone?.setText(user?.phone)

        if(!user?.image.isNullOrBlank()){
            Glide.with(this).load(user?.image).into(circleImageUser!!)
        }

    }

    private fun getUserFromSession(){
        val gson = Gson()
        if(!sharedPref?.getData("user").isNullOrBlank()){
            //si el usiario exsite en sesion
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }


    private fun updateData(){

        val telefono = editTextPhone?.text.toString()
        val apellido = editTextLastname?.text.toString()
        val nombre = editTextName?.text.toString()

        user?.name = nombre
        user?.lastname = apellido
        user?.phone = telefono

        if(imageFile != null){

            usersProvider?.update(imageFile!!, user!!)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    Log.d(TAG, "RESPONSE: $response")
                    Log.d(TAG, "BODY: ${response.body()}")

                    Toast.makeText(this@ClientUpdateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                    if (response.body()?.isSuccess == "true"){
                        saveUserInSession(response.body()?.data.toString())
                    }



                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        } else {
            usersProvider?.updateWithoutImage(user!!)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    Log.d(TAG, "RESPONSE: $response")
                    Log.d(TAG, "BODY: ${response.body()}")

                    Toast.makeText(this@ClientUpdateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                    if (response.body()?.isSuccess == "true"){
                        saveUserInSession(response.body()?.data.toString())
                    }

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }




    }

    private fun saveUserInSession(data: String) {

        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref?.save("user", user)


    }

    private val startImageForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->

        val resultCode = result.resultCode
        val data = result.data

        if(resultCode == Activity.RESULT_OK){
            val fileUri = data?.data
            imageFile = File(fileUri?.path) //arhivo que se guarda en el servidor
            circleImageUser?.setImageURI(fileUri)
        }
        else if(resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Tarea se cancelo", Toast.LENGTH_LONG).show()
        }

    }


    private fun selectImage() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)

            }
    }


}