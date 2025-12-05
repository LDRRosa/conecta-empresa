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


        holder.binding.tvDescricao.text = empresa.categoria

        holder.binding.ratingBar.rating = empresa.avaliacao.toFloat()

        // Carrega a imagem usando Glide
        Glide.with(holder.itemView.context)
            .load(empresa.imageUrl)
            .circleCrop()
            .into(holder.binding.imgEmpresa)
    }

    override fun getItemCount(): Int = lista.size
}