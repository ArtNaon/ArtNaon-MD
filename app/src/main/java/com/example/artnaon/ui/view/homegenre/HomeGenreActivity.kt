package com.example.artnaon.ui.view.homegenre

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.databinding.ActivityHomeGenreBinding
import com.example.artnaon.ui.view.detail.DetailActivity
import com.example.artnaon.ui.view.home.PaintingAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeGenreActivity : AppCompatActivity(), PaintingAdapter.OnItemClickListener {

    private lateinit var binding: ActivityHomeGenreBinding
    private lateinit var paintingAdapter: PaintingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeGenreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val genre = intent.getStringExtra("GENRE_NAME") ?: ""
        setupRecyclerView()
        fetchGenrePaintings(genre)

        binding.ivHomeGenreBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        paintingAdapter = PaintingAdapter(emptyList(), this)
        binding.rvHomeGenre.apply {
            layoutManager = GridLayoutManager(this@HomeGenreActivity, 2)
            adapter = paintingAdapter
        }
    }

    private fun fetchGenrePaintings(genre: String) {
        val apiConfig = ApiConfig()
        val apiService = apiConfig.getApiService()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getPaintingsByGenre(mapOf("genre" to genre))
                if (response.status == "success") {
                    val paintings = response.data ?: emptyList()
                    withContext(Dispatchers.Main) {
                        paintingAdapter.updateData(paintings)
                        binding.tvHomeGenreName.text = genre
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HomeGenreActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeGenreActivity", "Error fetching paintings", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeGenreActivity, "Failed to load paintings", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onItemClick(imageUrl: String) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("imageUrl", imageUrl)
        }
        startActivity(intent)
    }
}
