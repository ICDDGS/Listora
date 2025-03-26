package com.dan.listora.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dan.listora.data.db.model.ListEntity

@Database(
    entities = [ListEntity::class],
    version = 1,
    exportSchema = true,)

abstract class ListDB : RoomDatabase() {
    abstract fun listDao(): ListDAO

    companion object{
        @Volatile
        private var INSTANCE: ListDB? = null

        fun getDatabase(context: Context): ListDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ListDB::class.java,
                    "list_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }


    }

}