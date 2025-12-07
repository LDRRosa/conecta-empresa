package com.ifgoiano.conectaempresa.data.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class EmpresaRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuário não autenticado")

    suspend fun cadastrarEmpresa(
        nome: String,
        categoria: String,
        descricao: String,
        endereco: String,
        telefone: String,
        imagemUri: Uri?
    ): Result<Unit> = try {
        val imageUrl = imagemUri?.let { uploadImagem(it) } ?: ""

        val empresaData = hashMapOf(
            "nome" to nome,
            "categoria" to categoria,
            "descricao" to descricao,
            "endereco" to endereco,
            "telefone" to telefone,
            "imageUrl" to imageUrl,
            "active" to true,
            "avaliacao" to 0.0,
            "numAvaliacoes" to 0,
            "distancia" to "",
            "createdAt" to Timestamp.now()
        )

        val empresaRef = db.collection("empresas").add(empresaData).await()

        db.collection("usuarios")
            .document(userId)
            .update("empresas", FieldValue.arrayUnion(empresaRef))
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun uploadImagem(uri: Uri): String {
        val timestamp = System.currentTimeMillis()
        val ref = storage.reference.child("empresas/$userId/$timestamp.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}