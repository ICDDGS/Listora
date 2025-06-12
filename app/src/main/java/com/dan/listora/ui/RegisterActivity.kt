package com.dan.listora.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dan.listora.R
import com.dan.listora.databinding.ActivityRegisterBinding
import com.dan.listora.util.styledSnackbar


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.tiEmail.text.toString().trim()
            val password = binding.tiPassword.text.toString()
            val acceptTerms = binding.cbTerms.isChecked

            if (email.isNotEmpty() && password.isNotEmpty() && acceptTerms) {
                binding.root.styledSnackbar(getString(R.string.registro_exitoso), this)
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            } else {
                binding.root.styledSnackbar(getString(R.string.completar_campos), this)

            }

        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }



    }
}