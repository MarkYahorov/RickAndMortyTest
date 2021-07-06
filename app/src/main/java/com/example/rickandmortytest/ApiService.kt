package com.example.rickandmortytest

import com.example.rickandmortytest.data.CharactersResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("character")
    fun getAllCharacters(): Call<CharactersResponse>
}