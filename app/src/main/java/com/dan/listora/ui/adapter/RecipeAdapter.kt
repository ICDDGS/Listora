package com.dan.listora.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.RecipeEntity
import com.dan.listora.databinding.RecipeItemBinding
import com.dan.listora.ui.viewholder.RecipeViewHolder

class RecipeAdapter(
    private val recetas: List<RecipeEntity>,
    private val onItemClick: (RecipeEntity) -> Unit,
    private val onEditClick: (RecipeEntity) -> Unit,
    private val onDeleteClick: (RecipeEntity) -> Unit
) : RecyclerView.Adapter<RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recetas[position], onItemClick)
    }

    override fun getItemCount(): Int = recetas.size
}

