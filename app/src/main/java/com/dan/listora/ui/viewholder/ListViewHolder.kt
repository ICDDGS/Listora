package com.dan.listora.ui.viewholder

import android.annotation.SuppressLint
import android.view.ContextThemeWrapper
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

    @SuppressLint("StringFormatMatches")
    fun bind(lista: ListEntity) {
        val context = binding.root.context
        binding.tvNombre.text = lista.name
        binding.tvFecha.text = context.getString(R.string.fecha_limite_3, lista.date)
        binding.tvLimit.text = context.getString(R.string.presupuesto_2, lista.presupuesto)


        binding.cardView.setOnClickListener {
            onItemClick(lista)
        }

        binding.btnListOptions.setOnClickListener {
            val popup = PopupMenu(
                ContextThemeWrapper(binding.root.context, R.style.PopupMenuStyle),
                it
            )
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



