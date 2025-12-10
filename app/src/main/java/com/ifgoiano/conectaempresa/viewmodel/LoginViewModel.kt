package com.ifgoiano.conectaempresa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _navegarParaHome = MutableLiveData<Boolean>()
    val navegarParaHome: LiveData<Boolean> = _navegarParaHome

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _mensagemErro = MutableLiveData<String?>()
    val mensagemErro: LiveData<String?> = _mensagemErro

    private val _sucessoLogin = MutableLiveData<Boolean>()
    val sucessoLogin: LiveData<Boolean> = _sucessoLogin

    fun verificarSessao() {
        if (auth.currentUser != null) {
            _navegarParaHome.value = true
        }
    }

    fun fazerLogin(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _mensagemErro.value = "Preencha todos os campos"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _mensagemErro.value = "Email inválido"
            return
        }

        _loading.value = true

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    _sucessoLogin.value = true
                } else {
                    val mensagem = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "Usuário não encontrado"
                        is FirebaseAuthInvalidCredentialsException -> "Senha incorreta"
                        else -> "Erro ao fazer login: ${task.exception?.message}"
                    }
                    _mensagemErro.value = mensagem
                }
            }
    }

    fun limparMensagemErro() {
        _mensagemErro.value = null
    }

    fun navegarParaHomeAposSucesso() {
        _navegarParaHome.value = true
    }
}