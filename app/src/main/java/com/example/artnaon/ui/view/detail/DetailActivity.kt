package com.example.artnaon.ui.view.detail

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityDetailBinding
import com.example.artnaon.ui.view.main.ArtAdapter
import com.example.artnaon.ui.view.main.GenreAdapter

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailAdapter: DetailAdapter

    private val images = listOf(
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvDetailSimilarGenre.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        detailAdapter = DetailAdapter(images)
        binding.rvDetailSimilarGenre.adapter = detailAdapter

        binding.ivDetailBack.setOnClickListener {
            onBackPressed()
        }
    }
}