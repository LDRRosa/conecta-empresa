package com.ifgoiano.conectaempresa.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ifgoiano.conectaempresa.data.model.Empresa
import kotlinx.coroutines.tasks.await

class HomeRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {



    suspend fun carregarEmpresas(): List<Empresa> =
        try {
            db.collection("empresas")
                .get()
                .await()
                .toObjects(Empresa::class.java)
        } catch (e: Exception) {
            emptyList()
        }
}
