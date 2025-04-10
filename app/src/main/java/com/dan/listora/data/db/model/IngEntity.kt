package com.dan.listora.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dan.listora.util.Constants

@Entity(tableName = Constants.DATABASE_INGREDIENT_TABLE)
class IngEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id")
    var id: Long = 0,
    @ColumnInfo(name = "ingredient_name")
    var name: String,
    @ColumnInfo(name = "ingredient_cant")
    var cant: String,
    /*
    @ColumnInfo(name = "ingredient_unit")
    val unit: String,
    @ColumnInfo(name = "ingredient_list_id")
    val listId: Long*/
)