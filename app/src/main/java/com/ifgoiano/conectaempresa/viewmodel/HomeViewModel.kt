package com.ifgoiano.conectaempresa.viewmodel

import androidx.lifecycle.*
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.data.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository = HomeRepository()
) : ViewModel() {

    private val _banners = MutableLiveData<List<String>>()
    val banners: LiveData<List<String>> get() = _banners

    private val _empresas = MutableLiveData<List<Empresa>>()
    val empresas: LiveData<List<Empresa>> get() = _empresas

    private val _empresasFiltradas = MutableLiveData<List<Empresa>>()
    val empresasFiltradas: LiveData<List<Empresa>> get() = _empresasFiltradas

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var listaOriginal: List<Empresa> = emptyList()

    private val categoriasPrincipais = listOf("Restaurantes", "Mercados", "Farmácias", "Moda", "Serviços")

    fun carregarDados() {
        _loading.value = true

        viewModelScope.launch {

            val listaEmpresasRetorno = repository.carregarEmpresas()

            listaOriginal = listaEmpresasRetorno
            _empresas.value = listaEmpresasRetorno
            _empresasFiltradas.value = listaEmpresasRetorno

            _loading.value = false
        }
    }

    fun buscarEmpresas(query: String) {
        if (query.isEmpty()) {
            _empresasFiltradas.value = listaOriginal
            return
        }

        val resultados = listaOriginal.filter { empresa ->
            empresa.nome.contains(query, ignoreCase = true) ||
            empresa.categoria.contains(query, ignoreCase = true) ||
            empresa.descricao.contains(query, ignoreCase = true)
        }

        _empresasFiltradas.value = resultados
    }

    fun filtrarPorCategoria(categoria: String) {
        if (categoria == "Todas") {
            _empresasFiltradas.value = listaOriginal
            return
        }

        val resultados = if (categoria.equals("Outros", ignoreCase = true)) {
            // Retorna todas as empresas cuja categoria NÃO esteja nas categorias principais
            listaOriginal.filter { empresa ->
                categoriasPrincipais.none { principal ->
                    empresa.categoria.equals(principal, ignoreCase = true)
                }
            }
        } else {
            listaOriginal.filter { empresa ->
                empresa.categoria.equals(categoria, ignoreCase = true)
            }
        }

        _empresasFiltradas.value = resultados
    }
}