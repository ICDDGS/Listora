package com.dan.listora.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var barChart: BarChart
    private var currentWeekStart: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        barChart = binding.barChart
        ajustarASemanaActual()
        cargarDatos()

        binding.btnSemanaAnterior.setOnClickListener {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1)
            cargarDatos()
        }

        binding.btnSemanaSiguiente.setOnClickListener {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, 1)
            cargarDatos()
        }

        binding.btnExportarExcel.setOnClickListener {
            lifecycleScope.launch {
                exportarSemanaAExcel()
            }
        }
    }

    private fun ajustarASemanaActual() {
        currentWeekStart.firstDayOfWeek = Calendar.MONDAY
        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            val app = requireActivity().application as ListDBApp
            val historial = app.historialRepository.getHistorialEntreFechas(
                inicio = currentWeekStart.timeInMillis,
                fin = currentWeekStart.timeInMillis + 6 * 24 * 60 * 60 * 1000
            )

            val gastosPorDia = MutableList(7) { 0.0 } // Lunes a Domingo

            for (item in historial) {
                val cal = Calendar.getInstance()
                cal.timeInMillis = item.fecha
                val index = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // lunes = 0, ..., domingo = 6
                gastosPorDia[index] += item.costo
            }

            val entries = gastosPorDia.mapIndexed { index, valor ->
                BarEntry(index.toFloat(), valor.toFloat())
            }

            val dataSet = BarDataSet(entries, "Gastos por día").apply {
                valueTextSize = 12f
            }

            val data = BarData(dataSet)

            //barChart.axisLeft.gridColor = Color.LTGRAY
            //barChart.xAxis.gridColor = Color.LTGRAY
            barChart.axisLeft.setDrawGridLines(false)
            barChart.axisRight.setDrawGridLines(false)
            barChart.xAxis.setDrawGridLines(false)

            barChart.axisRight.isEnabled = false


            barChart.data = data

            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(
                listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            )
            barChart.invalidate()

            val formato = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val finSemana = Calendar.getInstance().apply { timeInMillis = currentWeekStart.timeInMillis }
            finSemana.add(Calendar.DAY_OF_MONTH, 6)
            binding.tvSemana.text = "Semana: ${formato.format(currentWeekStart.time)} - ${formato.format(finSemana.time)}"
        }
    }

    private suspend fun exportarSemanaAExcel() {
        val app = requireActivity().application as ListDBApp
        val historial = app.historialRepository.getHistorialEntreFechas(
            currentWeekStart.timeInMillis,
            currentWeekStart.timeInMillis + 6 * 24 * 60 * 60 * 1000
        )

        val file = File(requireContext().getExternalFilesDir(null), "semana_export_${System.currentTimeMillis()}.csv")
        val writer = FileWriter(file)

        writer.append("Ingrediente,Cantidad,Unidad,Costo,Fecha\n")

        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        historial.forEach {
            writer.append("${it.ingrediente},${it.cantidad},${it.unidad},${it.costo},${formato.format(Date(it.fecha))}\n")
        }

        writer.flush()
        writer.close()

        withContext(Dispatchers.Main) {
            Toast.makeText(requireContext(), "Exportado a ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }
}
