package com.dan.listora.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Listora"

        ingredientList = mutableListOf()

        adapter = IngredientAdapter(ingredientList) { ingrediente ->
            // TODO: acción editar
        }

        binding.rvIngredientes.layoutManager = LinearLayoutManager(this)
        binding.rvIngredientes.adapter = adapter

        binding.fabAddIngredient.setOnClickListener {
            // TODO: acción agregar ingrediente
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
    }

    private fun cargarIngredientes() {
        val repo = (application as ListDBApp).ingredientRepository

        lifecycleScope.launch {
            val ingredientes = repo.getAllIngredients()
            ingredientList.clear()
            ingredientList.addAll(ingredientes)
            adapter.notifyDataSetChanged()

            val total = ingredientList.sumOf { it.price ?: 0.0 }
            binding.tvPresupuesto.text = "Presupuesto: $%.2f".format(total)
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
                adapter.selectedItems.clear()
                adapter.selectionMode = false
                adapter.notifyDataSetChanged()
                cargarIngredientes()
            }
        } else {
            Toast.makeText(this, "Selecciona elementos para eliminar", Toast.LENGTH_SHORT).show()
        }
    }
}