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


    fun Ingresar(view: View) {
        val permissionsList = mutableListOf<String>()

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsList.isNotEmpty()) {
            val permissionsArray = permissionsList.toTypedArray()
            ActivityCompat.requestPermissions(
                this,
                permissionsArray,
                PERMISSIONS_REQUEST_ACCESS_LOCATION
            )
        } else {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_LOCATION) {
            val grantedPermissions = mutableListOf<String>()
            val deniedPermissions = mutableListOf<String>()

            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissions.add(permission)
                } else {
                    deniedPermissions.add(permission)
                }
            }

            if (grantedPermissions.isNotEmpty()) {
                // Los permisos han sido otorgados
                // Realizar acciones adicionales si es necesario
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }

            if (deniedPermissions.isNotEmpty()) {
                // Los permisos han sido denegados
                // Puedes mostrar un mensaje o tomar otras acciones en consecuencia
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
