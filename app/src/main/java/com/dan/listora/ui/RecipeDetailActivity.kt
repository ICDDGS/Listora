package com.dan.listora.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.db.ListDataBase
import com.dan.listora.data.db.model.RecipeIngredientEntity
import com.dan.listora.databinding.ActivityRecipeDetailBinding
import com.dan.listora.ui.adapter.RecipeIngredientAdapter
import kotlinx.coroutines.*

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private var originalServings = 1
    private var currentServings = 1
    private var ingredientList: List<RecipeIngredientEntity> = emptyList()
    private var currentRecipeId: Long = -1L
    private lateinit var adapter: RecipeIngredientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentRecipeId = intent.getLongExtra("recipe_id", -1)
        if (currentRecipeId == -1L) {
            Toast.makeText(this, "Receta no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val btnAddIngredient = findViewById<Button>(R.id.btnAddIngredient)
        btnAddIngredient.setOnClickListener {
            showIngredientDialog()
        }

        binding.rvIngredients.layoutManager = LinearLayoutManager(this)
        adapter = RecipeIngredientAdapter(mutableListOf()) { ingrediente ->
            eliminarIngrediente(ingrediente)
        }
        binding.rvIngredients.adapter = adapter

        loadRecipeAndIngredients()

        binding.fabAddToList.setOnClickListener {
            agregarIngredientesALista()
        }
    }

    private fun loadRecipeAndIngredients() {
        CoroutineScope(Dispatchers.Main).launch {
            val dao = (application as ListDBApp).database.recipeDAO()

            val recipe = withContext(Dispatchers.IO) { dao.getRecipeById(currentRecipeId) }
            ingredientList = withContext(Dispatchers.IO) { dao.getIngredientsForRecipe(currentRecipeId) }

            recipe?.let {
                originalServings = it.servings.coerceAtLeast(1)
                currentServings = it.servings.coerceAtLeast(1)

                title = it.name
                binding.tvRecipeTitle.text = it.name
                binding.tvCategory.text = it.category
                binding.tvOriginalServings.text = "Porciones originales: ${originalServings}"
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
    }

    private fun updateIngredients() {
        val escalados = ingredientList.map {
            it.copy().apply {
                quantity = baseQuantity * currentServings / originalServings
            }
        }
        adapter.updateData(escalados)
    }

    private fun showIngredientDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_ingredient_recipe, null)
        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val etCantidad = view.findViewById<EditText>(R.id.etCantidad)
        val spUnidad = view.findViewById<android.widget.Spinner>(R.id.spUnidad)

        val unidades = resources.getStringArray(R.array.unidades_array)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unidades)
        spUnidad.adapter = spinnerAdapter

        AlertDialog.Builder(this)
            .setTitle("Agregar ingrediente a receta")
            .setView(view)
            .setPositiveButton("Agregar") { dialog, _ ->
                val nombre = etNombre.text.toString().trim()
                val cantidad = etCantidad.text.toString().toDoubleOrNull() ?: 0.0
                val unidad = spUnidad.selectedItem.toString()

                if (nombre.isNotEmpty() && cantidad > 0) {
                    val ingrediente = RecipeIngredientEntity(
                        recipeId = currentRecipeId,
                        name = nombre,
                        unit = unidad,
                        baseQuantity = cantidad
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        ListDataBase.getDatabase(applicationContext)
                            .recipeDAO()
                            .insertIngredient(ingrediente)

                        withContext(Dispatchers.Main) {
                            loadRecipeAndIngredients()
                        }
                    }
                } else {
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarIngrediente(ingrediente: RecipeIngredientEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = ListDataBase.getDatabase(applicationContext)
            db.recipeDAO().deleteIngredient(ingrediente)
            loadRecipeAndIngredients()
        }
    }

    private fun agregarIngredientesALista() {
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
