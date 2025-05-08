package com.dan.listora.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.dan.listora.data.db.IngredientDAO
import com.dan.listora.data.db.model.IngEntity

class addIngredientsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddIngredientsBinding
    private var listaId: Long = -1
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


        loadIngredients()
    }

    private fun loadIngredients() {
        lifecycleScope.launch {
            val ingredientes = withContext(Dispatchers.IO) {
                val dao = (application as ListDBApp).ingredientDatabase.IngredientDAO()
                dao.getIngredientsByListId(listaId)
            }

            // Asumiendo que recibes el presupuesto desde el intent
            val presupuesto = intent.getDoubleExtra("lista_presupuesto", 0.0)
            val totalGastado = ingredientes.sumOf { it.price }
            val restante = presupuesto - totalGastado

            if (presupuesto > 0) {
                binding.tvPresupuestoRestante.visibility = View.VISIBLE
                binding.tvPresupuestoRestante.text = "Presupuesto restante: $${"%.2f".format(restante)}"
            } else {
                binding.tvPresupuestoRestante.visibility = View.GONE
            }

            binding.rvIngredientes.adapter = IngredientAdapter(ingredientes) { ingrediente: IngEntity ->
                val dialog = IngredientDialog(
                    listId = listaId,
                    ingredient = ingrediente
                ) {
                    loadIngredients()
                }
                dialog.show(supportFragmentManager, "EditIngredientDialog")
            }
        }
    }




}