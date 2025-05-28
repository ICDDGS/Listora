package com.dan.listora.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dan.listora.data.db.model.HistorialEntity

@Dao
interface HistorialDAO {
    @Insert
    suspend fun insert(historial: HistorialEntity)

    @Query("SELECT * FROM historial_table WHERE nombre_lista = :lista ORDER BY fecha ASC")
    suspend fun getHistorialPorLista(lista: String): List<HistorialEntity>

    @Query("SELECT * FROM historial_table WHERE fecha BETWEEN :inicio AND :fin")
    suspend fun getHistorialEntreFechas(inicio: Long, fin: Long): List<HistorialEntity>

}
