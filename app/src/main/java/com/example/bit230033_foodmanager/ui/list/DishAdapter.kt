package com.example.bit230033_foodmanager.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bit230033_foodmanager.R
import com.example.bit230033_foodmanager.data.local.entity.DishWithDetails
import com.example.bit230033_foodmanager.databinding.ItemDishBinding
import java.text.NumberFormat
import java.util.Locale

class DishAdapter(
    private val onItemClick: (DishWithDetails) -> Unit
) : ListAdapter<DishWithDetails, DishAdapter.DishViewHolder>(DishDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val binding = ItemDishBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DishViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DishViewHolder(
        private val binding: ItemDishBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(item: DishWithDetails) {
            val context = binding.root.context
            val dish = item.dish

            // Category name
            val rawCatName = item.category?.name ?: context.getString(R.string.filter_category)
            binding.tvDishCategory.text = com.example.bit230033_foodmanager.utils.CategoryLocalizationHelper.getLocalizedCategoryName(context, rawCatName)

            // Dish name
            binding.tvDishName.text = dish.name

            // Price formatted with comma separator
            val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
            binding.tvDishPrice.text = "${formatter.format(dish.price)} VND"


            // Availability Badge
            if (dish.isAvailable) {
                binding.tvDishStatusBadge.text = context.getString(R.string.badge_available)
                binding.tvDishStatusBadge.setBackgroundResource(R.drawable.bg_badge_available)
                binding.tvDishStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_available_text))
            } else {
                binding.tvDishStatusBadge.text = context.getString(R.string.badge_unavailable)
                binding.tvDishStatusBadge.setBackgroundResource(R.drawable.bg_badge_unavailable)
                binding.tvDishStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_unavailable_text))
            }

            // Preparation Time Badge (Requirement 4.3: Quick if <= 15 mins, Regular if > 15 mins)
            val timeText = context.getString(R.string.time_mins, dish.preparationTime)
            if (dish.isQuick) {
                binding.tvPrepTimeBadge.text = "⚡ ${context.getString(R.string.badge_quick)} • $timeText"
                binding.tvPrepTimeBadge.setBackgroundResource(R.drawable.bg_badge_quick)
                binding.tvPrepTimeBadge.setTextColor(ContextCompat.getColor(context, R.color.status_quick_text))
            } else {
                binding.tvPrepTimeBadge.text = "⏱️ ${context.getString(R.string.badge_regular)} • $timeText"
                binding.tvPrepTimeBadge.setBackgroundResource(R.drawable.bg_badge_regular)
                binding.tvPrepTimeBadge.setTextColor(ContextCompat.getColor(context, R.color.status_regular_text))
            }

            // Thumbnail Image loading with Coil
            val thumbnailUrl = item.thumbnailUrl
            binding.ivDishThumbnail.load(thumbnailUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_food_placeholder)
                error(R.drawable.ic_image_broken)
            }
        }
    }

    private class DishDiffCallback : DiffUtil.ItemCallback<DishWithDetails>() {
        override fun areItemsTheSame(oldItem: DishWithDetails, newItem: DishWithDetails): Boolean {
            return oldItem.dish.id == newItem.dish.id
        }

        override fun areContentsTheSame(oldItem: DishWithDetails, newItem: DishWithDetails): Boolean {
            return oldItem == newItem
        }
    }
}
