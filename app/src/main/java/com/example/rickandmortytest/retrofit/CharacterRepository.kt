package com.example.rickandmortytest.retrofit

import bolts.Task
import com.example.rickandmortytest.data.CharactersResponse
import com.example.rickandmortytest.retrofit.RetrofitBuilder
import retrofit2.Retrofit

class CharacterRepository {

    fun getCharacters(count: Int): Task<CharactersResponse> {
        return Task.callInBackground {
            val execute = RetrofitBuilder().apiService.getAllCharacters(count)
            execute.execute().body()
        }
    }
}