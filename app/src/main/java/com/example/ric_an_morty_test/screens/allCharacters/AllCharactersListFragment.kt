package com.example.ric_an_morty_test.screens.allCharacters

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import bolts.CancellationTokenSource
import bolts.Task
import com.example.ric_an_morty_test.R
import com.example.ric_an_morty_test.models.CharactersInfo
import com.example.ric_an_morty_test.models.CharactersResponse
import com.example.ric_an_morty_test.utils.App


class AllCharactersListFragment : Fragment() {

    private lateinit var allCharactersRecyclerView: RecyclerView
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var progressBarFirst: ProgressBar
    private lateinit var progressBarSecond: ProgressBar
    private lateinit var progressAnimator: ObjectAnimator

    private val characterRepositoryImpl = App.INSTANCE.repo
    private val cancellationTokenSource = CancellationTokenSource()
    private var oldSizeOfListCharacters = 0
    private var state = App.INSTANCE.state
    private var isLoading = false

    private var navigator: OpenDetailNavigator? = null

    companion object{
        private const val DURATION_FOR_PROGRESS_ANIMATOR = 5000L
        private const val REPEAT_COUNT = 3
        private const val PROPERTY_NAME = "progress"
    }

    interface OpenDetailNavigator {
        fun navigate(currentCharactersInfo: CharactersInfo)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigator = context as OpenDetailNavigator
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
        progressBarFirst = view.findViewById(R.id.first_progress_bar)
        progressBarSecond = view.findViewById(R.id.second_progress)
        progressAnimator = ObjectAnimator.ofInt(progressBarSecond, PROPERTY_NAME, 0, 100)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        if (state.page == 1) {
            characterRepositoryImpl.getCachedCharacters(cancellationTokenSource.token)
                .continueWith({ task ->
                    processResponseFromDb(task)
                    notifyAdapter()
                    loadCharacters(false)
                }, Task.UI_THREAD_EXECUTOR)
        }
    }

    private fun processResponseFromDb(task: Task<List<CharactersInfo>>) {
        if (task.error != null || task.result.isEmpty()) {
            progressBarSecond.isVisible = true
            defineObjectAnimator().start()
        } else if (!task.result.isNullOrEmpty()) {
            oldSizeOfListCharacters = state.list.size
            state.list.addAll(task.result)
            progressBarFirst.isVisible = true
            changePaginationFooter(false, null)
        }
    }

    override fun onStart() {
        super.onStart()
        setSwipeLayout()
        addScrollListener()
    }

    private fun defineObjectAnimator(): ObjectAnimator {
        progressAnimator.duration = DURATION_FOR_PROGRESS_ANIMATOR
        progressAnimator.interpolator = LinearInterpolator()
        progressAnimator.repeatCount = REPEAT_COUNT
        return progressAnimator
    }

    private fun loadCharacters(isRefresh: Boolean) {
        if (!isLoading) {
            isLoading = true
            if (isRefresh) {
                allCharactersRecyclerView.adapter?.notifyItemRangeRemoved(0, state.list.size)
                state.list.clear()
                state.page = 1
            }
            characterRepositoryImpl.getCharacters(state.page, cancellationTokenSource.token)
                .continueWith({
                    processResponseFromServer(it)
                }, Task.BACKGROUND_EXECUTOR, cancellationTokenSource.token)
                .continueWith({
                    notifyAdapter()
                    progressBarFirst.isVisible = false
                    progressBarSecond.isVisible = false
                }, Task.UI_THREAD_EXECUTOR, cancellationTokenSource.token)
        } else {
            swipeLayout.isRefreshing = false
        }
    }

    private fun notifyAdapter() {
        if (state.paginationFooter.errorMessage != null ||
            state.paginationFooter.isEndOfPages
        ) {
            notifyChangeAdapter()
        } else {
            notifyInsertAdapter()
        }
        if (progressAnimator.isRunning) {
            progressAnimator.cancel()
        }
        swipeLayout.isRefreshing = false
        isLoading = false
    }

    private fun notifyInsertAdapter() {
        allCharactersRecyclerView.adapter?.notifyItemRangeInserted(oldSizeOfListCharacters,
            state.list.size - oldSizeOfListCharacters)
        if (oldSizeOfListCharacters == 0) {
            allCharactersRecyclerView.scrollToPosition(0)
        }
        changePaginationFooter(false, null)
    }

    private fun notifyChangeAdapter() {
        allCharactersRecyclerView.adapter?.run { notifyItemChanged(itemCount - 1) }
        changePaginationFooter(false, null)
    }

    private fun changePaginationFooter(isEndPage: Boolean, errorMessage: String?) {
        state.paginationFooter.isEndOfPages = isEndPage
        state.paginationFooter.errorMessage = errorMessage
    }

    private fun processResponseFromServer(characterResponse: Task<CharactersResponse>) {
        when {
            characterResponse.error != null -> {
                changePaginationFooter(false, characterResponse.error.message)
            }
            characterResponse.result.info.next == null -> {
                changePaginationFooter(true, null)
            }
            else -> {
                oldSizeOfListCharacters = state.list.size
                if (state.list.isNotEmpty() && state.page == 1) {
                    state.list.clear()
                }
                state.list.addAll(characterResponse.result.characters)
                ++state.page
                changePaginationFooter(false, null)
            }
        }
    }

    private fun initRecycler() {
        with(allCharactersRecyclerView) {
            adapter = AllCharactersAdapter(state.list, state.paginationFooter, {
                navigator?.navigate(it)
            }, {
                loadCharacters(false)
            })
            layoutManager = LinearLayoutManager(context,
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
                if (!state.paginationFooter.isEndOfPages && totalItemCount > 0 && endHasBeenReached) {
                    loadCharacters(false)
                }
            }
        })
    }

    private fun setSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            loadCharacters(true)
        }
    }

    override fun onStop() {
        swipeLayout.setOnRefreshListener(null)
        allCharactersRecyclerView.clearOnScrollListeners()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancellationTokenSource.cancel()
    }


}
