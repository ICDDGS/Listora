package com.dan.listora.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dan.listora.application.ListDBApp
import com.dan.listora.databinding.ActivityResumeBinding
import com.dan.listora.ui.adapter.HistorialAdapter
import kotlinx.coroutines.launch

class ResumeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResumeBinding
    private lateinit var historialAdapter: HistorialAdapter
    private var nombreLista: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResumeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBarResume)
        binding.topAppBarResume.setNavigationOnClickListener {
            finish() // Cierra la actividad y regresa a la anterior
        }



        nombreLista = intent.getStringExtra("nombre_lista") ?: return

        binding.rvCompras.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val repo = (application as ListDBApp).historialRepository
            val historial = repo.getHistorialPorLista(nombreLista)

            historialAdapter = HistorialAdapter(historial)
            binding.rvCompras.adapter = historialAdapter

            val total = historial.sumOf { it.costo }
            binding.tvTotalGastado.text = "Total gastado: $%.2f".format(total)
        }

        binding.btnContinuar.setOnClickListener {
            finish() // Cierra ResumeActivity y vuelve a la anterior
        }

        binding.btnExportar.setOnClickListener {
            exportarAExcel()
        }
    }

    private fun exportarAExcel() {
        Toast.makeText(this, "Exportación pendiente de implementar", Toast.LENGTH_SHORT).show()
        // Aquí podrías generar Excel o CSV más adelante
    }
}
