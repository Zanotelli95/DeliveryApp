package com.example.delivery.activities.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.delivery.fragments.delivery.DeliveryOrdersStatusFragment

class DeliveryTabsPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    var numberOfTabs: Int
): FragmentStateAdapter(fragmentManager, lifecycle)
{

    override fun getItemCount(): Int {
        return numberOfTabs
    }

    override fun createFragment(position: Int): Fragment {
     when(position){
         0 -> {
             val bundle = Bundle()
             bundle.putString("status", "DESPACHADO")
             val f = DeliveryOrdersStatusFragment()
             f.arguments = bundle
             return f

         }
         1 -> {
             val bundle = Bundle()
             bundle.putString("status", "EN CAMINO")
             val f = DeliveryOrdersStatusFragment()
             f.arguments = bundle
             return f

         }
         2 -> {
             val bundle = Bundle()
             bundle.putString("status", "ENTREGADO")
             val f = DeliveryOrdersStatusFragment()
             f.arguments = bundle
             return f

         }
         else -> return DeliveryOrdersStatusFragment()
     }

    }

}