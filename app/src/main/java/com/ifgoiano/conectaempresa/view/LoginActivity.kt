package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifgoiano.conectaempresa.databinding.ActivityLoginBinding
import com.ifgoiano.conectaempresa.databinding.DialogErroBinding
import com.ifgoiano.conectaempresa.databinding.DialogSucessoBinding
import com.ifgoiano.conectaempresa.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.verificarSessao()

        configurarObservadores()
        configurarBotoes()
    }

    private fun configurarObservadores() {
        viewModel.loading.observe(this) { loading ->
            binding.loadingOverlay.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnEntrar.isEnabled = !loading
        }

        viewModel.mensagemErro.observe(this) { mensagem ->
            mensagem?.let {
                mostrarDialogoErro(it)
                viewModel.limparMensagemErro()
            }
        }

        viewModel.sucessoLogin.observe(this) { sucesso ->
            if (sucesso) {
                mostrarDialogoSucesso()
            }
        }

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

    private fun mostrarDialogoSucesso() {
        val dialogBinding = DialogSucessoBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvTituloSucesso.text = "Bem-vindo!"
        dialogBinding.tvMensagemSucesso.text = "Login realizado com sucesso!"

        dialogBinding.btnOkSucesso.setOnClickListener {
            dialog.dismiss()
            viewModel.navegarParaHomeAposSucesso()
        }

        dialog.show()

        dialogBinding.root.alpha = 0f
        dialogBinding.root.scaleX = 0.8f
        dialogBinding.root.scaleY = 0.8f
        dialogBinding.root.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start()
    }

    private fun mostrarDialogoErro(mensagem: String) {
        val dialogBinding = DialogErroBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvMensagemErro.text = mensagem

        dialogBinding.btnOkErro.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialogBinding.root.alpha = 0f
        dialogBinding.root.scaleX = 0.8f
        dialogBinding.root.scaleY = 0.8f
        dialogBinding.root.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start()
    }
}