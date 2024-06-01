package com.example.artnaon.ui.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.artnaon.databinding.ItemArtBinding


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
        }
    }
}