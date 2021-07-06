package com.example.rickandmortytest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        setFragment()
    }

    private fun setFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AllCharactersListFragment())
            .addToBackStack(null)
            .commit()
    }

}