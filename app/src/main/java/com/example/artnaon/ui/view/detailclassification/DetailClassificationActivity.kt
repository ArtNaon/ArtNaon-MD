package com.example.artnaon.ui.view.detailclassification

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityDetailClassificationBinding
import com.example.artnaon.ui.view.detail.DetailAdapter

class DetailClassificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailClassificationBinding
    private lateinit var detailClassAdapter: DetailAdapter

    private val images = listOf(
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvDetailClassSimilarGenre.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        detailClassAdapter = DetailAdapter(images)
        binding.rvDetailClassSimilarGenre.adapter = detailClassAdapter

        binding.ivDetailClassBack.setOnClickListener {
            onBackPressed()
        }
    }
}