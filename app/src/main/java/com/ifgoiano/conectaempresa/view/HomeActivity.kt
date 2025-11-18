package com.ifgoiano.conectaempresa.view

import EmpresaAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.adapter.BannerAdapter
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        aplicarWindowInsets()

        configurarBanners()
        configurarEmpresas()
        configurarBottomNav()
    }

    private fun configurarBanners() {
        val banners = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )

        binding.viewPagerBanner.adapter = BannerAdapter(banners)
    }

    private fun configurarEmpresas() {
        val empresas = listOf(
            Empresa("Lava Jato Turbo", "300m", R.drawable.empresa1),
            Empresa("Oficina Premium", "450m", R.drawable.empresa2),
            Empresa("Auto Peças Goiás", "900m", R.drawable.empresa3)
        )

        binding.rvEmpresasProximas.adapter = EmpresaAdapter(empresas)
        binding.rvEmpresasProximas.layoutManager = LinearLayoutManager(this)
    }

    private fun configurarBottomNav() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_avaliacoes -> {
                    Toast.makeText(this, "Minhas Avaliações", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_perfil -> {
                    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun aplicarWindowInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(0, 0, 0, bottom)
            insets
        }

}}
