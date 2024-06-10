package com.example.artnaon.ui.view.main

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityMainBinding
import com.example.artnaon.ui.view.upload.UploadActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var genreAdapter: GenreAdapter
    private lateinit var artAdapter: ArtAdapter
    private val images = listOf(
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art
    )

    private val genres = listOf(
        Genre("Romanticism"),
        Genre("Abstract"),
        Genre("Fauvism"),
        Genre("Pop Art")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)


        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

