package com.example.bit230033_foodmanager.ui.list

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bit230033_foodmanager.FoodApplication
import com.example.bit230033_foodmanager.databinding.ActivityMainBinding
import com.example.bit230033_foodmanager.ui.base.BaseActivity
import com.example.bit230033_foodmanager.ui.category.ManageCategoryActivity
import com.example.bit230033_foodmanager.ui.detail.DishDetailActivity
import com.example.bit230033_foodmanager.ui.filter.FilterBottomSheetDialog
import com.example.bit230033_foodmanager.ui.form.AddEditDishActivity
import com.example.bit230033_foodmanager.utils.Constants
import com.example.bit230033_foodmanager.utils.LocaleHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dishAdapter: DishAdapter

    private val viewModel: DishViewModel by viewModels {
        val app = application as FoodApplication
        DishViewModel.Factory(app.dishRepository, app.categoryRepository, app.networkMonitor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        setupListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        dishAdapter = DishAdapter { dishWithDetails ->
            val intent = Intent(this, DishDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_DISH_ID, dishWithDetails.dish.id)
            }
            startActivity(intent)
        }

        binding.recyclerViewDishes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = dishAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                viewModel.updateSearchQuery(query)
                binding.btnClearSearch.isVisible = query.isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.clearSearch()
        }
    }

    private fun setupListeners() {
        // FAB: Add new dish
        binding.fabAddDish.setOnClickListener {
            val intent = Intent(this, AddEditDishActivity::class.java)
            startActivity(intent)
        }

        // Manage Categories
        binding.btnManageCategories.setOnClickListener {
            val intent = Intent(this, ManageCategoryActivity::class.java)
            startActivity(intent)
        }

        // Open Filter & Sort Dialog
        binding.btnOpenFilter.setOnClickListener {
            val dialog = FilterBottomSheetDialog(
                currentFilter = viewModel.filterState.value,
                categories = viewModel.categories.value,
                onApply = { categoryId, status, minPrice, maxPrice, sort ->
                    viewModel.applyFilters(categoryId, status, minPrice, maxPrice, sort)
                },
                onReset = {
                    viewModel.resetFilters()
                }
            )
            dialog.show(supportFragmentManager, "FilterBottomSheetDialog")
        }

        // Reset Active Filter button
        binding.btnResetActiveFilter.setOnClickListener {
            viewModel.resetFilters()
            binding.etSearch.text?.clear()
        }

        // Language Toggle Switch (EN <-> VI)
        com.example.bit230033_foodmanager.utils.LanguageToggleHelper.setupToggle(
            this,
            binding.btnLangEn,
            binding.btnLangVi
        )
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe dishes
                launch {
                    viewModel.dishes.collectLatest { list ->
                        dishAdapter.submitList(list)
                        binding.layoutEmptyState.isVisible = list.isEmpty()
                        binding.recyclerViewDishes.isVisible = list.isNotEmpty()
                    }
                }

                // Observe filter state to show/hide filter banner
                launch {
                    viewModel.filterState.collectLatest { filter ->
                        if (filter.isActive) {
                            binding.layoutActiveFilters.isVisible = true
                            val catName = viewModel.categories.value.firstOrNull { it.id == filter.categoryId }?.name
                            binding.tvActiveFilterDesc.text = filter.buildDescription(this@MainActivity, catName)
                        } else {
                            binding.layoutActiveFilters.isVisible = false
                        }
                    }
                }

                // Observe network status for Offline notice
                launch {
                    viewModel.isOnline.collectLatest { isConnected ->
                        binding.layoutOfflineBanner.isVisible = !isConnected
                    }
                }
            }
        }
    }
}
