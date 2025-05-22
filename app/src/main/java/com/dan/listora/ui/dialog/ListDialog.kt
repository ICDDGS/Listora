package com.dan.listora.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.ListDialogBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ListDialog(
    private val newList: Boolean = true,
    private var list: ListEntity = ListEntity(
        name = "",
        date = "",
        presupuesto = 0.0
    ),
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit
) : DialogFragment() {

    private var _binding: ListDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: Dialog
    private lateinit var saveButton: Button
    private lateinit var repository: ListRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ListDialogBinding.inflate(layoutInflater)

        binding.apply {
            inputNombre.setText(list.name)
            inputFecha.setText(list.date)
            inputPresupuesto.setText(list.presupuesto.toString())
        }

        dialog = if (newList) {
            buildDialog("Guardar", "Cancelar", {
                binding.apply {
                    list.name = inputNombre.text.toString()
                    list.date = inputFecha.text.toString()
                    list.presupuesto = inputPresupuesto.text.toString().toDoubleOrNull() ?: 0.0
                }

                lifecycleScope.launch {
                    try {
                        repository.insertList(list)
                        message("Lista guardada exitosamente")
                        updateUI()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        message("Error al guardar la lista")
                    }
                }
            }, {
                dismiss()
            })
        } else {
            buildDialog("Actualizar", "Cancelar", {
                val nombre = _binding?.inputNombre?.text.toString()
                val fecha = _binding?.inputFecha?.text.toString()
                val presupuesto = _binding?.inputPresupuesto?.text.toString().toDoubleOrNull() ?: 0.0

                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar")
                    .setMessage("¿Deseas actualizar la lista \"$nombre\"?")
                    .setPositiveButton("Sí") { _, _ ->
                        list.name = nombre
                        list.date = fecha
                        list.presupuesto = presupuesto

                        lifecycleScope.launch {
                            try {
                                repository.updateList(list)
                                message("Lista actualizada exitosamente")
                                updateUI()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                message("Error al actualizar la lista")
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }, {
                dismiss()
            })
        }

        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        repository = (requireContext().applicationContext as ListDBApp).repository

        saveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        saveButton.isEnabled = false

        binding.apply {
            setupTextWatcher(inputNombre, inputFecha, inputPresupuesto)
            inputFechaLayout.setEndIconOnClickListener { mostrarDatePicker() }
            inputFecha.setOnClickListener { mostrarDatePicker() }
        }
    }

    private fun mostrarDatePicker() {
        val calendar = java.util.Calendar.getInstance()
        val picker = android.app.DatePickerDialog(requireContext(), { _, y, m, d ->
            val fecha = "%02d/%02d/%04d".format(d, m + 1, y)
            binding.inputFecha.setText(fecha)
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH))

        picker.show()
    }

    private fun validateFields(): Boolean =
        binding.inputNombre.text.toString().isNotEmpty()


    private fun setupTextWatcher(vararg fields: TextInputEditText) {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveButton.isEnabled = validateFields()
            }
        }
        fields.forEach { it.addTextChangedListener(watcher) }
    }

    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit,
    ): Dialog = AlertDialog.Builder(requireContext())
        .setView(binding.root)
        .setTitle("Lista")
        .setPositiveButton(btn1Text) { _, _ -> positiveButton() }
        .setNegativeButton(btn2Text) { _, _ -> negativeButton() }
        .create()
}
