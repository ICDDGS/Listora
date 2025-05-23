package com.dan.listora.data

import com.dan.listora.data.db.IngredientDAO
import com.dan.listora.data.db.ListDAO
import com.dan.listora.data.db.model.ListEntity

class ListRepository(
    private val listDAO: ListDAO,
    private val ingredientDAO: IngredientDAO
) {
    suspend fun insertList(list: ListEntity) {
        listDAO.insertList(list)
    }

    suspend fun getAllLists(): MutableList<ListEntity> {
        return listDAO.getAllLists()
    }

    suspend fun updateList(list: ListEntity) {
        listDAO.updateList(list)
    }

    suspend fun deleteList(list: ListEntity) {
        listDAO.deleteList(list)
    }

    suspend fun deleteListAndIngredients(listId: Long) {
        ingredientDAO.deleteByListId(listId)
        listDAO.deleteById(listId)
    }

    suspend fun getNombreListaPorId(id: Long): String {
        return listDAO.getListNameById(id) ?: "Sin nombre"
    }

}
