package com.dan.listora.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import com.dan.listora.data.db.model.RecipeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Spinner
import com.dan.listora.R
import com.dan.listora.ui.RecipeDetailActivity
import kotlinx.coroutines.CoroutineScope

import com.dan.listora.data.db.ListDataBase

class RecipeDialog(
    private val context: Context,
    private val onSuccess: () -> Unit
) {

    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_recipe, null)
        val recipeName = dialogView.findViewById<EditText>(R.id.etRecipeName)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)

        // Lista de categorías para el spinner
        val categories = listOf("Desayuno", "Comida", "Cena")
        categorySpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categories)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Nueva receta")
            .setView(dialogView)
            .setPositiveButton("Guardar", null) // Se setea manualmente para validación
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val name = recipeName.text.toString().trim()
                val category = categorySpinner.selectedItem.toString()

                if (name.isEmpty()) {
                    recipeName.error = "El nombre es obligatorio"
                    return@setOnClickListener
                }

                val recipe = RecipeEntity(
                    name = name,
                    imageUrl = null,
                    category = category,
                    servings = 0,
                    steps = ""
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val db = ListDataBase.getDatabase(context)
                    val recipeId = db.recipeDAO().insertRecipe(recipe)

                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                        onSuccess()

                        val intent = Intent(context, RecipeDetailActivity::class.java)
                        intent.putExtra("recipe_id", recipeId)
                        context.startActivity(intent)
                    }
                }
            }
        }

        dialog.show()
    }
}

