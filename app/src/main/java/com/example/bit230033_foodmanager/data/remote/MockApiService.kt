package com.example.bit230033_foodmanager.data.remote

import com.example.bit230033_foodmanager.data.remote.dto.CategoryDto
import com.example.bit230033_foodmanager.data.remote.dto.DishDto
import retrofit2.Response
import retrofit2.http.*

interface MockApiService {

    @GET("BIT230033_Dish")
    suspend fun getDishes(): Response<List<DishDto>>

    @GET("BIT230033_Dish/{id}")
    suspend fun getDishById(@Path("id") id: String): Response<DishDto>

    @POST("BIT230033_Dish")
    suspend fun createDish(@Body dish: DishDto): Response<DishDto>

    @PUT("BIT230033_Dish/{id}")
    suspend fun updateDish(@Path("id") id: String, @Body dish: DishDto): Response<DishDto>

    @DELETE("BIT230033_Dish/{id}")
    suspend fun deleteDish(@Path("id") id: String): Response<Unit>

    @GET("BIT230033_DishCategory")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @POST("BIT230033_DishCategory")
    suspend fun createCategory(@Body category: CategoryDto): Response<CategoryDto>

    @DELETE("BIT230033_DishCategory/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<Unit>
}
