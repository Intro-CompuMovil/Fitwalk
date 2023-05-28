package com.example.proyectocm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocm.adapter.PostAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class Blog : AppCompatActivity() {
    private val PERMISSION_REQUEST_CAMERA = 1 // Identificador para la solicitud de permiso de la cámara
    private val REQUEST_IMAGE_CAPTURE = 2 // Identificador para la solicitud de captura de imagen
    private val REQUEST_WRITE_EXTERNAL_STORAGE =3 // Identificador para guardar la imagen
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 4
    private lateinit var auth: FirebaseAuth


    private lateinit var database: DatabaseReference
    private lateinit var Mutalblelista:MutableList<datosPost>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blog)
        auth = Firebase.auth
        var salir=findViewById<Button>(R.id.salir)
        salir.setOnClickListener {
            auth.signOut()
            val intent =
                android.content.Intent(this, com.example.proyectocm.InicioSesion::class.java)
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        database = Firebase.database.reference
        val storage = Firebase.storage
        database.child("Post").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Mutalblelista= mutableListOf<datosPost>()
                for (data in snapshot.children) {
                    val titu = data.child("titulo").getValue(String::class.java) ?: ""
                    val desc = data.child("descripcion").getValue(String::class.java) ?: ""
                    val id = data.child("id").getValue(String::class.java) ?: ""

                    val postAux = datosPost(titu, desc,id)
                    Mutalblelista.add(postAux)

                }

                // Actualizar la UI con la nueva lista
                initRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Fallo la lectura: ${error.toException()}")
            }
        })


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
    fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerUsuario)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PostAdapter(Mutalblelista.toList(), this)

    }




}