package com.example.delivery.activities.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClienteHomeActivity
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.models.Rol
import com.example.delivery.utils.SharedPref

class RolesAdapter(val context: Activity, val roles: ArrayList<Rol>):
    RecyclerView.Adapter<RolesAdapter.RolesViewHolder>() {

    val sharedPref = SharedPref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_roles, parent, false)
        return RolesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return roles.size
    }

    override fun onBindViewHolder(holder: RolesViewHolder, position: Int) {
        val rol = roles[position]

        holder.textViewRol.text = rol.name
        Glide.with(context).load(rol.image).into(holder.imageViewRol)

        holder.itemView.setOnClickListener{
            goToRol(rol)
        }
    }

    private fun goToRol(rol:Rol){
        if(rol.name == "RESTAURANTE"){

            sharedPref.save("rol", "RESTAURANTE")

            val i = Intent(context, RestaurantHomeActivity::class.java)
            context.startActivity(i)
        } else {
            if(rol.name == "CLIENTE"){

                sharedPref.save("rol", "CLIENTE")

                val i = Intent(context, ClienteHomeActivity::class.java)
                context.startActivity(i)
            } else {
                if(rol.name == "REPARTIDOR"){

                    sharedPref.save("rol", "REPARTIDOR")

                    val i = Intent(context, DeliveryHomeActivity::class.java)
                    context.startActivity(i)
                }
            }
        }
    }


    class RolesViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textViewRol: TextView
        val imageViewRol: ImageView

        init {
            textViewRol = view.findViewById(R.id.textViewRol)
            imageViewRol = view.findViewById(R.id.imageViewRol)
        }

    }




}