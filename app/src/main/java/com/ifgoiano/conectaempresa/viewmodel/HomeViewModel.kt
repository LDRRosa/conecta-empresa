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

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun carregarDados() {
        _loading.value = true

        viewModelScope.launch {
            val listaBannerObjects = repository.carregarBanners()
            val listaEmpresasRetorno = repository.carregarEmpresas()

            _banners.value = listaBannerObjects.map { it.imageUrl }
            _empresas.value = listaEmpresasRetorno

            _loading.value = false
        }
    }
}
