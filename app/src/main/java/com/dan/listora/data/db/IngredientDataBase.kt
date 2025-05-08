package com.dan.listora.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.util.Constants

@Database(
    entities = [IngEntity::class],
    version = 2,
    exportSchema = false
)
abstract class IngredientDataBase: RoomDatabase() {

    abstract fun IngredientDAO(): IngredientDAO

    companion object{
        @Volatile
        private var INSTANCE: IngredientDataBase? = null

        fun getDatabase(context: Context): IngredientDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IngredientDataBase::class.java,
                    Constants.BATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // ← CRÍTICO
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
        }


}
