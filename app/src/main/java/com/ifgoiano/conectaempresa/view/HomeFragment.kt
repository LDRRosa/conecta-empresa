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
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.adapter.EmpresaAdapter
import com.ifgoiano.conectaempresa.databinding.FragmentHomeBinding
import com.ifgoiano.conectaempresa.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    // Guarda referência do card e textview atualmente selecionados para feedback visual
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
                    putExtra("empresa_email", "") // Adicione se tiver email no modelo
                    putExtra("empresa_latitude", -16.7290) // Substitua por dados reais
                    putExtra("empresa_longitude", -49.2643) // Substitua por dados reais
                }
                startActivity(intent)
            }

            binding.tvQuantidadeEmpresas.text = getString(R.string.empresas_disponiveis, lista.size)
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

    private fun selecionarCard(card: CardView, tvTextViewId: Int) {
        // Reset anterior
        cardSelecionado?.let { antigo ->
            antigo.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_bg))
            // tenta resetar o texto se existir
            try {
                val antigoTv = antigo.findViewById<View>(tvTextViewId)
                // não faz nada — simple reset handled by card color
            } catch (_: Exception) {}
        }

        // Aplica selecao no novo
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_yellow))
        cardSelecionado = card
    }

    private fun configurarCategorias() {
        binding.cardRestaurantes.setOnClickListener {
            viewModel.filtrarPorCategoria("Restaurantes")
            selecionarCard(binding.cardRestaurantes, R.id.tvCatRestaurantes)
        }
        binding.cardMercados.setOnClickListener {
            viewModel.filtrarPorCategoria("Mercados")
            selecionarCard(binding.cardMercados, R.id.tvCatMercados)
        }
        binding.cardFarmacias.setOnClickListener {
            viewModel.filtrarPorCategoria("Farmácias")
            selecionarCard(binding.cardFarmacias, R.id.tvCatFarmacias)
        }
        binding.cardModa.setOnClickListener {
            viewModel.filtrarPorCategoria("Moda")
            selecionarCard(binding.cardModa, R.id.tvCatModa)
        }
        binding.cardServicos.setOnClickListener {
            viewModel.filtrarPorCategoria("Serviços")
            selecionarCard(binding.cardServicos, R.id.tvCatServicos)
        }
        binding.cardOutros.setOnClickListener {
            // 'Outros' mostrará todas as empresas que NÃO sejam das categorias principais
            viewModel.filtrarPorCategoria("Outros")
            selecionarCard(binding.cardOutros, R.id.tvCatOutros)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}