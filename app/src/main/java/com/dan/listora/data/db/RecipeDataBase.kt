package com.dan.listora.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dan.listora.data.db.model.RecipeEntity
import com.dan.listora.data.db.model.RecipeIngredientEntity

@Database(
    entities = [RecipeEntity::class, RecipeIngredientEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDataBase : RoomDatabase() {
    abstract fun recipeDAO(): RecipeDAO

    companion object {
        @Volatile
        private var INSTANCE: RecipeDataBase? = null

        fun getDatabase(context: Context): RecipeDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDataBase::class.java,
                    "recipe_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}