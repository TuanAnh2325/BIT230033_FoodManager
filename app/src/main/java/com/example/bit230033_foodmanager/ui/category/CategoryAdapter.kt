package com.example.bit230033_foodmanager.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import com.example.bit230033_foodmanager.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onDeleteClick: (BIT230033_DishCategory) -> Unit
) : ListAdapter<BIT230033_DishCategory, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: BIT230033_DishCategory) {
            val context = binding.root.context
            binding.tvCatName.text = com.example.bit230033_foodmanager.utils.CategoryLocalizationHelper.getLocalizedCategoryName(context, category.name)
            binding.tvCatDesc.text = com.example.bit230033_foodmanager.utils.CategoryLocalizationHelper.getLocalizedCategoryDescription(context, category.name, category.description) ?: ""

            binding.btnDeleteCat.setOnClickListener {
                onDeleteClick(category)
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<BIT230033_DishCategory>() {
        override fun areItemsTheSame(oldItem: BIT230033_DishCategory, newItem: BIT230033_DishCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BIT230033_DishCategory, newItem: BIT230033_DishCategory): Boolean {
            return oldItem == newItem
        }
    }
}
