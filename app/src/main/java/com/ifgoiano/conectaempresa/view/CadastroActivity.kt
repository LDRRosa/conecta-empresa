package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifgoiano.conectaempresa.databinding.ActivityCadastroBinding
import com.ifgoiano.conectaempresa.databinding.DialogErroBinding
import com.ifgoiano.conectaempresa.databinding.DialogSucessoBinding
import com.ifgoiano.conectaempresa.viewmodel.CadastroViewModel

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private val viewModel: CadastroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarObservadores()
        configurarBotoes()
    }

    private fun configurarObservadores() {
        viewModel.loading.observe(this) { loading ->
            binding.loadingOverlay.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnCriarConta.isEnabled = !loading
        }

        viewModel.mensagemErro.observe(this) { mensagem ->
            mensagem?.let {
                mostrarDialogoErro(it)
                viewModel.limparMensagemErro()
            }
        }

        viewModel.sucessoCadastro.observe(this) { sucesso ->
            if (sucesso) {
                mostrarDialogoSucesso()
            }
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

    private fun configurarBotoes() {
        binding.btnCriarConta.setOnClickListener {
            viewModel.criarConta(
                binding.inputNome.text.toString(),
                binding.inputEmail.text.toString(),
                binding.inputSenha.text.toString(),
                binding.inputConfirmarSenha.text.toString()
            )
        }

        binding.tvJaPossuiConta.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun mostrarDialogoSucesso() {
        val dialogBinding = DialogSucessoBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvTituloSucesso.text = "Sucesso!"
        dialogBinding.tvMensagemSucesso.text = "Conta criada com sucesso!"

        dialogBinding.btnOkSucesso.setOnClickListener {
            dialog.dismiss()
            viewModel.navegarParaLoginAposSucesso()
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