package com.ifgoiano.conectaempresa.data.model

data class Avaliacao(
    val id: String = "",
    val empresaId: String = "",
    val usuarioId: String = "",
    val usuarioNome: String = "",
    val usuarioFoto: String = "",
    val nota: Float = 0f,
    val descricao: String = "",
    val timestamp: Long = System.currentTimeMillis()
)