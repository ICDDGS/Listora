
package com.dan.listora.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.databinding.IngredientElementBinding

class IngredientAdapter(
    private val ingredientes: List<IngEntity>,
    private val onEditClick: (IngEntity) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    val selectedItems = mutableSetOf<Long>()
    var selectionMode = false

    inner class IngredientViewHolder(private val binding: IngredientElementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingrediente: IngEntity) {
            binding.tvNombre.text = ingrediente.name
            binding.tvCantidadUnidad.text = "Cantidad: ${ingrediente.cant} ${ingrediente.unit}"
            binding.tvPrecio.text = "Precio: $${ingrediente.price}"

            binding.cbSeleccionar.apply {
                visibility = if (selectionMode) View.VISIBLE else View.GONE
                isChecked = selectedItems.contains(ingrediente.id)
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedItems.add(ingrediente.id)
                    else selectedItems.remove(ingrediente.id)
                }
            }

            binding.root.setOnClickListener {
                if (selectionMode) {
                    binding.cbSeleccionar.isChecked = !binding.cbSeleccionar.isChecked
                } else {
                    onEditClick(ingrediente)
                }
            }

            binding.btnEditIngredient.setOnClickListener {
                if (!selectionMode) onEditClick(ingrediente)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = IngredientElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(ingredientes[position])
    }

    override fun getItemCount(): Int = ingredientes.size
}

