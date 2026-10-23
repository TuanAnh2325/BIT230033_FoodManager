package com.example.bit230033_foodmanager.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bit230033_foodmanager.R
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishImage

class ImageGalleryAdapter : RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder>() {

    private val images = mutableListOf<BIT230033_DishImage>()

    fun submitList(newImages: List<BIT230033_DishImage>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    fun getImageAt(position: Int): BIT230033_DishImage? {
        return images.getOrNull(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = images[position]
        holder.imageView.load(item.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_food_placeholder)
            error(R.drawable.ic_image_broken)
        }
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
