package com.example.ric_an_morty_test.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ric_an_morty_test.helpers.CreateDBHelper
import com.example.ric_an_morty_test.screens.RICK_AND_MORTY_DB
import com.example.ric_an_morty_test.screens.VERSION_DB

class RickAndMortyDataBase(context: Context) :
    SQLiteOpenHelper(context, RICK_AND_MORTY_DB, null, VERSION_DB) {

    override fun onCreate(db: SQLiteDatabase?) {
        createTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    private fun createTable(db: SQLiteDatabase?) {
        db?.let {
            CreateDBHelper().setName("FirstTwentyCharacters")
                .addField("id", "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT")
                .addField("name", "TEXT NOT NULL")
                .addField("status", "TEXT NOT NULL")
                .addField("species", "TEXT NOT NULL")
                .addField("planetName", "TEXT NOT NULL")
                .addField("image", "TEXT NOT NULL")
                .addField("gender", "TEXT NOT NULL")
                .addField("type", "TEXT NOT NULL")
                .create(db)
        }
    }
}