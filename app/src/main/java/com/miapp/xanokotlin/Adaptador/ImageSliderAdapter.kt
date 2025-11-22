package com.miapp.xanokotlin.Adaptador

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.xanokotlin.databinding.ItemImageSliderBinding
import com.miapp.xanokotlin.model.ApiImage

class ImageSliderAdapter(private val images: List<ApiImage>) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(private val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(apiImage: ApiImage) {
            Glide.with(itemView.context)
                .load(apiImage.url)
                .into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}