package com.ifgoiano.conectaempresa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.ItemEmpresaBinding

class EmpresaAdapter(
    private val lista: List<Empresa>,
    private val onItemClick: (Empresa) -> Unit = {}
) : RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder>() {

    inner class EmpresaViewHolder(val binding: ItemEmpresaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // animação de toque antes de disparar ação
                    animateTouch(binding.root) {
                        onItemClick(lista[position])
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val binding = ItemEmpresaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmpresaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmpresaAdapter.EmpresaViewHolder, position: Int) {
        val empresa = lista[position]

        holder.binding.tvNome.text = empresa.nome
        holder.binding.tvDescricao.text = empresa.categoria
        holder.binding.ratingBar.rating = empresa.avaliacao.toFloat()

        Glide.with(holder.itemView.context)
            .load(empresa.imageUrl)
            .circleCrop()
            .into(holder.binding.imgEmpresa)
    }

    override fun getItemCount(): Int = lista.size

    // animação simples de escala para feedback tátil
    private fun animateTouch(view: View, onComplete: () -> Unit) {
        view.animate()
            .scaleX(0.96f)
            .scaleY(0.96f)
            .setDuration(80)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(80)
                    .withEndAction {
                        onComplete()
                    }
                    .start()
            }
            .start()
    }
}