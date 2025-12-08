package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ifgoiano.conectaempresa.adapter.EmpresaAdapter
import com.ifgoiano.conectaempresa.databinding.FragmentPerfilBinding
import com.ifgoiano.conectaempresa.viewmodel.PerfilViewModel

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PerfilViewModel by viewModels()
    private var novaFotoUri: android.net.Uri? = null
    private var isEditMode = false

    private var selectionMode = false
    private var fabMenuExpanded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEditingEnabled(false)
        observarViewModel()
        configurarListeners()
    }

    private fun configurarListeners() {
        binding.fabMenuEmpresas.setOnClickListener {
            // Se estiver em selectionMode, um clique no menu cancela seleção (feedback claro)
            if (selectionMode) {
                setSelectionMode(false)
                Toast.makeText(requireContext(), "Modo seleção cancelado.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            toggleFabMenu()
        }

        binding.fabActionAdicionar.setOnClickListener {
            collapseFabMenu()
            setSelectionMode(false)
            startActivity(Intent(requireContext(), CadastroEmpresaActivity::class.java))
        }

        binding.fabActionEditar.setOnClickListener {
            // agora este botão alterna o modo seleção diretamente e dá feedback visual claro
            val next = !selectionMode
            collapseFabMenu()
            setSelectionMode(next)
            if (next) {
                Toast.makeText(requireContext(), "Modo seleção ativado. Toque numa empresa para editar.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Modo seleção desativado.", Toast.LENGTH_SHORT).show()
            }
        }

        // listeners de perfil / foto / logout (mantidos)
        binding.btnEditarPerfil.setOnClickListener {
            isEditMode = true
            setEditingEnabled(true)
            binding.etNome.setText(binding.tvNome.text.toString())
        }

        binding.btnSalvar.setOnClickListener {
            if (!isEditMode) {
                Toast.makeText(requireContext(), "Entre em modo de edição primeiro.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val nome = binding.etNome.text.toString().trim()
            viewModel.atualizarPerfil(nome, null, null, null, novaFotoUri)
        }

        binding.btnSelecionarFoto.setOnClickListener {
            if (isEditMode) {
                val pick = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
                    uri?.let {
                        novaFotoUri = it
                        binding.imgPerfil.setImageURI(it)
                    }
                }
                pick.launch("image/*")
            }
        }

        binding.imgPerfil.setOnClickListener {
            if (isEditMode) {
                val pick = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
                    uri?.let {
                        novaFotoUri = it
                        binding.imgPerfil.setImageURI(it)
                    }
                }
                pick.launch("image/*")
            }
        }

        binding.btnLogout.setOnClickListener {
            viewModel.fazerLogout()
        }
    }

    private fun toggleFabMenu() {
        if (fabMenuExpanded) collapseFabMenu() else expandFabMenu()
    }

    private fun expandFabMenu() {
        fabMenuExpanded = true
        binding.fabActionAdicionar.visibility = View.VISIBLE
        binding.fabActionEditar.visibility = View.VISIBLE

        val spacing = (binding.fabMenuEmpresas.height * 1.15f)
        binding.fabActionAdicionar.animate()
            .translationX(-spacing)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(180)
            .start()

        binding.fabActionEditar.animate()
            .translationX(-spacing * 2)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .start()

        binding.fabMenuEmpresas.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        binding.fabMenuEmpresas.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.ifgoiano.conectaempresa.R.color.accent_red))
    }

    private fun collapseFabMenu() {
        fabMenuExpanded = false

        binding.fabActionAdicionar.animate()
            .translationX(0f)
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(160)
            .withEndAction { binding.fabActionAdicionar.visibility = View.GONE }
            .start()

        binding.fabActionEditar.animate()
            .translationX(0f)
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(160)
            .withEndAction { binding.fabActionEditar.visibility = View.GONE }
            .start()

        binding.fabMenuEmpresas.setImageResource(android.R.drawable.ic_menu_sort_by_size)
        binding.fabMenuEmpresas.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.ifgoiano.conectaempresa.R.color.primary_yellow))
    }

    private fun setSelectionMode(enabled: Boolean) {
        selectionMode = enabled
        val ctx = requireContext()
        if (enabled) {
            // menu mostra que estamos em modo seleção: ícone lápis + cor de alerta
            binding.fabMenuEmpresas.setImageResource(android.R.drawable.ic_menu_edit)
            binding.fabMenuEmpresas.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, com.ifgoiano.conectaempresa.R.color.accent_red))
            // destacar botão editar também
            binding.fabActionEditar.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, com.ifgoiano.conectaempresa.R.color.accent_red))
        } else {
            // restaurar aparência padrão
            binding.fabMenuEmpresas.setImageResource(android.R.drawable.ic_menu_sort_by_size)
            binding.fabMenuEmpresas.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, com.ifgoiano.conectaempresa.R.color.primary_yellow))
            binding.fabActionEditar.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, com.ifgoiano.conectaempresa.R.color.primary_yellow))
        }
    }

    private fun configurarListaEmpresas(empresas: List<com.ifgoiano.conectaempresa.data.model.Empresa>) {
        if (empresas.isEmpty()) {
            binding.tvNenhumaEmpresa.visibility = View.VISIBLE
            binding.rvEmpresas.visibility = View.GONE
        } else {
            binding.tvNenhumaEmpresa.visibility = View.GONE
            binding.rvEmpresas.visibility = View.VISIBLE
            binding.rvEmpresas.apply {
                if (adapter == null) {
                    layoutManager = LinearLayoutManager(requireContext())
                    isNestedScrollingEnabled = false
                }
                adapter = EmpresaAdapter(empresas) { empresa ->
                    if (selectionMode) {
                        val intent = Intent(requireContext(), CadastroEmpresaActivity::class.java).apply {
                            putExtra("edit_mode", true)
                            putExtra("empresa_id", empresa.id)
                        }
                        startActivity(intent)
                        setSelectionMode(false)
                    } else {
                        val intent = Intent(requireContext(), DetalhesEmpresaActivity::class.java).apply {
                            putExtra("empresa_nome", empresa.nome)
                            putExtra("empresa_imagem", empresa.imageUrl)
                            putExtra("empresa_descricao", empresa.descricao)
                            putExtra("empresa_categoria", empresa.categoria)
                            putExtra("empresa_telefone", empresa.telefone)
                            putExtra("empresa_endereco", empresa.endereco)
                            putExtra("empresa_avaliacao", empresa.avaliacao.toFloat())
                            putExtra("empresa_email", empresa.email)
                            putExtra("empresa_latitude", empresa.latitude ?: Double.NaN)
                            putExtra("empresa_longitude", empresa.longitude ?: Double.NaN)
                        }
                        startActivity(intent)
                    }
                }
            }
        }

        // ajustar padding inferior para evitar sobreposição dos FABs
        binding.rvEmpresas.post {
            // usa altura real do bottom_card e do fab menu (fallback para dp caso 0)
            val bottomCardH = binding.bottomCard.height.takeIf { it > 0 }
                ?: (56 * resources.displayMetrics.density).toInt()
            val fabH = binding.fabMenuEmpresas.height.takeIf { it > 0 }
                ?: (56 * resources.displayMetrics.density).toInt()
            val extra = (12 * resources.displayMetrics.density).toInt()
            val bottomPadding = bottomCardH + fabH + extra
            binding.rvEmpresas.setPadding(
                binding.rvEmpresas.paddingLeft,
                binding.rvEmpresas.paddingTop,
                binding.rvEmpresas.paddingRight,
                bottomPadding
            )
            binding.rvEmpresas.clipToPadding = false
        }

        // esconder/mostrar o menu ao rolar (mantém comportamento)
        binding.rvEmpresas.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.fabMenuEmpresas.hide()
                    binding.fabActionAdicionar.hide()
                    binding.fabActionEditar.hide()
                } else if (dy < 0) {
                    binding.fabMenuEmpresas.show()
                    // só mostra ações se o menu estiver expandido
                    if (fabMenuExpanded) {
                        binding.fabActionAdicionar.show()
                        binding.fabActionEditar.show()
                    }
                }
            }
        })
    }

    private fun observarViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvNome.text = user.name
            binding.tvEmail.text = user.email
            carregarImagemPerfil(user.imageurl)
            configurarListaEmpresas(user.empresas)
        }

        viewModel.status.observe(viewLifecycleOwner) { mensagem ->
            if (!mensagem.isNullOrEmpty()) {
                Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show()
                viewModel.limparStatus()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.btnSalvar.isEnabled = !loading
            binding.btnEditarPerfil.isEnabled = !loading
            binding.btnSelecionarFoto.isEnabled = !loading
            binding.btnLogout.isEnabled = !loading
        }

        viewModel.sucessoAtualizacao.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso == true) {
                Toast.makeText(requireContext(), "Perfil atualizado.", Toast.LENGTH_SHORT).show()
                isEditMode = false
                setEditingEnabled(false)
            }
        }

        viewModel.logout.observe(viewLifecycleOwner) { deveDeslogar ->
            if (deveDeslogar == true) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun setEditingEnabled(enabled: Boolean) {
        binding.etNome.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.tvNome.visibility = if (enabled) View.GONE else View.VISIBLE
        binding.btnSalvar.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.btnSelecionarFoto.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.btnEditarPerfil.visibility = if (enabled) View.GONE else View.VISIBLE

        if (!enabled) novaFotoUri = null
    }

    private fun carregarImagemPerfil(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            com.bumptech.glide.Glide.with(this)
                .load(imageUrl)
                .placeholder(com.ifgoiano.conectaempresa.R.drawable.icon_perfil)
                .circleCrop()
                .into(binding.imgPerfil)
        } else {
            binding.imgPerfil.setImageResource(com.ifgoiano.conectaempresa.R.drawable.icon_perfil)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}