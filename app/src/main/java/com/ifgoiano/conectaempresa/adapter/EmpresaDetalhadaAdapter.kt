package com.ifgoiano.conectaempresa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.ItemEmpresaDetalhadaBinding

class EmpresaDetalhadaAdapter(
    private val lista: List<Empresa>,
    private val onItemClick: (Empresa) -> Unit = {}
) : RecyclerView.Adapter<EmpresaDetalhadaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEmpresaDetalhadaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(lista[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmpresaDetalhadaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val empresa = lista[position]

        holder.binding.apply {
            tvNomeDetalhado.text = empresa.nome
            tvCategoriaDetalhada.text = empresa.categoria
            tvDescricaoDetalhada.text = empresa.descricao
            tvEnderecoDetalhado.text = empresa.endereco
            tvTelefoneDetalhado.text = empresa.telefone
            ratingBarDetalhado.rating = empresa.avaliacao.toFloat()

            Glide.with(holder.itemView.context)
                .load(empresa.imageUrl)
                .circleCrop()
                .into(imgEmpresaDetalhada)
        }
    }

    override fun getItemCount(): Int = lista.size
}