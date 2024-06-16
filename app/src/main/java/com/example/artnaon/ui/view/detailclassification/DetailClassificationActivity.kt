package com.example.artnaon.ui.view.detailclassification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.data.api.ApiService
import com.example.artnaon.data.response.ClassifyResult
import com.example.artnaon.databinding.ActivityDetailClassificationBinding
import com.example.artnaon.ui.view.detail.DetailActivity
import com.example.artnaon.ui.view.detail.DetailAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailClassificationActivity : AppCompatActivity(), DetailAdapter.OnItemClickListener {

    private lateinit var binding: ActivityDetailClassificationBinding
    private lateinit var detailAdapter: DetailAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiConfig().getApiService()
        setupRecyclerView()

        val result = intent.getSerializableExtra("result") as? ClassifyResult
        val imageUri = intent.getStringExtra("imageUri")?.let { Uri.parse(it) }

        if (result != null) {
            Log.d("DetailClassification", "Genre: ${result.genre}, Description: ${result.description}")
            binding.tvDetailClassGenre.text = result.genre
            binding.tvDetailClassDesc.text = result.description
            fetchSimilarGenrePaintings(result.genre ?: "")
        } else {
            Log.e("DetailClassification", "Result is null")
        }

        if (imageUri != null) {
            Glide.with(this).load(imageUri).into(binding.ivDetailClassArt)
        } else {
            Log.e("DetailClassification", "Image URI is null")
        }

        binding.ivDetailClassBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        detailAdapter = DetailAdapter(emptyList(), this)
        binding.rvDetailClassSimilarGenre.apply {
            layoutManager = LinearLayoutManager(this@DetailClassificationActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = detailAdapter
        }
    }

    private fun fetchSimilarGenrePaintings(genre: String) {
        showLoading(true)
        Log.d("DetailClassification", "Fetching paintings for genre: $genre")  // Tambahkan log di sini
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getPaintingsByGenre(mapOf("genre" to genre))
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Log.d("DetailClassification", "Response: $response")  // Tambahkan log di sini
                    if (response.status == "success") {
                        val paintings = response.data?.filterNotNull() ?: emptyList()
                        detailAdapter.updateData(paintings)
                    } else {
                        Toast.makeText(this@DetailClassificationActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@DetailClassificationActivity, "Failed to load similar genre paintings", Toast.LENGTH_SHORT).show()
                    Log.e("DetailClassification", "Error fetching paintings", e)  // Tambahkan log di sini
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pgDetailClassification.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onItemClick(imageUrl: String) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("imageUrl", imageUrl)
        }
        startActivity(intent)
    }
}
