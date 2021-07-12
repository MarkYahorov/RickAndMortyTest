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
import com.example.rickandmortytest.data.PaginationFooter

class AllCharactersAdapter(
    private val allCharactersList: List<CharactersInfo>,
    private val paginationFooter: PaginationFooter,
    private val goToDetailsScreen: (CharactersInfo) -> Unit,
    private val startReloading: () -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val PROGRESS_ITEM_VIEW_TYPE = 1
        private const val CHARACTER_ITEM_VIEW_TYPE = 0
    }

    inner class CurrentCharacterHolder(
        private val item: View,
        private val goToDetailsScreen: (CharactersInfo) -> Unit,
    ) : RecyclerView.ViewHolder(item) {
        private val currentPhoto = item.findViewById<ImageView>(R.id.current_character_photo_in_holder)
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

        fun bind(paginationFooter: PaginationFooter) {
            when {
                paginationFooter.isEndOfPages -> {
                    progress.visibility = GONE
                    errorMessage.visibility = GONE
                    item.visibility = GONE
                    reloadingBtn.visibility = GONE
                }
                paginationFooter.errorMessage== null -> {
                    progress.visibility = VISIBLE
                    errorMessage.visibility = GONE
                    item.visibility = VISIBLE
                    reloadingBtn.visibility = GONE
                }
                else -> {
                    progress.visibility = GONE
                    errorMessage.visibility = VISIBLE
                    item.visibility = VISIBLE
                    reloadingBtn.visibility = VISIBLE
                    errorMessage.text = paginationFooter.errorMessage
                    reloadingBtn.setOnClickListener {
                        startReloading
                    }
                }
            }
        }

        fun unbind() {
            reloadingBtn.setOnClickListener(null)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == allCharactersList.size) {
            PROGRESS_ITEM_VIEW_TYPE
        } else {
            CHARACTER_ITEM_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CHARACTER_ITEM_VIEW_TYPE) {
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
        if (holder.itemViewType == CHARACTER_ITEM_VIEW_TYPE) {
            (holder as CurrentCharacterHolder).bind(allCharactersList[position])
        } else {
            (holder as ProgressHolder).bind(paginationFooter)
        }
    }

//    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
//        super.onViewRecycled(holder)
//        if (holder.itemViewType == PROGRESS_ITEM_VIEW_TYPE ){
//            (holder as ProgressHolder).unbind()
//        } else {
//            (holder as CurrentCharacterHolder).unbind()
//        }
//    }

    override fun getItemCount(): Int = allCharactersList.size+1

}