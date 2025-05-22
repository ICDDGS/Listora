package com.dan.listora.ui.viewholder

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.R
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.ListElementBinding

class ListViewHolder(
    private val binding: ListElementBinding,
    private val onEditClick: (ListEntity) -> Unit,
    private val onItemClick: (ListEntity) -> Unit,
    private val onDeleteClick: (ListEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lista: ListEntity) {
        binding.tvNombre.text = lista.name
        binding.tvFecha.text = "Fecha límite: ${lista.date}"
        binding.tvLimit.text = "Presupuesto: $${lista.presupuesto}"


        binding.cardView.setOnClickListener {
            onItemClick(lista)
        }

        binding.btnListOptions.setOnClickListener {
            val popup = PopupMenu(binding.root.context, it)
            popup.inflate(R.menu.list_item_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit_list -> {
                        onEditClick(lista)
                        true
                    }
                    R.id.action_delete_list -> {
                        onDeleteClick(lista)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}



