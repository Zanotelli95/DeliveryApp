package com.example.delivery.activities.client.orders.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.bumptech.glide.Glide
import com.example.delivery.R
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.models.Order
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.SocketEmit
import com.example.delivery.models.User
import com.example.delivery.providers.OrdersProvider
import com.example.delivery.utils.SharedPref
import com.example.delivery.utils.SocketHandler
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.maps.route.extensions.drawRouteOnMap
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class ClientOrdersMapActivity : AppCompatActivity(), OnMapReadyCallback{

    var googleMap: GoogleMap? = null
    var TAG = "ClientOrdersMap"

    val PERMISSION_ID = 42
    var fusedLocationClient:  FusedLocationProviderClient? = null

    var city = ""
    var country = ""
    var address = ""
    var addressLatLong: LatLng? = null

    var markerDelivery: Marker? = null
    var markerAddress: Marker? = null
    var mylocationLatlng: LatLng? = null
    var deliveryLatLng: LatLng? = null
    var order: Order? = null
    var gson = Gson()

    var textViewClient: TextView? = null
    var textViewAddress: TextView? = null
    var textViewVecindario: TextView? = null

    var circleImageUser: CircleImageView? = null
    var imageViewPhone: ImageView? = null
    val REQUEST_PHONE_CALL = 30

    var user: User? = null
    var sharedPref: SharedPref? = null
    var ordersProvider: OrdersProvider? = null

    var socket: Socket? = null



    private  val locationCallback = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            //localizacion en tiempo real
            var lastLocation = locationResult.lastLocation
            mylocationLatlng = LatLng(lastLocation.latitude, lastLocation.longitude)

          //  removeDeliveryMarket()
           // addDeliveryMarker(deliveryLatLng?.latitude!!, deliveryLatLng?.longitude!!)

            Log.d("LOCALIZACION", "Callback: $lastLocation")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_orders_map)

        sharedPref = SharedPref(this)

        getUserFromSession()

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

        if(order?.lat != null && order?.lng != null){
            deliveryLatLng = LatLng(order?.lat!!, order?.lng!!)
        }


        ordersProvider = OrdersProvider(user?.sessionToken!!)
        addressLatLong = LatLng(order?.address?.lat!!, order?.address?.lng!!)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync ( this )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        textViewClient = findViewById(R.id.textview_ClienteDeliveryOrdersMap)
        textViewAddress = findViewById(R.id.textview_adressDeliveryOrdersMap)
        textViewVecindario = findViewById(R.id.textview_vecindario)
        circleImageUser = findViewById(R.id.circleImage_user)
        imageViewPhone = findViewById(R.id.imageview_phone)

        getLastLocation()
        connectSocket()


        textViewClient?.text = "${order?.delivery?.name} ${order?.delivery?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewVecindario?.text = order?.address?.neighborhood

        if(!order?.client?.image.isNullOrBlank()){
            Glide.with(this).load(order?.client?.image).into(circleImageUser!!)
        }





        imageViewPhone?.setOnClickListener {

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
            } else {
                call()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if(locationCallback != null && fusedLocationClient != null){
            fusedLocationClient?.removeLocationUpdates(locationCallback)
        }

      socket?.disconnect()
    }


    private fun goToHome(){
        val i = Intent(this, DeliveryHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }


    private fun connectSocket(){
        SocketHandler.setSocket()
        socket = SocketHandler.getSocket()
        socket?.connect()

        socket?.on("position/${order?.id}") {args ->
         if (args[0] != null){
             runOnUiThread {
                 val data = gson.fromJson(args[0].toString(), SocketEmit::class.java)
                 removeDeliveryMarket()
                 addDeliveryMarker(data.lat, data.lng)
             }
         }

        }
    }




    private fun call(){
        val i = Intent(Intent.ACTION_CALL)
        i.data = Uri.parse("tel:${order?.delivery?.phone}")

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "permido denegado", Toast.LENGTH_LONG).show()
            return
        }
        startActivity(i)
    }

    private fun removeDeliveryMarket(){
        markerDelivery?.remove()
    }

    private fun drawRoute(){

        if(deliveryLatLng != null){
            val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)


            googleMap?.drawRouteOnMap(
                getString(R.string.google_map_api_key),
                source = deliveryLatLng!!,
                destination = addressLocation,
                context = this,
                color = Color.BLACK,
                polygonWidth = 10,
                markers = false
            )
        }


    }

    private fun addDeliveryMarker(lat: Double, lng: Double){
        val location = LatLng(lat, lng)

            markerDelivery = googleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Posición del repartidor")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.deliverytres))
            )


    }

    private fun addAddressMarker(){
        val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)
        markerAddress = googleMap?.addMarker(
            MarkerOptions().position(addressLocation)
                .title("Entregar aqui").icon(BitmapDescriptorFactory.fromResource(R.drawable.homeredimensionado))
        )
    }

    private fun goToCreateAddress(){
        val i = Intent()
        i.putExtra("city", city)
        i.putExtra("address", address)
        i.putExtra("country", country)
        i.putExtra("lat", addressLatLong?.latitude)
        i.putExtra("lng", addressLatLong?.longitude)
        setResult(RESULT_OK, i)
        finish()

    }



    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
    }

    private fun isLocationEnabled(): Boolean{
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getLastLocation(){
        if (checkPermissions()){

            if(isLocationEnabled()){


                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    //obtiene la localizacion una sola vez

                    var location = task.result

                    if(location != null){

                        mylocationLatlng = LatLng(location.latitude, location.longitude)
                        removeDeliveryMarket()
                        addDeliveryMarker(deliveryLatLng?.latitude!!, deliveryLatLng?.longitude!!)
                        addAddressMarker()
                        drawRoute()

                        if(deliveryLatLng != null){
                            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder().target(
                                    LatLng(deliveryLatLng?.latitude!!, deliveryLatLng?.longitude!!)
                                ).zoom(15f).build()
                            ))
                        }


                    }


                }
            } else {
                Toast.makeText(this, "Habilita la locación", Toast.LENGTH_SHORT).show()
                val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(i)
            }

        }
        else {
            requestPermission()
        }
    }

    private fun requestNewLocationData(){
        val locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()) //inicializa la posicion en tiempo real
    }

    private fun checkPermissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun getUserFromSession(){
        val gson = Gson()
        if(!sharedPref?.getData("user").isNullOrBlank()){
            //si el usiario exsite en sesion
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }
        }

        if(requestCode == REQUEST_PHONE_CALL){
            call()
        }


    }


}