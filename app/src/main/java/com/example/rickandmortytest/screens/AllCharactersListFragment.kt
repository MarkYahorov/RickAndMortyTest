package com.example.rickandmortytest.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import bolts.Task
import com.example.rickandmortytest.AllCharactersAdapter
import com.example.rickandmortytest.R
import com.example.rickandmortytest.data.CharactersInfo
import com.example.rickandmortytest.data.CharactersResponse
import com.example.rickandmortytest.data.PaginationFooter
import com.example.rickandmortytest.retrofit.CharacterRepository
import java.util.ArrayList

class AllCharactersListFragment : Fragment() {

    companion object {
        private const val ALL_LIST_OF_CHARACTERS = "ALL_LIST_OF_CHARACTERS"
    }

    private lateinit var allCharactersRecyclerView: RecyclerView
    private lateinit var swipeLayout: SwipeRefreshLayout

    private val characterRepository = CharacterRepository()
    private var listCharacterInto = mutableListOf<CharactersInfo>()
    private var isLoading = false
    private var count: Int = 0
    private var sendInfo: OpenDetailNavigator? = null
    private val paginationFooter = PaginationFooter(true, null)
    private var oldListSize = 0

    interface OpenDetailNavigator {
        fun navigate(currentCharactersInfo: CharactersInfo)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sendInfo = context as OpenDetailNavigator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_characters_list, container, false)
        initAll(view)
        return view
    }

    private fun initAll(view: View) {
        allCharactersRecyclerView = view.findViewById(R.id.all_characters_list)
        swipeLayout = view.findViewById(R.id.swipe_layout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            listCharacterInto =
                savedInstanceState.getParcelableArrayList<CharactersInfo>(ALL_LIST_OF_CHARACTERS) as MutableList<CharactersInfo>
        }
        initRecycler()
        if (savedInstanceState == null) {
            loadCharacters(false)
        }
    }

    override fun onStart() {
        super.onStart()
        setSwipeLayout()
        addScrollListener()
    }

    private fun loadCharacters(isRefresh: Boolean) {
        if (!isLoading) {
            isLoading = true
            if (isRefresh){
                allCharactersRecyclerView.adapter?.notifyItemRangeRemoved(0,listCharacterInto.size)
                listCharacterInto.clear()
                count = 1
            } else {
                ++count
            }
            characterRepository.getCharacters(count)
                .continueWith { characterResponse ->
                   getCharacterRequest(characterResponse)
                }.onSuccess({
                    notifyAdapter()
                }, Task.UI_THREAD_EXECUTOR)
        }
    }

    private fun notifyAdapter(){
        when {
            paginationFooter.errorMessage!=null -> {
                allCharactersRecyclerView.adapter?.run { notifyItemChanged(itemCount - 1) }
            }
            paginationFooter.isEndOfPages -> {
                allCharactersRecyclerView.adapter?.run { notifyItemChanged(itemCount - 1) }
            }
            else -> {
                allCharactersRecyclerView.adapter?.notifyItemRangeInserted(oldListSize, listCharacterInto.size - oldListSize)
                if (oldListSize == 0) {
                    allCharactersRecyclerView.scrollToPosition(0)
                }
            }
        }
        swipeLayout.isRefreshing = false
        isLoading = false
        paginationFooter.errorMessage = null
        paginationFooter.isEndOfPages = false
    }

    private fun getCharacterRequest(characterResponse:Task<CharactersResponse>){
        when {
            characterResponse.error != null -> {
                paginationFooter.errorMessage = characterResponse.error.message
                --count
            }
            characterResponse.result.info.next == null -> {
                paginationFooter.isEndOfPages = true
            }
            else -> {
                oldListSize = listCharacterInto.size
                characterResponse.result.characters.forEach {
                    listCharacterInto.add(it)
                }
            }
        }
        Thread.sleep(3000)
    }

    private fun initRecycler() {
        with(allCharactersRecyclerView) {
            adapter = AllCharactersAdapter(listCharacterInto, paginationFooter, {
                sendInfo?.navigate(it)
            }, {
                loadCharacters(false)
            })
            layoutManager = LinearLayoutManager(activity?.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        }
    }

    private fun addScrollListener() {
        allCharactersRecyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val endHasBeenReached = lastVisible + 1 >= totalItemCount
                if (!paginationFooter.isEndOfPages && totalItemCount > 0 && endHasBeenReached) {
                    loadCharacters(false)
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(ALL_LIST_OF_CHARACTERS,
            listCharacterInto as ArrayList<CharactersInfo>)
    }

    private fun setSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            loadCharacters(true)
        }
    }

    override fun onStop() {
        super.onStop()
        swipeLayout.setOnRefreshListener(null)
        allCharactersRecyclerView.clearOnScrollListeners()
    }
}
