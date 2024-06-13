package com.example.artnaon.ui.view.profile.mypost

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityMyPostBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.home.PaintingAdapter
import com.example.artnaon.ui.view.main.MainViewModel
import com.example.artnaon.ui.view.profile.ProfileViewModel

class MyPostActivity : AppCompatActivity() {

    private lateinit var adapter: PaintingAdapter
    private lateinit var binding: ActivityMyPostBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvMainArt.layoutManager = GridLayoutManager(this, 2)

        setupViewModel()
    }

    private fun setupViewModel() {
        mainViewModel.getSession().observe(this) { userModel ->
            userModel?.let {
                viewModel.fetchUserPaintings(it.email)
            }
        }

        viewModel.paintings.observe(this) { paintings ->
            adapter = PaintingAdapter(paintings)
            binding.rvMainArt.adapter = adapter
        }
    }

}