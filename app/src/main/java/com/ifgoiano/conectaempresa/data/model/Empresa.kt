package com.ifgoiano.conectaempresa.data.model

import com.google.firebase.Timestamp

data class Empresa(
    val active: Boolean = false,
    val avaliacao: Double = 0.0,
    val categoria: String = "",
    val createdAt: Timestamp? = null,
    val descricao: String = "",
    val endereco: String = "",
    val imageUrl: String = "",
    val nome: String = "",
    val numAvaliacoes: Int = 0,
    val telefone: String = "",
    val distancia: String = ""
)