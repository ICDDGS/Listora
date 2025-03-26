package com.dan.listora.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.util.Constants

@Dao
interface ListDAO {

    @Insert
    suspend fun insertList(list: ListEntity)

    @Insert
    suspend fun insertLists(lists: List<ListEntity>)

    @Query("SELECT * FROM ${Constants.DATABASE_LIST_TABLE}")
    suspend fun getAllLists(): MutableList<ListEntity>

    @Update
    suspend fun updateList(list: ListEntity)

    @Delete
    suspend fun deleteList(list: ListEntity)



}