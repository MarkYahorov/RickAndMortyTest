package com.example.ric_an_morty_test.presentor

import bolts.Task
import com.example.ric_an_morty_test.models.CharactersInfo
import com.example.ric_an_morty_test.models.CharactersResponse
import com.example.ric_an_morty_test.utils.App

class MainPresenter : BasePresenter<MainContract.ViewCharactersList>(),
    MainContract.PresenterCharacterList {
    private var isLoading = false
    private val state = App.INSTANCE.state

    override fun loadCharacters(isRefresh: Boolean) {
        if (state.page == 1){
            getFromDb()
        } else {
            getFromServer(isRefresh)
        }
    }

    private fun getFromServer(isRefresh: Boolean){
        if (!isLoading) {
            isLoading = true
            if (isRefresh) {
                view?.showProgress()
                state.list.clear()
                state.page = 1
            }
            repo?.let {
                repo!!.getCharacters(state.page, cancellationTokenSource!!.token)
                    .continueWith({ characterResponse ->
                        processResponseFromServer(characterResponse)
                    }, Task.BACKGROUND_EXECUTOR, cancellationTokenSource!!.token)
                    .continueWith({
                        view?.hideProgress()
                        isLoading = false
                    }, Task.UI_THREAD_EXECUTOR, cancellationTokenSource!!.token)
            }
        } else {
            view?.hideProgress()
        }
    }

    private fun getFromDb(){
        repo!!.getCachedCharacters(cancellationTokenSource!!.token)
            .continueWith({ task ->
                processResponseFromDb(task)
                getFromServer(false)
            }, Task.UI_THREAD_EXECUTOR)
    }

    private fun processResponseFromDb(task: Task<List<CharactersInfo>>?) {
        if (task?.error != null || task?.result?.isEmpty() == true) {
            state.isSpoiledDb = true
            view?.showProgress()
        } else if (!task?.result.isNullOrEmpty()) {
            state.isSpoiledDb = false
            state.oldSizeListCharacters = state.list.size
            state.list.addAll(task!!.result)
            view?.showProgress()
        }
    }

    private fun processResponseFromServer(characterResponse: Task<CharactersResponse>) {
        when {
            characterResponse.error != null -> {
                view?.showErrorFooter(characterResponse)
            }
            characterResponse.result.info.next == null -> {
                view?.showEndPageFooter()
                isLoading = true
            }
            else -> {
                view?.showLoadingFooter(characterResponse.result.characters)
            }
        }
    }
}