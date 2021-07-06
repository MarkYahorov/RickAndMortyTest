package com.example.rickandmortytest.data

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("id")
    val id:Int,
    @SerializedName("name")
    val name:String,
    @SerializedName("status")
    val status: String,
    @SerializedName("species")
    val species: String,
    @SerializedName("origin")
    val origin: Origin,
    @SerializedName("image")
    val image: String
)
