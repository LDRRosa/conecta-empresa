package com.ifgoiano.conectaempresa.data.model

import com.google.firebase.Timestamp

data class Empresa(
    val active: Boolean = false,
    val avaliacao: Double = 0.0,
    val categoria: String = "",
    val createdAt: Timestamp? = null, // Manter como String ou usar um tipo de Data/Hora (como Instant ou LocalDateTime) dependendo da sua necessidade
    val descricao: String = "",
    val endereco: String = "",
    val imageUrl: String = "", // Corrigido de 'imagemUrl' para 'imageUrl' (camelCase)
    val nome: String = "",
    val numAvaliacoes: Int = 0, // Corrigido para Int, pois representa a quantidade
    val telefone: String = "",
    val distancia: String = "" // Mantenha este campo se for calculado ou adicionado no lado do app
)