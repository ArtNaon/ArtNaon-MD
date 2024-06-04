package com.example.artnaon.ui.view.detail

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.artnaon.databinding.ItemDetailArtBinding

class DetailAdapter(private val artImages: List<Int>) : RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemDetailArtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(artImages[position])
    }

    override fun getItemCount(): Int = artImages.size

    class DetailViewHolder(private val binding: ItemDetailArtBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageResId: Int) {
            binding.ivItemDetailArt.setImageResource(imageResId)
        }
    }
}