package com.ifgoiano.conectaempresa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    fun login(email: String, senha: String) {
        // Aqui você pode validar o login (ex: Firebase, API, etc.)
        _loginResult.value = email == "admin@ifgoiano.com" && senha == "123456"
    }
}
