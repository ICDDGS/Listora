package com.dan.listora.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_table")
data class HistorialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "nombre_lista")
    val nombreLista: String,
    @ColumnInfo(name = "ingrediente")
    val ingrediente: String,
    @ColumnInfo(name = "cantidad")
    val cantidad: String,
    @ColumnInfo(name = "unidad")
    val unidad: String,
    @ColumnInfo(name = "costo")
    val costo: Double,
    @ColumnInfo(name = "fecha")
    val fecha: Long

)
