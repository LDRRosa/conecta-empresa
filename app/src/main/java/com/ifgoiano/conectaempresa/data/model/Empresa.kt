package com.ifgoiano.conectaempresa.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Empresa(
    val active: Boolean = false,
    val avaliacao: Double = 0.0,
    val categoria: String = "",
    val createdAt: Timestamp? = null,
    val descricao: String = "",
    val endereco: String = "",
    val street: String = "",
    val number: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val postalcode: String = "",
    val imageUrl: String = "",
    val nome: String = "",
    val numAvaliacoes: Int = 0,
    val distancia: String = "",
    val notaMedia: Double = 0.0,
    val email: String = "",
    val telefone: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,

    @get:Exclude // não será salvo como campo no documento Firestore
    val id: String = ""
)