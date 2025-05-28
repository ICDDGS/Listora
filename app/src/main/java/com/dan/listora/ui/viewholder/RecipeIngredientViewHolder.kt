package com.dan.listora.ui.viewholder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.RecipeIngredientEntity
import com.dan.listora.databinding.IngredientItemBinding

class RecipeIngredientViewHolder(
    private val binding: IngredientItemBinding,
    private val onDeleteClick: (RecipeIngredientEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("DefaultLocale")
    fun bind(ingredient: RecipeIngredientEntity) {
        binding.tvNombre.text = ingredient.name
        binding.tvCantidad.text = String.format("%.2f", ingredient.quantity)
        binding.tvUnidad.text = ingredient.unit

        binding.btnEliminar.setOnClickListener {
            onDeleteClick(ingredient)
        }
    }
}
