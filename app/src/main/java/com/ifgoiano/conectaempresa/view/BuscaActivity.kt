package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifgoiano.conectaempresa.adapter.EmpresaDetalhadaAdapter
import com.ifgoiano.conectaempresa.databinding.ActivityBuscaBinding
import com.ifgoiano.conectaempresa.viewmodel.HomeViewModel

class BuscaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuscaBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        configurarBusca()
        configurarBotoes()

        viewModel.carregarDados()

        // Foca no campo de busca
        binding.etBuscaDetalhada.requestFocus()
    }

    private fun configurarRecyclerView() {
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
    }

    private fun configurarBusca() {
        binding.etBuscaDetalhada.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                viewModel.buscarEmpresas(query)

                if (query.isEmpty()) {
                    binding.tvMensagemVazia.visibility = View.VISIBLE
                    binding.rvResultados.visibility = View.GONE
                } else {
                    binding.tvMensagemVazia.visibility = View.GONE
                    binding.rvResultados.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        observarResultados()
    }

    private fun observarResultados() {
        viewModel.empresasFiltradas.observe(this) { lista ->
            binding.rvResultados.adapter = EmpresaDetalhadaAdapter(lista) { empresa ->
                val intent = Intent(this, DetalhesEmpresaActivity::class.java).apply {
                    putExtra("empresa_nome", empresa.nome)
                    putExtra("empresa_imagem", empresa.imageUrl)
                    putExtra("empresa_descricao", empresa.descricao)
                    putExtra("empresa_categoria", empresa.categoria)
                    putExtra("empresa_telefone", empresa.telefone)
                    putExtra(
                        "empresa_endereco",
                        if (empresa.endereco.isNotBlank()) empresa.endereco
                        else "${empresa.street}${if (empresa.number.isNotBlank()) ", ${empresa.number}" else ""}${if (empresa.city.isNotBlank()) ", ${empresa.city}" else ""}"
                    )
                    putExtra("empresa_avaliacao", empresa.avaliacao.toFloat())
                    putExtra("empresa_email", empresa.email)
                    putExtra("empresa_latitude", empresa.latitude ?: Double.NaN)
                    putExtra("empresa_longitude", empresa.longitude ?: Double.NaN)
                }
                startActivity(intent)
            }

            if (lista.isEmpty() && binding.etBuscaDetalhada.text.isNotEmpty()) {
                binding.tvMensagemVazia.text = "Nenhuma empresa encontrada"
                binding.tvMensagemVazia.visibility = View.VISIBLE
            }
        }
    }

    private fun configurarBotoes() {
        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }
}