package com.example.artnaon.ui.view.detail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.IOException
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.artnaon.R
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.data.api.ApiService
import com.example.artnaon.data.pref.UserPreference
import com.example.artnaon.data.pref.dataStore
import com.example.artnaon.data.response.DetailResult
import com.example.artnaon.data.response.ListPaintingResponse
import com.example.artnaon.databinding.ActivityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream

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
            val userSession = runBlocking { userPreference.getSession().first() }
            savePainting(userSession.email, imageUrl, userSession.name)
        }

        binding.btnDetailDownload.setOnClickListener {
            downloadImage(imageUrl)
        }

        binding.ivDetailShare.setOnClickListener {
            shareImage(imageUrl)
        }
    }

    private fun savePainting(email: String, imageUrl: String, username: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.likePaintings(mapOf("email" to email, "imageUrl" to imageUrl))
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.status == "success") {
                        isSaved = true
                        Toast.makeText(this@DetailActivity, "User $username has saved the photo.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DetailActivity, "Failed to save the photo.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (e.code() == 400) {
                        Toast.makeText(this@DetailActivity, "Painting already liked", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DetailActivity, "Error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun showLoading(isLoading: Boolean) {
        binding.pgDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun downloadImage(imageUrl: String) {
        showLoading(true)
        val request = DownloadManager.Request(Uri.parse(imageUrl))
            .setTitle("Downloading image")
            .setDescription("Downloading image from $imageUrl")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "downloaded_image_${System.currentTimeMillis()}.jpg")

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        showLoading(false)
        Toast.makeText(this, "Downloading image...", Toast.LENGTH_SHORT).show()
    }

    private fun shareImage(imageUrl: String) {
        showLoading(true)
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    shareBitmap(resource, imageUrl)
                    showLoading(false)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    showLoading(false)
                }
            })
    }

    private fun shareBitmap(bitmap: Bitmap, imageUrl: String) {
        val filename = "shared_image_${System.currentTimeMillis()}.jpg"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            shareUri(uri, imageUrl)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to share image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareUri(uri: Uri, imageUrl: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Check out this painting: $imageUrl")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(intent, "Share image via"))
    }

    override fun onItemClick(imageUrl: String) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("imageUrl", imageUrl)
        }
        startActivity(intent)
    }
}
