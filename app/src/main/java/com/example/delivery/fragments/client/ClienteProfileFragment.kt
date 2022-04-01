package com.example.delivery.fragments.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.delivery.R
import com.example.delivery.activities.MainActivity
import com.example.delivery.activities.SelectRolesActivity
import com.example.delivery.activities.client.update.ClientUpdateActivity
import com.example.delivery.models.User
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView


class ClienteProfileFragment : Fragment() {

    var myView: View? = null
    var buttonSelectRol: Button? = null
    var buttonUpdateProfile: Button? = null
    var circleImageUser: CircleImageView? = null
    var textViewName: TextView? = null
    var textViewEmail: TextView? = null
    var textViewTelefono: TextView? = null
    var sharedPref: SharedPref? = null
    var user: User? = null
    var imageViewLogout: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_cliente_profile, container, false)

        sharedPref = SharedPref(requireActivity())
        buttonUpdateProfile = myView?.findViewById<Button>(R.id.button_update_profile)
        buttonSelectRol = myView?.findViewById<Button>(R.id.button_select_rol)
        circleImageUser = myView?.findViewById(R.id.imagenCircularPerfil)
        textViewEmail = myView?.findViewById(R.id.emailUsuario)
        textViewName = myView?.findViewById(R.id.nombreUsuario)
        textViewTelefono = myView?.findViewById(R.id.telefonoUsuario)
        imageViewLogout = myView?.findViewById(R.id.imageView_logout)
        buttonSelectRol?.setOnClickListener { goToSelectRol() }

        getUserFromSession()

        textViewName?.text = "${user?.name} ${user?.lastname}"
        textViewEmail?.text = user?.email
        textViewTelefono?.text  = user?.phone

        imageViewLogout?.setOnClickListener { logout() }
        buttonUpdateProfile?.setOnClickListener { goToUpdate() }

        if(!user?.image.isNullOrBlank()){
            Glide.with(requireContext()).load(user?.image).into(circleImageUser!!)
        }


        return myView
    }

    private fun logout(){
        sharedPref?.remove("user")
        val i = Intent(requireContext(), MainActivity::class.java)
        startActivity(i)
    }

    private fun goToSelectRol(){
        val i = Intent(requireContext(), SelectRolesActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun goToUpdate(){
        val i = Intent(requireContext(), ClientUpdateActivity::class.java)
        startActivity(i)
    }

    private fun getUserFromSession(){

        val gson = Gson()

        if(!sharedPref?.getData("user").isNullOrBlank()){
            //si el usiario exsite en sesion
             user = gson.fromJson(sharedPref?.getData("user"), User::class.java)

        }
    }


}