package com.example.proyectocm.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocm.R
//import com.example.proyectocm.RastreoActivity
import com.example.proyectocm.datosPost
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PostViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {

    val foto = view.findViewById<ImageView>(R.id.post_image)
    val titulo = view.findViewById<TextView>(R.id.titulo)
    val desc = view.findViewById<TextView>(R.id.descpripcion)


    fun render(postModel: datosPost)
    {
        titulo.text =postModel.titulo
        desc.text="Descripción: \n"+postModel.descripcion
        val id =postModel.id


        val storage= FirebaseStorage.getInstance().reference.child("imagenes/$id")
        val localfile = File.createTempFile("TempImage","jpeg")
        storage.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            foto.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(context, "no se pudo subir la imagen", Toast.LENGTH_SHORT).show()
        }


        /*rastreo.setOnClickListener(){

            println( "lat:  " +usuarioModel.Latitud.toString()+" lon: "+
            usuarioModel.Longitud.toString() )
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("latitud",usuarioModel.Latitud)
            intent.putExtra("longitud",usuarioModel.Longitud)
            intent.putExtra("persona",persona)
            intent.putExtra("id",id)
            context.startActivity(intent)
        }*/
    }
}