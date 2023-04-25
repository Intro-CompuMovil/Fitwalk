package com.example.proyectocm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Blog : AppCompatActivity() {
    private val PERMISSION_REQUEST_CAMERA = 1 // Identificador para la solicitud de permiso de la cámara
    private val REQUEST_IMAGE_CAPTURE = 2 // Identificador para la solicitud de captura de imagen
    private val REQUEST_WRITE_EXTERNAL_STORAGE =3 // Identificador para guardar la imagen
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blog)
    }

    fun mapa(view: View){
        val intent = Intent(this,MapsActivity::class.java)
        startActivity(intent)
    }

    fun dashboard(view: View){
        val intent = Intent(this,Dashboard::class.java)
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

    fun post(view: View){
        // Solicitar permiso para usar la cámara si aún no se ha concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        } else {
            // El permiso ya ha sido concedido
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE)
            } else {
                // El permiso ya ha sido concedido
                val intent = Intent(this, Post::class.java)
                startActivity(intent)
            }
        }
    }



}