package com.dan.listora.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.util.Constants

@Dao
interface IngredientDAO {

    @Insert
    suspend fun insertIngredient(ingredient: IngEntity)

    @Insert
    suspend fun insertLists(ingredients: MutableList<IngEntity>)

    @Query("SELECT * FROM ${Constants.DATABASE_INGREDIENT_TABLE}")
    suspend fun getAllIngredients(): MutableList<IngEntity>

    @Query("SELECT * FROM ${Constants.DATABASE_INGREDIENT_TABLE} WHERE ingredient_id = :ingId")
    suspend fun getIngredientById(ingId: Long): IngEntity?

    @Update
    suspend fun updateIngredient(ingredient: IngEntity)

    @Update
    suspend fun updateIngredients(ingredients: MutableList<IngEntity>)

    @Delete
    suspend fun deleteIngredient(ingredient: IngEntity)

    @Query("SELECT * FROM ${Constants.DATABASE_INGREDIENT_TABLE} WHERE idLista = :listId")
    suspend fun getIngredientsByListId(listId: Long): List<IngEntity>

    @Query("DELETE FROM ${Constants.DATABASE_INGREDIENT_TABLE} WHERE ingredient_id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE ingredient_data_table SET isPurchased = :purchased WHERE ingredient_id = :id")
    suspend fun updateIngredientPurchaseStatus(id: Long, purchased: Boolean)

    @Query("DELETE FROM ingredient_data_table WHERE ingredient_id IN (:ids)")
    suspend fun deleteIngredientsByIds(ids: List<Long>)
    @Query("DELETE FROM ${Constants.DATABASE_INGREDIENT_TABLE} WHERE idLista = :listId")
    suspend fun deleteByListId(listId: Long)




}