package com.example.RickAndMortyTest

import com.example.RickAndMortyTest.data.CharactersInfo
import com.example.RickAndMortyTest.data.PaginationFooter

data class State(
    val list: MutableList<CharactersInfo> = emptyList<CharactersInfo>().toMutableList(),
    val paginationFooter: PaginationFooter = PaginationFooter(true, null),
    var page: Int = 1,
    var isLoading: Boolean = false,
)