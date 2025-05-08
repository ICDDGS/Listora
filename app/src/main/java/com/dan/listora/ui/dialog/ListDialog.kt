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
import com.dan.listora.data.IngredientRepository
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.ListDialogBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

class ListDialog (
    private val newList: Boolean = true,
    private var list: ListEntity = ListEntity(
        name = "",
        date = "",
        presupuesto = 0.0
    ),
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit
): DialogFragment() {

    private var _binding: ListDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: Dialog

    private lateinit var saveButton: Button


    private lateinit var repository: ListRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        _binding = ListDialogBinding.inflate(layoutInflater)

        binding.apply {
            inputNombre.setText(list.name)
            inputFecha.setText(list.date)
            inputPresupuesto.setText(list.presupuesto.toString())

        }

        dialog = if (newList)
            buildDialog("Guardar", "Cancelar", {
                //Guardar


                binding.apply {
                    list.name = inputNombre.text.toString()
                    list.date = inputFecha.text.toString()
                    list.presupuesto = inputPresupuesto.text.toString().toDoubleOrNull() ?: 0.0
                }

                try {

                    lifecycleScope.launch {

                        val result = async {
                            repository.insertList(list)
                        }

                        result.await()

                        message("Lista guardada exitosamente")

                        updateUI()

                    }

                } catch (e: IOException) {
                    //Manejamos la excepción
                    e.printStackTrace()

                    message("Error al guardar la lista")

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, {
                //Cancelar

            })
        else
            buildDialog(getString(R.string.update_button), getString(R.string.delete_button), {
                //Actualizar
                binding.apply {
                    list.name = inputNombre.text.toString()
                    list.date = inputFecha.text.toString()
                    list.presupuesto = inputPresupuesto.text.toString().toDoubleOrNull() ?: 0.0
                }

                try {

                    lifecycleScope.launch {

                        val result = async {
                            repository.updateList(list)
                        }

                        result.await()

                        message("Lista Actualizada exitosamente")


                        updateUI()

                    }

                } catch (e: IOException) {
                    //Manejamos la excepción
                    e.printStackTrace()
                    message("Error al actualizar la lista")

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, {
                //Eliminar

                //Guardamos el contexto antes de la corrutina para que no se pierda
                val context = requireContext()

                //Diálogo para confirmar
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirmation))
                    .setMessage(getString(R.string.delete_confirmation, list.name))
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        try {
                            lifecycleScope.launch {

                                val result = async {
                                    repository.deleteList(list)
                                }

                                result.await()


                                message(context.getString(R.string.list_deleted))

                                updateUI()

                            }

                        } catch (e: IOException) {
                            //Manejamos la excepción
                            e.printStackTrace()

                            message("Error al eliminar la lista")

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    .setNegativeButton("Cancelar") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .create()
                    .show()


            })
        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        saveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        saveButton.isEnabled = false


        //Instanciamos el repositorio
        repository = (requireContext().applicationContext as ListDBApp).repository

        //Referenciamos el botón "Guardar" del diálgo
        saveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = false

        binding.apply {
            setupTextWatcher(
                inputNombre,
                inputFecha,
                inputPresupuesto
            )
        }
        binding.inputFechaLayout.setEndIconOnClickListener {
            mostrarDatePicker()
        }

        binding.inputFecha.setOnClickListener {
            mostrarDatePicker()
        }


    }
    private fun mostrarDatePicker() {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        val picker = android.app.DatePickerDialog(requireContext(), { _, y, m, d ->
            val fecha = "%02d/%02d/%04d".format(d, m + 1, y)
            binding.inputFecha.setText(fecha)
        }, year, month, day)

        picker.show()
    }


    private fun validateFields(): Boolean =
        binding.inputNombre.text.toString().isNotEmpty()
                && binding.inputFecha.text.toString().isNotEmpty()
                && binding.inputPresupuesto.text.toString().isNotEmpty()


    private fun setupTextWatcher(vararg textFields: TextInputEditText) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        }

        textFields.forEach { it.addTextChangedListener(textWatcher) }
    }

    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit,
    ): Dialog =
        AlertDialog.Builder(requireContext()).setView(binding.root)
            .setTitle("Lista")
            .setPositiveButton(btn1Text) { _, _ ->
                //Click para el botón positivo
                positiveButton()
            }
            .setNegativeButton(btn2Text) { _, _ ->
                //Click para el botón negativo
                negativeButton()
            }
            .create()


}