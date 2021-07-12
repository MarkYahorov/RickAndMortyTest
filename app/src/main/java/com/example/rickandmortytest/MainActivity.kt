package com.example.rickandmortytest

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.rickandmortytest.data.CharactersInfo
import com.example.rickandmortytest.data.Origin
import com.example.rickandmortytest.screens.AllCharactersListFragment
import com.example.rickandmortytest.screens.DetailsFragment

const val CURRENT_CHARACTER_FOR_DETAIL_SCREEN = "CURRENT_CHARACTER"

class MainActivity : AppCompatActivity(), AllCharactersListFragment.OpenDetailNavigator {

    companion object {
        private const val CURRENT_CHARACTER_THIS = "CURRENT_CHARACTER_THIS"
        private const val BACK_STACK_DETAILS = "DETAILS"
        private const val BACK_STACK_LIST = "LIST"
    }

    private var charactersInfoSuper: CharactersInfo = CharactersInfo(1,
        "",
        "",
        "",
        Origin(""),
        "",
        "",
        "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val charactersInfo =
            savedInstanceState?.getParcelable<CharactersInfo>(CURRENT_CHARACTER_THIS)
        if (savedInstanceState == null) {
            setFragment()
        } else {
            setDetailsFragment(charactersInfo)
        }
    }

    private fun setFragment() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            addFragment()
        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            addFragment()
        }
    }

    private fun setDetailsFragment(charactersInfo: CharactersInfo?) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (supportFragmentManager.popBackStackImmediate(BACK_STACK_DETAILS, POP_BACK_STACK_INCLUSIVE)){
                replaceFragment(R.id.fragment_container, charactersInfo!!)
            }
        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (supportFragmentManager.popBackStackImmediate(BACK_STACK_DETAILS, POP_BACK_STACK_INCLUSIVE)){
                replaceFragment(R.id.details_container, charactersInfo!!)
            }
        }
    }

    override fun navigate(currentCharactersInfo: CharactersInfo) {
        charactersInfoSuper = currentCharactersInfo
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            replaceFragment(R.id.fragment_container, currentCharactersInfo)
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            replaceFragment(R.id.details_container, currentCharactersInfo)
        }
    }

    private fun addFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, AllCharactersListFragment())
            .addToBackStack(BACK_STACK_LIST)
            .commit()
    }

    private fun replaceFragment(containerId: Int, charactersInfo: CharactersInfo) {
        supportFragmentManager.popBackStack(BACK_STACK_DETAILS, POP_BACK_STACK_INCLUSIVE)
        val detailsFragment = DetailsFragment()
        val bundle = Bundle()
        bundle.putParcelable(CURRENT_CHARACTER_FOR_DETAIL_SCREEN, charactersInfo)
        detailsFragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, detailsFragment, "DetailsFragment")
            .addToBackStack(BACK_STACK_DETAILS)
            .commit()
    }


    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (supportFragmentManager.backStackEntryCount == 1) {
                finish()
            }
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack("BACK_STACK_DETAILS",
                    POP_BACK_STACK_INCLUSIVE)
            }
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish()
        }
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(CURRENT_CHARACTER_THIS, charactersInfoSuper)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        charactersInfoSuper = savedInstanceState.getParcelable(CURRENT_CHARACTER_THIS)!!
    }
}