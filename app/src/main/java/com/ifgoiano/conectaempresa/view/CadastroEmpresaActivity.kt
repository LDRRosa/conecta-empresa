package com.ifgoiano.conectaempresa.view

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ifgoiano.conectaempresa.databinding.ActivityCadastroEmpresaBinding
import com.ifgoiano.conectaempresa.viewmodel.CadastroEmpresaViewModel

class CadastroEmpresaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroEmpresaBinding
    private val viewModel: CadastroEmpresaViewModel by viewModels()
    private var imagemSelecionada: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imagemSelecionada = it
            binding.imgEmpresa.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroEmpresaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarMascaraTelefone()
        configurarListeners()
        observarViewModel()
    }

    private fun configurarMascaraTelefone() {
        binding.etTelefone.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "(##) #####-####"

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return

                val unmasked = s.toString().replace(Regex("[^\\d]"), "")
                val formatted = StringBuilder()
                var i = 0

                for (m in mask.toCharArray()) {
                    if (m != '#' && unmasked.length > i) {
                        formatted.append(m)
                        continue
                    }
                    if (i >= unmasked.length) break
                    formatted.append(unmasked[i])
                    i++
                }

                isUpdating = true
                binding.etTelefone.setText(formatted.toString())
                binding.etTelefone.setSelection(formatted.length)
                isUpdating = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun configurarListeners() {
        binding.btnSelecionarImagem.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.imgEmpresa.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnCadastrar.setOnClickListener {
            viewModel.cadastrarEmpresa(
                nome = binding.etNomeEmpresa.text.toString(),
                categoria = binding.etCategoria.text.toString(),
                descricao = binding.etDescricao.text.toString(),
                endereco = binding.etEndereco.text.toString(),
                telefone = binding.etTelefone.text.toString(),
                imagemUri = imagemSelecionada
            )
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun observarViewModel() {
        viewModel.status.observe(this) { mensagem ->
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        }

        viewModel.loading.observe(this) { loading ->
            binding.btnCadastrar.isEnabled = !loading
            binding.btnCancelar.isEnabled = !loading
            binding.btnSelecionarImagem.isEnabled = !loading
        }

        viewModel.sucessoCadastro.observe(this) { sucesso ->
            if (sucesso) {
                finish()
            }
        }
    }
}