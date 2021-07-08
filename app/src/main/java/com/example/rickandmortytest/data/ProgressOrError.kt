package com.example.rickandmortytest.data

data class ProgressOrError(
    var layoutVisible:Int,
    var progressBarVisible: Int,
    var errorMessageVisible: Int,
    var reloadingBtnVisible: Int,
    var errorMessage: String
)
