package com.dan.listora.ui.viewholder

import android.graphics.Paint
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.databinding.IngredientElementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IngredientViewHolder(
    private val binding: IngredientElementBinding,
    private val onEditClick: (IngEntity) -> Unit,
    private val selectedItems: MutableSet<Long>
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ingredient: IngEntity, selectionMode: Boolean) {
        binding.tvNombre.text = ingredient.name
        binding.tvCantidadUnidad.text = "Cantidad: ${ingredient.cant} ${ingredient.unit}"
        binding.tvPrecio.text = "Precio: $${ingredient.price}"

        val context = binding.root.context

        // Estado visual de compra
        val isPurchased = ingredient.isPurchased
        binding.root.alpha = if (isPurchased) 0.5f else 1.0f
        binding.tvNombre.paintFlags =
            if (isPurchased) binding.tvNombre.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else binding.tvNombre.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        binding.btnPurchaseConfirm.setColorFilter(
            ContextCompat.getColor(
                context,
                if (isPurchased) R.color.shopping_check_confirmed else R.color.shopping_check_default
            )
        )

        if (selectionMode) {
            binding.cbSeleccionar.visibility = View.VISIBLE

            // Reset listener antes de actualizar estado
            binding.cbSeleccionar.setOnCheckedChangeListener(null)
            binding.cbSeleccionar.isChecked = selectedItems.contains(ingredient.id)
            binding.cbSeleccionar.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                if (isChecked) selectedItems.add(ingredient.id)
                else selectedItems.remove(ingredient.id)
            }

            binding.root.setOnClickListener {
                binding.cbSeleccionar.isChecked = !binding.cbSeleccionar.isChecked
            }

            // Desactiva edición
            binding.btnEditIngredient.setOnClickListener(null)
        } else {
            binding.cbSeleccionar.visibility = View.GONE
            binding.cbSeleccionar.setOnCheckedChangeListener(null)
            binding.cbSeleccionar.isChecked = false

            binding.root.setOnClickListener {
                // Tocar fuera de modo selección → alternar comprado
                ingredient.isPurchased = !ingredient.isPurchased

                CoroutineScope(Dispatchers.IO).launch {
                    (context.applicationContext as? ListDBApp)?.ingredientRepository
                        ?.updateIngredientPurchaseStatus(ingredient.id, ingredient.isPurchased)
                }

                binding.root.alpha = if (ingredient.isPurchased) 0.5f else 1.0f
                binding.tvNombre.paintFlags =
                    if (ingredient.isPurchased) binding.tvNombre.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    else binding.tvNombre.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.btnPurchaseConfirm.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        if (ingredient.isPurchased) R.color.shopping_check_confirmed else R.color.shopping_check_default
                    )
                )
            }

            // Habilita edición normal
            binding.btnEditIngredient.setOnClickListener {
                onEditClick(ingredient)
            }
        }
    }

}