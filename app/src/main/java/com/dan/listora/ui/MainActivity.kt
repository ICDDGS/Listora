package com.dan.listora.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dan.listora.databinding.ActivityMainBinding
import androidx.core.content.edit
import com.dan.listora.R
import com.dan.listora.util.styledSnackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                prefs.edit { putBoolean("isLoggedIn", true) }

                startActivity(Intent(this, MenuActivity::class.java))
                finish()
                binding.root.styledSnackbar(getString(R.string.bienvenido), this)

            } else {
                binding.root.styledSnackbar(getString(R.string.completar_campos), this)

            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
