package com.example.ric_an_morty_test.utils

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.example.ric_an_morty_test.data.retrofit.CharacterRepository
import com.example.ric_an_morty_test.data.repository.CharacterRepositoryImpl
import com.example.ric_an_morty_test.data.database.RickAndMortyDatabase
import com.example.ric_an_morty_test.models.State

class App : Application() {

    val state: State = State()
    lateinit var db: SQLiteDatabase
    val repo: CharacterRepository = CharacterRepositoryImpl()

    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
        db = RickAndMortyDatabase(this).writableDatabase
    }
}