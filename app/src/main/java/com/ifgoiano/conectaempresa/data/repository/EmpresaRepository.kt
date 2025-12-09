package com.ifgoiano.conectaempresa.data.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

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
        street: String,
        number: String,
        city: String,
        state: String,
        country: String,
        postalcode: String,
        telefone: String,
        email: String,
        imagemUri: Uri?
    ): Result<Unit> = try {
        val imageUrl = imagemUri?.let { uploadImagem(it) } ?: ""

        val latlon = obterLatLonNominatim(street, city, state, country, postalcode)

        val enderecoCompleto = buildString {
            append(street)
            if (number.isNotBlank()) append(", $number")
            if (city.isNotBlank()) append(", $city")
        }

        val empresaData = hashMapOf(
            "nome" to nome,
            "categoria" to categoria,
            "descricao" to descricao,
            "street" to street,
            "number" to number,
            "city" to city,
            "state" to state,
            "country" to country,
            "postalcode" to postalcode,
            "endereco" to enderecoCompleto,
            "telefone" to telefone,
            "email" to email,
            "imageUrl" to imageUrl,
            "active" to true,
            "avaliacao" to 0.0,
            "numAvaliacoes" to 0,
            "distancia" to "",
            "createdAt" to Timestamp.now(),
            "latitude" to latlon?.first,
            "longitude" to latlon?.second
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

    suspend fun carregarEmpresaPorId(id: String): Result<com.ifgoiano.conectaempresa.data.model.Empresa> =
        try {
            val snap = db.collection("empresas").document(id).get().await()
            val empresa = snap.toObject(com.ifgoiano.conectaempresa.data.model.Empresa::class.java)
                ?: throw Exception("Empresa não encontrada")
            Result.success(empresa.copy(id = snap.id))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun atualizarEmpresa(
        id: String,
        nome: String,
        categoria: String,
        descricao: String,
        street: String,
        number: String,
        city: String,
        state: String,
        country: String,
        postalcode: String,
        telefone: String,
        email: String,
        imagemUri: Uri?
    ): Result<Unit> = try {
        // obter imagem atual se não for enviado novo arquivo
        val currentSnap = db.collection("empresas").document(id).get().await()
        val currentImage = currentSnap.getString("imageUrl") ?: ""

        val imageUrl = imagemUri?.let { uploadImagem(it) } ?: currentImage

        val latlon = obterLatLonNominatim(street, city, state, country, postalcode)

        val enderecoCompleto = buildString {
            append(street)
            if (number.isNotBlank()) append(", $number")
            if (city.isNotBlank()) append(", $city")
        }

        val updates = mutableMapOf<String, Any?>()
        updates["nome"] = nome
        updates["categoria"] = categoria
        updates["descricao"] = descricao
        updates["street"] = street
        updates["number"] = number
        updates["city"] = city
        updates["state"] = state
        updates["country"] = country
        updates["postalcode"] = postalcode
        updates["endereco"] = enderecoCompleto
        updates["telefone"] = telefone
        updates["email"] = email
        updates["imageUrl"] = imageUrl
        updates["latitude"] = latlon?.first
        updates["longitude"] = latlon?.second

        db.collection("empresas").document(id).update(updates).await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun uploadImagem(uri: Uri): String = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val ref = storage.reference.child("empresas/$userId/$timestamp.jpg")
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    }

    private suspend fun obterLatLonNominatim(
        street: String,
        city: String,
        state: String,
        country: String,
        postalcode: String
    ): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        try {
            val s = URLEncoder.encode(street, "UTF-8")
            val c = URLEncoder.encode(city, "UTF-8")
            val st = URLEncoder.encode(state, "UTF-8")
            val co = URLEncoder.encode(country, "UTF-8")
            val pc = URLEncoder.encode(postalcode, "UTF-8")
            val urlStr =
                "https://nominatim.openstreetmap.org/search?street=$s&city=$c&state=$st&country=$co&postalcode=$pc&format=json&addressdetails=1&limit=1"
            val url = URL(urlStr)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "ConectaCidade.")
                setRequestProperty("Accept-Language", "pt-BR")
                setRequestProperty("Accept", "*/*")
                connectTimeout = 10_000
                readTimeout = 10_000
            }

            val code = conn.responseCode
            val stream =
                if (code == HttpURLConnection.HTTP_OK) conn.inputStream else conn.errorStream
            val reader = BufferedReader(InputStreamReader(stream))
            val sb = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            conn.disconnect()

            val arr = JSONArray(sb.toString())
            if (arr.length() == 0) return@withContext null
            val first = arr.getJSONObject(0)
            val latStr = first.optString("lat", "")
            val lonStr = first.optString("lon", "")
            if (latStr.isBlank() || lonStr.isBlank()) return@withContext null
            Pair(latStr.toDouble(), lonStr.toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun salvarAvaliacao(
        empresaId: String,
        nota: Float,
        comentario: String
    ): Result<Unit> = try {
        val avaliacaoData = hashMapOf(
            "empresaId" to empresaId,
            "userId" to userId,
            "nota" to nota,
            "comentario" to comentario,
            "createdAt" to Timestamp.now()
        )

        // Salva a avaliação
        db.collection("avaliacoes").add(avaliacaoData).await()

        // Atualiza a média da empresa
        val empresa = db.collection("empresas").document(empresaId).get().await()
        val avaliacaoAtual = empresa.getDouble("avaliacao") ?: 0.0
        val numAvaliacoes = empresa.getLong("numAvaliacoes")?.toInt() ?: 0

        val novaMedia = ((avaliacaoAtual * numAvaliacoes) + nota) / (numAvaliacoes + 1)

        db.collection("empresas").document(empresaId).update(
            mapOf(
                "avaliacao" to novaMedia,
                "numAvaliacoes" to (numAvaliacoes + 1)
            )
        ).await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}