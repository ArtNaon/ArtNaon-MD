package com.example.artnaon.ui.view.main

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityMainBinding
import com.example.artnaon.ui.view.upload.UploadActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.ivActionBarUpload.setOnClickListener {
//            startActivity(Intent(this, UploadActivity::class.java))
//        }
    }

//    @SuppressLint("DiscouragedApi")
//    private fun setupSearch() {
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        binding.searchViewActionBar.apply {
//            setSearchableInfo(searchManager.getSearchableInfo(componentName))
//            setQueryHint(context.getString(R.string.search_hint))
//            val searchTextViewId = resources.getIdentifier("android:id/search_src_text", null, null)
//            val searchTextView = findViewById<TextView>(searchTextViewId)
//            searchTextView?.let {
//                it.typeface = ResourcesCompat.getFont(context, R.font.sfui_regular)
//                it.textSize = 13f
//            }
//            setIconifiedByDefault(false)
//            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    query?.let {
//                        if (isDataAvailable(it)) {
//
//                        } else {
//                            toastMessage("Maat tidak terdapat data $it")
//                        }
//                    }
//                    return true
//                }
//
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    return true
//                }
//            })
//
//            setOnCloseListener {
//                visibility = View.GONE
//                binding.tvActionBarName.visibility = View.VISIBLE
//                false
//            }
//        }
//    }

//    private fun isDataAvailable(query: String): Boolean {
//        return genres.any { it.name.equals(query, ignoreCase = true) }
//    }
//        val navView: BottomNavigationView = binding.navView
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//
//        navView.setupWithNavController(navController)
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
