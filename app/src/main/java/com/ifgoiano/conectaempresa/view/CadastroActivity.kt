package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.viewmodel.CadastroViewModel

class CadastroActivity : AppCompatActivity() {

    private val viewModel: CadastroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val nome = findViewById<EditText>(R.id.inputNome)
        val email = findViewById<EditText>(R.id.inputEmail)
        val senha = findViewById<EditText>(R.id.inputSenha)
        val confirmarSenha = findViewById<EditText>(R.id.inputConfirmarSenha)
        val botaoCriar = findViewById<Button>(R.id.btnCriarConta)

        botaoCriar.setOnClickListener {
            viewModel.criarConta(
                nome.text.toString(),
                email.text.toString(),
                senha.text.toString(),
                confirmarSenha.text.toString()
            )
        }

        viewModel.status.observe(this) { mensagem ->
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        }

        viewModel.navegarParaLogin.observe(this) { navegar ->
            if (navegar) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }
    }
}