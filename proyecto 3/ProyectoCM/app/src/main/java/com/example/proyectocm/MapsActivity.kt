package com.example.proyectocm


import android.Manifest
import android.content.Intent

import android.content.pm.PackageManager


import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.proyectocm.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import kotlin.math.*
import com.google.android.gms.location.LocationRequest

import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth
    val PATH_USERS="Usuarios/"
    private lateinit var myRef: DatabaseReference
    private lateinit var myRef02: DatabaseReference
    private val database = FirebaseDatabase.getInstance()

    var miLat: Double = 0.0
    var miLon: Double = 0.0
    var miLatRec: Double = 0.0
    var miLonRec: Double = 0.0
    var distancia = 0.0
    var distRec = 0.0
    var distanciaToast = 0.0
    var marcador = 1
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private val REQUEST_LOCATION_PERMISSION = 1
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 4
    private val REQUEST_WRITE_CODE = 101

    var inic: Boolean = false
    var cambios = -1 //para que no cuente el de 0.0

    var guardarDat=""//string para guardar datos


    var velocidades: Array<Double> = emptyArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Solicitar permiso de escritura
        auth = Firebase.auth

        myRef = database.getReference(PATH_USERS+auth.currentUser!!.uid)
        myRef02 = database.getReference(PATH_USERS)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MapsActivity)
        mLocationRequest = createLocationRequest()
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                var ubicacion = locationResult.lastLocation
                Log.i("ubicacion", "--------------$ubicacion---------")
                if (ubicacion != null) {

                    if (miLat != ubicacion.latitude || miLon != ubicacion.longitude) {
                        if (cambios > -1 && inic) {
                            distRec = distRec + abs(
                                calcularDistancia(
                                    miLatRec,
                                    miLonRec,
                                    ubicacion.latitude,
                                    ubicacion.longitude
                                )
                            )
                        }
                        cambios++


                        //ubicacion recorrido
                        miLatRec = ubicacion.latitude
                        miLonRec = ubicacion.longitude
                        //ubicacion auxiliar para el marcador
                        miLat = ubicacion.latitude
                        miLon = ubicacion.longitude

                        var ubicAc = LatLng(miLat, miLon)
                        print("------------------------------------------------" + ubicAc + "-------------------------")
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicAc))


                        val aprox2: String = String.format("%.2f", distRec)


                        binding.textView.text = " " + aprox2 + " metros recorridos"

                        velocidades += distRec/10

                    }

                }
            }
        }



        binding.iniciar.setOnClickListener {
            inic = true

            Toast.makeText(this, "Trayecto iniciado", Toast.LENGTH_SHORT).show()
            //guardar hora y dia
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val fechaHora = LocalDateTime.now().format(formatter)
            guardarDat="fecha y hora inicial: "+ fechaHora.toString()+"| "
            //guardar ubicacion
            guardarDat=guardarDat+"latitud inicial: "+miLatRec+"| "
            guardarDat=guardarDat+"longitud inicial: "+miLonRec+"| "

        }
        binding.terminar.setOnClickListener {
            inic = false//detener las actualizaciones
            if (velocidades.isNotEmpty()) {
            //guardar hora y dia
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val fechaHora = LocalDateTime.now().format(formatter)
            guardarDat=guardarDat+"fecha y hora final: "+ fechaHora.toString()+"| "
            //guardar ubicacion
            guardarDat=guardarDat+"latitud final: "+miLatRec+"| "
            guardarDat=guardarDat+"longitud final: "+miLonRec+"| "

            //guardar velocidad promedio
                val sum = velocidades.sum()
                val velocidadProm = sum / velocidades.size

                guardarDat = guardarDat + "velocidad promedio: " + String.format("%.2f", velocidadProm) + " m/s| "


                //guardar METROS
                guardarDat = guardarDat + "metros recorridos : " + String.format("%.2f", distRec) + "\n"
                escribirDatos(guardarDat)

                //borrar datos
                guardarDat =""

                distRec=0.0
                binding.textView.text = " "+distRec+" metros recorridos"

                Toast.makeText(this, "Trayecto finalizado", Toast.LENGTH_SHORT).show()

            }
            else
                Toast.makeText(this, "No hay datos suficientes para hacer un registro de este trayecto", Toast.LENGTH_SHORT).show()










        }


        var salir=findViewById<Button>(R.id.salir)
        salir.setOnClickListener {
            auth.signOut()
            val intent =
                android.content.Intent(this, com.example.proyectocm.InicioSesion::class.java)
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MapsActivity)

        mLocationRequest = createLocationRequest()



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun dashboard(view: View){
        val intent = Intent(this,Dashboard::class.java)
        startActivity(intent)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.cerrarSesion->{
                auth.signOut()
                val intent = Intent(this,InicioSesion::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                true
            }

        }
        return super.onOptionsItemSelected(item)
    }
    fun blog(view: View){
        val intent = Intent(this,Blog::class.java)
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

    private fun escribirDatos(guardarDat: String) {

        val recorridosRef = myRef.child("recorridos")

        // Obtener la lista actual del nodo "recorridos"
        recorridosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val disponibleList = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {})

                    // Verificar si la lista es nula y crear una nueva lista en caso afirmativo
                    val updatedList = disponibleList ?: ArrayList()

                    // Agregar los recorridos seleccionados a la lista
                    updatedList.addAll(listOf(guardarDat))

                    // Eliminar duplicados utilizando un conjunto
                    val uniqueSet = LinkedHashSet(updatedList)
                    updatedList.clear()
                    updatedList.addAll(uniqueSet)

                    // Actualizar la lista en Firebase
                    recorridosRef.setValue(updatedList)
                        .addOnSuccessListener {
                            // La lista se actualizó correctamente
                            Toast.makeText(this@MapsActivity, "Trayecto finalizado", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener { error ->
                            // Ocurrió un error al actualizar la lista
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Ocurrió un error al leer los datos de Firebase
            }
        })

    }



    private fun agregarServ(servicios: ArrayList<String>) {

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        showUserLocation()

        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18F))

        requestLocationFunction()


        mMap.setOnMapLongClickListener { latLng ->

            mMap.addMarker(
                //MarkerOptions().position(latLng).title(geoCoderSearchLatLang(latLng))
                MarkerOptions().position(latLng).title("MARCADOR " + marcador.toString())

            )
            marcador++


            distanciaToast =
                abs(calcularDistancia(miLat, miLon, latLng.latitude, latLng.longitude))
        }

    }



    private fun showUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLocation = LatLng(it.latitude, it.longitude)

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                        var ubicAc = LatLng(it.latitude, it.longitude)
                        distancia = abs(calcularDistancia(miLat, miLon, it.latitude, it.longitude))



                    }
                }
        }

    }


    private fun requestLocationFunction() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        )
            return

        mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            showUserLocation()
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            setInterval(25000)
            setFastestInterval(10000)//actualizacion periodica para la distancia recorrida
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }
    }

    fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radioTierra = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a =
            sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(
                dLon / 2
            ) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distanciaEnKM = radioTierra * c
        val distanciaEnMetros = distanciaEnKM * 1000

        return distanciaEnMetros
    }

    // Manejar la respuesta del usuario a la solicitud de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                // Si el usuario concede el permiso de ubicación, obtener la ubicación actual
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showUserLocation()
                } else {
                    // Si el usuario no concede el permiso de ubicación, mostrar un mensaje de error
                    Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_WRITE_CODE -> {
                // Si el usuario concede el permiso de ubicación, obtener la escribir actual
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    escribirDatos(guardarDat)
                } else {
                    // Si el usuario no concede el permiso de escribir, mostrar un mensaje de error
                    Toast.makeText(this, "Permiso de escribir denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}


