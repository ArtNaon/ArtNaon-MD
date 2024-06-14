// MyPostAdapter.kt
package com.example.artnaon.ui.view.mypost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artnaon.R
import com.google.android.material.imageview.ShapeableImageView

class MyPostAdapter(private var paintings: List<String?>) : RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder>() {

    class MyPostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ShapeableImageView = view.findViewById(R.id.iv_item_art)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_art, parent, false)
        return MyPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        val paintingUrl = paintings[position]
        Glide.with(holder.itemView.context)
            .load(paintingUrl)
            .placeholder(R.drawable.dummy_art) // Optional placeholder
            .error(R.drawable.ic_launcher_background) // Optional error image
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = paintings.size

    fun updateData(newPaintings: List<String?>) {
        paintings = newPaintings
        notifyDataSetChanged()
    }
}