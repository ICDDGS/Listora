package com.dan.listora.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.db.model.RecipeEntity
import com.dan.listora.data.db.model.RecipeIngredientEntity
import com.dan.listora.databinding.ActivityRecipeDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private var originalServings = 1
    private var currentServings = 1
    private var ingredientList: List<RecipeIngredientEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipeId = intent.getLongExtra("recipe_id", -1)
        if (recipeId == -1L) {
            Toast.makeText(this, "Receta no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val dao = (application as ListDBApp).database.recipeDAO()

            val recipe = withContext(Dispatchers.IO) { dao.getRecipeById(recipeId) }
            ingredientList = withContext(Dispatchers.IO) { dao.getIngredientsForRecipe(recipeId) }

            recipe?.let {
                originalServings = it.servings
                currentServings = it.servings

                title = it.name
                binding.tvRecipeTitle.text = it.name
                binding.tvCategory.text = it.category
                binding.tvOriginalServings.text = "Porciones originales: ${it.servings}"
                binding.tvCurrentServings.text = currentServings.toString()
                binding.tvSteps.text = it.steps

                if (!it.imageUrl.isNullOrEmpty()) {
                    Glide.with(this@RecipeDetailActivity)
                        .load(it.imageUrl)
                        .into(binding.ivRecipeImage)
                }

                updateIngredients()

                binding.btnIncrease.setOnClickListener {
                    if (currentServings < 20) {
                        currentServings++
                        binding.tvCurrentServings.text = currentServings.toString()
                        updateIngredients()
                    }
                }

                binding.btnDecrease.setOnClickListener {
                    if (currentServings > 1) {
                        currentServings--
                        binding.tvCurrentServings.text = currentServings.toString()
                        updateIngredients()
                    }
                }
            }
        }
        binding.fabAddToList.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val listDao = (application as ListDBApp).database.listDao()
                val ingredientDao = (application as ListDBApp).database.ingredientDAO()

                val listas = withContext(Dispatchers.IO) { listDao.getAllLists() }

                if (listas.isEmpty()) {
                    Toast.makeText(this@RecipeDetailActivity, "No hay listas disponibles", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val nombres = listas.map { it.name }.toTypedArray()

                AlertDialog.Builder(this@RecipeDetailActivity)
                    .setTitle("Selecciona una lista")
                    .setItems(nombres) { _, which ->
                        val listaId = listas[which].id

                        CoroutineScope(Dispatchers.IO).launch {
                            ingredientList.forEach { ing ->
                                val escalado = ing.baseQuantity * currentServings / originalServings
                                ingredientDao.insertIngredient(
                                    com.dan.listora.data.db.model.IngEntity(
                                        name = ing.name,
                                        cant = escalado.toString(),
                                        unit = ing.unit,
                                        price = 0.0,
                                        idLista = listaId
                                    )
                                )
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@RecipeDetailActivity, "Ingredientes añadidos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

    }

    private fun updateIngredients() {
        val scaled = ingredientList.joinToString("\n") { ing ->
            val cantidadEscalada = ing.baseQuantity * currentServings / originalServings
            "- ${ing.name}: ${"%.2f".format(cantidadEscalada)} ${ing.unit}"
        }
        binding.tvIngredients.text = scaled
    }
}