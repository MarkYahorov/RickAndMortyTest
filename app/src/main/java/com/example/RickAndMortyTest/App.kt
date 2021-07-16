package com.example.RickAndMortyTest

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.example.RickAndMortyTest.data.CharactersInfo
import com.example.RickAndMortyTest.data.Origin
import com.example.RickAndMortyTest.retrofit.CharacterRepository

class App : Application() {

    val state: State = State()
    lateinit var db: SQLiteDatabase
    val characterRepository: CharacterRepository = CharacterRepository()
    var isLoading = false

    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
        db = RickAndMortyDataBase(this).writableDatabase
    }

    fun createEmptyCharacter(): CharactersInfo {
        return CharactersInfo(1,
            "",
            "",
            "",
            Origin(""),
            "",
            "",
            "")
    }
}