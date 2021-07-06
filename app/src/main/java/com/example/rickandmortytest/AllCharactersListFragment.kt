package com.example.rickandmortytest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortytest.data.Result
import com.example.rickandmortytest.data.CharactersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllCharactersListFragment : Fragment() {

    private lateinit var allCharactersRecyclerView: RecyclerView

    private val characterRepository = CharacterRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_all_characters_list, container, false)
        allCharactersRecyclerView = view.findViewById(R.id.all_characters_list)
        return view
    }

    override fun onStart() {
        super.onStart()
        createRecycler()
    }

    private fun createRecycler(){

        var list:List<Result> = emptyList()

        characterRepository.getCharacters()
            .onSuccess {
                if (it.error!=null){
                    Log.e("key", it.error.message!!)
                } else {
                    list = it.result.result
                }
            }
        with(allCharactersRecyclerView){
            adapter = AllCharactersAdapter(list)
            layoutManager = LinearLayoutManager(activity?.applicationContext,LinearLayoutManager.VERTICAL, false)
        }
    }

}