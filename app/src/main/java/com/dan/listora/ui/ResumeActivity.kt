package com.dan.listora.ui

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dan.listora.application.ListDBApp
import com.dan.listora.databinding.ActivityResumeBinding
import com.dan.listora.ui.adapter.HistorialAdapter
import com.dan.listora.ui.adapter.ResumenCompraAdapter
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ResumeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResumeBinding
    private lateinit var historialAdapter: HistorialAdapter
    private var nombreLista: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResumeBinding.inflate(layoutInflater)
        setContentView(binding.root)





        nombreLista = intent.getStringExtra("nombre_lista") ?: return

        binding.rvCompras.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val app = application as ListDBApp
            val ingredientesComprados = app.ingredientRepository
                .getIngredientsByListId(intent.getLongExtra("lista_id", 0L))
                .filter { it.isPurchased }

            val resumenAdapter = ResumenCompraAdapter(ingredientesComprados)
            binding.rvCompras.adapter = resumenAdapter

            val total = ingredientesComprados.sumOf { it.price ?: 0.0 }
            binding.tvTotalGastado.text = "Total gastado: $%.2f".format(total)
        }


        binding.btnContinuar.setOnClickListener {
            lifecycleScope.launch {
                val app = application as ListDBApp
                val idLista = intent.getLongExtra("lista_id", 0L)
                val ingredientesComprados = app.ingredientRepository
                    .getIngredientsByListId(idLista)
                    .filter { it.isPurchased }

                ingredientesComprados.forEach { ingrediente ->
                    app.historialRepository.guardarDesdeIngrediente(app.repository, ingrediente)
                }

                val intent = Intent(this@ResumeActivity, MenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }



        binding.btnExportar.setOnClickListener {
            lifecycleScope.launch {
                exportarAExcel()
            }
        }

    }

    private suspend fun exportarAExcel() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd_MMM_yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        val safeNombreLista = nombreLista.replace(Regex("[^a-zA-Z0-9]"), "_")
        val baseFileName = "${safeNombreLista}_$formattedDate.csv"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        // Verifica existencia y agrega índice si ya existe
        var finalFile = File(downloadsDir, baseFileName)
        var index = 1
        while (finalFile.exists()) {
            val indexedFileName = "${safeNombreLista}_$formattedDate($index).csv"
            finalFile = File(downloadsDir, indexedFileName)
            index++
        }

        try {
            val writer = FileWriter(finalFile, false)

            writer.append("Ingrediente,Cantidad,Unidad,Costo,Fecha\n")

            val historial = (application as ListDBApp)
                .historialRepository
                .getHistorialPorLista(nombreLista)

            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            historial.forEach {
                writer.append("${it.ingrediente},${it.cantidad},${it.unidad},${it.costo},${formato.format(Date(it.fecha))}\n")
            }

            writer.flush()
            writer.close()

            Toast.makeText(this, "Exportado a Descargas: ${finalFile.name}", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }



}
