package com.dan.listora.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.dan.listora.util.styledSnackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
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

            val gastosPorDia = MutableList(7) { 0.0 }

            for (item in historial) {
                val cal = Calendar.getInstance()
                cal.timeInMillis = item.fecha
                val index = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                gastosPorDia[index] += item.costo
            }

            val entries = gastosPorDia.mapIndexed { index, valor ->
                BarEntry(index.toFloat(), valor.toFloat())
            }

            val dataSet = BarDataSet(entries, getString(R.string.gastos_por_dia)).apply {
                valueTextSize = 12f
            }

            val data = BarData(dataSet)

            barChart.axisLeft.setDrawGridLines(false)
            barChart.axisRight.setDrawGridLines(false)
            barChart.xAxis.setDrawGridLines(false)

            barChart.axisRight.isEnabled = false


            barChart.data = data

            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(
                listOf(getString(R.string.lun),
                    getString(R.string.mar), getString(R.string.mie),
                    getString(R.string.jue), getString(R.string.vie), getString(R.string.sab),
                    getString(
                        R.string.dom
                    ))
            )
            barChart.invalidate()

            val formato = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val finSemana = Calendar.getInstance().apply { timeInMillis = currentWeekStart.timeInMillis }
            finSemana.add(Calendar.DAY_OF_MONTH, 6)
            binding.tvSemana.text = getString(
                R.string.semana,
                formato.format(currentWeekStart.time),
                formato.format(finSemana.time)
            )
        }
    }

    @SuppressLint("StringFormatMatches")
    private suspend fun exportarSemanaAExcel() {
        val app = requireActivity().application as ListDBApp
        val historial = app.historialRepository.getHistorialEntreFechas(
            currentWeekStart.timeInMillis,
            currentWeekStart.timeInMillis + 6 * 24 * 60 * 60 * 1000
        )

        val calendar = Calendar.getInstance()
        val weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        val dateFormat = SimpleDateFormat("MMM_yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        var fileName = getString(R.string.semana_csv, weekOfMonth, formattedDate)
        var file = File(downloadsDir, fileName)
        var index = 1

        while (file.exists()) {
            fileName = getString(R.string.semana_csv_2, weekOfMonth, formattedDate, index)
            file = File(downloadsDir, fileName)
            index++
        }

        try {
            val writer = OutputStreamWriter(FileOutputStream(file))
            writer.append(getString(R.string.ingrediente_cantidad_unidad_costo_fecha))

            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            historial.forEach {
                writer.append("${it.ingrediente},${it.cantidad},${it.unidad},${it.costo},${formato.format(Date(it.fecha))}\n")
            }

            writer.flush()
            writer.close()

            withContext(Dispatchers.Main) {
                binding.root.styledSnackbar(getString(R.string.exportado_a_descargas, file.name), requireContext())

            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                binding.root.styledSnackbar(getString(R.string.error_al_exportar, e.message), requireContext())


            }
        }
    }


}
