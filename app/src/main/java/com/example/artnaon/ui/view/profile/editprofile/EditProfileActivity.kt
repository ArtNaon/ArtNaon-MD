package com.example.artnaon.ui.view.profile.editprofile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.helper.uriToFile
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityEditProfileBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.profile.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var selectedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.changePhotoTextView.setOnClickListener {
            openGallery()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.changeButton.setOnClickListener {
            val name = binding.edtUsernameEditProfile.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val picturePart = selectedImageFile?.let { convertFileToMultipart(it) }

            showLoading(true)
            viewModel.editProfile(name, password, picturePart)
        }

        viewModel.editProfileResult.observe(this) { result ->
            showLoading(false)
            if (result != null && result.status == "success") {
                binding.edtUsernameEditProfile.setText(result.result?.nameEditProfile)
                val pictureUrl = result.result?.pictureEditProfile
                if (pictureUrl != null && pictureUrl != "Nothing is changed") {
                    Glide.with(this)
                        .load(pictureUrl)
                        .placeholder(R.drawable.dummy_art)
                        .error(R.drawable.ic_launcher_background)
                        .into(binding.profileImageView)
                } else {
                    Log.e("EditProfileActivity", "Picture URL is null or 'Nothing is changed'")
                }
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Log.e("EditProfileActivity", "Edit Profile Failed: ${result?.message}")
            }

        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImageFile = uriToFile(selectedImg, this)
            binding.profileImageView.setImageURI(selectedImg)
        }
    }

    private fun convertFileToMultipart(file: File): MultipartBody.Part {
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("profilePicture", file.name, requestBody)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pgEditPost.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
