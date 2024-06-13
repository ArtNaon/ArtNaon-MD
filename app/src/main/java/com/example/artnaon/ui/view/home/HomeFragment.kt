package com.example.artnaon.ui.view.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artnaon.R
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.databinding.FragmentHomeBinding
import com.example.artnaon.ui.view.main.Genre
import com.example.artnaon.ui.view.main.GenreAdapter
import com.example.artnaon.ui.view.upload.UploadActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var genreAdapter: GenreAdapter
    private lateinit var paintingAdapter: PaintingAdapter

    private val genres = listOf(
        Genre("Romanticism"),
        Genre("Abstract")

    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearch()
        setupActionBar()
        fetchPaintings()
    }

    private fun setupRecyclerViews() {
        // Setup RecyclerView for genres
        genreAdapter = GenreAdapter(genres)
        binding.rvMainGenre.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = genreAdapter
        }

        // Setup RecyclerView for art
        paintingAdapter = PaintingAdapter(emptyList())
        binding.rvMainArt.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = paintingAdapter
        }
    }

    private fun fetchPaintings() {
        val apiService = ApiConfig().getApiService()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getHomePage()
                val paintings = response.result ?: emptyList()
                Log.d("HomeFragment", "Paintings: $paintings")
                paintingAdapter.updateData(paintings)
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error fetching paintings", e)
                Toast.makeText(requireContext(), "Failed to load paintings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearch() {
        val searchManager = requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchViewActionBar.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            setQueryHint(getString(R.string.search_hint))
            val searchTextViewId = resources.getIdentifier("android:id/search_src_text", null, null)
            val searchTextView = findViewById<TextView>(searchTextViewId)
            searchTextView?.let {
                it.typeface = ResourcesCompat.getFont(requireContext(), R.font.sfui_regular)
                it.textSize = 13f
            }
            setIconifiedByDefault(false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        if (isDataAvailable(it)) {
                            // Handle data available case
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

    private fun setupActionBar() {
        binding.ivActionBarSearch.setOnClickListener {
            binding.tvActionBarName.visibility = View.GONE
            binding.searchViewActionBar.visibility = View.VISIBLE
            binding.searchViewActionBar.requestFocus()
        }

        binding.ivActionBarUpload.setOnClickListener {
            val intent = Intent(requireContext(), UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isDataAvailable(query: String): Boolean {
        return genres.any { it.name.equals(query, ignoreCase = true) }
    }

    private fun toastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        fetchPaintings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
