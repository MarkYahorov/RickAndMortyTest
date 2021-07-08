package com.example.rickandmortytest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.rickandmortytest.data.CharactersInfo
import com.example.rickandmortytest.screens.AllCharactersListFragment
import com.example.rickandmortytest.screens.DetailsFragment

class MainActivity : AppCompatActivity(), AllCharactersListFragment.ItemOfRecyclerClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFragment()
    }

    private fun setFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, AllCharactersListFragment())
            .addToBackStack(null)
            .commit()
        supportFragmentManager.executePendingTransactions()

    }

    override fun goToDetailsScreen(currentCharactersInfo: CharactersInfo) {
        val bundle = Bundle()
        val detailsFragment = DetailsFragment()
        bundle.putParcelable("CURRENT_CHARACTER", currentCharactersInfo)
        detailsFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailsFragment)
            .addToBackStack(null)
            .commit()
        supportFragmentManager.executePendingTransactions()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate("", POP_BACK_STACK_INCLUSIVE)
        }
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        }
        super.onBackPressed()
    }
}