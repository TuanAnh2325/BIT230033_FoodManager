package com.example.bit230033_foodmanager.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.bit230033_foodmanager.R
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import com.example.bit230033_foodmanager.databinding.DialogFilterBinding
import com.example.bit230033_foodmanager.ui.list.FilterState
import com.example.bit230033_foodmanager.ui.list.SortOption
import com.example.bit230033_foodmanager.ui.list.StatusFilter
import com.example.bit230033_foodmanager.utils.ValidationResult
import com.example.bit230033_foodmanager.utils.ValidationUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheetDialog(
    private val currentFilter: FilterState,
    private val categories: List<BIT230033_DishCategory>,
    private val onApply: (categoryId: Long?, status: StatusFilter, minPrice: Double?, maxPrice: Double?, sort: SortOption) -> Unit,
    private val onReset: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategorySpinner()
        populateCurrentFilters()
        setupListeners()
    }

    private fun setupCategorySpinner() {
        val categoryNames = mutableListOf(getString(R.string.filter_all_categories))
        categoryNames.addAll(categories.map { 
            com.example.bit230033_foodmanager.utils.CategoryLocalizationHelper.getLocalizedCategoryName(requireContext(), it.name) 
        })

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames)
        binding.spinnerFilterCategory.adapter = adapter

        // Select currently filtered category
        if (currentFilter.categoryId != null) {
            val selectedIndex = categories.indexOfFirst { it.id == currentFilter.categoryId }
            if (selectedIndex >= 0) {
                binding.spinnerFilterCategory.setSelection(selectedIndex + 1)
            }
        }
    }

    private fun populateCurrentFilters() {
        // Status
        when (currentFilter.statusFilter) {
            StatusFilter.ALL -> binding.rbStatusAll.isChecked = true
            StatusFilter.AVAILABLE_ONLY -> binding.rbStatusAvailable.isChecked = true
            StatusFilter.UNAVAILABLE_ONLY -> binding.rbStatusUnavailable.isChecked = true
        }

        // Price range
        currentFilter.minPrice?.let { binding.etMinPrice.setText(it.toInt().toString()) }
        currentFilter.maxPrice?.let { binding.etMaxPrice.setText(it.toInt().toString()) }

        // Sort option
        when (currentFilter.sortOption) {
            SortOption.DEFAULT -> binding.rbSortDefault.isChecked = true
            SortOption.PRICE_ASC -> binding.rbSortPriceAsc.isChecked = true
            SortOption.PRICE_DESC -> binding.rbSortPriceDesc.isChecked = true
            SortOption.PREPARATION_TIME_ASC -> binding.rbSortPrepAsc.isChecked = true
        }
    }

    private fun setupListeners() {
        binding.btnApplyFilterDialog.setOnClickListener {
            // Selected Category
            val selectedCategoryPos = binding.spinnerFilterCategory.selectedItemPosition
            val selectedCategoryId = if (selectedCategoryPos > 0) {
                categories[selectedCategoryPos - 1].id
            } else null

            // Selected Status
            val status = when (binding.rgStatus.checkedRadioButtonId) {
                R.id.rbStatusAvailable -> StatusFilter.AVAILABLE_ONLY
                R.id.rbStatusUnavailable -> StatusFilter.UNAVAILABLE_ONLY
                else -> StatusFilter.ALL
            }

            // Price range with validation
            val minPrice = binding.etMinPrice.text.toString().trim().toDoubleOrNull()
            val maxPrice = binding.etMaxPrice.text.toString().trim().toDoubleOrNull()

            val priceValidation = ValidationUtils.validatePriceRange(minPrice, maxPrice)
            if (priceValidation is ValidationResult.Error) {
                binding.etMinPrice.error = getString(priceValidation.messageResId)
                Toast.makeText(requireContext(), priceValidation.messageResId, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sort option
            val sortOption = when (binding.rgSort.checkedRadioButtonId) {
                R.id.rbSortPriceAsc -> SortOption.PRICE_ASC
                R.id.rbSortPriceDesc -> SortOption.PRICE_DESC
                R.id.rbSortPrepAsc -> SortOption.PREPARATION_TIME_ASC
                else -> SortOption.DEFAULT
            }

            onApply(selectedCategoryId, status, minPrice, maxPrice, sortOption)
            dismiss()
        }

        binding.btnResetFilterDialog.setOnClickListener {
            onReset()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
