package com.dan.listora.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.db.model.IngEntity
import com.dan.listora.databinding.ActivityAddIngredientsBinding
import com.dan.listora.ui.adapter.IngredientAdapter
import com.dan.listora.util.styledSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        listaId = intent?.getLongExtra(getString(R.string.lista_id), 0L) ?: 0L


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
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirmar_eliminacion))
                .setMessage(getString(R.string.mensaje_confirmar_eliminacion))
                .setPositiveButton(getString(R.string.si)) { _, _ ->
                    deleteSelectedIngredients()
                    deactivateSelectionMode()
                }
                .setNegativeButton(getString(R.string.no), null)
                .create()
            dialog.show()
            true
        }


        binding.menuSelection.menu.findItem(R.id.action_cancel)?.setOnMenuItemClickListener {
            deactivateSelectionMode()
            true
        }

        cargarIngredientes()

        binding.btnListaCompletada.setOnClickListener {
            val nombreLista = intent.getStringExtra("lista_nombre") ?: return@setOnClickListener
            val idLista = intent.getLongExtra("lista_id", -1)
            if (idLista == -1L) return@setOnClickListener

            CoroutineScope(Dispatchers.IO).launch {
                val repo = (application as ListDBApp).ingredientRepository
                val ingredientes = repo.getIngredientsByListId(idLista)

                val todosComprados = ingredientes.isNotEmpty() && ingredientes.all { it.isPurchased }

                withContext(Dispatchers.Main) {
                    if (!todosComprados) {
                        val dialog = androidx.appcompat.app.AlertDialog.Builder(this@AddIngredientsActivity)
                            .setTitle(getString(R.string.lista_incompleta))
                            .setMessage(getString(R.string.mensaje_lista_incompleta))
                            .setPositiveButton(getString(R.string.si)) { _, _ ->
                                abrirResumeActivity(nombreLista, idLista)
                            }
                            .setNegativeButton(getString(R.string.no), null)
                            .create()
                        dialog.show()
                    } else {
                        abrirResumeActivity(nombreLista, idLista)
                    }
                }
            }
        }


    }
    private fun abrirResumeActivity(nombreLista: String, idLista: Long) {
        val intent = Intent(this, ResumeActivity::class.java)
        intent.putExtra("nombre_lista", nombreLista)
        intent.putExtra("lista_id", idLista)
        startActivity(intent)
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "Recycle")
    private fun cargarIngredientes() {
        val repo = (application as ListDBApp).ingredientRepository
        val presupuestoOriginal = intent.getDoubleExtra("lista_presupuesto", 0.0)

        lifecycleScope.launch {
            val ingredientes = repo.getIngredientsByListId(listaId)

            ingredientList.clear()
            ingredientList.addAll(ingredientes)
            adapter.notifyDataSetChanged()

            if (presupuestoOriginal > 0.0) {
                val totalGastado = ingredientList.sumOf { it.price }
                val presupuestoRestante = presupuestoOriginal - totalGastado

                binding.tvPresupuesto.visibility = View.VISIBLE
                binding.tvPresupuesto.text = getString(R.string.presupuesto_2f).format(presupuestoRestante)

                if (presupuestoRestante < 0) {
                    binding.tvPresupuesto.setTextColor(
                        ContextCompat.getColor(this@AddIngredientsActivity, android.R.color.holo_red_dark)
                    )
                } else {
                    val themedColor = obtainStyledAttributes(intArrayOf(android.R.attr.textColorPrimary))
                        .getColor(0, Color.BLACK)
                    binding.tvPresupuesto.setTextColor(themedColor)

                }
            } else {
                binding.tvPresupuesto.visibility = View.GONE
            }
        }

        binding.btnListaCompletada.apply {

            setTextColor(ContextCompat.getColor(this@AddIngredientsActivity, android.R.color.white))
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

    @SuppressLint("NotifyDataSetChanged")
    private fun activateSelectionMode() {
        selectionActive = true
        adapter.selectionMode = true
        adapter.notifyDataSetChanged()
        binding.topAppBar.visibility = View.GONE
        binding.menuSelection.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deactivateSelectionMode() {
        selectionActive = false
        adapter.selectionMode = false
        adapter.selectedItems.clear()
        adapter.notifyDataSetChanged()
        binding.menuSelection.visibility = View.GONE
        binding.topAppBar.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    @SuppressLint("NotifyDataSetChanged", "StringFormatMatches")
    private fun deleteSelectedIngredients() {
        val idsToDelete = adapter.selectedItems.toList()
        if (idsToDelete.isNotEmpty()) {
            lifecycleScope.launch {
                val repo = (application as ListDBApp).ingredientRepository
                repo.deleteIngredientsByIds(idsToDelete)
                adapter.removeItemsByIds(idsToDelete)
                val cantidadEliminada = idsToDelete.size

                adapter.selectedItems.clear()
                adapter.selectionMode = false
                adapter.notifyDataSetChanged()
                deactivateSelectionMode()
                cargarIngredientes()

                binding.root.let {
                    binding.root.styledSnackbar(getString(R.string.ingrediente_s_eliminado_s, cantidadEliminada), this@AddIngredientsActivity)
                }
            }
        } else {
            binding.root.styledSnackbar(getString(R.string.selecciona_elementos_para_eliminar), this)
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
            .setTitle(getString(R.string.editar_ingrediente))
            .setView(view)
            .setPositiveButton(getString(R.string.guardar)) { _, _ ->
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
            .setNegativeButton(getString(R.string.cancelar), null)
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
            .setTitle(getString(R.string.agregar_ingrediente))
            .setView(view)
            .setPositiveButton(getString(R.string.agregar)) { _, _ ->
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
                        isPurchased = false,
                        date = System.currentTimeMillis()
                    )

                    val repo = (application as ListDBApp).ingredientRepository
                    lifecycleScope.launch {
                        repo.insertIngredient(nuevoIngrediente)
                        cargarIngredientes()
                    }
                } else {
                    binding.root.styledSnackbar(getString(R.string.error_nombre_cantidad), this)
                }
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .create()

        dialog.show()
    }





}