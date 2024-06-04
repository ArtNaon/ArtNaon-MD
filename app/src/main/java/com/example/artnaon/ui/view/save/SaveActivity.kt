package com.example.artnaon.ui.view.save

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityHomeGenreBinding
import com.example.artnaon.databinding.ActivitySaveBinding
import com.example.artnaon.ui.view.main.ArtAdapter

class SaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaveBinding
    private lateinit var saveAdapter: ArtAdapter

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
        binding = ActivitySaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvSave.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        saveAdapter = ArtAdapter(images)
        binding.rvSave.adapter = saveAdapter

        binding.ivSaveBack.setOnClickListener {
            onBackPressed()
        }
    }
}