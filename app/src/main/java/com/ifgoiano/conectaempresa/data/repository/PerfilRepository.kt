package com.ifgoiano.conectaempresa.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.data.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class PerfilRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuário não autenticado.")

    suspend fun carregarPerfil(): Result<User> =
        try {
            val documentSnapshot = db.collection("usuarios").document(userId).get().await()

            val tempUser = documentSnapshot.toObject(User::class.java)
                ?: throw Exception("Usuário não encontrado.")

            @Suppress("UNCHECKED_CAST")
            val empresasRef = documentSnapshot.data?.get("empresas") as? List<DocumentReference> ?: emptyList()

            val empresasCarregadas = carregarEmpresasPorReferencia(empresasRef)

            val user = tempUser.copy(empresas = empresasCarregadas)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }

    private suspend fun carregarEmpresasPorReferencia(referencias: List<DocumentReference>): List<Empresa> =
        coroutineScope {
            val tasks = referencias.map { ref ->
                async {
                    try {
                        val snap = ref.get().await()
                        val emp = snap.toObject(Empresa::class.java)
                        emp?.copy(id = snap.id)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            tasks.awaitAll().filterNotNull()
        }

    // UPLOAD DE FOTO
    private suspend fun uploadFotoPerfil(uri: Uri): String {
        val ref = storage.reference.child("perfil_fotos/$userId/profile.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    // ATUALIZA PERFIL
    suspend fun atualizarPerfilCompleto(
        nome: String,
        novaFotoUri: Uri?
    ): Result<Unit> =
        try {
            // Faz upload da foto se houver
            val imageUrl = novaFotoUri?.let { uploadFotoPerfil(it) }

            // Monta mapa de updates
            val updates = mutableMapOf<String, Any>()
            updates["name"] = nome
            imageUrl?.let { updates["imageurl"] = it }

            db.collection("usuarios").document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }


}
