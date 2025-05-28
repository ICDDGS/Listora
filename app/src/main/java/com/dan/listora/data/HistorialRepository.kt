package com.dan.listora.data

import com.dan.listora.data.db.HistorialDAO
import com.dan.listora.data.db.model.HistorialEntity
import com.dan.listora.data.db.model.IngEntity


class HistorialRepository(private val historialDao: HistorialDAO) {

    suspend fun insert(historial: HistorialEntity) {
        historialDao.insert(historial)
    }

    suspend fun getHistorialPorLista(nombreLista: String): List<HistorialEntity> {
        return historialDao.getHistorialPorLista(nombreLista)
    }

    suspend fun guardarDesdeIngrediente(
        listaRepository: ListRepository,
        ingredient: IngEntity
    ) {
        val nombreLista = listaRepository.getNombreListaPorId(ingredient.idLista)

        val historial = HistorialEntity(
            nombreLista = nombreLista,
            ingrediente = ingredient.name,
            cantidad = ingredient.cant,
            unidad = ingredient.unit,
            costo = ingredient.price,
            fecha = System.currentTimeMillis(),
        )

        historialDao.insert(historial)
    }
    suspend fun getHistorialEntreFechas(inicio: Long, fin: Long): List<HistorialEntity> {
        return historialDao.getHistorialEntreFechas(inicio, fin)
    }



}
