package com.dan.listora.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.RecipeIngredientEntity
import com.dan.listora.databinding.IngredientItemBinding
import com.dan.listora.ui.viewholder.RecipeIngredientViewHolder

class RecipeIngredientAdapter(
    private val ingredientes: MutableList<RecipeIngredientEntity>,
    private val onDeleteClick: (RecipeIngredientEntity) -> Unit
) : RecyclerView.Adapter<RecipeIngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeIngredientViewHolder {
        val binding = IngredientItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeIngredientViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: RecipeIngredientViewHolder, position: Int) {
        holder.bind(ingredientes[position])
    }

    override fun getItemCount(): Int = ingredientes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<RecipeIngredientEntity>) {
        ingredientes.clear()
        ingredientes.addAll(newList)
        notifyDataSetChanged()
    }
}
