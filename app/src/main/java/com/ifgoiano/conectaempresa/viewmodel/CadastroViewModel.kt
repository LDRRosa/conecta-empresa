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

        private val _loading = MutableLiveData<Boolean>()
        val loading: LiveData<Boolean> = _loading

        private val _mensagemErro = MutableLiveData<String?>()
        val mensagemErro: LiveData<String?> = _mensagemErro

        private val _sucessoCadastro = MutableLiveData<Boolean>()
        val sucessoCadastro: LiveData<Boolean> = _sucessoCadastro

        private val _navegarParaLogin = MutableLiveData<Boolean>()
        val navegarParaLogin: LiveData<Boolean> = _navegarParaLogin

        fun criarConta(nome: String, email: String, senha: String, confirmarSenha: String) {
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                _mensagemErro.value = "Preencha todos os campos"
                return
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _mensagemErro.value = "Email inválido"
                return
            }

            if (senha.length < 6) {
                _mensagemErro.value = "A senha deve ter no mínimo 6 caracteres"
                return
            }

            if (senha != confirmarSenha) {
                _mensagemErro.value = "As senhas não conferem"
                return
            }

            _loading.value = true

            viewModelScope.launch {
                val resultado = repository.cadastrarUsuario(nome, email, senha)
                _loading.value = false

                if (resultado.isSuccess) {
                    _sucessoCadastro.value = true
                } else {
                    _mensagemErro.value = "Erro ao criar conta: ${resultado.exceptionOrNull()?.message}"
                }
            }
        }

        fun limparMensagemErro() {
            _mensagemErro.value = null
        }

        fun navegarParaLoginAposSucesso() {
            _navegarParaLogin.value = true
        }
    }