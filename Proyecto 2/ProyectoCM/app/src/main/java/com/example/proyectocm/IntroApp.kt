package com.example.proyectocm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class IntroApp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_app)
    }

    fun iniciar(view: View){
        val intent = Intent(this, InicioSesion::class.java)
        startActivity(intent)
    }
}