package com.example.rickandmortytest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmortytest.data.Result

class AllCharactersAdapter(private val allCharactersList: List<Result>) :
    RecyclerView.Adapter<AllCharactersAdapter.ViewHolder>() {

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val currentPhoto = item.findViewById<ImageView>(R.id.current_character_photo)
        private val currentName = item.findViewById<TextView>(R.id.current_character_name)

        fun bind(information: Result){
            Glide.with(currentPhoto.context).load(information.image).into(currentPhoto)
            currentName.text = information.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.current_character_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(allCharactersList[position])
    }

    override fun getItemCount(): Int = allCharactersList.size
}