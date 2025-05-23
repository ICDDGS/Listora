package com.dan.listora.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.databinding.ActivityAddIngredientsBinding
import com.dan.listora.ui.adapter.IngredientAdapter
import kotlinx.coroutines.launch

class AddIngredientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIngredientsBinding
    private lateinit var adapter: IngredientAdapter
    private lateinit var ingredientList: MutableList<IngEntity>
    private var selectionActive = false
    private var listaId: Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listaId = intent?.getLongExtra("lista_id", 0L) ?: 0L


        setSupportActionBar(binding.topAppBar)
        val nombreLista = intent.getStringExtra("lista_nombre") ?: "Lista"
        supportActionBar?.title = nombreLista


        ingredientList = mutableListOf()

        adapter = IngredientAdapter(ingredientList) { ingrediente ->
            mostrarDialogoEditarIngrediente(ingrediente)
        }


        binding.rvIngredientes.layoutManager = LinearLayoutManager(this)
        binding.rvIngredientes.adapter = adapter

        binding.fabAddIngredient.setOnClickListener {
            mostrarDialogoAgregarIngrediente()
        }


        binding.menuSelection.menu.findItem(R.id.action_accept)?.setOnMenuItemClickListener {
            deleteSelectedIngredients()
            deactivateSelectionMode()
            true
        }

        binding.menuSelection.menu.findItem(R.id.action_cancel)?.setOnMenuItemClickListener {
            deactivateSelectionMode()
            true
        }

        cargarIngredientes()

        binding.btnListaCompletada.setOnClickListener {
            val intent = Intent(this, ResumeActivity::class.java) // si usarás ResumeFragment en una actividad
            intent.putExtra("nombre_lista", intent.getStringExtra("lista_nombre") ?: "")
            startActivity(intent)
        }

    }

    private fun cargarIngredientes() {
        val repo = (application as ListDBApp).ingredientRepository
        val presupuestoOriginal = intent.getDoubleExtra("lista_presupuesto", 0.0)

        lifecycleScope.launch {
            val ingredientes = repo.getIngredientsByListId(listaId)

            ingredientList.clear()
            ingredientList.addAll(ingredientes)
            adapter.notifyDataSetChanged()

            if (presupuestoOriginal > 0.0) {
                val totalGastado = ingredientList.sumOf { it.price ?: 0.0 }
                val presupuestoRestante = presupuestoOriginal - totalGastado

                binding.tvPresupuesto.visibility = View.VISIBLE
                binding.tvPresupuesto.text = "Presupuesto: $%.2f".format(presupuestoRestante)

                if (presupuestoRestante < 0) {
                    binding.tvPresupuesto.setTextColor(
                        androidx.core.content.ContextCompat.getColor(this@AddIngredientsActivity, android.R.color.holo_red_dark)
                    )
                } else {
                    binding.tvPresupuesto.setTextColor(
                        androidx.core.content.ContextCompat.getColor(this@AddIngredientsActivity, com.dan.listora.R.color.black)
                    )
                }
            } else {
                binding.tvPresupuesto.visibility = View.GONE
            }
        }
        val todosComprados = ingredientList.isNotEmpty() && ingredientList.all { it.isPurchased }

        binding.btnListaCompletada.apply {
            alpha = if (todosComprados) 1.0f else 0.5f
            setTextColor(
                ContextCompat.getColor(
                    this@AddIngredientsActivity,
                    if (todosComprados) R.color.colorPrimary else R.color.colorPrimaryVariant
                )
            )
        }

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!selectionActive) {
            menuInflater.inflate(R.menu.menu_add_ingredients, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_ingredients -> {
                activateSelectionMode()
                true
            }
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun activateSelectionMode() {
        selectionActive = true
        adapter.selectionMode = true
        adapter.notifyDataSetChanged()
        binding.topAppBar.visibility = View.GONE
        binding.menuSelection.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    private fun deactivateSelectionMode() {
        selectionActive = false
        adapter.selectionMode = false
        adapter.selectedItems.clear()
        adapter.notifyDataSetChanged()
        binding.menuSelection.visibility = View.GONE
        binding.topAppBar.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    private fun deleteSelectedIngredients() {
        val idsToDelete = adapter.selectedItems.toList()
        if (idsToDelete.isNotEmpty()) {
            lifecycleScope.launch {
                val repo = (application as ListDBApp).ingredientRepository
                repo.deleteIngredientsByIds(idsToDelete)
                adapter.removeItemsByIds(idsToDelete)
                val cantidadEliminada = idsToDelete.size

                // Limpiar estado
                adapter.selectedItems.clear()
                adapter.selectionMode = false
                adapter.notifyDataSetChanged()
                deactivateSelectionMode()
                cargarIngredientes()

                // Mostrar Snackbar
                binding.root?.let {
                    com.google.android.material.snackbar.Snackbar.make(
                        it,
                        "$cantidadEliminada ingrediente(s) eliminado(s)",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "Selecciona elementos para eliminar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoEditarIngrediente(ingrediente: IngEntity) {
        val view = layoutInflater.inflate(R.layout.dialog_ingredient, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val etCantidad = view.findViewById<EditText>(R.id.etCantidad)
        val spUnidad = view.findViewById<Spinner>(R.id.spUnidad)
        val etPrecio = view.findViewById<EditText>(R.id.etPrecio)

        etNombre.setText(ingrediente.name)
        etCantidad.setText(ingrediente.cant)
        etPrecio.setText(ingrediente.price.toString())

        val unidades = resources.getStringArray(R.array.unidades_array)
        val indexUnidad = unidades.indexOf(ingrediente.unit)
        if (indexUnidad >= 0) spUnidad.setSelection(indexUnidad)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Editar Ingrediente")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val repo = (application as ListDBApp).ingredientRepository
                lifecycleScope.launch {
                    ingrediente.name = etNombre.text.toString()
                    ingrediente.cant = etCantidad.text.toString()
                    ingrediente.unit = spUnidad.selectedItem.toString()
                    ingrediente.price = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                    repo.updateIngredient(ingrediente)
                    cargarIngredientes()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun mostrarDialogoAgregarIngrediente() {
        val view = layoutInflater.inflate(R.layout.dialog_ingredient, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val etCantidad = view.findViewById<EditText>(R.id.etCantidad)
        val spUnidad = view.findViewById<Spinner>(R.id.spUnidad)
        val etPrecio = view.findViewById<EditText>(R.id.etPrecio)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Agregar Ingrediente")
            .setView(view)
            .setPositiveButton("Agregar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val cantidad = etCantidad.text.toString().trim()
                val unidad = spUnidad.selectedItem.toString()
                val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0

                if (nombre.isNotEmpty() && cantidad.isNotEmpty()) {
                    val nuevoIngrediente = IngEntity(
                        name = nombre,
                        cant = cantidad,
                        unit = unidad,
                        price = precio,
                        idLista = listaId,
                        isPurchased = false
                    )

                    val repo = (application as ListDBApp).ingredientRepository
                    lifecycleScope.launch {
                        repo.insertIngredient(nuevoIngrediente)
                        cargarIngredientes()
                    }
                } else {
                    Toast.makeText(this, "Nombre y cantidad son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }



}