package com.example.artnaon.ui.view.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.artnaon.R
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.data.api.ApiService
import com.example.artnaon.data.pref.UserPreference
import com.example.artnaon.data.pref.dataStore
import com.example.artnaon.data.response.DetailResult
import com.example.artnaon.data.response.ListPaintingResponse
import com.example.artnaon.data.response.Result
import com.example.artnaon.databinding.ActivityDetailBinding
import com.example.artnaon.ui.view.home.PaintingAdapter
import com.example.artnaon.ui.view.main.ArtAdapter
import com.example.artnaon.ui.view.main.GenreAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity(), DetailAdapter.OnItemClickListener {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailAdapter: DetailAdapter
    private lateinit var apiService: ApiService
    private lateinit var apiConfig: ApiConfig
    private lateinit var userPreference: UserPreference
    private var isSaved: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiConfig = ApiConfig()
        apiService = apiConfig.getApiService()
        userPreference = UserPreference.getInstance(applicationContext.dataStore)

        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        Glide.with(this).load(imageUrl).into(binding.ivDetailArt)

        fetchPaintingDetails(imageUrl)

        binding.rvDetailSimilarGenre.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        detailAdapter = DetailAdapter(emptyList(), this)
        binding.rvDetailSimilarGenre.adapter = detailAdapter

        binding.ivDetailBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnDetailSave.setOnClickListener {
            checkAndSavePainting(imageUrl)
        }
    }

    private fun fetchPaintingDetails(imageUrl: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.paintingsDetail(imageUrl)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.status == "success") {
                        displayPaintingDetails(response.result)
                        response.result?.genre?.let { genre ->
                            fetchSimilarGenrePaintings(genre)
                        }
                    } else {
                        // handle error
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    // handle exception
                }
            }
        }
    }

    private fun fetchSimilarGenrePaintings(genre: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getPaintingsByGenre(mapOf("genre" to genre))
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.status == "success") {
                        val paintings = response.data?.filterNotNull() ?: emptyList()
                        detailAdapter.updateData(paintings)
                    } else {
                        Toast.makeText(this@DetailActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@DetailActivity, "Failed to load similar genre paintings", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayPaintingDetails(result: DetailResult?) {
        result?.let {
            binding.tvDetailName.text = it.name
            binding.tvDetailDesc.text = it.description
            binding.tvDetailGenre.text = it.genre
            Glide.with(this).load(it.picture).into(binding.ivDetailProfile)
        }
    }

    private fun logResponse(response: ListPaintingResponse) {
        Log.d("API Response", "Status: ${response.status}, Message: ${response.message}, Result: ${response.result}")
    }

    private fun checkAndSavePainting(imageUrl: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userSession = userPreference.getSession().first()
                val email = userSession.email

                val likedPaintingsResponse = apiService.getLikedPaintings(email)
                logResponse(likedPaintingsResponse)
                val likedPaintings = likedPaintingsResponse.result ?: emptyList()

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (likedPaintings.contains(imageUrl)) {
                        Toast.makeText(this@DetailActivity, "Sorry, the photo has already been saved.", Toast.LENGTH_SHORT).show()
                    } else {
                        savePainting(email, imageUrl, userSession.name)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@DetailActivity, "Error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun savePainting(email: String, imageUrl: String, username: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.likePaintings(email, imageUrl)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.status == "success") {
                        isSaved = true
                        Toast.makeText(this@DetailActivity, "User $username has saved the photo.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DetailActivity, "Failed to save the photo.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@DetailActivity, "Error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun showLoading(isLoading: Boolean) {
        binding.pgDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

