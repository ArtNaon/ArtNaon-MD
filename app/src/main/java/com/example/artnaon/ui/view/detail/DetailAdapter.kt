package com.example.artnaon.ui.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artnaon.R
import com.example.artnaon.databinding.ItemDetailArtBinding

class DetailAdapter(
    private var images: List<String>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(imageUrl: String)
    }

    class DetailViewHolder(val binding: ItemDetailArtBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemDetailArtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.dummy_art)
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.ivItemDetailArt)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(imageUrl)
        }
    }

    override fun getItemCount(): Int = images.size

    fun updateData(newImages: List<String>) {
        images = if (newImages.size > 7) newImages.subList(0, 7) else newImages
        notifyDataSetChanged()
    }
}
