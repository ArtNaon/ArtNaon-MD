package com.example.artnaon.ui.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.artnaon.R
import com.example.artnaon.ui.view.homegenre.HomeGenreActivity


class GenreAdapter(private var genres: List<Genre>) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {

    fun updateData(newGenres: List<Genre>) {
        genres = newGenres
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(genres[position])
    }

    override fun getItemCount(): Int {
        return genres.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val genreButton: Button = itemView.findViewById(R.id.tv_item_genre)

        fun bind(genre: Genre) {
            genreButton.text = genre.name
            genreButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, HomeGenreActivity::class.java).apply {
                    putExtra("GENRE_NAME", genre.name)
                }
                context.startActivity(intent)
            }
        }
    }
}