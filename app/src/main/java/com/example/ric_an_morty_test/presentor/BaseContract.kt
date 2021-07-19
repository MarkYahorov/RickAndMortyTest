package com.example.ric_an_morty_test.presentor

interface BaseContract {

    interface View{
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter<View:BaseContract.View>{
        fun attach(view:View)
        fun detach()
    }
}