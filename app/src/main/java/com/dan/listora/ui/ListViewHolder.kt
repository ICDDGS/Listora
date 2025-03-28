package com.dan.listora.ui

import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.ListElementBinding

class ListViewHolder(
    private val binding: ListElementBinding
):RecyclerView.ViewHolder(binding.root) {
    fun bind(list : ListEntity){
        binding.apply {
            tvListName.text = list.name
        }
    }
}