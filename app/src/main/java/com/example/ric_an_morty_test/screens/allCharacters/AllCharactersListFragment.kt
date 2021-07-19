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
import bolts.Task
import com.example.ric_an_morty_test.R
import com.example.ric_an_morty_test.models.CharactersInfo
import com.example.ric_an_morty_test.models.CharactersResponse
import com.example.ric_an_morty_test.presentor.MainContract
import com.example.ric_an_morty_test.presentor.MainPresenter
import com.example.ric_an_morty_test.utils.App


class AllCharactersListFragment : Fragment(), MainContract.ViewCharactersList {

    private lateinit var allCharactersRecyclerView: RecyclerView
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var progressBarFirst: ProgressBar
    private lateinit var progressBarSecond: ProgressBar
    private lateinit var progressAnimator: ObjectAnimator

    private val mainPresenter = MainPresenter()
    private var oldSizeOfListCharacters = 0
    private var state = App.INSTANCE.state

    private var navigator: OpenDetailNavigator? = null

    companion object {
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
        mainPresenter.attach(this)
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
        mainPresenter.loadCharacters(false)
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
        mainPresenter.loadCharacters(isRefresh)
    }


    private fun changePaginationFooter(isEndPage: Boolean, errorMessage: String?) {
        state.paginationFooter.isEndOfPages = isEndPage
        state.paginationFooter.errorMessage = errorMessage
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
        mainPresenter.detach()
    }

    override fun showErrorFooter(charactersResponse: Task<CharactersResponse>) {
        changePaginationFooter(false, charactersResponse.error.message)
    }

    override fun showEndPageFooter() {
        changePaginationFooter(true, null)
    }

    override fun showLoadingFooter(list: List<CharactersInfo>) {
        oldSizeOfListCharacters = state.list.size
        if (state.list.isNotEmpty() && state.page == 1) {
            state.list.clear()
        }
        state.list.addAll(list)
        ++state.page
        changePaginationFooter(false, null)
    }

    override fun showProgress() {
        when (state.isSpoiledDb) {
            true -> {
                progressBarSecond.isVisible = true
                defineObjectAnimator().start()
                state.isSpoiledDb = null
            }
            false -> {
                progressBarFirst.isVisible = true
                changePaginationFooter(false, null)
                state.isSpoiledDb = null
            }
            else -> {
                allCharactersRecyclerView.adapter?.notifyDataSetChanged()
                changePaginationFooter(false, null)
            }
        }
    }

    override fun hideProgress() {
        allCharactersRecyclerView.adapter?.notifyDataSetChanged()
        if (progressAnimator.isRunning) {
            progressAnimator.cancel()
        }
        swipeLayout.isRefreshing = false
        progressBarFirst.isVisible = false
        progressBarSecond.isVisible = false
    }
}
