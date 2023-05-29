package com.example.proyectocm.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocm.R
import com.example.proyectocm.Usuario
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File

class UsuarioViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {

    val foto = view.findViewById<ImageView>(R.id.user_image)
    val nombre = view.findViewById<TextView>(R.id.user_name)
    val apellido = view.findViewById<TextView>(R.id.user_lastname)
    val correo = view.findViewById<TextView>(R.id.correo)


    fun render(usuarioModel: Usuario)
    {
        nombre.text =usuarioModel.Nombre
        apellido.text=usuarioModel.Apellido
        correo.text =usuarioModel.email

        val id =usuarioModel.Id

        val storage= FirebaseStorage.getInstance().reference.child("imagenes/$id")
        val localfile = File.createTempFile("TempImage","jpeg")
        storage.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            foto.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(context, "no se pudo subir la imagen", Toast.LENGTH_SHORT).show()
        }



    }
}