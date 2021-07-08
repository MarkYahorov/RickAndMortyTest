package com.example.rickandmortytest

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmortytest.data.CharactersInfo
import com.example.rickandmortytest.data.ProgressOrError

class AllCharactersAdapter(
    private val allCharactersList: List<CharactersInfo>,
    private val listOfProgress: List<ProgressOrError>,
    private val goToDetailsScreen: (CharactersInfo) -> Unit,
    private val startReloading: () -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class CurrentCharacterHolder(
        private val item: View,
        private val goToDetailsScreen: (CharactersInfo) -> Unit,
    ) : RecyclerView.ViewHolder(item) {
        private val currentPhoto = item.findViewById<ImageView>(R.id.current_character_photo)
        private val currentName = item.findViewById<TextView>(R.id.current_character_name)

        fun bind(information: CharactersInfo) {
            Glide.with(currentPhoto.context).load(information.image).into(currentPhoto)
            currentName.text = information.name
            item.setOnClickListener { goToDetailsScreen(information) }
        }

        fun unbind() {
            item.setOnClickListener(null)
        }
    }

    inner class ProgressHolder(
        private val item: View,
        private val startReloading: () -> Unit
    ) : RecyclerView.ViewHolder(item) {

        private val progress = item.findViewById<ProgressBar>(R.id.loading_progress_in_holder)
        private val errorMessage = item.findViewById<TextView>(R.id.error_message_in_holder)
        private val reloadingBtn = item.findViewById<Button>(R.id.reloading_btn)

        fun bind(progressAndError: ProgressOrError) {
            progress.visibility = progressAndError.progressBarVisible
            errorMessage.visibility = progressAndError.errorMessageVisible
            item.visibility = progressAndError.layoutVisible
            reloadingBtn.visibility = progressAndError.reloadingBtnVisible
            errorMessage.text = progressAndError.errorMessage
            reloadingBtn.setOnClickListener {
                startReloading()
            }
        }

        fun unbind() {
            reloadingBtn.setOnClickListener(null)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == allCharactersList.lastIndex) {
            1
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.current_character_item,
                parent,
                false)
            CurrentCharacterHolder(view, goToDetailsScreen)
        } else {
            val progress = LayoutInflater.from(parent.context).inflate(
                R.layout.progress_item,
                parent,
                false
            )
            ProgressHolder(progress,startReloading)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != allCharactersList.lastIndex) {
            (holder as CurrentCharacterHolder).bind(allCharactersList[position])
        } else {
            (holder as ProgressHolder).bind(listOfProgress[0])
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = allCharactersList.size

}