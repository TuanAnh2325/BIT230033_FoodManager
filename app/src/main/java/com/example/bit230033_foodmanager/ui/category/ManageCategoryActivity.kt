package com.example.bit230033_foodmanager.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bit230033_foodmanager.FoodApplication
import com.example.bit230033_foodmanager.R
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import com.example.bit230033_foodmanager.databinding.ActivityManageCategoryBinding
import com.example.bit230033_foodmanager.databinding.DialogAddCategoryBinding
import com.example.bit230033_foodmanager.ui.base.BaseActivity
import com.example.bit230033_foodmanager.utils.ValidationResult
import com.example.bit230033_foodmanager.utils.ValidationUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManageCategoryActivity : BaseActivity() {

    private lateinit var binding: ActivityManageCategoryBinding
    private lateinit var adapter: CategoryAdapter

    private val viewModel: CategoryViewModel by viewModels {
        val app = application as FoodApplication
        CategoryViewModel.Factory(app.categoryRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupToolbar() {
        binding.toolbarCategory.setNavigationOnClickListener { finish() }

        com.example.bit230033_foodmanager.utils.LanguageToggleHelper.setupToggle(
            this,
            binding.btnLangEnCategory,
            binding.btnLangViCategory
        )
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(
            onDeleteClick = { category ->
                confirmDeleteCategory(category)
            }
        )
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this@ManageCategoryActivity)
            adapter = this@ManageCategoryActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnConfirmAddCategory.setOnClickListener {
            val name = dialogBinding.etCatName.text.toString().trim()
            val desc = dialogBinding.etCatDesc.text.toString().trim()

            val validation = ValidationUtils.validateCategoryName(name)
            if (validation is ValidationResult.Error) {
                dialogBinding.tilCatName.error = getString(validation.messageResId)
                Toast.makeText(this@ManageCategoryActivity, validation.messageResId, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = viewModel.addCategory(name, desc)
                result.onSuccess {
                    Toast.makeText(this@ManageCategoryActivity, R.string.msg_category_saved, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }.onFailure { ex ->
                    val errorMsg = if (ex.message?.contains("already exists", ignoreCase = true) == true ||
                        ex.message?.contains("UNIQUE", ignoreCase = true) == true
                    ) {
                        getString(R.string.err_cat_name_duplicate)
                    } else {
                        ex.localizedMessage ?: getString(R.string.err_cat_name_duplicate)
                    }
                    dialogBinding.tilCatName.error = errorMsg
                    Toast.makeText(this@ManageCategoryActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialogBinding.btnCancelCategory.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun confirmDeleteCategory(category: BIT230033_DishCategory) {
        val catDisplayName = com.example.bit230033_foodmanager.utils.CategoryLocalizationHelper.getLocalizedCategoryName(this, category.name)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_cat_title)
            .setMessage(getString(R.string.dialog_delete_cat_msg, catDisplayName))
            .setNegativeButton(R.string.btn_cancel, null)
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                lifecycleScope.launch {
                    val result = viewModel.deleteCategory(category)
                    result.onSuccess {
                        Toast.makeText(this@ManageCategoryActivity, R.string.msg_category_deleted, Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        // Show clear toast and dialog when category is in use
                        Toast.makeText(this@ManageCategoryActivity, R.string.err_cat_in_use, Toast.LENGTH_LONG).show()
                        MaterialAlertDialogBuilder(this@ManageCategoryActivity)
                            .setTitle(R.string.dialog_delete_cat_title)
                            .setMessage(R.string.err_cat_in_use)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                    }
                }
            }
            .show()
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collectLatest { list ->
                    adapter.submitList(list)
                    binding.tvEmptyCategories.isVisible = list.isEmpty()
                    binding.rvCategories.isVisible = list.isNotEmpty()
                }
            }
        }
    }
}
