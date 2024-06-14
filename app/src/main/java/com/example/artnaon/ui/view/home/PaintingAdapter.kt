package com.example.artnaon.ui.view.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artnaon.R
import com.google.android.material.imageview.ShapeableImageView

class PaintingAdapter(private var paintings: List<String?>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<PaintingAdapter.PaintingViewHolder>() {

    class PaintingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ShapeableImageView = view.findViewById(R.id.iv_item_art)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaintingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_art, parent, false)
        return PaintingViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaintingViewHolder, position: Int) {
        val paintingUrl = paintings[position]
        Log.d("PaintingAdapter", "Loading URL: $paintingUrl") // Logging URL
        Glide.with(holder.itemView.context)
            .load(paintingUrl)
            .placeholder(R.drawable.dummy_art) // Optional placeholder
            .error(R.drawable.ic_launcher_background) // Optional error image
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            paintingUrl?.let { url ->
                itemClickListener.onItemClick(url)
            }
        }
    }

    override fun getItemCount(): Int = paintings.size

    fun updateData(newPaintings: List<String?>) {
        paintings = newPaintings
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(imageUrl: String)
    }
}
