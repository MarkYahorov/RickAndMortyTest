package com.example.ric_an_morty_test.retrofit

import android.content.Context
import android.database.Cursor
import bolts.CancellationToken
import bolts.Task
import com.example.ric_an_morty_test.R
import com.example.ric_an_morty_test.data.CharactersInfo
import com.example.ric_an_morty_test.data.CharactersResponse
import com.example.ric_an_morty_test.data.ColumnIndexForDb
import com.example.ric_an_morty_test.data.Origin
import com.example.ric_an_morty_test.helpers.InsertDBHelper
import com.example.ric_an_morty_test.helpers.SelectDbHelper
import com.example.ric_an_morty_test.utils.App
import com.example.ric_an_morty_test.utils.RickAndMortyDatabase.Companion.GENDER
import com.example.ric_an_morty_test.utils.RickAndMortyDatabase.Companion.ID
import com.example.ric_an_morty_test.utils.RickAndMortyDatabase.Companion.IMAGE
import com.example.ric_an_morty_test.utils.RickAndMortyDatabase.Companion.NAME
import com.example.ric_an_morty_test.utils.RickAndMortyDatabase.Companion.PLANET_NAME
import com.example.ric_an_morty_test.utils.RickAndMortyDatabase.Companion.SPECIES
import com.example.ric_an_morty_test.utils.RickAndMortyDatabase.Companion.STATUS
import com.example.ric_an_morty_test.utils.RickAndMortyDatabase.Companion.TABLE_NAME
import com.example.ric_an_morty_test.utils.RickAndMortyDatabase.Companion.TYPE
import java.util.*

class CharacterRepository {

    companion object {
        private const val RICK_AND_MORTY_PREF = "RICK_AND_MORTY_PREF"
        private const val CURRENT_TIME = "CURRENT_TIME"
        private const val DIFFERENCE = 459000L
    }

    private val currentTime: Long
        get() = Calendar.getInstance().time.time

    fun getServerResponse(
        page: Int,
        cancellationToken: CancellationToken,
    ): Task<CharactersResponse> {
        return Task.callInBackground({
            Thread.sleep(5000)
            val execute = RetrofitBuilder().apiService.getAllCharacters(page)
            execute.clone().execute().body()
        }, cancellationToken)
    }


    fun getListCharactersFromDb(
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
                .nameOfTable(TABLE_NAME)
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
        val index = getColumnIndexes(cursor)
        do {
            val character = createCharacter(cursor, index)
            list.add(character)
        } while (cursor.moveToNext())
    }

    private fun createCharacter(cursor: Cursor, index: ColumnIndexForDb): CharactersInfo {
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

    private fun getColumnIndexes(cursor: Cursor): ColumnIndexForDb {
        return ColumnIndexForDb(
            id = cursor.getColumnIndexOrThrow(ID),
            name = cursor.getColumnIndexOrThrow(NAME),
            status = cursor.getColumnIndexOrThrow(STATUS),
            species = cursor.getColumnIndexOrThrow(SPECIES),
            origin = cursor.getColumnIndexOrThrow(PLANET_NAME),
            image = cursor.getColumnIndexOrThrow(IMAGE),
            gender = cursor.getColumnIndexOrThrow(GENDER),
            type = cursor.getColumnIndexOrThrow(TYPE)
        )
    }

    private fun checkDbHaveData(): Boolean {
        val checkingCursor = SelectDbHelper()
            .selectParams("*")
            .nameOfTable(TABLE_NAME)
            .select(App.INSTANCE.db)
        val isMoveToFirst = checkingCursor.moveToFirst()
        checkingCursor.close()
        return isMoveToFirst
    }

    fun insertFirstPageInDB(context: Context, charactersInfo: List<CharactersInfo>) {
        deleteAllFromDb(context)
        if (!checkDbHaveData()) {
            charactersInfo.forEach {
                if (it.type== "" || it.type == ""){
                    it.type = context.resources.getString(R.string.current_character_type_if_null)
                }
                InsertDBHelper()
                    .setTableName(TABLE_NAME)
                    .addFieldsAndValuesToInsert(NAME, it.name)
                    .addFieldsAndValuesToInsert(STATUS, it.status)
                    .addFieldsAndValuesToInsert(SPECIES, it.species)
                    .addFieldsAndValuesToInsert(PLANET_NAME, it.origin.planetName)
                    .addFieldsAndValuesToInsert(IMAGE, it.image)
                    .addFieldsAndValuesToInsert(GENDER, it.gender)
                    .addFieldsAndValuesToInsert(TYPE, it.type)
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