package com.dan.listora.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.R
import com.dan.listora.data.db.model.IngEntity

class ResumenCompraAdapter(private val lista: List<IngEntity>) :
    RecyclerView.Adapter<ResumenCompraAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLineaResumen: TextView = itemView.findViewById(R.id.tvLineaResumen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        val resumen = "${item.name} ${item.cant}${item.unit} \$${item.price}"
        holder.tvLineaResumen.text = resumen
    }

    override fun getItemCount(): Int = lista.size
}
