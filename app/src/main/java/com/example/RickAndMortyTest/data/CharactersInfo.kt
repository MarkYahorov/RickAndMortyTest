package com.example.RickAndMortyTest.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharactersInfo(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("species")
    var species: String,
    @SerializedName("origin")
    val origin: Origin,
    @SerializedName("image")
    var image: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("type")
    var type: String,
) : Parcelable
