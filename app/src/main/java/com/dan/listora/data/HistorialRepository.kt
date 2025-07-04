package com.dan.listora.data

import com.dan.listora.data.db.HistorialDAO
import com.dan.listora.data.db.model.HistorialEntity
import com.dan.listora.data.db.model.IngEntity


class HistorialRepository(private val historialDao: HistorialDAO) {

    suspend fun insert(historial: HistorialEntity) {
        historialDao.insert(historial)
    }

    suspend fun getHistorialEntreFechas(inicio: Long, fin: Long): List<HistorialEntity> {
        return historialDao.getHistorialEntreFechas(inicio, fin)
    }

    suspend fun guardarDesdeIngrediente(ingrediente: IngEntity, nombreLista: String) {
        val historial = HistorialEntity(
            nombreLista = nombreLista,
            ingrediente = ingrediente.name,
            cantidad = ingrediente.cant,
            unidad = ingrediente.unit,
            costo = ingrediente.price,
            fecha = System.currentTimeMillis()
        )
        historialDao.insert(historial)
    }



}
