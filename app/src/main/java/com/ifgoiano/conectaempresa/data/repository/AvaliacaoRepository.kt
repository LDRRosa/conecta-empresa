package com.ifgoiano.conectaempresa.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.protobuf.LazyStringArrayList.emptyList
import com.ifgoiano.conectaempresa.data.model.Avaliacao
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class AvaliacaoRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun gerarChaveEmpresa(nome: String, telefone: String): String {
        val texto = "${nome.trim().lowercase()}_${telefone.trim().replace(Regex("[^0-9]"), "")}"
        return MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

   suspend fun adicionarAvaliacao(
        nomeEmpresa: String,
        telefoneEmpresa: String,
        nota: Float,
        descricao: String
    ): Result<Unit> {
        return try {
            val usuarioAtual = auth.currentUser
                ?: return Result.failure(Exception("Usuário não autenticado"))

            val chaveEmpresa = gerarChaveEmpresa(nomeEmpresa, telefoneEmpresa)

            // Buscar a empresa para verificar o dono
            val empresasSnapshot = firestore.collection("empresas")
                .whereEqualTo("nome", nomeEmpresa)
                .whereEqualTo("telefone", telefoneEmpresa)
                .get()
                .await()

            if (!empresasSnapshot.isEmpty) {
                val empresaDoc = empresasSnapshot.documents.first()
                val empresaRef = empresaDoc.reference

                // Verificar se o usuário é dono da empresa
                val usuarioDoc = firestore.collection("usuarios")
                    .document(usuarioAtual.uid)
                    .get()
                    .await()

                val empresasDoUsuario = usuarioDoc.get("empresas") as? List<*> ?: emptyList()

                if (empresasDoUsuario.contains(empresaRef)) {
                    return Result.failure(Exception("Você não pode avaliar sua própria empresa"))
                }
            }

            val perfilDoc = firestore.collection("usuarios")
                .document(usuarioAtual.uid)
                .get()
                .await()

            val usuarioNome = perfilDoc.getString("name") ?: "Usuário"
            val usuarioFoto = perfilDoc.getString("imageurl") ?: ""

            val avaliacao = Avaliacao(
                empresaId = chaveEmpresa,
                usuarioId = usuarioAtual.uid,
                usuarioNome = usuarioNome,
                usuarioFoto = usuarioFoto,
                nota = nota,
                descricao = descricao
            )

            val docRef = firestore.collection("avaliacoes")
                .document(chaveEmpresa)
                .collection("lista")
                .document()

            val avaliacaoComId = avaliacao.copy(id = docRef.id)
            docRef.set(avaliacaoComId).await()

            atualizarMediaAvaliacoes(chaveEmpresa)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun carregarAvaliacoes(
        nomeEmpresa: String,
        telefoneEmpresa: String
    ): Result<List<Avaliacao>> {
        return try {
            val chaveEmpresa = gerarChaveEmpresa(nomeEmpresa, telefoneEmpresa)

            val snapshot = firestore.collection("avaliacoes")
                .document(chaveEmpresa)
                .collection("lista")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val avaliacoes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Avaliacao::class.java)
            }

            Result.success(avaliacoes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun atualizarMediaAvaliacoes(chaveEmpresa: String) {
        try {
            val snapshot = firestore.collection("avaliacoes")
                .document(chaveEmpresa)
                .collection("lista")
                .get()
                .await()

            val avaliacoes = snapshot.documents.mapNotNull {
                it.toObject(Avaliacao::class.java)
            }

            val media = if (avaliacoes.isNotEmpty()) {
                avaliacoes.map { it.nota }.average()
            } else {
                0.0
            }

            firestore.collection("avaliacoes")
                .document(chaveEmpresa)
                .set(
                    mapOf(
                        "mediaAvaliacao" to media,
                        "totalAvaliacoes" to avaliacoes.size
                    )
                )
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
