package com.example.artnaon.ui.view.upload

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.artnaon.R
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.databinding.ActivityUploadBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSpinner()

        binding.ivUploadArt.setOnClickListener {
            openGallery()
        }

        binding.btnUpload.setOnClickListener {
            uploadPainting()
        }
    }

    private fun setupSpinner() {
        val items = resources.getStringArray(R.array.spinner_items).toList()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            items
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUploadGenre.adapter = adapter
        binding.spinnerUploadGenre.setSelection(0)
        binding.spinnerUploadGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val textView = parent.getChildAt(0) as? TextView
                textView?.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.ivUploadArt.setImageURI(selectedImageUri)
        }
    }

    private fun uploadPainting() {
        val userId = RequestBody.create("text/plain".toMediaTypeOrNull(), "123") // Replace with actual user ID
        val genre = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.spinnerUploadGenre.selectedItem.toString())
        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.edtCameraDesc.text.toString())

        val file = File(selectedImageUri?.path ?: "")
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val painting = MultipartBody.Part.createFormData("painting", file.name, requestFile)

        val apiService = ApiConfig.getApiService()


    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1
    }
}