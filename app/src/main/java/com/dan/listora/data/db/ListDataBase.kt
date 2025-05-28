package com.dan.listora.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.data.db.model.RecipeEntity
import com.dan.listora.data.db.model.RecipeIngredientEntity
import com.dan.listora.data.db.model.HistorialEntity


@Database(
    entities = [
        ListEntity::class,
        IngEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        HistorialEntity::class // debe estar aquí
    ],
    version = 1, // o mayor
    exportSchema = false
)

abstract class ListDataBase : RoomDatabase() {
    abstract fun listDao(): ListDAO
    abstract fun ingredientDAO(): IngredientDAO
    abstract fun recipeDAO(): RecipeDAO
    abstract fun historialDao(): HistorialDAO


    companion object {
        @Volatile
        private var INSTANCE: ListDataBase? = null

        fun getDatabase(context: Context): ListDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ListDataBase::class.java,
                    "list_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}