package com.example.proyectocm

import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CursorAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocm.adapter.UsuarioAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference

class amigosActivity : AppCompatActivity() {

    private val CONTACT_ID_INDEX=0
    private val DISPLAY_NAME_INDEX=1

    var mProjection: Array<String>? =null
    var mCursor: Cursor? = null
    var mContactsAdapter:ContactsAdapter? = null
    //var mlista: ListView?=null
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    val PATH_USERS="Usuarios/"
    var valida=false
    private lateinit var usuariosAdapter: UsuarioAdapter
    private lateinit var myRef: DatabaseReference
    var califica = false
    private lateinit var Mutalblelista:MutableList<Usuario>


    lateinit var amigo:EditText
    var amigosUs: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)

        mProjection = arrayOf(
            ContactsContract.Profile._ID,
            ContactsContract.Profile.DISPLAY_NAME_PRIMARY
        )
        mContactsAdapter = ContactsAdapter(this, null, 0)
        mCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            mProjection,
            null,
            null,
            null
        )
        auth = Firebase.auth
        myRef = database.getReference(PATH_USERS + auth.currentUser!!.uid)
        obtenerAmigos()
        leerUsuarios()

        var salir = findViewById<Button>(R.id.salir)
        salir.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, com.example.proyectocm.InicioSesion::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        amigo = findViewById<EditText>(R.id.nombreAmigo)

        val agregar = findViewById<Button>(R.id.agregar)
        agregar.setOnClickListener{

            escribirDatos(amigo.text.toString())
            obtenerAmigos()
            leerUsuarios()

        }



    }

    fun mapa(view: View){
        val intent = Intent(this,MapsActivity::class.java)
        startActivity(intent)
    }

    fun dashboard(view: View){
        val intent = Intent(this,Dashboard::class.java)
        startActivity(intent)
    }

    fun blog(view: View){
        val intent = Intent(this,Blog::class.java)
        startActivity(intent)
    }

    private fun obtenerAmigos() {
        val amigosRef = myRef.child("amigos")

        amigosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    amigosUs = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {})
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Ocurrió un error al leer los datos de Firebase
                Toast.makeText(this@amigosActivity, "error al leer amigos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class ContactsAdapter(context: Context?, c: Cursor?, flags: Int) : CursorAdapter(context, c, flags) {
        private val CONTACT_ID_INDEX = 0
        private val DISPLAY_NAME_INDEX = 1

        override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
            return LayoutInflater.from(context)
                .inflate(R.layout.linear, parent, false)
        }

        override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
            val tvIdContacto = view?.findViewById<TextView>(R.id.idContacto)
            val tvNombre = view?.findViewById<TextView>(R.id.nombre)
            val idnum = cursor?.getInt(CONTACT_ID_INDEX)
            val nombre = cursor?.getString(DISPLAY_NAME_INDEX)
            tvIdContacto?.text = idnum?.toString()
            tvNombre?.text = nombre
        }
    }



    private fun escribirDatos(guardarDat: String) {

        val amigosRef = myRef.child("amigos")

        // Obtener la lista actual del nodo "amigos"
        amigosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val disponibleList = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {})

                    // Verificar si la lista es nula y crear una nueva lista en caso afirmativo
                    val updatedList = disponibleList ?: ArrayList()

                    // Agregar los amigos seleccionados a la lista
                    updatedList.addAll(listOf(guardarDat))

                    // Eliminar duplicados utilizando un conjunto
                    val uniqueSet = LinkedHashSet(updatedList)
                    updatedList.clear()
                    updatedList.addAll(uniqueSet)

                    // Actualizar la lista en Firebase
                    amigosRef.setValue(updatedList)
                        .addOnSuccessListener {
                            // La lista se actualizó correctamente
                            Toast.makeText(this@amigosActivity, "amigo agregado", Toast.LENGTH_SHORT).show()

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

    private fun leerUsuarios() {

        val pathUsuarios = "Usuarios"
        val usuariosRef = database.getReference(pathUsuarios)

        usuariosRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val usuario = snapshot.getValue(Usuario::class.java)
                if (usuario != null) {
                    if (usuario.email in amigosUs.orEmpty()) {
                        usuario?.let {
                            guardarUsuario(it)
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Lógica para el caso de que un usuario existente haya sido modificado
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Lógica para el caso de que un usuario existente haya sido eliminado
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Lógica para el caso de que un usuario existente haya sido movido
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al leer los usuarios: ${error.message}")
            }
        })
    }



    private fun guardarUsuario(usuario: Usuario) {
        var myRef02 = database.getReference(PATH_USERS)

        myRef02.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Mutalblelista= mutableListOf<Usuario>()
                for (data in snapshot.children) {
                    val nombre = data.child("nombre").getValue(String::class.java) ?: ""
                    val apellido = data.child("apellido").getValue(String::class.java) ?: ""
                    val id = data.child("id").getValue(String::class.java)?:""
                    val latitud = data.child("latitud").getValue(Double::class.java) ?: 0.0
                    val longitud = data.child("longitud").getValue(Double::class.java) ?: 0.0
                    val email = data.child("email").getValue(String::class.java) ?: ""
                    val usuario = Usuario(nombre, apellido, id, latitud, longitud,null,null,email)
                    if (email in amigosUs.orEmpty()) {

                            Mutalblelista.add(usuario)

                    }

                }
                // Actualizar la UI con la nueva lista
                initRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Fallo la lectura: ${error.toException()}")
            }
        })
    }

    fun initRecyclerView(){
        //Toast.makeText(this@Chat, "entro al init", Toast.LENGTH_SHORT).show()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = UsuarioAdapter(Mutalblelista.toList(), this)
    }


}