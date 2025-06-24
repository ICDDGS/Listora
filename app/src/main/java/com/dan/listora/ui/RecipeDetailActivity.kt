package com.dan.listora.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
import com.dan.listora.util.styledSnackbar
import kotlinx.coroutines.*

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private var originalServings = 1
    private var currentServings = 1
    private var ingredientList: List<RecipeIngredientEntity> = emptyList()
    private var currentRecipeId: Long = -1L
    private lateinit var adapter: RecipeIngredientAdapter


    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        currentRecipeId = intent.getLongExtra("recipe_id", -1)
        if (currentRecipeId == -1L) {
            binding.root.styledSnackbar(getString(R.string.receta_no_encontrada), this)
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

        binding.btnGuardarPasos.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val stepsText = findViewById<EditText>(R.id.editSteps).text.toString()

                val dao = (application as ListDBApp).database.recipeDAO()
                val currentRecipe = dao.getRecipeById(currentRecipeId)

                currentRecipe?.let {
                    val updatedRecipe = it.copy(steps = stepsText)
                    dao.updateRecipe(updatedRecipe)

                    withContext(Dispatchers.Main) {
                        binding.root.styledSnackbar(getString(R.string.pasos_actualizados), this@RecipeDetailActivity)

                    }
                }
            }
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
                binding.tvCurrentServings.text = currentServings.toString()
                findViewById<EditText>(R.id.editSteps).setText(it.steps)

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
        val spUnidad = view.findViewById<Spinner>(R.id.spUnidad) // <- AQUÍ estaba el problema

        val unidades = resources.getStringArray(R.array.unidades_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unidades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spUnidad.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.agg_ingrediente_receta))
            .setView(view)
            .setPositiveButton(getString(R.string.agregar)) { dialog, _ ->
                val nombre = etNombre.text.toString().trim()
                val cantidad = etCantidad.text.toString().toDoubleOrNull() ?: 0.0
                val unidad = spUnidad.selectedItem.toString() // <- Ahora sí funciona

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
                    binding.root.styledSnackbar(getString(R.string.completar_campos), this)
                }

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }


    private fun eliminarIngrediente(ingrediente: RecipeIngredientEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = ListDataBase.getDatabase(applicationContext)
            db.recipeDAO().deleteIngredient(ingrediente)
            loadRecipeAndIngredients()
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun agregarIngredientesALista() {
        CoroutineScope(Dispatchers.Main).launch {
            val listDao = (application as ListDBApp).database.listDao()
            val ingredientDao = (application as ListDBApp).database.ingredientDAO()

            val listas = withContext(Dispatchers.IO) { listDao.getAllLists() }

            if (listas.isEmpty()) {
                binding.root.styledSnackbar(getString(R.string.no_hay_listas_disponibles), this@RecipeDetailActivity)
                return@launch
            }

            val nombres = listas.map { it.name }.toTypedArray()

            AlertDialog.Builder(this@RecipeDetailActivity)
                .setTitle(getString(R.string.selecciona_lista))
                .setItems(nombres) { _, which ->
                    val listaId = listas[which].id

                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("Debug",
                            getString(R.string.ingredientes_a_agregar, ingredientList.size))

                        ingredientList.forEach { ing ->
                            val escalado = ing.baseQuantity * currentServings / originalServings
                            ingredientDao.insertIngredient(
                                com.dan.listora.data.db.model.IngEntity(
                                    name = ing.name,
                                    cant = escalado.toString(),
                                    unit = ing.unit,
                                    price = 0.0,
                                    idLista = listaId,
                                    date = System.currentTimeMillis(),
                                )
                            )
                        }

                        withContext(Dispatchers.Main) {
                            binding.root.styledSnackbar(getString(R.string.ingredientes_anadidos), this@RecipeDetailActivity)

                        }
                    }

                }
                .setNegativeButton(getString(R.string.cancelar), null)
                .show()
        }
    }
}


