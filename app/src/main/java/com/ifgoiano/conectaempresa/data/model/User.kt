package com.ifgoiano.conectaempresa.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude

data class User(
    val name: String = "",
    val email: String = "",
    val createdAt: Timestamp? = null,
    val imageurl: String = "",

    @get:Exclude // Faz com que o Firebase não tente deserializar o campo automaticamente
    val empresas: List<Empresa> = emptyList() )