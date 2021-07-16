package com.example.ric_an_morty_test.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ric_an_morty_test.data.database.helpers.CreateDBHelper

const val RICK_AND_MORTY_DB = "RICK_AND_MORTY.db"
const val VERSION_DB = 1

class RickAndMortyDatabase(context: Context) :
    SQLiteOpenHelper(context, RICK_AND_MORTY_DB, null, VERSION_DB) {

    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val STATUS = "status"
        const val SPECIES = "species"
        const val PLANET_NAME = "planetName"
        const val IMAGE = "image"
        const val GENDER = "gender"
        const val TYPE = "type"
        const val TABLE_NAME = "FirstTwentyCharacters"
        private const val INTEGER_NOT_NULL_PRIMARY_KEY_AUTOINCREMENT =
            "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT"
        private const val TEXT_NOT_NULL = "TEXT NOT NULL"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        createTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    private fun createTable(db: SQLiteDatabase?) {
        db?.let {
            CreateDBHelper().setName(TABLE_NAME)
                .addField(ID, INTEGER_NOT_NULL_PRIMARY_KEY_AUTOINCREMENT)
                .addField(NAME, TEXT_NOT_NULL)
                .addField(STATUS, TEXT_NOT_NULL)
                .addField(SPECIES, TEXT_NOT_NULL)
                .addField(PLANET_NAME, TEXT_NOT_NULL)
                .addField(IMAGE, TEXT_NOT_NULL)
                .addField(GENDER, TEXT_NOT_NULL)
                .addField(TYPE, TEXT_NOT_NULL)
                .create(db)
        }
    }
}