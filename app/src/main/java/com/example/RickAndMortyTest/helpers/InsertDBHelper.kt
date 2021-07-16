package com.example.RickAndMortyTest.helpers

import android.database.sqlite.SQLiteDatabase

class InsertDBHelper {

    private var tableName: String = ""
    private val selectedFieldsInTable = mutableMapOf<String, String>()

    fun setTableName(name: String): InsertDBHelper {
        this.tableName = name
        return this
    }

    fun addFieldsAndValuesToInsert(nameOfField: String, insertingValue: String): InsertDBHelper {
        selectedFieldsInTable[nameOfField] = insertingValue
        return this
    }

    fun insertTheValues(db: SQLiteDatabase) {
        var selectedFields = ""
        var values = ""
        selectedFields = selectedFieldsInTable.keys.joinToString()
        values = selectedFieldsInTable.values.joinToString()
        val statement =
            db.compileStatement("INSERT INTO $tableName ($selectedFields) VALUES ($values)")
        statement.bindString(0, selectedFields)
        statement.bindString(1, values)
        statement.execute()
    }
}