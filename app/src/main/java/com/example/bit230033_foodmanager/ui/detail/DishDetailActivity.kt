package com.example.bit230033_foodmanager.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.example.bit230033_foodmanager.FoodApplication
import com.example.bit230033_foodmanager.R
import com.example.bit230033_foodmanager.databinding.ActivityDishDetailBinding
import com.example.bit230033_foodmanager.databinding.DialogAddImageBinding
import com.example.bit230033_foodmanager.ui.base.BaseActivity
import com.example.bit230033_foodmanager.ui.form.AddEditDishActivity
import com.example.bit230033_foodmanager.utils.Constants
import com.example.bit230033_foodmanager.utils.ValidationResult
import com.example.bit230033_foodmanager.utils.ValidationUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class DishDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDishDetailBinding
    private lateinit var galleryAdapter: ImageGalleryAdapter

    private var dishId: Long = 0

    private val viewModel: DishDetailViewModel by viewModels {
        val app = application as FoodApplication
        DishDetailViewModel.Factory(dishId, app.dishRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dishId = intent.getLongExtra(Constants.EXTRA_DISH_ID, 0)
        if (dishId <= 0) {
            Toast.makeText(this, R.string.err_dish_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupGallery()
        setupActions()
        observeData()
    }

    private fun setupToolbar() {
        binding.toolbarDetail.setNavigationOnClickListener {
            finish()
        }
        com.example.bit230033_foodmanager.utils.LanguageToggleHelper.setupToggle(
            this,
            binding.btnLangEnDetail,
            binding.btnLangViDetail
        )
    }

    private fun setupGallery() {
        galleryAdapter = ImageGalleryAdapter()
        binding.viewPagerGallery.adapter = galleryAdapter

        binding.viewPagerGallery.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateGalleryControls(position)
            }
        })
    }

    private fun updateGalleryControls(position: Int) {
        val total = galleryAdapter.itemCount
        binding.tvGalleryIndicator.text = if (total > 0) "${position + 1} / $total" else "0 / 0"

        val currentImage = galleryAdapter.getImageAt(position)
        if (currentImage != null) {
            binding.tvCurrentIsThumbnailBadge.isVisible = currentImage.isThumbnail
            binding.btnSetThumbnail.isEnabled = !currentImage.isThumbnail
        } else {
            binding.tvCurrentIsThumbnailBadge.isVisible = false
            binding.btnSetThumbnail.isEnabled = false
        }
    }

    private fun setupActions() {
        // Set as Thumbnail
        binding.btnSetThumbnail.setOnClickListener {
            val currentPos = binding.viewPagerGallery.currentItem
            val image = galleryAdapter.getImageAt(currentPos)
            if (image != null) {
                viewModel.setThumbnail(image.imageId)
                Toast.makeText(this, R.string.msg_thumbnail_updated, Toast.LENGTH_SHORT).show()
            }
        }

        // Add Image Dialog
        binding.btnAddImageDetail.setOnClickListener {
            showAddImageDialog()
        }

        // Edit Dish
        binding.btnEditDish.setOnClickListener {
            val intent = Intent(this, AddEditDishActivity::class.java).apply {
                putExtra(Constants.EXTRA_DISH_ID, dishId)
            }
            startActivity(intent)
        }

        // Delete Dish with Confirmation Dialog
        binding.btnDeleteDish.setOnClickListener {
            val dishName = binding.tvDetailName.text.toString()
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_dish_title)
                .setMessage(getString(R.string.dialog_delete_dish_msg, dishName))
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_delete) { _, _ ->
                    viewModel.deleteDish {
                        Toast.makeText(this@DishDetailActivity, R.string.msg_dish_deleted, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .show()
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
            viewModel.addImage(url, setThumbnail)
            dialog.dismiss()
        }

        dialogBinding.btnCancelImage.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dishWithDetails.collectLatest { item ->
                    if (item == null) return@collectLatest

                    val dish = item.dish

                    // Name & Category
                    binding.tvDetailName.text = dish.name
                    val rawCat = item.category?.name ?: getString(R.string.filter_category)
                    binding.tvDetailCategory.text = com.example.bit230033_foodmanager.utils.CategoryLocalizationHelper.getLocalizedCategoryName(this@DishDetailActivity, rawCat)

                    // Price
                    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
                    binding.tvDetailPrice.text = "${formatter.format(dish.price)} VND"

                    // Description
                    binding.tvDetailDescription.text = dish.description ?: ""

                    // Availability Status Badge
                    if (dish.isAvailable) {
                        binding.tvDetailStatusBadge.text = getString(R.string.badge_available)
                        binding.tvDetailStatusBadge.setBackgroundResource(R.drawable.bg_badge_available)
                        binding.tvDetailStatusBadge.setTextColor(ContextCompat.getColor(this@DishDetailActivity, R.color.status_available_text))
                    } else {
                        binding.tvDetailStatusBadge.text = getString(R.string.badge_unavailable)
                        binding.tvDetailStatusBadge.setBackgroundResource(R.drawable.bg_badge_unavailable)
                        binding.tvDetailStatusBadge.setTextColor(ContextCompat.getColor(this@DishDetailActivity, R.color.status_unavailable_text))
                    }

                    // Preparation Time Badge (Requirement 4.3: Quick if <= 15 mins, Regular if > 15 mins)
                    val timeText = getString(R.string.time_mins, dish.preparationTime)
                    if (dish.isQuick) {
                        binding.tvDetailPrepTimeBadge.text = "⚡ ${getString(R.string.badge_quick)} • $timeText"
                        binding.tvDetailPrepTimeBadge.setBackgroundResource(R.drawable.bg_badge_quick)
                        binding.tvDetailPrepTimeBadge.setTextColor(ContextCompat.getColor(this@DishDetailActivity, R.color.status_quick_text))
                    } else {
                        binding.tvDetailPrepTimeBadge.text = "⏱️ ${getString(R.string.badge_regular)} • $timeText"
                        binding.tvDetailPrepTimeBadge.setBackgroundResource(R.drawable.bg_badge_regular)
                        binding.tvDetailPrepTimeBadge.setTextColor(ContextCompat.getColor(this@DishDetailActivity, R.color.status_regular_text))
                    }

                    // Image Gallery
                    galleryAdapter.submitList(item.images)
                    updateGalleryControls(binding.viewPagerGallery.currentItem)
                }
            }
        }
    }
}
