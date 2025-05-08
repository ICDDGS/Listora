package com.dan.listora.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.ListElementBinding

class ListViewHolder(
    private val binding: ListElementBinding,
    private val onEditClick: (ListEntity) -> Unit,
    private val onItemClick: (ListEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lista: ListEntity) {
        binding.tvNombre.text = lista.name
        binding.tvFecha.text = "Fecha límite: ${lista.date}"
        binding.tvLimit.text = "Presupuesto: $${lista.presupuesto}"

        // Click en el ícono de edición
        binding.btnEdit.setOnClickListener {
            onEditClick(lista)
        }

        // Click en el resto del item
        binding.cardView.setOnClickListener {
            onItemClick(lista)
        }
    }
}


