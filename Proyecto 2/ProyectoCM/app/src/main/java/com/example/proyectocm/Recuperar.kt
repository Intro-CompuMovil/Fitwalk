package com.example.proyectocm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Recuperar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recuperar)
    }

    fun guardar(view: View){
        val intent = Intent (this, InicioSesion::class.java)
        startActivity(intent)

        /*
        Firebase
         */
    }
}