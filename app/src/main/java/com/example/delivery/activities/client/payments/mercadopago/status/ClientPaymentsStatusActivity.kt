package com.example.delivery.activities.client.payments.status

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClienteHomeActivity
import de.hdodenhof.circleimageview.CircleImageView


class ClientPaymentsStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_payments_status)

        var textViewStatus: TextView? = null
        var circleImageStatus: CircleImageView? = null
        var buttonFinish: Button? = null

        var paymentMethodId = ""
        var paymentStatus = ""
        var lastFourDigits = ""

        textViewStatus = findViewById(R.id.textview_status)
        circleImageStatus = findViewById(R.id.circleimage_status)
        buttonFinish = findViewById(R.id.btn_finish)

        paymentMethodId = intent.getStringExtra("paymentMethodId").toString()
        paymentStatus = intent.getStringExtra("paymentStatus").toString()
        lastFourDigits = intent.getStringExtra("lastFourDigits").toString()

        if (paymentStatus == "approved") {
            circleImageStatus?.setImageResource(R.drawable.ic_baseline_check_24)
            textViewStatus?.text = "Tu orden fue procesada exitosamente usando ( $paymentMethodId **** $lastFourDigits ) \n\nMira el estado de tu compra en la seccion de Mis Pedidos"
        }
        else {
            circleImageStatus?.setImageResource(R.drawable.ic_baseline_cancel_24)
            textViewStatus?.text = "Hubo un error procesando el pago"
        }

        buttonFinish?.setOnClickListener { goToHome() }
    }

    private fun goToHome() {
        val i = Intent(this, ClienteHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }
}