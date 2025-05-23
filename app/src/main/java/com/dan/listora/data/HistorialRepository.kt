package com.dan.listora.data

import com.dan.listora.data.db.HistorialDAO
import com.dan.listora.data.db.model.HistorialEntity

class HistorialRepository(private val historialDao: HistorialDAO) {

    suspend fun insert(historial: HistorialEntity) {
        historialDao.insert(historial)
    }

    suspend fun getHistorialPorLista(nombreLista: String): List<HistorialEntity> {
        return historialDao.getHistorialPorLista(nombreLista)
    }
}
