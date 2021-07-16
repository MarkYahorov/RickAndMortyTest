package com.example.ric_an_morty_test.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.ric_an_morty_test.R
import com.example.ric_an_morty_test.data.CharactersInfo

class DetailsFragment : Fragment() {

    private lateinit var currentPhoto: ImageView
    private lateinit var currentName: TextView
    private lateinit var currentStatus: TextView
    private lateinit var currentSpecies: TextView
    private lateinit var currentType: TextView
    private lateinit var currentGender: TextView
    private lateinit var currentPlanetName: TextView

    companion object {
        private const val EMPTY_STRING = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        initAllViews(view)
        return view
    }

    private fun initAllViews(view: View) {
        currentPhoto = view.findViewById(R.id.current_character_photo)
        currentName = view.findViewById(R.id.current_character_name_in_details)
        currentStatus = view.findViewById(R.id.current_status)
        currentSpecies = view.findViewById(R.id.current_species)
        currentType = view.findViewById(R.id.current_type)
        currentGender = view.findViewById(R.id.current_gender)
        currentPlanetName = view.findViewById(R.id.current_name_of_planet)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentCharacter =
            arguments?.getParcelable<CharactersInfo>(CURRENT_CHARACTER_FOR_DETAIL_SCREEN)
        if (currentCharacter != null) {
            getInfoAboutCharacter(currentCharacter)
        }
    }

    private fun getInfoAboutCharacter(currentInfo: CharactersInfo) {
        Glide.with(currentPhoto.context).load(currentInfo.image).into(currentPhoto)
        currentName.text = currentInfo.name
        currentStatus.text = currentInfo.status
        currentSpecies.text = currentInfo.species
        if (currentInfo.type != EMPTY_STRING) {
            currentType.text = currentInfo.type
        } else {
            currentType.text = getString(R.string.current_character_type_if_null)
        }
        currentGender.text = currentInfo.gender
        currentPlanetName.text = currentInfo.origin.planetName
    }
}