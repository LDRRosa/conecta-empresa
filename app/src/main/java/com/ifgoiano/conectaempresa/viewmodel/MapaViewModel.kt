package com.ifgoiano.conectaempresa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.data.repository.HomeRepository
import kotlinx.coroutines.launch

class MapaViewModel(
    private val repository: HomeRepository = HomeRepository()
) : ViewModel() {

    private val _empresas = MutableLiveData<List<Empresa>>(emptyList())
    val empresas: LiveData<List<Empresa>> = _empresas

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    init {
        carregarEmpresas()
    }

    fun carregarEmpresas() {
        viewModelScope.launch {
            try {
                val lista = repository.carregarEmpresas()
                _empresas.value = lista
            } catch (e: Exception) {
                _status.value = "Erro ao carregar empresas: ${e.message}"
            }
        }
    }
}