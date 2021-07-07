package com.example.rickandmortytest.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.rickandmortytest.R
import com.example.rickandmortytest.data.CharactersInfo

class DetailsFragment : Fragment() {

    private lateinit var currentPhoto: ImageView
    private lateinit var currentName: TextView
    private lateinit var currentStatus: TextView
    private lateinit var currentSpecies: TextView
    private lateinit var currentType: TextView
    private lateinit var currentGender: TextView
    private lateinit var currentPlanetName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        initAll(view)
        return view
    }

    private fun initAll(view: View){
        currentPhoto = view.findViewById(R.id.current_chatacter_photo)
        currentName = view.findViewById(R.id.current_character_name_in_details)
        currentStatus = view.findViewById(R.id.current_status)
        currentSpecies = view.findViewById(R.id.current_species)
        currentType = view.findViewById(R.id.current_type)
        currentGender = view.findViewById(R.id.current_gender)
        currentPlanetName = view.findViewById(R.id.current_name_of_planet)
    }

    override fun onStart() {
        super.onStart()
        val bundle = arguments
        if (bundle!=null){
            getInfoAboutCharacter(bundle.getParcelable("CURRENT_CHARACTER")!!)
        }
    }

    private fun getInfoAboutCharacter(currentInfo: CharactersInfo){
        Glide.with(currentPhoto.context).load(currentInfo.image).into(currentPhoto)
        currentName.text = currentInfo.name
        currentStatus.text = currentInfo.status
        currentSpecies.text = currentInfo.species
        if (currentInfo.type!="") {
            currentType.text = currentInfo.type
        } else {
            currentType.text = "WE DON'T KNOW"
        }
        currentGender.text = currentInfo.gender
        currentPlanetName.text = currentInfo.origin.planetName
    }
}