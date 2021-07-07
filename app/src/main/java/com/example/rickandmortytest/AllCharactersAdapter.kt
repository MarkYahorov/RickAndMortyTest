package com.example.rickandmortytest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmortytest.data.CharactersInfo

class AllCharactersAdapter(
    private val allCharactersList: List<CharactersInfo>,
    private val goToDetailsScreen:(CharactersInfo) -> Unit
) :
    RecyclerView.Adapter<AllCharactersAdapter.ViewHolder>() {

    class ViewHolder(private val item: View, private val goToDetailsScreen: (CharactersInfo) -> Unit) : RecyclerView.ViewHolder(item) {
        private val currentPhoto = item.findViewById<ImageView>(R.id.current_character_photo)
        private val currentName = item.findViewById<TextView>(R.id.current_character_name)

        fun bind(information: CharactersInfo) {
            Glide.with(currentPhoto.context).load(information.image).into(currentPhoto)
            currentName.text = information.name
            item.setOnClickListener { goToDetailsScreen(information) }
        }

        fun unbind(){
            item.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.current_character_item,
            parent,
            false
        )
        return ViewHolder(view, goToDetailsScreen)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(allCharactersList[position])
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun getItemCount(): Int = allCharactersList.size
}