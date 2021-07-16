package com.example.RickAndMortyTest.retrofit

import android.content.Context
import android.database.Cursor
import bolts.CancellationToken
import bolts.Task
import com.example.RickAndMortyTest.App
import com.example.RickAndMortyTest.data.CharactersInfo
import com.example.RickAndMortyTest.data.CharactersResponse
import com.example.RickAndMortyTest.data.ColumnIndexForDb
import com.example.RickAndMortyTest.data.Origin
import com.example.RickAndMortyTest.helpers.InsertDBHelper
import com.example.RickAndMortyTest.helpers.SelectDbHelper
import java.util.*

class CharacterRepository {

    companion object {
        private const val RICK_AND_MORTY_PREF = "RICK_AND_MORTY_PREF"
        private const val CURRENT_TIME = "CURRENT_TIME"
        private const val DIFFERENCE = 459000L
    }

    private val currentTime: Long
        get() = Calendar.getInstance().time.time

    fun getServerRequest(
        page: Int,
        cancellationToken: CancellationToken,
    ): Task<CharactersResponse> {
        return Task.callInBackground({
            val execute = RetrofitBuilder().apiService.getAllCharacters(page)
            execute.clone().execute().body()
        }, cancellationToken)
    }


    fun getRequestFromDb(
        cancellationToken: CancellationToken,
    ): Task<List<CharactersInfo>> {
        return Task.callInBackground({
            getListCharacters()
        }, cancellationToken)
    }

    private fun getListCharacters(): List<CharactersInfo> {
        var cursor: Cursor? = null
        val list = mutableListOf<CharactersInfo>()
        try {
            cursor = SelectDbHelper()
                .selectParams("*")
                .nameOfTable("FirstTwentyCharacters")
                .select(App.INSTANCE.db)
            if (cursor.moveToFirst()) {
                createListCharactersFromDB(cursor, list)
            }
        } catch (e: Exception) {
            throw e
        } finally {
            cursor?.close()
        }
        return list
    }

    private fun createListCharactersFromDB(cursor: Cursor, list: MutableList<CharactersInfo>) {
        val index = getColumnIndexOrThrow(cursor)
        do {
            val character = getCharacter(cursor, index)
            list.add(character)
        } while (cursor.moveToNext())
    }

    private fun getCharacter(cursor: Cursor, index: ColumnIndexForDb): CharactersInfo {
        return CharactersInfo(
            id = cursor.getInt(index.id),
            name = cursor.getString(index.name),
            status = cursor.getString(index.status),
            species = cursor.getString(index.species),
            origin = Origin(cursor.getString(index.origin)),
            image = cursor.getString(index.image),
            gender = cursor.getString(index.gender),
            type = cursor.getString(index.type),
        )
    }

    private fun getColumnIndexOrThrow(cursor: Cursor): ColumnIndexForDb {
        return ColumnIndexForDb(
            id = cursor.getColumnIndexOrThrow("id"),
            name = cursor.getColumnIndexOrThrow("name"),
            status = cursor.getColumnIndexOrThrow("status"),
            species = cursor.getColumnIndexOrThrow("species"),
            origin = cursor.getColumnIndexOrThrow("planetName"),
            image = cursor.getColumnIndexOrThrow("image"),
            gender = cursor.getColumnIndexOrThrow("gender"),
            type = cursor.getColumnIndexOrThrow("type")
        )
    }

    private fun checkDbHaveData(): Boolean {
        val checkingCursor = SelectDbHelper()
            .selectParams("*")
            .nameOfTable("FirstTwentyCharacters")
            .select(App.INSTANCE.db)
        val isMoveToFirst = checkingCursor.moveToFirst()
        checkingCursor.close()
        return isMoveToFirst
    }

    fun insertFirstPageInDB(context: Context, charactersInfo: List<CharactersInfo>) {
        deleteAllFromDb(context)
        if (!checkDbHaveData()) {
            charactersInfo.forEach {
                InsertDBHelper()
                    .setTableName("FirstTwentyCharacters")
                    .addFieldsAndValuesToInsert("name", it.name)
                    .addFieldsAndValuesToInsert("status", it.status)
                    .addFieldsAndValuesToInsert("species", it.species)
                    .addFieldsAndValuesToInsert("planetName", it.origin.planetName)
                    .addFieldsAndValuesToInsert("image", it.image)
                    .addFieldsAndValuesToInsert("gender", it.gender)
                    .addFieldsAndValuesToInsert("type", it.type)
                    .insertTheValues(App.INSTANCE.db)
            }
            putInSharedPref(context)
        }
    }


    private fun putInSharedPref(context: Context) {
        context.getSharedPreferences(RICK_AND_MORTY_PREF, Context.MODE_PRIVATE)
            .edit()
            .putLong(CURRENT_TIME, currentTime)
            .apply()
    }


    private fun deleteAllFromDb(context: Context) {
        val insertingTime = context.getSharedPreferences(RICK_AND_MORTY_PREF, Context.MODE_PRIVATE)
            .getLong(CURRENT_TIME, currentTime)
        if (currentTime - insertingTime >= DIFFERENCE) {
            App.INSTANCE.db.execSQL("DELETE FROM FirstTwentyCharacters")
        }
    }
}