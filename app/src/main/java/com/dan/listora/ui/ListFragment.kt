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
import com.dan.listora.ui.adapter.ListAdapter
import com.dan.listora.ui.dialog.ListDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class ListFragment : Fragment(R.layout.fragment_list) {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        repository = (requireActivity().application as ListDBApp).repository

        listAdapter = ListAdapter(
            onEditClick = { selectedList ->
                val dialog = ListDialog(
                    newList = false,
                    list = selectedList,
                    updateUI = { updateUI() },
                    message = { message(it) }
                )
                dialog.show(childFragmentManager, "ListDialog")
            },
            onItemClick = { selectedList ->
                val intent = Intent(requireContext(), AddIngredientsActivity::class.java)
                intent.putExtra("lista_id", selectedList.id)
                intent.putExtra("lista_nombre", selectedList.name)
                intent.putExtra("lista_presupuesto", selectedList.presupuesto)
                startActivity(intent)
            }
        )


        binding.apply {
            rvListas.layoutManager = LinearLayoutManager(requireContext())
            rvListas.adapter = listAdapter
        }

        binding.addListButton.setOnClickListener {
            val dialog = ListDialog(
                updateUI = { updateUI() },
                message = { message(it) }
            )
            dialog.show(childFragmentManager, "ListDialog")
        }




    }
    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        lifecycleScope.launch {
            lists = repository.getAllLists()

            binding.tvSinRegistros.visibility =
                if (lists.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            listAdapter.updateList(lists)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun message(text: String) {

        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT)
            .setTextColor(requireContext().getColor(R.color.colorOnSecondary))
            .setBackgroundTint(requireContext().getColor(R.color.colorSecondary))
            .show()


    }

}