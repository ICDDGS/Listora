package com.dan.listora.data

import com.dan.listora.data.db.IngredientDAO
import com.dan.listora.data.db.model.IngEntity


class IngredientRepository (
    private val ingredientDAO: IngredientDAO
) {
    suspend fun insertIngredient(ingredient: IngEntity) {
        ingredientDAO.insertIngredient(ingredient)
    }


    suspend fun updateIngredient(ingredient: IngEntity) {
        ingredientDAO.updateIngredient(ingredient)
    }

    suspend fun updateIngredientPurchaseStatus(id: Long, purchased: Boolean) {
        ingredientDAO.updateIngredientPurchaseStatus(id, purchased)
    }

    suspend fun deleteIngredientsByIds(ids: List<Long>) {
        ingredientDAO.deleteIngredientsByIds(ids)
    }

    suspend fun getIngredientsByListId(listaId: Long): List<IngEntity> {
        return ingredientDAO.getIngredientsByListId(listaId)
    }


}

