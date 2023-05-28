package com.example.proyectocm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Registrar : AppCompatActivity() {


    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var database: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val CODIGO_SELECCIONAR_IMAGEN = 1
    private val CODIGO_TOMAR_FOTO = 2
    private val archivo = 9

    private lateinit var imagen: ImageView
    private lateinit var rutaImagen: Uri
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar)
        imagen = findViewById<ImageView>(R.id.imageView8)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        registrar()
    }


    fun registrarse(view: View){
        val intent= Intent (this, InicioSesion::class.java)
        startActivity(intent)

        auth = Firebase.auth


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        var lat=0.0
        var long=0.0
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            Log.i("LOCATION", "onSuccess location")
            if (location != null) {
                Log.i("LOCATION", "Longitud: " + location.longitude)
                Log.i("LOCATION", "Latitud: " + location.latitude)
            }

        }

    }

    private fun registrar(){

        //Botones
        val aceptar = findViewById<Button>(R.id.AceptarRegistro)
        val galeria = findViewById<Button>(R.id.galeriaRegistro)
        val camara = findViewById<Button>(R.id.camaraRegistro)

        //Info
        val email = findViewById<EditText>(R.id.emailRegistro)
        val password = findViewById<EditText>(R.id.passwordRegistro)
        val nombre = findViewById<EditText>(R.id.NombreRegistro)
        val apellido = findViewById<EditText>(R.id.ApellidoRegistro)
        val identificacion = findViewById<EditText>(R.id.IdentificacionRegistro)


        imagen = findViewById<ImageView>(R.id.imageView8)


        aceptar.setOnClickListener{

            if(email.text.isNotEmpty() && password.text.isNotEmpty() && nombre.text.isNotEmpty() &&
                apellido.text.isNotEmpty() && identificacion.text.isNotEmpty()  && imagen.drawable != null && imagen.drawable is BitmapDrawable && (imagen.drawable as BitmapDrawable).bitmap != null){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener(){
                    if(it.isSuccessful){

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
                        mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                            Log.i("LOCATION", "onSuccess location")
                            if (location != null) {
                                Log.i("LOCATION", "Longitud: " + location.longitude)
                                Log.i("LOCATION", "Latitud: " + location.latitude)
                            }


                            database = FirebaseDatabase.getInstance().getReference("Usuarios")
                            val usuario = Usuario(
                                nombre.text.toString(),
                                apellido.text.toString(),
                                identificacion.text.toString(),
                                location.latitude,
                                location.longitude,
                                (listOf(""))
                            )
                            val user = FirebaseAuth.getInstance().currentUser
                            val userId = user?.uid
                            database.child(userId.toString()).setValue(usuario)
                                .addOnSuccessListener {
                                    nombre.text.clear()
                                    apellido.text.clear()
                                    identificacion.text.clear()

                                }

                            val storage = Firebase.storage
                            val storageRef = storage.reference.child("imagenes/" + identificacion.text.toString())
                            val drawable = imagen.drawable as BitmapDrawable
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


                            home(it.result?.user?.email ?: "")
                        }
                    }
                    else{
                        Toast.makeText(applicationContext,"Error al guardar los datos", Toast.LENGTH_LONG).show()
                    }

                }
            }
            else{
                Toast.makeText(applicationContext,"Llene todos los datos", Toast.LENGTH_LONG).show()
            }
        }

        camara.setOnClickListener{
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CODIGO_TOMAR_FOTO
                )
            }
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            rutaImagen = crearRutaImagen()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, rutaImagen)
            startActivityForResult(intent, CODIGO_TOMAR_FOTO)
        }

        galeria.setOnClickListener{
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CODIGO_TOMAR_FOTO
                )
            }
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, CODIGO_SELECCIONAR_IMAGEN)
        }

    }

    private fun home(email: String){

        val intent = Intent(this,MapsActivity:: class.java).apply {
            putExtra("email",email)
        }
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CODIGO_SELECCIONAR_IMAGEN -> {
                    val imagenSeleccionada: Uri? = data?.data
                    try {
                        // Cargar la imagen en el ImageView
                        imagen.setImageURI(imagenSeleccionada)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                CODIGO_TOMAR_FOTO -> {
                    // Cargar la imagen en el ImageView
                    imagen.setImageURI(rutaImagen)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODIGO_TOMAR_FOTO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de cámara concedido, puedes lanzar la actividad de la cámara aquí
            } else {
                // Permiso de cámara denegado, muestra un mensaje o realiza alguna acción adicional si es necesario
            }
        }
    }

}