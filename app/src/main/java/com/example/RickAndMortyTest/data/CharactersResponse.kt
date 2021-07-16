package com.example.RickAndMortyTest.data

import com.google.gson.annotations.SerializedName

data class CharactersResponse(
    @SerializedName("info")
    val info: Info,
    @SerializedName("results")
    val characters: List<CharactersInfo>,
)
