package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.adapter.AvaliacaoAdapter
import com.ifgoiano.conectaempresa.data.model.Avaliacao
import com.ifgoiano.conectaempresa.databinding.ActivityDetalhesEmpresaBinding
import com.ifgoiano.conectaempresa.databinding.DialogAvaliacaoBinding
import com.ifgoiano.conectaempresa.viewmodel.AvaliacaoViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.protobuf.LazyStringArrayList.emptyList
import java.util.Collections.emptyList
import kotlin.text.get

class DetalhesEmpresaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesEmpresaBinding
    private val viewModel: AvaliacaoViewModel by viewModels()
    private var latitude: Double = Double.NaN
    private var longitude: Double = Double.NaN
    private var nomeEmpresa: String = ""
    private var telefoneEmpresa: String = ""

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
        configurarAvaliacoes()
    }

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun carregarDadosEmpresa() {
        nomeEmpresa = intent.getStringExtra("empresa_nome") ?: "Empresa"
        val categoria = intent.getStringExtra("empresa_categoria") ?: ""
        val descricao = intent.getStringExtra("empresa_descricao") ?: ""
        telefoneEmpresa = intent.getStringExtra("empresa_telefone") ?: ""
        val email = intent.getStringExtra("empresa_email") ?: ""
        val endereco = intent.getStringExtra("empresa_endereco") ?: ""
        val imagem = intent.getStringExtra("empresa_imagem") ?: ""
        val avaliacao = intent.getFloatExtra("empresa_avaliacao", 0f)

        val latExtra = intent.getDoubleExtra("empresa_latitude", Double.NaN)
        val lonExtra = intent.getDoubleExtra("empresa_longitude", Double.NaN)

        binding.apply {
            tvNomeEmpresa.text = nomeEmpresa
            tvCategoriaEmpresa.text = categoria
            tvDescricaoEmpresa.text = descricao
            tvTelefoneEmpresa.text = telefoneEmpresa
            tvEmailEmpresa.text = email
            tvEnderecoEmpresa.text = endereco

            Glide.with(this@DetalhesEmpresaActivity)
                .load(imagem)
                .placeholder(R.drawable.icon_perfil)
                .into(imgEmpresaDetalhes)
        }

        if (!latExtra.isNaN() && !lonExtra.isNaN() && latExtra != 0.0 && lonExtra != 0.0) {
            latitude = latExtra
            longitude = lonExtra
            configurarMapa()
        } else {
            Toast.makeText(
                this,
                "Coordenadas não disponíveis. Mapa não será exibido.",
                Toast.LENGTH_LONG
            ).show()
            binding.cardViewMapa.visibility = View.GONE
            binding.btnAbrirMapa.visibility = View.GONE
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
        val endereco = binding.tvEnderecoEmpresa.text.toString()

        if (endereco.isNotEmpty()) {
            // Usar o endereço completo para busca mais precisa
            val enderecoCompleto = "$endereco, $nomeEmpresa"
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(enderecoCompleto)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // Fallback para navegador
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(enderecoCompleto)}")
                )
                startActivity(browserIntent)
            }
        } else if (!latitude.isNaN() && !longitude.isNaN() && latitude != 0.0 && longitude != 0.0) {
            // Fallback para coordenadas se não houver endereço
            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(nomeEmpresa)})")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                )
                startActivity(browserIntent)
            }
        } else {
            Toast.makeText(this, "Endereço não disponível", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarAvaliacoes() {
        viewModel.avaliacoes.observe(this) { lista ->
            configurarListaAvaliacoes(lista)
            atualizarEstatisticas(lista)
        }

        viewModel.status.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.limparStatus()
            }
        }

        viewModel.sucessoAvaliacao.observe(this) { sucesso ->
            if (sucesso == true) {
                viewModel.carregarAvaliacoes(nomeEmpresa, telefoneEmpresa)
                Toast.makeText(this, "Avaliação enviada com sucesso!", Toast.LENGTH_SHORT).show()
                viewModel.limparSucessoAvaliacao()
            }
        }

        verificarSeDonoEmpresa()
        viewModel.carregarAvaliacoes(nomeEmpresa, telefoneEmpresa)
    }

    private fun verificarSeDonoEmpresa() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("empresas")
            .whereEqualTo("nome", nomeEmpresa)
            .whereEqualTo("telefone", telefoneEmpresa)
            .get()
            .addOnSuccessListener { empresasSnapshot ->
                if (!empresasSnapshot.isEmpty) {
                    val empresaRef = empresasSnapshot.documents.first().reference

                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { usuarioDoc ->
                            val empresasDoUsuario =
                                usuarioDoc.get("empresas") as? List<*> ?: emptyList()

                            if (empresasDoUsuario.contains(empresaRef)) {
                                // É dono da empresa - ocultar botão
                                binding.btnAvaliar.visibility = View.GONE
                            } else {
                                // Não é dono - mostrar botão e configurar clique
                                binding.btnAvaliar.visibility = View.VISIBLE
                                binding.btnAvaliar.setOnClickListener {
                                    mostrarDialogAvaliacao()
                                }
                            }
                        }
                } else {
                    // Empresa não encontrada - mostrar botão por padrão
                    binding.btnAvaliar.visibility = View.VISIBLE
                    binding.btnAvaliar.setOnClickListener {
                        mostrarDialogAvaliacao()
                    }
                }
            }
            .addOnFailureListener {
                // Em caso de erro - mostrar botão por padrão
                binding.btnAvaliar.visibility = View.VISIBLE
                binding.btnAvaliar.setOnClickListener {
                    mostrarDialogAvaliacao()
                }
            }
    }

    private fun configurarListaAvaliacoes(lista: List<Avaliacao>) {
        if (lista.isEmpty()) {
            binding.tvSemAvaliacoes.visibility = View.VISIBLE
            binding.rvAvaliacoes.visibility = View.GONE
        } else {
            binding.tvSemAvaliacoes.visibility = View.GONE
            binding.rvAvaliacoes.visibility = View.VISIBLE
            binding.rvAvaliacoes.apply {
                layoutManager = LinearLayoutManager(this@DetalhesEmpresaActivity)
                adapter = AvaliacaoAdapter(lista)
            }
        }
    }

    private fun atualizarEstatisticas(lista: List<Avaliacao>) {
        binding.tvTotalAvaliacoes.text = "${lista.size} avaliações"

        if (lista.isNotEmpty()) {
            val media = lista.map { it.nota }.average()
            binding.ratingBarEmpresa.rating = media.toFloat()
            binding.tvMediaAvaliacoes.text = String.format("%.1f", media)
        }
    }

    private fun mostrarDialogAvaliacao() {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = DialogAvaliacaoBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnEnviarAvaliacao.setOnClickListener {
            val nota = dialogBinding.ratingBarDialog.rating
            val descricao = dialogBinding.etDescricaoAvaliacao.text.toString().trim()

            viewModel.adicionarAvaliacao(nomeEmpresa, telefoneEmpresa, nota, descricao)
            dialog.dismiss()
        }

        dialogBinding.btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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