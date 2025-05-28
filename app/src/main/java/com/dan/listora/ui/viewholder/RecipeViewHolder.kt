package com.dan.listora.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dan.listora.R
import com.dan.listora.data.db.model.RecipeEntity
import com.dan.listora.databinding.RecipeItemBinding

class RecipeViewHolder(
    private val binding: RecipeItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(recipe: RecipeEntity, onClick: (RecipeEntity) -> Unit, onToggleFavorite: (RecipeEntity) -> Unit) {
        binding.tvRecipeName.text = recipe.name
        binding.tvCategory.text = recipe.category

        if (!recipe.imageUrl.isNullOrEmpty()) {
            Glide.with(binding.root.context)
                .load(recipe.imageUrl)
                .into(binding.ivRecipeImage)
        } else {
            binding.ivRecipeImage.setImageResource(R.drawable.food)
        }

        val iconRes = if (recipe.isFavorite) R.drawable.ic_star else R.drawable.ic_star_border
        binding.ivFavorite.setImageResource(iconRes)

        binding.root.setOnClickListener {
            onClick(recipe)
        }

        binding.ivFavorite.setOnClickListener {
            onToggleFavorite(recipe)
        }
    }
}