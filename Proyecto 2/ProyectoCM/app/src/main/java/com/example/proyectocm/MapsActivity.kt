package com.example.proyectocm


import android.Manifest
import android.content.Context
import android.content.Intent

import android.content.pm.PackageManager


import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Solicitar permiso de escritura

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

                       /* Toast.makeText(
                            this@MapsActivity,
                            "La distancia recorrida es de $aprox2 metros",
                            Toast.LENGTH_LONG
                        ).show()*/
                        binding.textView.text = " " + aprox2 + " metros recorridos"
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

            //guardar hora y dia
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val fechaHora = LocalDateTime.now().format(formatter)
            guardarDat=guardarDat+"fecha y hora final: "+ fechaHora.toString()+"| "
            //guardar ubicacion
            guardarDat=guardarDat+"latitud final: "+miLatRec+"| "
            guardarDat=guardarDat+"longitud final: "+miLonRec+"| "

            //guardar METROS
            guardarDat=guardarDat+"metros recorridos : "+distRec+"\n"

            escribirDatos(this@MapsActivity)




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

    private fun escribirDatos(context: Context) {
        // Solicitar permiso de escritura
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_CODE
            )
        } else {
            val fileName = "recorridos.json"
            val file = File(context.filesDir, fileName)
            val escrib: Writer

            escrib = BufferedWriter(OutputStreamWriter(FileOutputStream(file, true)))

            if (!file.exists()) {
                file.createNewFile()
            }
            escrib.write(guardarDat)


            escrib.close()
            //borrar datos
            guardarDat =""

            distRec=0.0
            binding.textView.text = " "+distRec+" metros recorridos"
            Toast.makeText(this, "Trayecto finalizado", Toast.LENGTH_SHORT).show()
        }
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
            val aprox: String = String.format("%.2f", distanciaToast)
            /*Toast.makeText(
                this@MapsActivity,
                "La distancia entre las dos ubicaciones es de $aprox metros",
                Toast.LENGTH_LONG
            ).show()*/

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

                        val aprox: String = String.format("%.2f", distancia)
                        /*Toast.makeText(
                            this@MapsActivity,
                            "La distancia entre las dos ubicaciones es de $aprox metros",
                            Toast.LENGTH_LONG
                        ).show()*/

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
                    escribirDatos(this@MapsActivity)
                } else {
                    // Si el usuario no concede el permiso de escribir, mostrar un mensaje de error
                    Toast.makeText(this, "Permiso de escribir denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}


