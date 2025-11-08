package com.example.benakmoume_yahi.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val loading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState(user = repo.currentUser))
    val state: StateFlow<AuthUiState> = _state

    init {
        // Réagit aux changements FirebaseAuth via le flow du repo
        viewModelScope.launch {
            repo.currentUserFlow.collectLatest { user: FirebaseUser? ->
                _state.update { it.copy(user = user) }
            }
        }
    }

    // Email sign-up avec création du profil Firestore
    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val res = repo.signUp(email, password)
            res.fold(
                onSuccess = { user ->
                    _state.update { it.copy(loading = false, user = user) }
                    try {
                        val profile = userProfileChangeRequest { displayName = "$firstName $lastName" }
                        user.updateProfile(profile).await()
                        val doc = mapOf(
                            "uid" to user.uid,
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "photoUrl" to null,
                            "profileCompleted" to true,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                        db.collection("users").document(user.uid)
                            .set(doc, SetOptions.merge()).await()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(loading = false, error = e.message ?: "Erreur") }
                }
            )
        }
    }

    // Email sign-in
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val res = repo.signIn(email, password)
            _state.update { s ->
                res.fold(
                    onSuccess = { user -> s.copy(loading = false, user = user) },
                    onFailure = { e -> s.copy(loading = false, error = e.message ?: "Erreur") }
                )
            }
        }
    }

    // Après Google sign-in: créer doc minimal si absent, puis router selon profileCompleted
    fun onGoogleAuthSuccess(
        navToComplete: () -> Unit,
        navNext: () -> Unit
    ) = viewModelScope.launch {
        val user = repo.currentUser ?: return@launch
        _state.update { it.copy(user = user) }

        val ref = db.collection("users").document(user.uid)
        var snap = ref.get().await()
        if (!snap.exists()) {
            val base = mapOf(
                "uid" to user.uid,
                "email" to (user.email ?: ""),
                "profileCompleted" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
            ref.set(base, SetOptions.merge()).await()
            snap = ref.get().await() // éviter une lecture obsolète
        }
        val completed = snap.getBoolean("profileCompleted") ?: false
        if (completed) navNext() else navToComplete()
    }

    // Compléter profil Google
    fun completeGoogleProfile(
        firstName: String,
        lastName: String,
        onDone: () -> Unit,
        onError: (String?) -> Unit = {}
    ) = viewModelScope.launch {
        val user = repo.currentUser ?: return@launch onError("Utilisateur non connecté")
        try {
            val profile = userProfileChangeRequest { displayName = "$firstName $lastName" }
            user.updateProfile(profile).await()
            val doc = mapOf(
                "uid" to user.uid,
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to (user.email ?: ""),
                "profileCompleted" to true,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(user.uid)
                .set(doc, SetOptions.merge()).await()
            onDone()
        } catch (e: Exception) {
            onError(e.message)
        }
    }

    fun signOut() {
        repo.signOut()
        _state.update { AuthUiState() }
    }
}
