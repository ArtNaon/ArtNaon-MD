package com.example.artnaon.ui.view.main

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityMainBinding
import com.example.artnaon.ui.view.upload.UploadActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var genreAdapter: GenreAdapter
    private lateinit var artAdapter: ArtAdapter
    private val images = listOf(
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art,
        R.drawable.dummy_art
    )

    private val genres = listOf(
        Genre("Romanticism"),
        Genre("Abstract"),
        Genre("Fauvism"),
        Genre("Pop Art")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvMainGenre.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        genreAdapter = GenreAdapter(genres)
        binding.rvMainGenre.adapter = genreAdapter

        binding.rvMainArt.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        artAdapter = ArtAdapter(images)
        binding.rvMainArt.adapter = artAdapter

        setupActionBar()
        setupSearch()
    }

    private fun setupActionBar() {
        binding.ivActionBarSearch.setOnClickListener {
            binding.tvActionBarName.visibility = View.GONE
            binding.searchViewActionBar.visibility = View.VISIBLE
            binding.searchViewActionBar.requestFocus()
        }

        binding.ivActionBarUpload.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun setupSearch() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchViewActionBar.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setQueryHint(context.getString(R.string.search_hint))
            val searchTextViewId = resources.getIdentifier("android:id/search_src_text", null, null)
            val searchTextView = findViewById<TextView>(searchTextViewId)
            searchTextView?.let {
                it.typeface = ResourcesCompat.getFont(context, R.font.sfui_regular)
                it.textSize = 13f
            }
            setIconifiedByDefault(false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        if (isDataAvailable(it)) {

                        } else {
                            toastMessage("Maat tidak terdapat data $it")
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

            setOnCloseListener {
                visibility = View.GONE
                binding.tvActionBarName.visibility = View.VISIBLE
                false
            }
        }
    }

    private fun isDataAvailable(query: String): Boolean {
        return genres.any { it.name.equals(query, ignoreCase = true) }
    }

    private fun toastMessage (message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.searchViewActionBar.visibility == View.VISIBLE) {
            binding.searchViewActionBar.visibility = View.GONE
            binding.tvActionBarName.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}