package com.dan.listora.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
    private val recipeToEdit: RecipeEntity? = null,
    private val onSuccess: () -> Unit
) {

    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_recipe, null)
        val recipeName = dialogView.findViewById<EditText>(R.id.etRecipeName)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)

        val categorias = context.resources.getStringArray(R.array.categoria_receta_array)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.nueva_receta))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.guardar), null)
            .setNegativeButton(context.getString(R.string.cancelar)) { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val name = recipeName.text.toString().trim()
                val category = spinner.selectedItem.toString()

                if (name.isEmpty()) {
                    recipeName.error = context.getString(R.string.el_nombre_es_obligatorio)
                    return@setOnClickListener
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val db = ListDataBase.getDatabase(context)
                    val recipeId: Long = if (recipeToEdit != null) {
                        val updated = recipeToEdit.copy(name = name, category = category)
                        db.recipeDAO().updateRecipe(updated)
                        updated.id
                    } else {
                        val newRecipe = RecipeEntity(
                            name = name,
                            imageUrl = null,
                            category = category,
                            servings = 0,
                            steps = ""
                        )
                        db.recipeDAO().insertRecipe(newRecipe)
                    }

                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                        onSuccess()
                    }
                }
            }

            recipeToEdit?.let { recipe ->
                recipeName.setText(recipe.name)
                val index = categorias.indexOf(recipe.category)
                if (index >= 0) spinner.setSelection(index)
            }
        }

        dialog.show()
    }
}

