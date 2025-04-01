package com.dan.listora.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dan.listora.R
import com.dan.listora.databinding.ActivityAddIngredientsBinding

class addIngredientsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddIngredientsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        val listaId = intent.getIntExtra("lista_id", -1)
        val listaNombre = intent.getStringExtra("lista_nombre")

        binding.topAppBar.title = "Lista: $listaNombre"



    }
}