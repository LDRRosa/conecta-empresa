package com.ifgoiano.conectaempresa.viewmodel

        import androidx.lifecycle.LiveData
        import androidx.lifecycle.MutableLiveData
        import androidx.lifecycle.ViewModel
        import androidx.lifecycle.viewModelScope
        import com.ifgoiano.conectaempresa.data.repository.AuthRepository
        import kotlinx.coroutines.launch

        class LoginViewModel(
            private val repository: AuthRepository = AuthRepository()
        ) : ViewModel() {

            private val _loginStatus = MutableLiveData<String>()
            val loginStatus: LiveData<String> = _loginStatus

            private val _navegarParaHome = MutableLiveData<Boolean>()
            val navegarParaHome: LiveData<Boolean> = _navegarParaHome

            fun verificarSessao() {
                if (repository.usuarioEstaLogado()) {
                    _navegarParaHome.value = true
                }
            }

            fun fazerLogin(email: String, senha: String) {
                if (email.isEmpty() || senha.isEmpty()) {
                    _loginStatus.value = "Preencha todos os campos"
                    return
                }

                viewModelScope.launch {
                    val resultado = repository.loginUsuario(email, senha)
                    if (resultado.isSuccess) {
                        _navegarParaHome.value = true
                    } else {
                        _loginStatus.value = "Erro: ${resultado.exceptionOrNull()?.message}"
                    }
                }
            }
        }