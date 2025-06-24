package com.dan.listora.ui.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.dan.listora.R
import com.dan.listora.data.db.ListDataBase
import com.dan.listora.data.db.model.RecipeIngredientEntity
import com.dan.listora.databinding.DialogIngredientBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeIngredientDialog(
    private val recipeId: Long,
    private val onSuccess: () -> Unit
) : DialogFragment() {

    private var _binding: DialogIngredientBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogIngredientBinding.inflate(LayoutInflater.from(context))

        val unidades = resources.getStringArray(R.array.unidades_array)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, unidades)
        binding.spUnidad.adapter = spinnerAdapter

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.agregar_ingrediente_receta))
            .setView(binding.root)
            .setPositiveButton(getString(R.string.guardar)) { _, _ ->
                val nombre = binding.etNombre.text.toString().trim()
                val cantidad = binding.etCantidad.text.toString().toDoubleOrNull() ?: 0.0
                val unidad = binding.spUnidad.selectedItem.toString()

                if (nombre.isNotEmpty() && cantidad > 0) {
                    val nuevoIngrediente = RecipeIngredientEntity(
                        recipeId = recipeId,
                        name = nombre,
                        unit = unidad,
                        baseQuantity = cantidad
                    )

                    lifecycleScope.launch(Dispatchers.IO) {
                        val dao = ListDataBase.getDatabase(requireContext()).recipeDAO()
                        dao.insertIngredient(nuevoIngrediente)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), getString(R.string.ingrediente_agregado), Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.completar_campos), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
