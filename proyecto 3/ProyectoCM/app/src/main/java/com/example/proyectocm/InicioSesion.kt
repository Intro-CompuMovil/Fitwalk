package com.example.proyectocm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class InicioSesion : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_ACCESS_LOCATION = 5 //id localizacion
    private val REQUEST_CODE = 101
    private val PERMISSIONS_REQUEST_CAMERA = 200
    private val PERMISSIONS_REQUEST_STORAGE = 300
    private lateinit var auth: FirebaseAuth

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
        auth = Firebase.auth

        iniciar()


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de ubicación concedido, verifica los otros permisos
                val permissionsList = mutableListOf<String>()

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsList.add(Manifest.permission.CAMERA)
                }

                if (permissionsList.isNotEmpty()) {
                    val permissionsArray = permissionsList.toTypedArray()
                    ActivityCompat.requestPermissions(
                        this,
                        permissionsArray,
                        PERMISSIONS_REQUEST_STORAGE
                    )
                } else {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
            } else {
                // Permiso de ubicación denegado, muestra un mensaje o realiza alguna acción adicional si es necesario
            }
        } else if (requestCode == PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de almacenamiento concedido, verifica el permiso de cámara
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        PERMISSIONS_REQUEST_CAMERA
                    )
                } else {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
            } else {
                // Permiso de almacenamiento denegado, muestra un mensaje o realiza alguna acción adicional si es necesario
            }
        } else if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de cámara concedido, puedes lanzar la actividad de la cámara aquí
            } else {
                // Permiso de cámara denegado, muestra un mensaje o realiza alguna acción adicional si es necesario
            }
        }
    }



    fun Registrarse(view: View){
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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(Manifest.permission.CAMERA)
        }

        if (permissionsList.isNotEmpty()) {
            val permissionsArray = permissionsList.toTypedArray()
            ActivityCompat.requestPermissions(
                this,
                permissionsArray,
                PERMISSIONS_REQUEST_ACCESS_LOCATION
            )
        } else {
            val intent = Intent(this, Registrar::class.java)
            startActivity(intent)
        }

    }




    private fun iniciar(){

        //Botones
        val registrarse = findViewById<Button>(R.id.registroInicio)
        val home = findViewById<Button>(R.id.iniciarInicio)

        //Info
        val email = findViewById<EditText>(R.id.emailInicio)
        val password = findViewById<EditText>(R.id.passwordInicio)

        home.setOnClickListener{
            //si no tiene permiso preguntar
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

                    if(email.text.isNotEmpty() && password.text.isNotEmpty()){

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener(){
                            if(it.isSuccessful){
                                home(it.result?.user?.email ?:"")
                            }
                            else{
                                Toast.makeText(applicationContext,"Error en los datos", Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                    else{
                        Toast.makeText(applicationContext,"Llene todos los datos", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }

        registrarse.setOnClickListener{
            //si no tiene permiso preguntar
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

                    // Solicitar permiso para usar la cámara si aún no se ha concedido
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_CAMERA)
                    } else {
                        // El permiso ya ha sido concedido
                        val intent = Intent(this, Registrar::class.java)
                        startActivity(intent)

                    }
                }
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?){
        if(currentUser!=null){
            val intent = Intent(this,MapsActivity:: class.java)
            intent.putExtra("email",currentUser.email)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun home(email: String){
        val intent = Intent(this,MapsActivity:: class.java).apply {
            putExtra("email",email)
        }
        startActivity(intent)
    }


}