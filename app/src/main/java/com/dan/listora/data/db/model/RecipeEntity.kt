package com.dan.listora.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "recipe_name")
    val name: String,
    @ColumnInfo(name = "recipe_img")
    val imageUrl: String?,
    @ColumnInfo(name = "recipe_category")
    val category: String,
    @ColumnInfo(name = "recipe_servings")
    val servings: Int,
    @ColumnInfo(name = "recipe_steps")
    val steps: String,
    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false

)
