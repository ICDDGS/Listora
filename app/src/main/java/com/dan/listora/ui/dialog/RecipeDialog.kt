package com.dan.listora.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.db.model.RecipeEntity
import com.dan.listora.data.db.model.RecipeIngredientEntity
import com.dan.listora.databinding.DialogRecipeBinding
import com.dan.listora.ui.adapter.RecipeIngredientAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.LinearLayout


class RecipeDialog(
    private val recipe: RecipeEntity? = null,
    private val onSuccess: () -> Unit
) : DialogFragment() {

    private var _binding: DialogRecipeBinding? = null
    private val binding get() = _binding!!

    private val ingredientes = mutableListOf<RecipeIngredientEntity>()
    private lateinit var adapter: RecipeIngredientAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRecipeBinding.inflate(LayoutInflater.from(context))

        adapter = RecipeIngredientAdapter(ingredientes) { index ->
            AlertDialog.Builder(requireContext())
                .setTitle("Ingrediente")
                .setItems(arrayOf("Editar", "Eliminar")) { _, which ->
                    when (which) {
                        0 -> showIngredientDialog(editIndex = index)
                        1 -> {
                            ingredientes.removeAt(index)
                            adapter.notifyItemRemoved(index)
                        }
                    }
                }
                .show()
        }


        binding.rvIngredientes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngredientes.adapter = adapter

        recipe?.let {
            binding.etNombre.setText(it.name)
            binding.etCategoria.setText(it.category)
            binding.etPorciones.setText(it.servings.toString())
            binding.etImagen.setText(it.imageUrl ?: "")
            binding.etPasos.setText(it.steps)

            lifecycleScope.launch {
                val dao = (requireActivity().application as ListDBApp).database.recipeDAO()
                val ingr = withContext(Dispatchers.IO) {
                    dao.getIngredientsForRecipe(it.id)
                }
                ingredientes.addAll(ingr)
                adapter.notifyDataSetChanged()
            }
        }

        binding.btnAddIngredient.setOnClickListener {
            showIngredientDialog()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (recipe == null) "Agregar Receta" else "Editar Receta")
            .setView(binding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = binding.etNombre.text.toString().trim()
                val categoria = binding.etCategoria.text.toString().trim()
                val porciones = binding.etPorciones.text.toString().toIntOrNull() ?: 1
                val imagen = binding.etImagen.text.toString().trim()
                val pasos = binding.etPasos.text.toString().trim()

                if (nombre.isNotEmpty() && categoria.isNotEmpty()) {
                    lifecycleScope.launch {
                        val dao = (requireActivity().application as ListDBApp).database.recipeDAO()
                        val newId = if (recipe == null) {
                            val id = withContext(Dispatchers.IO) {
                                dao.insertRecipe(
                                    RecipeEntity(
                                        name = nombre,
                                        category = categoria,
                                        servings = porciones,
                                        imageUrl = imagen,
                                        steps = pasos
                                    )
                                )
                            }
                            id
                        } else {
                            withContext(Dispatchers.IO) {
                                dao.updateRecipe(
                                    recipe.copy(
                                        name = nombre,
                                        category = categoria,
                                        servings = porciones,
                                        imageUrl = imagen,
                                        steps = pasos
                                    )
                                )
                                recipe.id
                            }
                        }

                        withContext(Dispatchers.IO) {
                            ingredientes.forEach {
                                dao.insertIngredient(
                                    it.copy(recipeId = newId)
                                )
                            }
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Receta guardada", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Completa los campos requeridos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    private fun showIngredientDialog(editIndex: Int? = null) {
        val inputName = EditText(requireContext())
        inputName.hint = "Nombre"

        val inputQty = EditText(requireContext())
        inputQty.hint = "Cantidad (número)"
        inputQty.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        val inputUnit = EditText(requireContext())
        inputUnit.hint = "Unidad"

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
            addView(inputName)
            addView(inputQty)
            addView(inputUnit)
        }

        editIndex?.let {
            val ing = ingredientes[it]
            inputName.setText(ing.name)
            inputQty.setText(ing.baseQuantity.toString())
            inputUnit.setText(ing.unit)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (editIndex == null) "Nuevo Ingrediente" else "Editar Ingrediente")
            .setView(container)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = inputName.text.toString().trim()
                val unidad = inputUnit.text.toString().trim()
                val cantidad = inputQty.text.toString().toDoubleOrNull() ?: 0.0

                if (nombre.isNotEmpty() && unidad.isNotEmpty() && cantidad > 0) {
                    val nuevo = RecipeIngredientEntity(
                        recipeId = recipe?.id ?: 0L,
                        name = nombre,
                        unit = unidad,
                        baseQuantity = cantidad
                    )
                    if (editIndex == null) {
                        ingredientes.add(nuevo)
                        adapter.notifyItemInserted(ingredientes.size - 1)
                    } else {
                        ingredientes[editIndex] = nuevo
                        adapter.notifyItemChanged(editIndex)
                    }
                } else {
                    Toast.makeText(requireContext(), "Datos inválidos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}