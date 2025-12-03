package com.ifgoiano.conectaempresa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.ItemEmpresaBinding

class EmpresaAdapter(private val lista: List<Empresa>) :
    RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder>() {

    inner class EmpresaViewHolder(val binding: ItemEmpresaBinding) :
        RecyclerView.ViewHolder(binding.root)

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

        // ✅ 1. NOVO: Preenche a descrição/categoria (usando 'categoria' de Empresa.kt)
        holder.binding.tvDescricao.text = empresa.categoria

        // ✅ 2. NOVO: Preenche o Rating Bar (convertendo Double para Float)
        holder.binding.ratingBar.rating = empresa.avaliacao.toFloat()

        // Carrega a imagem usando Glide
        Glide.with(holder.itemView.context)
            .load(empresa.imageUrl)
            .circleCrop() // ✅ 3. NOVO: Aplica o corte circular para o logo
            .into(holder.binding.imgEmpresa)
    }

    override fun getItemCount(): Int = lista.size
}