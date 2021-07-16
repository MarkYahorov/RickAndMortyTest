package com.example.RickAndMortyTest.retrofit

import com.example.RickAndMortyTest.data.CharactersResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    fun getAllCharacters(@Query("page") countPage: Int): Call<CharactersResponse>
}