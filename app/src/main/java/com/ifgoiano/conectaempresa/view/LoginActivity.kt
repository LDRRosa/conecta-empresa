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

        binding.btnEntrar.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val senha = binding.etSenha.text.toString()
            viewModel.login(email, senha)
        }

        //Tela de cadastro
        binding.tvCriarConta.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        //Observa resultado do login
        viewModel.loginResult.observe(this) { sucesso ->
            if (sucesso) {
                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish() // impede voltar para a tela de login
            } else {
                Toast.makeText(this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
