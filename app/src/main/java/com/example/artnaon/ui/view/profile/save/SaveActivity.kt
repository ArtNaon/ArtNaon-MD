package com.example.artnaon.ui.view.profile.save

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.artnaon.R
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.data.api.ApiService
import com.example.artnaon.data.pref.UserPreference
import com.example.artnaon.data.pref.dataStore
import com.example.artnaon.databinding.ActivityHomeGenreBinding
import com.example.artnaon.databinding.ActivitySaveBinding
import com.example.artnaon.ui.view.detail.DetailActivity
import com.example.artnaon.ui.view.home.PaintingAdapter
import com.example.artnaon.ui.view.main.ArtAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SaveActivity : AppCompatActivity(), PaintingAdapter.OnItemClickListener {

    private lateinit var binding: ActivitySaveBinding
    private lateinit var saveAdapter: PaintingAdapter
    private lateinit var apiService: ApiService
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = ApiConfig().getApiService()
        userPreference = UserPreference.getInstance(applicationContext.dataStore)

        binding.rvSave.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        saveAdapter = PaintingAdapter(emptyList(), this)
        binding.rvSave.adapter = saveAdapter

        binding.ivSaveBack.setOnClickListener {
            onBackPressed()
        }

        loadLikedPaintings()
    }

    private fun loadLikedPaintings() {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userSession = userPreference.getSession().first()
                val email = userSession.email
                val response = apiService.getLikedPaintings(email)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.status == "success") {
                        val paintings = response.result ?: emptyList()
                        saveAdapter.updateData(paintings)
                    } else {
                        Toast.makeText(this@SaveActivity, "Failed to load liked paintings.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@SaveActivity, "Error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
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
        binding.pgSave.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
