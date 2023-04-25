package com.example.proyectocm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat

class Dashboard : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
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
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }
    }
}