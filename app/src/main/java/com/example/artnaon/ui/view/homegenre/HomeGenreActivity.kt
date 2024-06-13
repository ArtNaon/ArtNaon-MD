package com.example.artnaon.ui.view.homegenre

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityHomeGenreBinding
import com.example.artnaon.ui.view.main.ArtAdapter

class HomeGenreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeGenreBinding
    private lateinit var homeGenreAdapter: ArtAdapter

    private val images = listOf(
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeGenreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvHomeGenre.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        homeGenreAdapter = ArtAdapter(images)
        binding.rvHomeGenre.adapter = homeGenreAdapter

        binding.ivHomeGenreBack.setOnClickListener {
            onBackPressed()
        }
    }
}