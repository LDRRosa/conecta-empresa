package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifgoiano.conectaempresa.adapter.EmpresaAdapter
import com.ifgoiano.conectaempresa.databinding.FragmentHomeBinding
import com.ifgoiano.conectaempresa.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private var cardSelecionado: CardView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarRecyclerView()
        observarViewModel()
        configurarBusca()
        configurarCategorias()

        viewModel.carregarDados()
    }

    private fun configurarRecyclerView() {
        binding.rvEmpresasProximas.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observarViewModel() {
        viewModel.empresasFiltradas.observe(viewLifecycleOwner) { lista ->
            binding.rvEmpresasProximas.adapter = EmpresaAdapter(lista) { empresa ->
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

            binding.tvQuantidadeEmpresas.text =
                getString(com.ifgoiano.conectaempresa.R.string.empresas_disponiveis, lista.size)
        }
    }

    private fun configurarBusca() {
        binding.searchContainer.setOnClickListener {
            startActivity(Intent(requireContext(), BuscaActivity::class.java))
        }

        binding.etBusca.setOnClickListener {
            startActivity(Intent(requireContext(), BuscaActivity::class.java))
        }
    }

    // agora selecionarCard faz toggle: seleciona ou desseleciona e chama o ViewModel
    private fun selecionarCard(card: CardView, categoria: String) {
        // se clicar no mesmo card selecionado => desseleciona e mostra todas
        if (cardSelecionado == card) {
            card.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    com.ifgoiano.conectaempresa.R.color.card_bg
                )
            )
            cardSelecionado = null
            viewModel.filtrarPorCategoria("Todas")
            return
        }

        // limpa seleção anterior
        cardSelecionado?.let { antigo ->
            antigo.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    com.ifgoiano.conectaempresa.R.color.card_bg
                )
            )
        }

        // seleciona novo
        card.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                com.ifgoiano.conectaempresa.R.color.primary_yellow
            )
        )
        cardSelecionado = card

        // aplica filtro
        viewModel.filtrarPorCategoria(categoria)
    }

    private fun configurarCategorias() {
        binding.cardRestaurantes.setOnClickListener {
            selecionarCard(binding.cardRestaurantes, "Restaurantes")
        }
        binding.cardMercados.setOnClickListener {
            selecionarCard(binding.cardMercados, "Mercados")
        }
        binding.cardFarmacias.setOnClickListener {
            selecionarCard(binding.cardFarmacias, "Farmácias")
        }
        binding.cardModa.setOnClickListener {
            selecionarCard(binding.cardModa, "Moda")
        }
        binding.cardServicos.setOnClickListener {
            selecionarCard(binding.cardServicos, "Serviços")
        }
        binding.cardOutros.setOnClickListener {
            selecionarCard(binding.cardOutros, "Outros")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}