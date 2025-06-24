package com.dan.listora.ui.viewholder

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dan.listora.R
import com.dan.listora.data.db.model.RecipeEntity
import com.dan.listora.databinding.RecipeItemBinding

class RecipeViewHolder(
    private val binding: RecipeItemBinding,
    private val onEditClick: (RecipeEntity) -> Unit,
    private val onDeleteClick: (RecipeEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        recipe: RecipeEntity,
        onClick: (RecipeEntity) -> Unit
    ) {
        binding.tvRecipeName.text = recipe.name
        binding.tvCategory.text = recipe.category

        if (!recipe.imageUrl.isNullOrEmpty()) {
            Glide.with(binding.root.context)
                .load(recipe.imageUrl)
                .into(binding.ivRecipeImage)
        } else {
            binding.ivRecipeImage.setImageResource(R.drawable.food)
        }

        binding.root.setOnClickListener {
            onClick(recipe)
        }

        binding.btnListOptions.setOnClickListener {
            val popup = PopupMenu(binding.root.context, it)
            popup.inflate(R.menu.recipe_item_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit_recipe -> {
                        onEditClick(recipe)
                        true
                    }
                    R.id.action_delete_recipe -> {
                        onDeleteClick(recipe)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }


}