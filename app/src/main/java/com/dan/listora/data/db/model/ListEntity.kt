package com.dan.listora.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dan.listora.util.Constants

@Entity(tableName = Constants.DATABASE_LIST_TABLE)
data class ListEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    val id: Long = 0,
    @ColumnInfo(name = "list_name")
    val name: String
)
