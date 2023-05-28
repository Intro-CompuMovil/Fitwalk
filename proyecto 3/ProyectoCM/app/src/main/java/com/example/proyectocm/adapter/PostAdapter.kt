package com.example.proyectocm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocm.R
import com.example.proyectocm.datosPost

class PostAdapter(private val UsuarioList: List<datosPost>, private val context: Context) : RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostViewHolder(layoutInflater.inflate(R.layout.item_post,parent,false),context)
    }

    override fun getItemCount(): Int {
        return UsuarioList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = UsuarioList[position]
        holder.render(item)
    }
}