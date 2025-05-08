package com.dan.listora.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.ListElementBinding
import com.dan.listora.ui.viewholder.ListViewHolder

class ListAdapter(
    private val onEditClick: (ListEntity) -> Unit,
    private val onItemClick: (ListEntity) -> Unit
) : RecyclerView.Adapter<ListViewHolder>() {

    private var lists: List<ListEntity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ListElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding, onEditClick, onItemClick)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(lists[position])
    }

    override fun getItemCount(): Int = lists.size

    fun updateList(list: List<ListEntity>) {
        lists = list
        notifyDataSetChanged()
    }
}

