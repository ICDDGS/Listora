
package com.dan.listora.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.databinding.ActivityAddIngredientsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.dan.listora.ui.dialog.IngredientDialog
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.ui.adapter.IngredientAdapter

class addIngredientsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddIngredientsBinding
    private var listaId: Long = -1
    private var adapter: IngredientAdapter? = null
    private var selectionMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        listaId = intent.getLongExtra("lista_id", -1)
        val listaNombre = intent.getStringExtra("lista_nombre") ?: "Sin nombre"
        binding.topAppBar.title = "Lista: $listaNombre"

        binding.rvIngredientes.layoutManager = LinearLayoutManager(this)

        binding.fabAddIngredient.setOnClickListener {
            val dialog = IngredientDialog(
                listId = listaId,
                ingredient = null
            ) {
                loadIngredients()
            }
            dialog.show(supportFragmentManager, "IngredientDialog")
        }


        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_select -> {
                    selectionMode = true
                    adapter?.selectionMode = true
                    adapter?.notifyDataSetChanged()
                    true
                }
                R.id.action_accept -> {
                    val idsToDelete = adapter?.selectedItems ?: emptySet()
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val dao = (application as ListDBApp).database.ingredientDAO()
                            idsToDelete.forEach { id -> dao.deleteById(id) }
                        }
                        loadIngredients()
                        selectionMode = false
                        adapter?.selectionMode = false
                        adapter?.selectedItems?.clear()
                    }
                    true
                }
                R.id.action_cancel -> {
                    selectionMode = false
                    adapter?.selectionMode = false
                    adapter?.selectedItems?.clear()
                    adapter?.notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }

        loadIngredients()
    }

    private fun loadIngredients() {
        lifecycleScope.launch {
            val ingredientes = withContext(Dispatchers.IO) {
                val dao = (application as ListDBApp).database.ingredientDAO()
                dao.getIngredientsByListId(listaId)
            }

            val presupuesto = intent.getDoubleExtra("lista_presupuesto", 0.0)
            val totalGastado = ingredientes.sumOf { it.price }
            val restante = presupuesto - totalGastado

            if (presupuesto > 0) {
                binding.tvPresupuestoRestante.visibility = View.VISIBLE
                binding.tvPresupuestoRestante.text = "Presupuesto restante: $${"%.2f".format(restante)}"
            } else {
                binding.tvPresupuestoRestante.visibility = View.GONE
            }

            adapter = IngredientAdapter(ingredientes) { ingrediente: IngEntity ->
                val dialog = IngredientDialog(
                    listId = listaId,
                    ingredient = ingrediente
                ) {
                    loadIngredients()
                }
                dialog.show(supportFragmentManager, "EditIngredientDialog")
            }

            binding.rvIngredientes.adapter = adapter
        }
    }
}
