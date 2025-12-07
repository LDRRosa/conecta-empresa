package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        viewModel.carregarDados()
    }

    private fun configurarRecyclerView() {
        binding.rvEmpresasProximas.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observarViewModel() {
        viewModel.empresas.observe(viewLifecycleOwner) { lista ->
            binding.rvEmpresasProximas.adapter = EmpresaAdapter(lista)
            binding.tvQuantidadeEmpresas.text = "${lista.size} empresas disponíveis"
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}