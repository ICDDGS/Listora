package com.dan.listora.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dan.listora.R
import com.dan.listora.databinding.ActivityAddListBinding
import java.util.Calendar

class AddListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.inputNombre.text.toString().trim()
            val fecha = binding.inputFecha.text.toString().trim()
            val presupuestoStr = binding.inputPresupuesto.text.toString().trim()

            if (nombre.isEmpty()) {
                binding.inputNombre.error = "El nombre es obligatorio"
                return@setOnClickListener
            }

            val fechaFinal = if (fecha.isEmpty()) "Sin fecha" else fecha
            val presupuestoFinal = presupuestoStr.toDoubleOrNull() ?: 0.0

            //Agregar datos a nueva lista
            /*
            val nuevaLista = ListEntity(
                name = nombre,
                deadline = fechaFinal,
                budget = presupuestoFinal
            )

             lifecycleScope.launch(Dispatchers.IO) {
                repository.insertList(nuevaLista)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddListActivity, "Lista guardada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            */
            val intent = Intent(this, addIngredientsActivity::class.java)
            startActivity(intent)

        }

        binding.inputFecha.setOnClickListener {
            mostrarDatePicker()
        }
        binding.inputFechaLayout.setEndIconOnClickListener {
            mostrarDatePicker()
        }

    }
    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(this, { _, y, m, d ->
            val fecha = "%02d/%02d/%04d".format(d, m + 1, y)
            binding.inputFecha.setText(fecha)
        }, year, month, day)

        picker.show()
    }

}