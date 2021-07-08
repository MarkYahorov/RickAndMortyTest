package com.example.rickandmortytest.screens

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import bolts.Task
import com.example.rickandmortytest.AllCharactersAdapter
import com.example.rickandmortytest.R
import com.example.rickandmortytest.data.CharactersInfo
import com.example.rickandmortytest.data.ProgressOrError
import com.example.rickandmortytest.retrofit.CharacterRepository

class AllCharactersListFragment : Fragment() {

    private lateinit var allCharactersRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeLayout: SwipeRefreshLayout

    private val characterRepository = CharacterRepository()
    private var listCharacterInto = mutableListOf<CharactersInfo>()
    private val listOfProgress = mutableListOf<ProgressOrError>()
    private var isLoading = false
    private var isFirstLoading = true
    var count: Int = 1
    private var errorText = ""


    interface ItemOfRecyclerClickListener {
        fun goToDetailsScreen(currentCharactersInfo: CharactersInfo)
    }

    private var sendInfo: ItemOfRecyclerClickListener? = null

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
        initAll(view)
        return view
    }

    private fun initAll(view: View) {
        allCharactersRecyclerView = view.findViewById(R.id.all_characters_list)
        // progressBar = view.findViewById(R.id.loading_progress)
        swipeLayout = view.findViewById(R.id.swipe_layout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecycler()
    }

    override fun onStart() {
        super.onStart()
        setSwipeLayout()
        createListResult()
    }

    private fun createListResult() {
        characterRepository.getCharacters(count)
            .continueWith { characterResponse ->
                when {
                    characterResponse.error != null -> {
                        errorText = characterResponse.error.message.toString()
                    }
                    else -> {
                        if (isFirstLoading) {
                            characterResponse.result.characters.forEach {
                                listCharacterInto.add(it)
                            }
                        }
                        isFirstLoading = false
                    }
                }
                Thread.sleep(3000)
            }.onSuccess({
                if (errorText != "") {
                    listOfProgress[0] = ProgressOrError(VISIBLE, GONE, VISIBLE, VISIBLE, errorText)
                    allCharactersRecyclerView.adapter?.notifyDataSetChanged()
                    createDialog(errorText)
                    swipeLayout.isRefreshing = false
                    errorText = ""
                    allCharactersRecyclerView.adapter?.notifyDataSetChanged()
                } else {
                    allCharactersRecyclerView.adapter?.notifyDataSetChanged()
                    swipeLayout.isRefreshing = false
                }
            }, Task.UI_THREAD_EXECUTOR)
        listOfProgress[0] = ProgressOrError(VISIBLE, VISIBLE, GONE, GONE, "")
        isLoading = false
    }

    private fun createRecycler() {
        listOfProgress.add(ProgressOrError(VISIBLE, VISIBLE, GONE, GONE, ""))
        with(allCharactersRecyclerView) {
            adapter = AllCharactersAdapter(listCharacterInto, listOfProgress, {
                sendInfo?.goToDetailsScreen(it)
            }, {
                checkIsLoading(false, count)
            })
            layoutManager = LinearLayoutManager(activity?.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val endHasBeenReached = lastVisible + 1 >= totalItemCount
                    if (totalItemCount > 0 && endHasBeenReached) {
                        checkIsLoading(true, ++count)
                    }
                }
            })
        }
    }

    private fun checkIsLoading(isFirstLoad: Boolean, count: Int) {
        if (!isLoading) {
            isLoading = true
            listOfProgress[0] = ProgressOrError(VISIBLE, VISIBLE, GONE, GONE, "")
            allCharactersRecyclerView.adapter?.notifyDataSetChanged()
            isFirstLoading = isFirstLoad
            count
            createListResult()
            allCharactersRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun createDialog(error: String) {
        val dialog = Dialog(requireContext())
        with(dialog) {
            setCancelable(false)
            setContentView(R.layout.error_dialog)
        }
        val errorMessage = dialog.findViewById<TextView>(R.id.error_message)
        val closeDialogBtn = dialog.findViewById<Button>(R.id.close_btn)

        errorMessage.text = error
        closeDialogBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            createListResult()
        }
    }

    override fun onStop() {
        super.onStop()
        allCharactersRecyclerView.clearOnScrollListeners()
    }
}
