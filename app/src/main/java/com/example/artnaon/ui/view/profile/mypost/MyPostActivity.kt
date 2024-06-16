// MyPostActivity.kt
package com.example.artnaon.ui.view.profile.mypost

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityMyPostBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.main.MainViewModel
import com.example.artnaon.ui.view.profile.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPostActivity : AppCompatActivity() {

    private lateinit var adapter: MyPostAdapter
    private lateinit var binding: ActivityMyPostBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvMainArt.layoutManager = GridLayoutManager(this, 2)

        binding.ivHomeGenreBack.setOnClickListener {
            finish()
        }
        setupViewModel()
    }

    private fun setupViewModel() {
        mainViewModel.getSession().observe(this) { userModel ->
            userModel?.let {
                showLoading(true)
                viewModel.fetchUserDetails(it.email)
            }
        }

        viewModel.userDetails.observe(this) { userDetails ->
            showLoading(false)
            userDetails?.result?.let { paintings ->
                adapter = MyPostAdapter(paintings) { paintingUrl ->
                    showDeleteConfirmationDialog(paintingUrl)
                }
                binding.rvMainArt.adapter = adapter
            }
        }
    }

    private fun showDeleteConfirmationDialog(paintingUrl: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Photo")
            .setMessage("Are you sure you want to delete the photo?")
            .setPositiveButton("Yes") { dialog, _ ->
                deletePainting(paintingUrl)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deletePainting(paintingUrl: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = viewModel.deletePainting(paintingUrl)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.status == "success") {
                        Toast.makeText(this@MyPostActivity, "Painting deleted successfully", Toast.LENGTH_SHORT).show()
                        mainViewModel.getSession().observe(this@MyPostActivity) { userModel ->
                            userModel?.let {
                                viewModel.fetchUserDetails(it.email)
                            }
                        }
                    } else {
                        Toast.makeText(this@MyPostActivity, "Failed to delete painting", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@MyPostActivity, "Error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pgMyPost.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
