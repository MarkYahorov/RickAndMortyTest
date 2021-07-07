package com.example.rickandmortytest.screens

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import bolts.Task
import com.example.rickandmortytest.AllCharactersAdapter
import com.example.rickandmortytest.R
import com.example.rickandmortytest.data.CharactersInfo
import com.example.rickandmortytest.retrofit.CharacterRepository

class AllCharactersListFragment : Fragment() {

    private lateinit var allCharactersRecyclerView: RecyclerView

    private val characterRepository = CharacterRepository()
    private val listCharacterInto = mutableListOf<CharactersInfo>()
    private var isLoading = false
    var count: Int = 0
    var position = 0

    interface ItemOfRecyclerClickListener {
        fun goToDetailsScreen(currentCharactersInfo: CharactersInfo)
    }

    private var sendInfo: ItemOfRecyclerClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            sendInfo = context as ItemOfRecyclerClickListener
        } catch (e: Exception) {
            Toast.makeText(context, "IMPL INTERFACE", Toast.LENGTH_LONG).show()
        }
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
        count = getFromSharedPrefCountOfPages("CURRENT_PAGE")
        createListResult()
    }

    private fun createListResult() {
        characterRepository.getCharacters(count)
            .onSuccess { characterResponse ->
                characterResponse.result.characters.forEach {
                    listCharacterInto.add(it)
                }
            }.onSuccess({
                if (isLoading) {
                    allCharactersRecyclerView.adapter?.notifyDataSetChanged()
                } else {
                    createRecycler()
                }
                isLoading = false

            }, Task.UI_THREAD_EXECUTOR)
    }

    private fun createRecycler() {
        position = getFromSharedPrefCountOfPages("POSITION")
        with(allCharactersRecyclerView) {
            adapter = AllCharactersAdapter(listCharacterInto) {
                sendInfo?.goToDetailsScreen(it)
            }
            scrollTo(0, position)
            layoutManager = LinearLayoutManager(activity?.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    position = layoutManager.findFirstVisibleItemPosition()
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val endHasBeenReached: Boolean = lastVisible + 1 >= totalItemCount
                    if (totalItemCount > 0 && endHasBeenReached) {
                        if (!isLoading) {
                            isLoading = true
                            putInSharedPrefCountOfPages("CURRENT_PAGE",++count)
                            createListResult()
                            putInSharedPrefCountOfPages("POSITION",position)
                        }
                    }
                }
            })
        }
    }

    private fun putInSharedPrefCountOfPages(key:String,countOfPages: Int) {
        activity?.let {
            it.getSharedPreferences("SHARED", Context.MODE_PRIVATE)
                .edit().putInt(key, countOfPages)
                ?.apply()
        }
    }

    private fun getFromSharedPrefCountOfPages(key: String): Int {
        activity?.let {
            return it.getSharedPreferences("SHARED", Context.MODE_PRIVATE).getInt(key, 1)
        }
        return 1
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        putInSharedPrefCountOfPages("CURRENT_PAGE",1)
        allCharactersRecyclerView.clearOnScrollListeners()
    }

    override fun onDestroyView() {
        Log.e("key", "DESTROY ")
        super.onDestroyView()

    }
}