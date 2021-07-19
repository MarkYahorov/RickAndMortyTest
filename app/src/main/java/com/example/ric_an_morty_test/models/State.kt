package com.example.ric_an_morty_test.models

data class State(
    val list: MutableList<CharactersInfo> = emptyList<CharactersInfo>().toMutableList(),
    val paginationFooter: PaginationFooter = PaginationFooter(true, null),
    var page: Int = 1,
    var isLoading: Boolean = false,
)