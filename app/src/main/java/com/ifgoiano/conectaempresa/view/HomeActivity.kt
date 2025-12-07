package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.adapter.BannerAdapter
import com.ifgoiano.conectaempresa.adapter.EmpresaAdapter
import com.ifgoiano.conectaempresa.databinding.ActivityHomeBinding
import com.ifgoiano.conectaempresa.viewmodel.HomeViewModel

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        observarViewModel()
        configurarBusca()
        configurarBottomNavigation(binding.bottomNavigation, R.id.nav_home)

        viewModel.carregarDados()
    }

    private fun configurarRecyclerView() {
        binding.rvEmpresasProximas.layoutManager = LinearLayoutManager(this)
    }

    private fun observarViewModel() {
        viewModel.banners.observe(this) { urls ->
            binding.viewPagerBanner.adapter = BannerAdapter(urls)
        }

        viewModel.empresas.observe(this) { lista ->
            binding.rvEmpresasProximas.adapter = EmpresaAdapter(lista)
        }
    }

    private fun configurarBusca() {
        binding.etBusca.setOnClickListener {
            startActivity(Intent(this, BuscaActivity::class.java))
        }
    }
}