package com.example.ric_an_morty_test.presentor

import bolts.Task
import com.example.ric_an_morty_test.models.CharactersInfo
import com.example.ric_an_morty_test.models.CharactersResponse

interface MainContract {

    interface ViewCharactersList:BaseContract.View {
        fun showErrorFooter(charactersResponse: Task<CharactersResponse>)
        fun showEndPageFooter()
        fun showLoadingFooter(list: List<CharactersInfo>)
    }

    interface PresenterCharacterList : BaseContract.Presenter<ViewCharactersList> {
        fun loadCharacters(isRefresh:Boolean)
    }
}