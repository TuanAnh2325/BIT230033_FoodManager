package com.example.bit230033_foodmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DishDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("preparationTime")
    val preparationTime: Int,

    @SerializedName("isAvailable")
    val isAvailable: Boolean = true,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("dishCategoryId")
    val dishCategoryId: Long,

    @SerializedName("images")
    val images: List<DishImageDto> = emptyList()
)

data class DishImageDto(
    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("isThumbnail")
    val isThumbnail: Boolean = false
)

data class CategoryDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null
)
