package com.example.proyectocm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocm.R
import com.example.proyectocm.Usuario

class UsuarioAdapter(private val UsuarioList: List<Usuario>, private val context: Context) : RecyclerView.Adapter<UsuarioViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UsuarioViewHolder(layoutInflater.inflate(R.layout.item_usuario,parent,false),context)
    }

    override fun getItemCount(): Int {
        return UsuarioList.size
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val item = UsuarioList[position]
        holder.render(item)
    }
}