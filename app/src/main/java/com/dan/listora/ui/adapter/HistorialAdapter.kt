package com.dan.listora.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.R
import com.dan.listora.data.db.model.HistorialEntity

class HistorialAdapter(private val historialList: List<HistorialEntity>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLineaResumen: TextView = itemView.findViewById(R.id.tvLineaResumen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = historialList[position]
        val resumen = "${item.ingrediente} ${item.cantidad}${item.unidad} $${item.costo}"
        holder.tvLineaResumen.text = resumen
    }

    override fun getItemCount(): Int = historialList.size
}

