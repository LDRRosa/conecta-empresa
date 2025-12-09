package com.ifgoiano.conectaempresa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.data.model.Avaliacao
import com.ifgoiano.conectaempresa.databinding.ItemAvaliacaoBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AvaliacaoAdapter(
    private val avaliacoes: List<Avaliacao>
) : RecyclerView.Adapter<AvaliacaoAdapter.AvaliacaoViewHolder>() {

    inner class AvaliacaoViewHolder(private val binding: ItemAvaliacaoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(avaliacao: Avaliacao) {
            binding.tvNomeUsuario.text = avaliacao.usuarioNome
            binding.ratingBarAvaliacao.rating = avaliacao.nota
            binding.tvDescricaoAvaliacao.text = avaliacao.descricao.ifEmpty { "Sem comentário" }
            binding.tvData.text = formatarData(avaliacao.timestamp)

            if (avaliacao.usuarioFoto.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(avaliacao.usuarioFoto)
                    .placeholder(R.drawable.icon_perfil)
                    .circleCrop()
                    .into(binding.imgUsuario)
            } else {
                binding.imgUsuario.setImageResource(R.drawable.icon_perfil)
            }
        }

        private fun formatarData(timestamp: Long): String {
            val diff = System.currentTimeMillis() - timestamp
            val dias = TimeUnit.MILLISECONDS.toDays(diff)

            return when {
                dias == 0L -> "Hoje"
                dias == 1L -> "Ontem"
                dias < 7 -> "Há $dias dias"
                dias < 30 -> "Há ${dias / 7} semanas"
                else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaliacaoViewHolder {
        val binding = ItemAvaliacaoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AvaliacaoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvaliacaoViewHolder, position: Int) {
        holder.bind(avaliacoes[position])
    }

    override fun getItemCount() = avaliacoes.size
}