package com.example.bit230033_foodmanager.ui.form

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bit230033_foodmanager.R
import com.example.bit230033_foodmanager.databinding.ItemDishImageBinding

data class FormImageItem(
    val url: String,
    var isThumbnail: Boolean = false
)

class FormImageAdapter(
    private val onSetThumbnail: (Int) -> Unit,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<FormImageAdapter.FormImageViewHolder>() {

    private val items = mutableListOf<FormImageItem>()

    fun submitList(newItems: List<FormImageItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getItems(): List<FormImageItem> = items

    fun addImage(url: String, setAsThumbnail: Boolean = false) {
        if (setAsThumbnail || items.isEmpty()) {
            items.forEach { it.isThumbnail = false }
            items.add(FormImageItem(url, true))
        } else {
            items.add(FormImageItem(url, false))
        }
        notifyDataSetChanged()
    }

    fun setThumbnailAt(position: Int) {
        if (position in items.indices) {
            items.forEachIndexed { index, item ->
                item.isThumbnail = (index == position)
            }
            notifyDataSetChanged()
        }
    }

    fun removeAt(position: Int) {
        if (position in items.indices) {
            val wasThumbnail = items[position].isThumbnail
            items.removeAt(position)
            if (wasThumbnail && items.isNotEmpty()) {
                items[0].isThumbnail = true
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormImageViewHolder {
        val binding = ItemDishImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FormImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormImageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class FormImageViewHolder(
        private val binding: ItemDishImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FormImageItem) {
            binding.ivItemImage.load(item.url) {
                crossfade(true)
                placeholder(R.drawable.ic_food_placeholder)
                error(R.drawable.ic_image_broken)
            }

            binding.tvItemIsThumbnail.isVisible = item.isThumbnail
            binding.btnMakeThumbnail.isEnabled = !item.isThumbnail

            binding.btnMakeThumbnail.setOnClickListener {
                setThumbnailAt(bindingAdapterPosition)
                onSetThumbnail(bindingAdapterPosition)
            }

            binding.btnRemoveImage.setOnClickListener {
                removeAt(bindingAdapterPosition)
                onRemove(bindingAdapterPosition)
            }
        }
    }
}
