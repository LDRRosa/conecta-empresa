package com.ifgoiano.conectaempresa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifgoiano.conectaempresa.data.repository.AuthRepository
import kotlinx.coroutines.launch

class CadastroViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _navegarParaLogin = MutableLiveData<Boolean>()
    val navegarParaLogin: LiveData<Boolean> = _navegarParaLogin

    fun criarConta(nome: String, email: String, senha: String, confirmarSenha: String) {
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            _status.value = "Preencha todos os campos."
            return
        }

        if (senha != confirmarSenha) {
            _status.value = "As senhas não conferem."
            return
        }

        viewModelScope.launch {
            val resultado = repository.cadastrarUsuario(nome, email, senha)
            if (resultado.isSuccess) {
                _status.value = "Conta criada com sucesso!"
                _navegarParaLogin.value = true
            } else {
                _status.value = "Erro: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
}