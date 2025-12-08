package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.databinding.ActivityDetalhesEmpresaBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class DetalhesEmpresaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesEmpresaBinding
    private var latitude: Double = Double.NaN
    private var longitude: Double = Double.NaN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        binding = ActivityDetalhesEmpresaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbar()
        carregarDadosEmpresa()
        configurarBotoes()
    }

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun carregarDadosEmpresa() {
        val nome = intent.getStringExtra("empresa_nome") ?: "Empresa"
        val categoria = intent.getStringExtra("empresa_categoria") ?: ""
        val descricao = intent.getStringExtra("empresa_descricao") ?: ""
        val telefone = intent.getStringExtra("empresa_telefone") ?: ""
        val email = intent.getStringExtra("empresa_email") ?: ""
        val endereco = intent.getStringExtra("empresa_endereco") ?: ""
        val imagem = intent.getStringExtra("empresa_imagem") ?: ""
        val avaliacao = intent.getFloatExtra("empresa_avaliacao", 0f)

        // agora esperamos que o cadastro tenha salvo latitude/longitude no banco e passado na intent
        val latExtra = intent.getDoubleExtra("empresa_latitude", Double.NaN)
        val lonExtra = intent.getDoubleExtra("empresa_longitude", Double.NaN)

        binding.apply {
            tvNomeEmpresa.text = nome
            tvCategoriaEmpresa.text = categoria
            tvDescricaoEmpresa.text = descricao
            tvTelefoneEmpresa.text = telefone
            tvEmailEmpresa.text = email
            tvEnderecoEmpresa.text = endereco
            ratingBarDetalhes.rating = avaliacao
            tvAvaliacaoNumero.text = String.format("%.1f", avaliacao)

            Glide.with(this@DetalhesEmpresaActivity)
                .load(imagem)
                .placeholder(R.drawable.icon_perfil)
                .into(imgEmpresaDetalhes)
        }

        if (!latExtra.isNaN() && !lonExtra.isNaN()) {
            latitude = latExtra
            longitude = lonExtra
            configurarMapa()
        } else {
            // sem lat/lon nos extras: não fazemos chamada a Nominatim aqui (conforme solicitado)
            Toast.makeText(
                this,
                "Coordenadas não disponíveis. Mostrando localização padrão.",
                Toast.LENGTH_LONG
            ).show()
            latitude = -16.7290
            longitude = -49.2643
            configurarMapa()
        }
    }

    private fun configurarMapa() {
        binding.mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)

            controller.setZoom(16.0)
            val startPoint = GeoPoint(latitude, longitude)
            controller.setCenter(startPoint)

            overlays.clear()
            val marker = Marker(this)
            marker.position = startPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = binding.tvNomeEmpresa.text.toString()
            marker.snippet = binding.tvEnderecoEmpresa.text.toString()
            overlays.add(marker)
        }
    }

    private fun configurarBotoes() {
        binding.layoutTelefone.setOnClickListener {
            val telefone = binding.tvTelefoneEmpresa.text.toString()
            if (telefone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$telefone")
                }
                startActivity(intent)
            }
        }

        binding.layoutEmail.setOnClickListener {
            val email = binding.tvEmailEmpresa.text.toString()
            if (email.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                }
                startActivity(intent)
            }
        }

        binding.btnAbrirMapa.setOnClickListener {
            abrirNoGoogleMaps()
        }
    }

    private fun abrirNoGoogleMaps() {
        val lat = latitude
        val lon = longitude
        if (!lat.isNaN() && !lon.isNaN()) {
            val uri =
                Uri.parse("geo:$lat,$lon?q=$lat,$lon(${Uri.encode(binding.tvNomeEmpresa.text.toString())})")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
                )
                startActivity(browserIntent)
            }
        } else {
            Toast.makeText(this, "Coordenadas não encontradas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
}