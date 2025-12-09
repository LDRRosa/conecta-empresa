package com.ifgoiano.conectaempresa.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.databinding.ActivityMainBinding
import com.ifgoiano.conectaempresa.view.fragment.HomeFragment
import com.ifgoiano.conectaempresa.view.fragment.PerfilFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragment())
                .commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HomeFragment())
                        .commit()
                    true
                }

                R.id.nav_perfil -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, PerfilFragment())
                        .commit()
                    true
                }

                R.id.nav_mapa -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MapaFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}