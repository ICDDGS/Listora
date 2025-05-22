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
    suspend fun insertLists(lists: MutableList<ListEntity>)

    @Query("SELECT * FROM ${Constants.DATABASE_LIST_TABLE}")
    suspend fun getAllLists(): MutableList<ListEntity>

    @Query("SELECT * FROM ${Constants.DATABASE_LIST_TABLE} WHERE list_id = :listId")
    suspend fun getListById(listId: Long): ListEntity?

    @Update
    suspend fun updateList(list: ListEntity)

    @Update
    suspend fun updateLists(lists: MutableList<ListEntity>)

    @Delete
    suspend fun deleteList(list: ListEntity)

    @Query("DELETE FROM list_data_table WHERE list_id = :id")
    suspend fun deleteById(id: Long)




}