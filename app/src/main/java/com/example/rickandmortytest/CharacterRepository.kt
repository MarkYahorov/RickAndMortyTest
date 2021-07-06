package com.example.rickandmortytest

import bolts.Task
import com.example.rickandmortytest.data.CharactersResponse
import com.example.rickandmortytest.data.Result

class CharacterRepository {

    fun getCharacters(): Task<CharactersResponse>{
        return Task.callInBackground {
            val execute = RetrofitBuilder.apiService.getAllCharacters()
            execute.execute().body()
        }
    }
}