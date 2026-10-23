package com.example.bit230033_foodmanager.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bit230033_foodmanager.data.local.dao.BIT230033_CategoryDao
import com.example.bit230033_foodmanager.data.local.dao.BIT230033_DishDao
import com.example.bit230033_foodmanager.data.local.dao.BIT230033_DishImageDao
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_Dish
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishImage
import com.example.bit230033_foodmanager.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BIT230033_DishCategory::class,
        BIT230033_Dish::class,
        BIT230033_DishImage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BIT230033_FoodDatabase : RoomDatabase() {

    abstract fun categoryDao(): BIT230033_CategoryDao
    abstract fun dishDao(): BIT230033_DishDao
    abstract fun dishImageDao(): BIT230033_DishImageDao

    companion object {
        @Volatile
        private var INSTANCE: BIT230033_FoodDatabase? = null

        fun getDatabase(context: Context): BIT230033_FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BIT230033_FoodDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database)
                    }
                }
            }

            suspend fun seedDatabase(db: BIT230033_FoodDatabase) {
                val catDao = db.categoryDao()
                val dishDao = db.dishDao()
                val imageDao = db.dishImageDao()

                // 1. Seed 3 Categories
                val cat1Id = catDao.insertCategory(
                    BIT230033_DishCategory(name = "Appetizers", description = "Light dishes served before main courses")
                )
                val cat2Id = catDao.insertCategory(
                    BIT230033_DishCategory(name = "Main Courses", description = "Hearty and fulfilling main meal dishes")
                )
                val cat3Id = catDao.insertCategory(
                    BIT230033_DishCategory(name = "Desserts & Drinks", description = "Sweet treats and refreshing beverages")
                )

                // 2. Seed 5 Dishes
                // Dish 1: Quick (10 min), Available, 2 images
                val d1Id = dishDao.insertDish(
                    BIT230033_Dish(
                        name = "Phở Bò Tái Nạm",
                        price = 55000.0,
                        preparationTime = 10,
                        isAvailable = true,
                        description = "Traditional Vietnamese beef noodle soup with tender beef and rich savory broth.",
                        dishCategoryId = cat2Id
                    )
                )
                imageDao.insertImage(BIT230033_DishImage(dishId = d1Id, imageUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=800", isThumbnail = true))
                imageDao.insertImage(BIT230033_DishImage(dishId = d1Id, imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800", isThumbnail = false))

                // Dish 2: Regular (20 min), Available, 2 images
                val d2Id = dishDao.insertDish(
                    BIT230033_Dish(
                        name = "Bún Chả Hà Nội",
                        price = 60000.0,
                        preparationTime = 20,
                        isAvailable = true,
                        description = "Grilled pork meatballs and pork slices served over vermicelli noodles with sweet garlic fish sauce.",
                        dishCategoryId = cat2Id
                    )
                )
                imageDao.insertImage(BIT230033_DishImage(dishId = d2Id, imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=800", isThumbnail = true))
                imageDao.insertImage(BIT230033_DishImage(dishId = d2Id, imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800", isThumbnail = false))

                // Dish 3: Quick (12 min), Available, 2 images
                val d3Id = dishDao.insertDish(
                    BIT230033_Dish(
                        name = "Nem Rán Truyền Thống",
                        price = 45000.0,
                        preparationTime = 12,
                        isAvailable = true,
                        description = "Crispy golden fried spring rolls stuffed with seasoned minced pork, mushrooms and glass noodles.",
                        dishCategoryId = cat1Id
                    )
                )
                imageDao.insertImage(BIT230033_DishImage(dishId = d3Id, imageUrl = "https://images.unsplash.com/photo-1541544741938-0af808871cc0?w=800", isThumbnail = true))
                imageDao.insertImage(BIT230033_DishImage(dishId = d3Id, imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800", isThumbnail = false))

                // Dish 4: Quick (8 min), Unavailable (Ngừng bán), 1 image
                val d4Id = dishDao.insertDish(
                    BIT230033_Dish(
                        name = "Salad Nga Rau Củ",
                        price = 35000.0,
                        preparationTime = 8,
                        isAvailable = false,
                        description = "Russian style vegetable potato salad dressed in creamy mayonnaise dressing.",
                        dishCategoryId = cat1Id
                    )
                )
                imageDao.insertImage(BIT230033_DishImage(dishId = d4Id, imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800", isThumbnail = true))

                // Dish 5: Regular (25 min), Available, 2 images
                val d5Id = dishDao.insertDish(
                    BIT230033_Dish(
                        name = "Bánh Flan Cà Phê",
                        price = 25000.0,
                        preparationTime = 25,
                        isAvailable = true,
                        description = "Silky French caramel custard flan drizzled with aromatic Vietnamese dark drip coffee.",
                        dishCategoryId = cat3Id
                    )
                )
                imageDao.insertImage(BIT230033_DishImage(dishId = d5Id, imageUrl = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=800", isThumbnail = true))
                imageDao.insertImage(BIT230033_DishImage(dishId = d5Id, imageUrl = "https://images.unsplash.com/photo-1587314168485-3236d6710814?w=800", isThumbnail = false))
            }
        }
    }
}
