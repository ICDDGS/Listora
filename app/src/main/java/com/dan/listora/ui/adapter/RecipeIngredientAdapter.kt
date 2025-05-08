package com.dan.listora.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.RecipeIngredientEntity
import com.dan.listora.databinding.RecipeIngredientItemBinding
import com.dan.listora.ui.viewholder.RecipeIngredientViewHolder

class RecipeIngredientAdapter(
    private val ingredientes: List<RecipeIngredientEntity>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<RecipeIngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeIngredientViewHolder {
        val binding = RecipeIngredientItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeIngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeIngredientViewHolder, position: Int) {
        holder.bind(ingredientes[position])
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = ingredientes.size
}