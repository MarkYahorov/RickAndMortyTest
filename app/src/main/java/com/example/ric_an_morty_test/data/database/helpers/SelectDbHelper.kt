package com.example.ric_an_morty_test.data.database.helpers

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class SelectDbHelper {
    private var tables: MutableList<String> = mutableListOf()
    private var allParams: MutableList<String> = mutableListOf()

    fun nameOfTable(table: String): SelectDbHelper {
        this.tables.add(table)
        return this
    }

    fun selectParams(allParams: String): SelectDbHelper {
        this.allParams.add(allParams)
        return this
    }

    fun select(db: SQLiteDatabase): Cursor {
        val tableText = tables.joinToString(",")
        val allParamsText = allParams.joinToString(",")
        return db.rawQuery("SELECT $allParamsText FROM $tableText", null)
    }
}