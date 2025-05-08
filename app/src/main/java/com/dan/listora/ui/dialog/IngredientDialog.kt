package com.dan.listora.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.room.util.copy
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.databinding.DialogIngredientBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngredientDialog(
    private val listId: Long,
    private var ingredient: IngEntity? = null,
    private val onSuccess: () -> Unit
) : DialogFragment() {

    private var _binding: DialogIngredientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogIngredientBinding.inflate(LayoutInflater.from(context))

        ingredient?.let {
            binding.etNombre.setText(it.name)
            binding.etCantidad.setText(it.cant)
            binding.etPrecio.setText(it.price.toString())

            val unidades = resources.getStringArray(R.array.unidades_array)
            val index = unidades.indexOf(it.unit)
            if (index >= 0) {
                binding.spUnidad.setSelection(index)
            }
        }


        return AlertDialog.Builder(requireContext())
            .setTitle(if (ingredient == null) "Agregar Ingrediente" else "Editar Ingrediente")
            .setView(binding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = binding.etNombre.text.toString().trim()
                val cantidad = binding.etCantidad.text.toString().trim()
                val unidad = binding.spUnidad.selectedItem.toString()
                val precio = binding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0

                if (nombre.isNotEmpty() && cantidad.isNotEmpty() && unidad.isNotEmpty()) {
                    lifecycleScope.launch {
                        val dao = (requireActivity().application as ListDBApp).ingredientDatabase.IngredientDAO()


                        if (ingredient == null) {
                            val nuevo = IngEntity(name = nombre, cant = cantidad, unit = unidad, price = precio, idLista = listId)
                            nuevo.id = 0
                            dao.insertIngredient(nuevo)

                        } else {
                            ingredient!!.apply {
                                name = nombre
                                cant = cantidad
                                unit = unidad
                                price = precio
                            }
                            dao.updateIngredient(ingredient!!)
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Guardado", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}