package com.ifgoiano.conectaempresa.view

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.view.fragment.HomeFragment
import com.ifgoiano.conectaempresa.view.fragment.PerfilFragment

abstract class BaseActivity : AppCompatActivity() {

    protected fun configurarBottomNavigation(
        bottomNav: BottomNavigationView,
        itemSelecionadoId: Int
    ) {
        bottomNav.selectedItemId = itemSelecionadoId

        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == itemSelecionadoId) {
                return@setOnItemSelectedListener true
            }

            when (item.itemId) {
                R.id.nav_home -> carregarFragmento(HomeFragment())
                R.id.nav_perfil -> carregarFragmento(PerfilFragment())
            }
            true
        }
    }

    private fun carregarFragmento(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}