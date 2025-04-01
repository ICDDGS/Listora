package com.dan.listora.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.ListElementBinding

class ListAdapter(
    private val onListClick: (ListEntity) -> Unit,
) : RecyclerView.Adapter<ListViewHolder>() {
    private var lists : List<ListEntity> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding = ListElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = lists[position]
        holder.bind(list)
        holder.itemView.setOnClickListener {
            onListClick(list)
        }
    }

    override fun getItemCount(): Int = lists.size

    fun updateList(list: MutableList<ListEntity>){
        lists = list
        notifyDataSetChanged()
    }



}