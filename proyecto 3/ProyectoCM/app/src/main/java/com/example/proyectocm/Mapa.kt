package com.example.proyectocm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class Mapa : AppCompatActivity() , OnMapReadyCallback {
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 4
    //private lateinit var mLocationCallback: LocationCallback
    private lateinit var photoImageView: ImageView // Referencia al ImageView donde se mostrará la foto capturada
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var map:GoogleMap
    private lateinit var mapView: MapView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapa)
        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        auth = Firebase.auth
        var salir=findViewById<Button>(R.id.salir)
        salir.setOnClickListener {
            auth.signOut()
            val intent =
                android.content.Intent(this, com.example.proyectocm.InicioSesion::class.java)
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }

    fun dashboard(view: View){
        val intent = Intent(this,Dashboard::class.java)
        startActivity(intent)
    }

    fun blog(view: View){
        val intent = Intent(this,Blog::class.java)
        startActivity(intent)
    }

    fun chat(view: View){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            // aun no hay permiso preguntar
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS)
        }
        else
        {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (googleMap != null) {
            map = googleMap
            // Configura el mapa
        }
    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}