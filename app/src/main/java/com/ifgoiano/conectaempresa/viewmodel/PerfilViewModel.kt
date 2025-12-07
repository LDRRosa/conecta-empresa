package com.ifgoiano.conectaempresa.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifgoiano.conectaempresa.data.model.User
import com.ifgoiano.conectaempresa.data.repository.AuthRepository
import com.ifgoiano.conectaempresa.data.repository.PerfilRepository
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: PerfilRepository = PerfilRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _sucessoAtualizacao = MutableLiveData<Boolean>()
    val sucessoAtualizacao: LiveData<Boolean> get() = _sucessoAtualizacao

    private val _logout = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean> = _logout

    init {
        carregarPerfil()
    }

    private fun carregarPerfil() {
        _loading.value = true
        viewModelScope.launch {
            repository.carregarPerfil().onSuccess { user ->
                _user.value = user
            }.onFailure { e ->
                _status.value = "Erro ao carregar perfil: ${e.message}"
            }
            _loading.value = false
        }
    }

    fun atualizarPerfil(
        nome: String,
        telefone: String?,
        endereco: String?,
        categoria: String?,
        novaFotoUri: Uri?
    ) {
        if (nome.isBlank()) {
            _status.value = "O nome não pode ser vazio."
            return
        }

        _loading.value = true
        viewModelScope.launch {
            repository.atualizarPerfilCompleto(nome, novaFotoUri)
                .onSuccess {
                    _status.value = "Perfil atualizado com sucesso!"
                    carregarPerfil()
                    _sucessoAtualizacao.value = true
                }.onFailure { e ->
                    _status.value = "Erro ao atualizar perfil: ${e.message}"
                    _sucessoAtualizacao.value = false
                }
            _loading.value = false
        }
    }

    fun fazerLogout() {
        authRepository.logout()
        _logout.value = true
    }

    fun limparStatus() {
        _status.value = ""
    }
}