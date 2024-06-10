package com.example.artnaon.ui.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artnaon.R
import com.google.android.material.imageview.ShapeableImageView

class PaintingAdapter(private val paintings: List<String?>) : RecyclerView.Adapter<PaintingAdapter.PaintingViewHolder>() {

    class PaintingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ShapeableImageView = view.findViewById(R.id.iv_item_art)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaintingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_art, parent, false)
        return PaintingViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaintingViewHolder, position: Int) {
        val paintingUrl = paintings[position]
        Glide.with(holder.itemView.context)
            .load(paintingUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = paintings.size
}