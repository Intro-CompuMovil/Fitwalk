package com.example.proyectocm

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Post : AppCompatActivity() {

    private val CODIGO_SELECCIONAR_IMAGEN = 1
    private val CODIGO_TOMAR_FOTO = 2

    private lateinit var foto: ImageView
    private lateinit var rutaImagen: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post)
        foto = findViewById<ImageView>(R.id.imageView)
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
        val intent =Intent(this,Blog::class.java)
        startActivity(intent)
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
            "com.example.ProyectoCM.fileprovider",
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