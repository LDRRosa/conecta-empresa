package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ifgoiano.conectaempresa.databinding.ActivityLoginBinding
import com.ifgoiano.conectaempresa.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verifica se já está logado
        viewModel.verificarSessao()

        configurarObservadores()
        configurarBotoes()
    }

    private fun configurarObservadores() {
        viewModel.navegarParaHome.observe(this) { navegar ->
            if (navegar) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun configurarBotoes() {
        binding.btnEntrar.setOnClickListener {
            viewModel.fazerLogin(
                binding.etEmail.text.toString(),
                binding.etSenha.text.toString()
            )
        }

        binding.tvCriarConta.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }
}