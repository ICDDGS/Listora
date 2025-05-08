package com.dan.listora.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = [ForeignKey(
        entity = RecipeEntity::class,
        parentColumns = ["id"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "recipeId")
    val recipeId: Long,
    @ColumnInfo(name = "recipe_ing_name")
    val name: String,
    @ColumnInfo(name = "recipe_ing_unit")
    val unit: String,
    @ColumnInfo(name = "recipe_ing_base")
    val baseQuantity: Double
)
