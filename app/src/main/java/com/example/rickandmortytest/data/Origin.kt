package com.example.rickandmortytest.data

import com.google.gson.annotations.SerializedName

data class Origin(
    @SerializedName("name")
    val planetName:String
)
