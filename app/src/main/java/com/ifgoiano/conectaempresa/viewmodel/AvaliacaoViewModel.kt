package com.ifgoiano.conectaempresa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifgoiano.conectaempresa.data.model.Avaliacao
import com.ifgoiano.conectaempresa.data.repository.AvaliacaoRepository
import kotlinx.coroutines.launch

class AvaliacaoViewModel(
    private val repository: AvaliacaoRepository = AvaliacaoRepository()
) : ViewModel() {

    private val _avaliacoes = MutableLiveData<List<Avaliacao>>()
    val avaliacoes: LiveData<List<Avaliacao>> = _avaliacoes

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _sucessoAvaliacao = MutableLiveData<Boolean>()
    val sucessoAvaliacao: LiveData<Boolean> = _sucessoAvaliacao

    fun carregarAvaliacoes(nomeEmpresa: String, telefoneEmpresa: String) {
        _loading.value = true
        viewModelScope.launch {
            repository.carregarAvaliacoes(nomeEmpresa, telefoneEmpresa)
                .onSuccess { lista ->
                    _avaliacoes.value = lista
                }
                .onFailure { e ->
                    _status.value = "Erro ao carregar avaliações: ${e.message}"
                }
            _loading.value = false
        }
    }

    fun adicionarAvaliacao(
        nomeEmpresa: String,
        telefoneEmpresa: String,
        nota: Float,
        descricao: String
    ) {
        if (nota <= 0) {
            _status.value = "Selecione uma nota"
            return
        }

        _loading.value = true
        viewModelScope.launch {
            repository.adicionarAvaliacao(nomeEmpresa, telefoneEmpresa, nota, descricao)
                .onSuccess {
                    _status.value = "Avaliação enviada com sucesso!"
                    _sucessoAvaliacao.value = true
                    carregarAvaliacoes(nomeEmpresa, telefoneEmpresa)
                }
                .onFailure { e ->
                    _status.value = "Erro ao enviar avaliação: ${e.message}"
                    _sucessoAvaliacao.value = false
                }
            _loading.value = false
        }
    }

    fun limparStatus() {
        _status.value = ""
    }

    fun limparSucessoAvaliacao() {
        _sucessoAvaliacao.value = null
    }
}