package com.example.proyectocm

data class Usuario(val Nombre : String? =null,
                   val Apellido : String? =null,
                   val Id : String? =null,
                   val Latitud : Double? =null,
                   val Longitud : Double? =null,
                   val recorridos: List<String>? = null)

