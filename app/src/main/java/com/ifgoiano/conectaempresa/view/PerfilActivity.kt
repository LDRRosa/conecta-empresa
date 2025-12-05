package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.adapter.EmpresaAdapter
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.ActivityPerfilBinding
import com.ifgoiano.conectaempresa.viewmodel.PerfilViewModel

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private val viewModel: PerfilViewModel by viewModels()
    private var novaFotoUri: Uri? = null
    private var isEditMode = false

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            novaFotoUri = it
            binding.imgPerfil.setImageURI(it) // Mostra a imagem selecionada imediatamente
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // edição do perfil, inicialmente desabilitada
        setEditingEnabled(false)

        observarViewModel()
        configurarListeners()
        configurarBottomNav()
    }

    private fun configurarListeners() {
        //entra em modo edição
        binding.btnEditarPerfil.setOnClickListener {
            isEditMode = true
            setEditingEnabled(true)
        }

        // aplica alterações
        binding.btnSalvar.setOnClickListener {
            if (!isEditMode) {
                Toast.makeText(this, "Entre em modo de edição primeiro.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nome = binding.etNome.text.toString().trim()
            // Campos removidos: telefone, endereco, categoria

            // Passa null para os campos removidos
            viewModel.atualizarPerfil(nome, null, null, null, novaFotoUri)
        }

        // Selecionar foto (apenas quando em edição)
        binding.btnSelecionarFoto.setOnClickListener {
            if (isEditMode) {
                pickImage.launch("image/*")
            }
        }

        // Tornar a imagem clicável para seleção em modo de edição
        binding.imgPerfil.setOnClickListener {
            if (isEditMode) pickImage.launch("image/*")
        }
    }

    private fun setEditingEnabled(enabled: Boolean) {
        // Mostra/oculta campos de edição
        binding.etNome.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.tvNome.visibility = if (enabled) View.GONE else View.VISIBLE
        binding.btnSalvar.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.btnSelecionarFoto.visibility = if (enabled) View.VISIBLE else View.GONE

        // Se desativando edição, limpa a seleção temporária
        if (!enabled) {
            novaFotoUri = null
        }
    }

    private fun observarViewModel() {
        viewModel.user.observe(this) { user ->
            // Preenche campos de visualização
            binding.tvNome.text = user.name
            binding.tvEmail.text = user.email

            // Carrega imagem
            carregarImagemPerfil(user.imageurl)

            // Empresas
            configurarListaEmpresas(user.empresas)
        }

        viewModel.status.observe(this) { mensagem ->
            if (!mensagem.isNullOrEmpty()) {
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                viewModel.limparStatus()
            }
        }

        viewModel.loading.observe(this) { loading ->
            binding.btnSalvar.isEnabled = !loading
            binding.btnEditarPerfil.isEnabled = !loading
            binding.btnSelecionarFoto.isEnabled = !loading
        }

        viewModel.sucessoAtualizacao.observe(this) { sucesso ->
            if (sucesso == true) {
                Toast.makeText(this, "Perfil atualizado.", Toast.LENGTH_SHORT).show()
                // Sai do modo edição
                isEditMode = false
                setEditingEnabled(false)
            }
        }
    }

    private fun configurarListaEmpresas(empresas: List<Empresa>) {
        if (empresas.isEmpty()) {
            binding.tvNenhumaEmpresa.visibility = View.VISIBLE
            binding.rvEmpresas.visibility = View.GONE
        } else {
            binding.tvNenhumaEmpresa.visibility = View.GONE
            binding.rvEmpresas.visibility = View.VISIBLE
            binding.rvEmpresas.apply {
                if (adapter == null) {
                    layoutManager = LinearLayoutManager(this@PerfilActivity)
                    isNestedScrollingEnabled = false
                }
                adapter = EmpresaAdapter(empresas)
            }
        }
    }

    private fun carregarImagemPerfil(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.icon_perfil)
                .circleCrop()
                .into(binding.imgPerfil)
        } else {
            binding.imgPerfil.setImageResource(R.drawable.icon_perfil)
        }
    }

    private fun configurarBottomNav() {
        binding.bottomNavigation.selectedItemId = R.id.nav_perfil
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> true
                else -> false
            }
        }
    }
}