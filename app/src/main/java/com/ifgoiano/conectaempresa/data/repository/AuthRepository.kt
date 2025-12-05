package com.ifgoiano.conectaempresa.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.ifgoiano.conectaempresa.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun cadastrarUsuario(nome: String, email: String, senha: String): Result<Unit> {
        return try {
            //Cria o usuário no Firebase Authentication
            val userCredential = auth.createUserWithEmailAndPassword(email, senha).await()
            val userId = userCredential.user?.uid ?: throw Exception("UID do usuário nulo após cadastro.")

            // Cria o objeto de perfil (Usando o modelo User existente)
            val userProfile = User(
                name = nome,
                email = email,
                createdAt = Timestamp.now(),
                // imageurl e empresas usam ficam sem valor pois no cadastro o usuário não fornece esses dados
                imageurl = "",
                empresas = emptyList()
            )

            // Salva o perfil na coleção usuarios
            db.collection("usuarios")
                .document(userId)
                .set(userProfile)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUsuario(email: String, senha: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, senha).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}