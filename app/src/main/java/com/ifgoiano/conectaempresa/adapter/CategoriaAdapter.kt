package com.ifgoiano.conectaempresa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ifgoiano.conectaempresa.R

data class CategoriaItem(val icone: String, val nome: String) {
    override fun toString(): String {
        return nome
    }
}

class CategoriaAdapter(
    context: Context,
    private val categorias: List<CategoriaItem>
) : ArrayAdapter<CategoriaItem>(context, 0, categorias) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_categoria_dropdown,
            parent,
            false
        )

        val categoria = categorias[position]
        view.findViewById<TextView>(R.id.tvIcone).text = categoria.icone
        view.findViewById<TextView>(R.id.tvCategoria).text = categoria.nome

        return view
    }
}