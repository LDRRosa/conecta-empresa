package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.adapter.EmpresaAdapter
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.FragmentPerfilBinding
import com.ifgoiano.conectaempresa.viewmodel.PerfilViewModel

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PerfilViewModel by viewModels()
    private var novaFotoUri: Uri? = null
    private var isEditMode = false

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            novaFotoUri = it
            binding.imgPerfil.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        binding.btnEditarPerfil.setOnClickListener {
            isEditMode = true
            setEditingEnabled(true)
            binding.etNome.setText(binding.tvNome.text.toString())
        }

        binding.btnSalvar.setOnClickListener {
            if (!isEditMode) {
                Toast.makeText(
                    requireContext(),
                    "Entre em modo de edição primeiro.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val nome = binding.etNome.text.toString().trim()
            viewModel.atualizarPerfil(nome, null, null, null, novaFotoUri)
        }

        binding.btnSelecionarFoto.setOnClickListener {
            if (isEditMode) pickImage.launch("image/*")
        }

        binding.imgPerfil.setOnClickListener {
            if (isEditMode) pickImage.launch("image/*")
        }

        binding.btnLogout.setOnClickListener {
            viewModel.fazerLogout()
        }

        binding.fabAdicionarEmpresa.setOnClickListener {
            startActivity(Intent(requireContext(), CadastroEmpresaActivity::class.java))
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

    private fun configurarListaEmpresas(empresas: List<Empresa>) {
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
                adapter = EmpresaAdapter(empresas)
            }

            // Garantir que o último item não fique coberto pelo FAB:
            // calculamos padding inferior após layout (height do FAB pode ser zero antes)
            binding.rvEmpresas.post {
                val fab = binding.fabAdicionarEmpresa
                val margin =
                    (fab.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.bottomMargin
                        ?: 0
                val extraDp = (16 * resources.displayMetrics.density).toInt() // espaço extra
                val bottomPadding = fab.height + margin + extraDp
                binding.rvEmpresas.setPadding(
                    binding.rvEmpresas.paddingLeft,
                    binding.rvEmpresas.paddingTop,
                    binding.rvEmpresas.paddingRight,
                    bottomPadding
                )
            }

            // Esconder o FAB ao rolar para baixo e mostrar ao rolar para cima
            binding.rvEmpresas.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && binding.fabAdicionarEmpresa.isShown) {
                        binding.fabAdicionarEmpresa.hide()
                    } else if (dy < 0 && !binding.fabAdicionarEmpresa.isShown) {
                        binding.fabAdicionarEmpresa.show()
                    }
                }
            })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}