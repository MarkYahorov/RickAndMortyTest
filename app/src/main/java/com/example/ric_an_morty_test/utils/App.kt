package com.example.ric_an_morty_test.utils

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.example.ric_an_morty_test.data.State
import com.example.ric_an_morty_test.retrofit.CharacterRepository

class App : Application() {

    val state: State = State()
    lateinit var db: SQLiteDatabase
    val characterRepository: CharacterRepository = CharacterRepository()
    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
        db = RickAndMortyDatabase(this).writableDatabase
    }
}