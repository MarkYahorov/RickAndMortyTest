package com.example.ric_an_morty_test.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Origin(
    @SerializedName("name")
    var planetName: String,
) : Parcelable
