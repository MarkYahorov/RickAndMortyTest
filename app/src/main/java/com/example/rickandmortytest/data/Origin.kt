package com.example.rickandmortytest.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Origin(
    @SerializedName("name")
    val planetName:String
): Parcelable
