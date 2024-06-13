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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.helper.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.helper.uriToFile
import com.example.artnaon.R
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.databinding.ActivityUploadBinding
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

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val TAG = "UploadActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupSpinner()
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

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.genre_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerUploadGenre.adapter = adapter
        }

        binding.spinnerUploadGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGenre = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedGenre = ""
            }
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
            val file = uriToFile(uri, this) // Mengonversi URI ke File
            val processedFile = file.reduceFileImage() // Mengurangi ukuran gambar

            val requestFile = RequestBody.create("image/*".toMediaType(), processedFile)
            val body = MultipartBody.Part.createFormData("painting", processedFile.name, requestFile)
            val genreBody = RequestBody.create("text/plain".toMediaType(), genre)
            val descriptionBody = RequestBody.create("text/plain".toMediaType(), description)

            showLoading(true)

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig().getApiService()
                    val response = apiService.uploadPainting(genreBody, descriptionBody, body)
                    showLoading(false)
                    if (response.status == "success") {
                        showToast(response.message)
                        finish()  // Close the activity and return to the previous screen
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
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
}
