package com.ifgoiano.conectaempresa.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifgoiano.conectaempresa.data.model.Empresa
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

    // Empresa carregada para edição
    private val _empresa = MutableLiveData<Empresa?>()
    val empresa: LiveData<Empresa?> = _empresa

    fun carregarEmpresa(id: String) {
        _loading.value = true
        viewModelScope.launch {
            repository.carregarEmpresaPorId(id).onSuccess { emp ->
                _empresa.value = emp
            }.onFailure { e ->
                _status.value = "Erro ao carregar empresa: ${e.message}"
                _empresa.value = null
            }
            _loading.value = false
        }
    }

    fun cadastrarEmpresa(
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
    ) {
        if (nome.isBlank() || categoria.isBlank() || descricao.isBlank() ||
            street.isBlank() || city.isBlank() || state.isBlank() || country.isBlank() ||
            postalcode.isBlank() || telefone.isBlank() || email.isBlank()
        ) {
            _status.value = "Preencha todos os campos obrigatórios"
            return
        }

        _loading.value = true
        viewModelScope.launch {
            repository.cadastrarEmpresa(
                nome, categoria, descricao,
                street, number, city, state, country, postalcode,
                telefone, email, imagemUri
            ).onSuccess {
                _status.value = "Empresa cadastrada com sucesso!"
                _sucessoCadastro.value = true
            }.onFailure { e ->
                _status.value = "Erro ao cadastrar: ${e.message}"
                _sucessoCadastro.value = false
            }
            _loading.value = false
        }
    }

    fun atualizarEmpresa(
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
    ) {
        if (nome.isBlank() || categoria.isBlank() || descricao.isBlank() ||
            street.isBlank() || city.isBlank() || state.isBlank() || country.isBlank() ||
            postalcode.isBlank() || telefone.isBlank() || email.isBlank()
        ) {
            _status.value = "Preencha todos os campos obrigatórios"
            return
        }

        _loading.value = true
        viewModelScope.launch {
            repository.atualizarEmpresa(
                id,
                nome, categoria, descricao,
                street, number, city, state, country, postalcode,
                telefone, email, imagemUri
            ).onSuccess {
                _status.value = "Empresa atualizada com sucesso!"
                _sucessoCadastro.value = true
            }.onFailure { e ->
                _status.value = "Erro ao atualizar: ${e.message}"
                _sucessoCadastro.value = false
            }
            _loading.value = false
        }
    }
}