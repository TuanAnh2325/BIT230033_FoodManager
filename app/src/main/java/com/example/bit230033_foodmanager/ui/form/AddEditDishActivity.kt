package com.example.bit230033_foodmanager.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.bit230033_foodmanager.FoodApplication
import com.example.bit230033_foodmanager.R
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import com.example.bit230033_foodmanager.databinding.ActivityAddEditDishBinding
import com.example.bit230033_foodmanager.databinding.DialogAddImageBinding
import com.example.bit230033_foodmanager.ui.base.BaseActivity
import com.example.bit230033_foodmanager.utils.Constants
import com.example.bit230033_foodmanager.utils.ValidationResult
import com.example.bit230033_foodmanager.utils.ValidationUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddEditDishActivity : BaseActivity() {

    private lateinit var binding: ActivityAddEditDishBinding
    private lateinit var imageAdapter: FormImageAdapter

    private var dishId: Long = 0
    private var categoriesList = listOf<BIT230033_DishCategory>()

    private val viewModel: AddEditDishViewModel by viewModels {
        val app = application as FoodApplication
        AddEditDishViewModel.Factory(dishId, app.dishRepository, app.categoryRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dishId = intent.getLongExtra(Constants.EXTRA_DISH_ID, 0)

        setupToolbar()
        setupImageRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupToolbar() {
        binding.toolbarForm.title = if (dishId > 0) {
            getString(R.string.title_edit_dish)
        } else {
            getString(R.string.title_add_dish)
        }
        binding.toolbarForm.setNavigationOnClickListener { finish() }

        com.example.bit230033_foodmanager.utils.LanguageToggleHelper.setupToggle(
            this,
            binding.btnLangEnForm,
            binding.btnLangViForm
        )
    }

    private fun setupImageRecyclerView() {
        imageAdapter = FormImageAdapter(
            onSetThumbnail = { /* handled inside adapter */ },
            onRemove = { updateImagesHeader() }
        )
        binding.rvFormImages.apply {
            layoutManager = LinearLayoutManager(this@AddEditDishActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
        updateImagesHeader()
    }

    private fun updateImagesHeader() {
        val count = imageAdapter.itemCount
        binding.tvImagesHeader.text = if (count > 0) {
            "${getString(R.string.label_images)} ($count)"
        } else {
            getString(R.string.label_images)
        }
    }

    private fun setupListeners() {
        binding.btnAddImageForm.setOnClickListener {
            showAddImageDialog()
        }

        binding.btnSaveDish.setOnClickListener {
            validateAndSave()
        }
    }

    private fun showAddImageDialog() {
        val dialogBinding = DialogAddImageBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnPreviewImage.setOnClickListener {
            val url = dialogBinding.etImageUrl.text.toString().trim()
            val validation = ValidationUtils.validateImageUrl(url)
            if (validation is ValidationResult.Error) {
                Toast.makeText(this, validation.messageResId, Toast.LENGTH_SHORT).show()
            } else {
                dialogBinding.ivPreview.load(url) {
                    crossfade(true)
                    placeholder(R.drawable.ic_food_placeholder)
                    error(R.drawable.ic_image_broken)
                }
            }
        }

        dialogBinding.btnConfirmAddImage.setOnClickListener {
            val url = dialogBinding.etImageUrl.text.toString().trim()
            val validation = ValidationUtils.validateImageUrl(url)
            if (validation is ValidationResult.Error) {
                Toast.makeText(this, validation.messageResId, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val setThumbnail = dialogBinding.cbIsThumbnail.isChecked
            imageAdapter.addImage(url, setThumbnail)
            updateImagesHeader()
            dialog.dismiss()
        }

        dialogBinding.btnCancelImage.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun validateAndSave() {
        // Clear previous inline errors
        binding.tilDishName.error = null
        binding.tilPrice.error = null
        binding.tilPrepTime.error = null
        binding.tvCategoryError.isVisible = false

        val name = binding.etDishName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val prepTimeStr = binding.etPrepTime.text.toString().trim()
        val isAvailable = binding.switchIsAvailable.isChecked
        val description = binding.etDescription.text.toString().trim()

        var hasError = false
        var firstErrorMessageResId: Int? = null

        // 1. Validate Name
        val nameValidation = ValidationUtils.validateDishName(name)
        if (nameValidation is ValidationResult.Error) {
            binding.tilDishName.error = getString(nameValidation.messageResId)
            if (firstErrorMessageResId == null) firstErrorMessageResId = nameValidation.messageResId
            hasError = true
        }

        // 2. Validate Price
        val priceValidation = ValidationUtils.validatePrice(priceStr)
        if (priceValidation is ValidationResult.Error) {
            binding.tilPrice.error = getString(priceValidation.messageResId)
            if (firstErrorMessageResId == null) firstErrorMessageResId = priceValidation.messageResId
            hasError = true
        }

        // 3. Validate Prep Time
        val prepValidation = ValidationUtils.validatePreparationTime(prepTimeStr)
        if (prepValidation is ValidationResult.Error) {
            binding.tilPrepTime.error = getString(prepValidation.messageResId)
            if (firstErrorMessageResId == null) firstErrorMessageResId = prepValidation.messageResId
            hasError = true
        }

        // 4. Validate Category
        val selectedCategoryPos = binding.spinnerCategory.selectedItemPosition
        if (selectedCategoryPos !in categoriesList.indices) {
            binding.tvCategoryError.text = getString(R.string.err_category_required)
            binding.tvCategoryError.isVisible = true
            if (firstErrorMessageResId == null) firstErrorMessageResId = R.string.err_category_required
            hasError = true
        }

        if (hasError) {
            firstErrorMessageResId?.let {
                Toast.makeText(this@AddEditDishActivity, it, Toast.LENGTH_SHORT).show()
            }
            return
        }

        val categoryId = categoriesList[selectedCategoryPos].id
        val price = priceStr.toDouble()
        val prepTime = prepTimeStr.toInt()

        val images = imageAdapter.getItems().map { Pair(it.url, it.isThumbnail) }

        lifecycleScope.launch {
            binding.btnSaveDish.isEnabled = false
            val result = viewModel.saveDish(
                name = name,
                price = price,
                prepTime = prepTime,
                categoryId = categoryId,
                isAvailable = isAvailable,
                description = description,
                images = images
            )

            binding.btnSaveDish.isEnabled = true
            result.onSuccess {
                Toast.makeText(this@AddEditDishActivity, R.string.msg_dish_saved, Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { ex ->
                if (ex.message?.contains("already exists", ignoreCase = true) == true) {
                    binding.tilDishName.error = getString(R.string.err_dish_name_duplicate)
                    Toast.makeText(this@AddEditDishActivity, R.string.err_dish_name_duplicate, Toast.LENGTH_SHORT).show()
                } else {
                    val msg = ex.localizedMessage ?: getString(R.string.err_dish_name_duplicate)
                    Toast.makeText(this@AddEditDishActivity, msg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe categories for spinner
                launch {
                    viewModel.categories.collectLatest { cats ->
                        categoriesList = cats
                        val names = cats.map { 
                            com.example.bit230033_foodmanager.utils.CategoryLocalizationHelper.getLocalizedCategoryName(this@AddEditDishActivity, it.name) 
                        }
                        val adapter = ArrayAdapter(
                            this@AddEditDishActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            names
                        )
                        binding.spinnerCategory.adapter = adapter

                        // Re-select if editing
                        val currentDish = viewModel.existingDish.value?.dish
                        if (currentDish != null) {
                            val index = cats.indexOfFirst { it.id == currentDish.dishCategoryId }
                            if (index >= 0) binding.spinnerCategory.setSelection(index)
                        }
                    }
                }

                // Observe existing dish data when editing
                launch {
                    viewModel.existingDish.collectLatest { item ->
                        if (item == null) return@collectLatest

                        val dish = item.dish
                        binding.etDishName.setText(dish.name)
                        binding.etPrice.setText(dish.price.toInt().toString())
                        binding.etPrepTime.setText(dish.preparationTime.toString())
                        binding.switchIsAvailable.isChecked = dish.isAvailable
                        binding.etDescription.setText(dish.description ?: "")

                        val index = categoriesList.indexOfFirst { it.id == dish.dishCategoryId }
                        if (index >= 0) binding.spinnerCategory.setSelection(index)

                        // Populate images
                        val formImages = item.images.map { FormImageItem(it.imageUrl, it.isThumbnail) }
                        imageAdapter.submitList(formImages)
                        updateImagesHeader()
                    }
                }
            }
        }
    }
}
