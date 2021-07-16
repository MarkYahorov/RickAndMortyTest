package com.example.ric_an_morty_test.retrofit

import com.example.ric_an_morty_test.data.CharactersResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    fun getAllCharacters(@Query("page") countPage: Int): Call<CharactersResponse>
}