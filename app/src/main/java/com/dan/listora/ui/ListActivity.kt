package com.dan.listora.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Update
import com.dan.listora.R
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.ListRepository
import com.dan.listora.data.db.model.ListEntity
import com.dan.listora.databinding.ActivityListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding

    //Listado lectura BD
    private var lists: MutableList<ListEntity> = mutableListOf()

    private lateinit var repository: ListRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //repository = (application as ListDBApp).repository

        //UpdateUI()

    }

    private fun UpdateUI(){
        lifecycleScope.launch(Dispatchers.IO) {
            lists = repository.getAllLists()

            withContext(Dispatchers.Main){
                binding.tvSinRegistros.visibility =
                    if (lists.isNotEmpty()) View.INVISIBLE else View.VISIBLE
            }

        }
    }

    fun click (view: View){
        val list = ListEntity(
            name = "Nueva lista",
        )
        lifecycleScope.launch(Dispatchers.IO) {
            repository.insertList(list)
        }
    }
}