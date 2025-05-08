package com.dan.listora.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.RecipeEntity
import com.dan.listora.databinding.RecipeItemBinding

class RecipeAdapter(
    private val recetas: List<RecipeEntity>,
    private val onItemClick: (RecipeEntity) -> Unit,
    private val onToggleFavorite: (RecipeEntity) -> Unit
) : RecyclerView.Adapter<RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recetas[position], onItemClick, onToggleFavorite)
    }

    override fun getItemCount(): Int = recetas.size
}