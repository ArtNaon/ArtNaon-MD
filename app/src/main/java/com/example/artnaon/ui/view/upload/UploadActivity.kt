// UploadActivity.kt
package com.example.artnaon.ui.view.upload

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.helper.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.helper.uriToFile
import com.example.artnaon.R
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.databinding.ActivityUploadBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.main.MainViewModel
import com.example.artnaon.ui.view.profile.ProfileViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private var selectedFileUri: Uri? = null
    private lateinit var selectedGenre: String
    private lateinit var email: String

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupAutoCompleteTextView()
        observeSession()
    }

    private fun observeSession() {
        mainViewModel.getSession().observe(this) { userModel ->
            if (userModel != null) {
                email = userModel.email
            }
        }
    }

    private fun setupListeners() {
        binding.btnAddImage.setOnClickListener {
            selectImage()
        }

        binding.btnUpload.setOnClickListener {
            val description = binding.edtCameraDesc.text.toString()
            if (selectedFileUri == null || description.isEmpty() || selectedGenre.isEmpty()) {
                showToast("Please fill all fields and select an image.")
            } else {
                uploadPainting(selectedGenre, description)
            }
        }

        binding.ivUploadBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupAutoCompleteTextView() {
        val genres = listOf("Abstract", "Expressionism", "Neoclassicism", "Primitivism", "Realism", "Romanticism", "Symbolism")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, genres)
        binding.edtUploadGenre.setAdapter(adapter)

        binding.edtUploadGenre.setOnItemClickListener { parent, view, position, id ->
            selectedGenre = parent.getItemAtPosition(position) as String
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Painting"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedFileUri = data.data
            Log.d(TAG, "Selected Image URI: $selectedFileUri")
            binding.ivUploadArt.setImageURI(selectedFileUri)
        }
    }

    private fun uploadPainting(genre: String, description: String) {
        if (!isNetworkAvailable(this)) {
            showToast("No internet connection. Please try again later.")
            return
        }

        selectedFileUri?.let { uri ->
            val file = uriToFile(uri, this)
            val processedFile = file.reduceFileImage()

            val requestFile = RequestBody.create("image/*".toMediaType(), processedFile)
            val body = MultipartBody.Part.createFormData("painting", processedFile.name, requestFile)
            val emailBody = RequestBody.create("text/plain".toMediaType(), email)
            val genreBody = RequestBody.create("text/plain".toMediaType(), genre)
            val descriptionBody = RequestBody.create("text/plain".toMediaType(), description)

            showLoading(true)

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig().getApiService()
                    val response = apiService.uploadPainting(emailBody, genreBody, descriptionBody, body)
                    showLoading(false)
                    if (response.status == "success") {
                        showToast(response.message)
                        fetchUserProfile()
                        finish()
                    } else {
                        showToast("Upload failed: ${response.message}")
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    showToast("Upload failed: ${e.message}")
                    Log.e(TAG, "Error uploading painting", e)
                }
            }
        } ?: showToast("No Image")
    }

    private fun fetchUserProfile() {
        profileViewModel.fetchUserDetails(email)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pgUpload.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val TAG = "UploadActivity"
    }
}