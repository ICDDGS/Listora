package com.dan.listora.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.util.Constants

@Database(
    entities = [ListEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class ListDataBase: RoomDatabase() {

    abstract fun listDao(): ListDAO

    companion object{
        @Volatile
        private var INSTANCE: ListDataBase? = null

        fun getDatabase(context: Context): ListDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ListDataBase::class.java,
                    Constants.BATABASE_NAME
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

