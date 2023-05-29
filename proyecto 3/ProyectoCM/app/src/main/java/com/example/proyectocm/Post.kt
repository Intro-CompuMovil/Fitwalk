package com.example.proyectocm

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocm.adapter.UsuarioAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Post : AppCompatActivity() {

    private val CODIGO_SELECCIONAR_IMAGEN = 1
    private val CODIGO_TOMAR_FOTO = 2

    private lateinit var foto: ImageView
    private lateinit var rutaImagen: Uri
    private lateinit var titulo: EditText
    private lateinit var desc: EditText
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post)
        foto = findViewById<ImageView>(R.id.imageView)
        titulo = findViewById<EditText>(R.id.tituloPostID)
        desc = findViewById<EditText>(R.id.bioPostID)


    }

    fun galeria(view: View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, CODIGO_SELECCIONAR_IMAGEN)
    }

    fun camara(view: View){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        rutaImagen = crearRutaImagen()
        intent.putExtra(MediaStore.EXTRA_OUTPUT, rutaImagen)
        startActivityForResult(intent, CODIGO_TOMAR_FOTO)
    }

    fun guardarPost(view: View){

        if (titulo.text.isNotEmpty() && desc.text.isNotEmpty() && foto.drawable != null && foto.drawable is BitmapDrawable && (foto.drawable as BitmapDrawable).bitmap != null) {
            database = FirebaseDatabase.getInstance().getReference("Post")

            //REALTIME DATABASE
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

            }
            var id =UUID.randomUUID().toString()

            val post = datosPost(
                titulo.text.toString(),
                desc.text.toString(),
                id
            )


            val nuevoNodo = database.push()
            nuevoNodo.setValue(post)
                .addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(applicationContext,"Post publicado correctamente", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(applicationContext,"Error al publicar el post", Toast.LENGTH_LONG).show()
                }
            /* database.child(userId.toString()).setValue(usuario)
         .addOnSuccessListener {
             nombre.text.clear()
             apellido.text.clear()
             identificacion.text.clear()

         }*/


            val storage = Firebase.storage
            val storageRef = storage.reference.child("imagenes/" + id)
            val drawable = foto.drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            storageRef.putBytes(data)
                .addOnSuccessListener { taskSnapshot ->
                    // El archivo se ha subido exitosamente
                }
                .addOnFailureListener { exception ->
                    // Error al subir el archivo
                }

            val intent =Intent(this,Blog::class.java)
            startActivity(intent)
        }
        else{
            Toast.makeText(applicationContext,"Llene todos los datos", Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CODIGO_SELECCIONAR_IMAGEN -> {
                    val imagenSeleccionada: Uri? = data?.data
                    try {
                        // Cargar la imagen en el ImageView
                        foto.setImageURI(imagenSeleccionada)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                CODIGO_TOMAR_FOTO -> {
                    // Cargar la imagen en el ImageView
                    foto.setImageURI(rutaImagen)
                }
            }
        } else {
            Log.e("TAG", "Error al obtener la imagen")
            Toast.makeText(this, "Error al obtener la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun crearRutaImagen(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nombreArchivoImagen = "JPEG_" + timeStamp + "_"
        val directorioAlmacenamiento: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val imagen = File.createTempFile(
            nombreArchivoImagen,
            ".jpg",
            directorioAlmacenamiento
        )
        return FileProvider.getUriForFile(
            this,
            "com.example.proyectocm.fileprovider",
            imagen
        )
    }

    private fun guardarImagenEnGaleria(bitmap: Bitmap) {
        val archivo = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "nombre_imagen.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, archivo)!!

        val stream = contentResolver.openOutputStream(uri)!!
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()

        Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
    }



}