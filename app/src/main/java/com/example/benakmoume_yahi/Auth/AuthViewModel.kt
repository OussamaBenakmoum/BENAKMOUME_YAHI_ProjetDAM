package com.example.benakmoume_yahi.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState(user = repo.currentUser))
    val state: StateFlow<AuthUiState> = _state

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _state.update { s -> s.copy(loading = true, error = null) }
            val res = repo.signUp(email, password)
            _state.update { s ->
                res.fold(
                    onSuccess = { user -> s.copy(loading = false, user = user) },
                    onFailure = { e -> s.copy(loading = false, error = e.message ?: "Erreur") }
                )
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.update { s -> s.copy(loading = true, error = null) }
            val res = repo.signIn(email, password)
            _state.update { s ->
                res.fold(
                    onSuccess = { user -> s.copy(loading = false, user = user) },
                    onFailure = { e -> s.copy(loading = false, error = e.message ?: "Erreur") }
                )
            }
        }
    }

    fun signOut() {
        repo.signOut()
        _state.update { AuthUiState() }
    }
}
