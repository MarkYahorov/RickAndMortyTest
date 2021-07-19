package com.example.ric_an_morty_test.data.retrofit

import bolts.CancellationToken
import bolts.Task
import com.example.ric_an_morty_test.models.CharactersInfo
import com.example.ric_an_morty_test.models.CharactersResponse

interface CharacterRepository {
    fun getCharacters(
        page: Int,
        cancellationToken: CancellationToken,
    ): Task<CharactersResponse>

    fun getCachedCharacters(
        cancellationToken: CancellationToken,
    ): Task<List<CharactersInfo>>
}