package com.dan.listora.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.RecipeIngredientEntity
import com.dan.listora.databinding.RecipeIngredientItemBinding

class RecipeIngredientViewHolder(
    private val binding: RecipeIngredientItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ingredient: RecipeIngredientEntity) {
        binding.tvIngredientName.text = ingredient.name
        binding.tvIngredientQty.text = "%.2f %s".format(ingredient.baseQuantity, ingredient.unit)
    }
}