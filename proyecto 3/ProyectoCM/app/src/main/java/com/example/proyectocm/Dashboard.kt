package com.example.proyectocm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Dashboard : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 4
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

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

    fun mapa(view: View){
        val intent = Intent(this,MapsActivity::class.java)
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
            val intent = Intent(this, amigosActivity::class.java)
            startActivity(intent)
        }
    }
}