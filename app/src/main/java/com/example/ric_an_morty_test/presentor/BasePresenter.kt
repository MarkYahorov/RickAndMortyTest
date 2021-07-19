package com.example.ric_an_morty_test.presentor

import bolts.CancellationTokenSource
import com.example.ric_an_morty_test.data.repository.CharacterRepositoryImpl
import com.example.ric_an_morty_test.data.retrofit.CharacterRepository

abstract class BasePresenter<View: BaseContract.View> : BaseContract.Presenter<View>{

    var view: View? = null
    var repo: CharacterRepository? = null
    var cancellationTokenSource: CancellationTokenSource? = null

    override fun attach(view: View) {
        this.view = view
        this.repo = CharacterRepositoryImpl()
        this.cancellationTokenSource = CancellationTokenSource()
    }

    override fun detach() {
        this.view = null
        this.repo = null
        this.cancellationTokenSource = null
    }
}