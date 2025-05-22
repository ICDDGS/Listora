package com.dan.listora.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.databinding.IngredientElementBinding
import com.dan.listora.ui.viewholder.IngredientViewHolder

class IngredientAdapter(
    private val ingredients: MutableList<IngEntity>,
    private val onEditClick: (IngEntity) -> Unit
) : RecyclerView.Adapter<IngredientViewHolder>() {

    val selectedItems = mutableSetOf<Long>()
    var selectionMode = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = IngredientElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding, onEditClick, selectedItems)

    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(ingredients[position], selectionMode)
    }

    override fun getItemCount(): Int = ingredients.size

    fun removeItemsByIds(ids: List<Long>) {
        ingredients.removeAll { ids.contains(it.id) }
        notifyDataSetChanged()
    }

    fun enableSelectionMode() {
        selectionMode = true
        notifyDataSetChanged()
    }
}