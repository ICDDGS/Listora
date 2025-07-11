package com.dan.listora.ui.viewholder

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.View
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

    @SuppressLint("StringFormatMatches")
    fun bind(ingredient: IngEntity, selectionMode: Boolean) {
        val context = binding.root.context

        binding.tvNombre.text = ingredient.name
        binding.tvCantidadUnidad.text =
            context.getString(R.string.cantidad_2, ingredient.cant, ingredient.unit)
        binding.tvPrecio.text = context.getString(R.string.precio_2, ingredient.price)

        val isPurchased = ingredient.isPurchased
        updateVisualState(isPurchased)

        if (selectionMode) {
            binding.cbSeleccionar.visibility = View.VISIBLE

            binding.cbSeleccionar.setOnCheckedChangeListener(null)
            binding.cbSeleccionar.isChecked = selectedItems.contains(ingredient.id)
            binding.cbSeleccionar.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedItems.add(ingredient.id)
                else selectedItems.remove(ingredient.id)
            }

            binding.root.setOnClickListener {
                binding.cbSeleccionar.isChecked = !binding.cbSeleccionar.isChecked
            }

            binding.btnEditIngredient.setOnClickListener(null)
            binding.btnPurchaseConfirm.setOnClickListener(null)

        } else {
            binding.cbSeleccionar.visibility = View.GONE
            binding.cbSeleccionar.setOnCheckedChangeListener(null)
            binding.cbSeleccionar.isChecked = false

            binding.root.setOnClickListener {
                ingredient.isPurchased = !ingredient.isPurchased

                CoroutineScope(Dispatchers.IO).launch {
                    val app = context.applicationContext as? ListDBApp
                    app?.ingredientRepository?.updateIngredientPurchaseStatus(ingredient.id, ingredient.isPurchased)

                    if (ingredient.isPurchased) {
                        ingredient.date = System.currentTimeMillis()

                    }
                }


                updateVisualState(ingredient.isPurchased)
            }


            binding.btnEditIngredient.setOnClickListener {
                onEditClick(ingredient)
            }
        }
    }

    private fun updateVisualState(isPurchased: Boolean) {
        val context = binding.root.context

        binding.root.alpha = if (isPurchased) 0.5f else 1.0f
        binding.tvNombre.paintFlags =
            if (isPurchased) binding.tvNombre.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else binding.tvNombre.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        binding.btnPurchaseConfirm.setColorFilter(
            ContextCompat.getColor(
                context,
                if (isPurchased) R.color.incomplete else R.color.complete
            )
        )
    }
}

