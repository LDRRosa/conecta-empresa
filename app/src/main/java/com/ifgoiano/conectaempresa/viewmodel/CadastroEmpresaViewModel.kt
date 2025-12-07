package com.ifgoiano.conectaempresa.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifgoiano.conectaempresa.data.repository.EmpresaRepository
import kotlinx.coroutines.launch

class CadastroEmpresaViewModel(
    private val repository: EmpresaRepository = EmpresaRepository()
) : ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _sucessoCadastro = MutableLiveData<Boolean>()
    val sucessoCadastro: LiveData<Boolean> = _sucessoCadastro

    fun cadastrarEmpresa(
        nome: String,
        categoria: String,
        descricao: String,
        endereco: String,
        telefone: String,
        imagemUri: Uri?
    ) {
        if (nome.isBlank() || categoria.isBlank() || descricao.isBlank() ||
            endereco.isBlank() || telefone.isBlank()
        ) {
            _status.value = "Preencha todos os campos"
            return
        }

        _loading.value = true
        viewModelScope.launch {
            repository.cadastrarEmpresa(nome, categoria, descricao, endereco, telefone, imagemUri)
                .onSuccess {
                    _status.value = "Empresa cadastrada com sucesso!"
                    _sucessoCadastro.value = true
                }
                .onFailure { e ->
                    _status.value = "Erro ao cadastrar: ${e.message}"
                    _sucessoCadastro.value = false
                }
            _loading.value = false
        }
    }
}