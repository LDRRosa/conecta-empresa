package com.ifgoiano.conectaempresa.view

import android.content.Intent
import com.ifgoiano.conectaempresa.adapter.EmpresaAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.adapter.BannerAdapter
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.ActivityHomeBinding
import com.ifgoiano.conectaempresa.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observarViewModel()
        viewModel.carregarDados()

        configurarBottomNav()
    }

    private fun observarViewModel() {

        viewModel.banners.observe(this) { lista ->
            binding.viewPagerBanner.adapter = BannerAdapter(lista)
        }

        viewModel.empresas.observe(this) { lista ->
            binding.rvEmpresasProximas.layoutManager = LinearLayoutManager(this)
            binding.rvEmpresasProximas.adapter = EmpresaAdapter(lista)
        }


    }

    private fun configurarBottomNav() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Já estamos na Home, nada a fazer ou rolar para o topo
                    true
                }
                R.id.nav_perfil -> {
                    //Navegar para a tela de Perfil
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                // ... outros itens (avaliacoes, minha_empresa)
                else -> false
            }
        }
    }


}
