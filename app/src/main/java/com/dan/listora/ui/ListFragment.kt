package com.dan.listora.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.FragmentListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ListFragment : Fragment(R.layout.fragment_list) {
    private lateinit var binding: FragmentListBinding

    private var lists: MutableList<ListEntity> = mutableListOf()

    private lateinit var repository: ListRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(layoutInflater)

        binding.tvTitle.text = "Mis Listas"

        val app = requireActivity().application as ListDBApp
        repository = app.repository


        updateUI()
        binding.addListButton.setOnClickListener {
            val list = ListEntity(name = "Hola Lista")
            lifecycleScope.launch(Dispatchers.IO) {
                repository.insertList(list)
            }
        }




        return binding.root

    }

    private fun updateUI() {
        lifecycleScope.launch(Dispatchers.IO) {
            lists = repository.getAllLists()

        }
    }





}