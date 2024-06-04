package com.example.artnaon.ui.view.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.artnaon.databinding.ItemArtBinding
import com.example.artnaon.ui.view.detail.DetailActivity
import com.example.artnaon.ui.view.signin.SignInActivity


class ArtAdapter(private val artImages: List<Int>) : RecyclerView.Adapter<ArtAdapter.ArtViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val binding = ItemArtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        holder.bind(artImages[position])
    }

    override fun getItemCount(): Int = artImages.size

    class ArtViewHolder(private val binding: ItemArtBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageResId: Int) {
            binding.ivItemArt.setImageResource(imageResId)
            binding.ivItemArt.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                itemView.context.startActivity(intent)            }
        }
    }
}