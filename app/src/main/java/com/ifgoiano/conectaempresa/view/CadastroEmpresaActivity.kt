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
import com.ifgoiano.conectaempresa.adapter.CategoriaAdapter
import com.ifgoiano.conectaempresa.adapter.CategoriaItem

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
        configurarDropdownCategorias()
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

    private fun configurarDropdownCategorias() {
        val categorias = listOf(
            CategoriaItem("🍔", "Restaurantes"),
            CategoriaItem("🛒", "Mercados"),
            CategoriaItem("💊", "Farmácias"),
            CategoriaItem("✂️", "Moda"),
            CategoriaItem("🛠️", "Serviços"),
            CategoriaItem("➕", "Outros")
        )

        val adapter = CategoriaAdapter(this, categorias)
        binding.etCategoria.setAdapter(adapter)

        var categoriaSelecionada: String = ""

        binding.etCategoria.inputType = android.text.InputType.TYPE_NULL
        binding.etCategoria.keyListener = null

        binding.etCategoria.setOnItemClickListener { _, _, position, _ ->
            categoriaSelecionada = categorias[position].nome

            if (categorias[position].nome == "Outros") {
                binding.etCategoria.setText("")
                binding.etCategoria.hint = "Digite a categoria"
                binding.etCategoria.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
                binding.etCategoria.keyListener = android.text.method.TextKeyListener.getInstance()
                binding.etCategoria.requestFocus()
            } else {
                binding.etCategoria.inputType = android.text.InputType.TYPE_NULL
                binding.etCategoria.keyListener = null
                binding.etCategoria.setText(categoriaSelecionada)
            }
        }
    }

    private fun configurarListeners() {
        binding.btnSelecionarImagem.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.imgEmpresa.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }

        // botão cadastrar agora pega todos os campos separados
        binding.btnCadastrar.setOnClickListener {
            val categoriaFinal = binding.etCategoria.text.toString()
            viewModel.cadastrarEmpresa(
                nome = binding.etNomeEmpresa.text.toString(),
                categoria = categoriaFinal,
                descricao = binding.etDescricao.text.toString(),
                street = binding.etLogradouro.text.toString(),
                number = binding.etNumero.text.toString(),
                city = binding.etCidade.text.toString(),
                state = binding.etEstado.text.toString(),
                country = binding.etPais.text.toString(),
                postalcode = binding.etCep.text.toString(),
                telefone = binding.etTelefone.text.toString(),
                email = binding.etEmailEmpresa.text.toString(),
                imagemUri = imagemSelecionada
            )
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
            if (sucesso) finish()
        }
    }
}