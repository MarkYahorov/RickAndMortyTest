package com.example.RickAndMortyTest.helpers

import android.database.sqlite.SQLiteDatabase

class CreateDBHelper {
    private var name: String = ""
    private var fields: MutableMap<String, String> = mutableMapOf()

    fun setName(table: String): CreateDBHelper {
        this.name = table
        return this
    }

    fun addField(title: String, condition: String): CreateDBHelper {
        this.fields[title] = condition
        return this
    }

    fun create(db: SQLiteDatabase) {
        val stringBuilder = fields.entries.joinToString {
            "${it.key} ${it.value}"
        }
        db.execSQL("CREATE TABLE $name ($stringBuilder)")
    }
}