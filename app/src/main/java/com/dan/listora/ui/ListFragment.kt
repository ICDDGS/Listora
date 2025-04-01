package com.dan.listora.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.FragmentListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListFragment : Fragment(R.layout.fragment_list) {
    private lateinit var binding: FragmentListBinding

    private var lists: MutableList<ListEntity> = mutableListOf()

    private lateinit var repository: ListRepository

    private lateinit var listAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(layoutInflater)
        //binding.tvTitle.text = "Mis Listas"


        binding.addListButton.setOnClickListener {
            val intent = Intent(requireContext(), AddListActivity::class.java)
            startActivity(intent)

        }






        return binding.root

    }

    private fun updateUI() {
        lifecycleScope.launch(Dispatchers.IO) {
            val listas = repository.getAllLists()

            withContext(Dispatchers.Main) {
                if (listas.isEmpty()) {
                    binding.tvSinRegistros.visibility = View.VISIBLE
                } else {
                    binding.tvSinRegistros.visibility = View.GONE
                }

                val adapter = ListAdapter { listaSeleccionada ->
                    // Ir a AddIngredientsActivity con datos
                    val intent = Intent(requireContext(), addIngredientsActivity::class.java)
                    intent.putExtra("lista_id", listaSeleccionada.id)
                    intent.putExtra("lista_nombre", listaSeleccionada.name)
                    startActivity(intent)
                }

                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = adapter
                adapter.updateList(listas.toMutableList())
            }
        }
    }






}