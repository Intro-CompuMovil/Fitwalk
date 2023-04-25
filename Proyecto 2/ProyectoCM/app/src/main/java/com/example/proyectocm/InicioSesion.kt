package com.example.proyectocm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class InicioSesion : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_ACCESS_LOCATION = 5 //id localizacion
    private val REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_sesion)

        val prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        if (prefs.getBoolean("primeraEjecucion", true)) {
            // La aplicación se está ejecutando por primera vez
            // Inicie la actividad deseada aquí
            val intent = Intent(this@InicioSesion, IntroApp::class.java)
            startActivity(intent)

            // Marque la aplicación como ya ejecutada
            val editor = prefs.edit()
            editor.putBoolean("primeraEjecucion", false)
            editor.apply()
        }

    }

    /*fun Ingresar(view: View){
        val intent = Intent(this,Mapa::class.java)
        startActivity(intent)

    }*/
    fun Ingresar(view: View){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_LOCATION)
        } else //si tiene permiso usar la ubicacion
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            } else {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }

        /*
        firebase
         */
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Si el permiso es concedido, realizar la acción deseada
                    // Por ejemplo, acceder a los contactos
                } else {
                    Toast.makeText(applicationContext,"Se necesita para el funcionamiento de la app", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    fun Registrarse(view: View){
        val intent = Intent(this, Registrar::class.java)
        startActivity(intent)
    }

    fun Recuperar(view: View){
        val intent = Intent(this,Recuperar::class.java)
        startActivity(intent)
        /*

        Firebase= codigo correo electronico
         */
    }

}