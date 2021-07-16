package com.example.RickAndMortyTest

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.RickAndMortyTest.data.CharactersInfo
import com.example.RickAndMortyTest.screens.AllCharactersListFragment
import com.example.RickAndMortyTest.screens.DetailsFragment

const val CURRENT_CHARACTER_FOR_DETAIL_SCREEN = "CURRENT_CHARACTER"
const val RICK_AND_MORTY_DB = "RICK_AND_MORTY.db"
const val VERSION_DB = 1

class MainActivity : AppCompatActivity(), AllCharactersListFragment.OpenDetailNavigator {

    companion object {
        private const val CURRENT_CHARACTER_THIS = "CURRENT_CHARACTER_THIS"
        private const val BACK_STACK_DETAILS = "DETAILS"
        private const val BACK_STACK_LIST = "LIST"
    }

    private var charactersInfoSuper: CharactersInfo = App.INSTANCE.createEmptyCharacter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            addListFragment()
        } else {
            charactersInfoSuper =
                savedInstanceState.getParcelable(CURRENT_CHARACTER_THIS)!!

            setDetailsFragment(charactersInfoSuper)
        }
    }

    private fun setDetailsFragment(charactersInfo: CharactersInfo?) {
        replaceFragmentOfCertainConfiguration(Configuration.ORIENTATION_PORTRAIT,
            R.id.fragment_container,
            charactersInfo)
        replaceFragmentOfCertainConfiguration(Configuration.ORIENTATION_LANDSCAPE,
            R.id.details_container,
            charactersInfo)
    }

    private fun replaceFragmentOfCertainConfiguration(
        orientation: Int,
        idContainer: Int,
        charactersInfo: CharactersInfo?,
    ) {
        if (resources.configuration.orientation == orientation) {
            if (supportFragmentManager.popBackStackImmediate(BACK_STACK_DETAILS,
                    POP_BACK_STACK_INCLUSIVE) && charactersInfo != null
            ) {
                replaceFragment(idContainer, charactersInfo)
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

    private fun addListFragment() {
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
}