// MyPostActivity.kt
package com.example.artnaon.ui.view.profile.mypost

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityMyPostBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.main.MainViewModel
import com.example.artnaon.ui.view.mypost.MyPostAdapter
import com.example.artnaon.ui.view.profile.ProfileViewModel

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
                adapter = MyPostAdapter(paintings)
                binding.rvMainArt.adapter = adapter
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pgMyPost.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
