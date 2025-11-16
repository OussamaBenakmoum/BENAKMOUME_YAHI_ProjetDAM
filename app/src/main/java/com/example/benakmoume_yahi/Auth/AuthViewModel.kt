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

// ===== Helper backend (Postgres) =====
private suspend fun ensureBackendUser(
    uid: String,
    email: String,
    displayName: String?
): Result<Unit> {
    val api = com.example.benakmoume_yahi.remote.RetrofitInstance.api

    // 0) GET /users/{uid}
    val get = api.getUser(uid)
    if (get.isSuccessful && get.body() != null) return Result.success(Unit)
    if (get.code() != 404) {
        val err = get.errorBody()?.string()
        return Result.failure(Exception("GET /users/$uid -> ${get.code()} ${err ?: ""}".trim()))
    }

    // 1) POST /users (création)
    val first = displayName?.substringBefore(" ").takeUnless { it.isNullOrBlank() } ?: "User"
    val last  = displayName?.substringAfter(" ", missingDelimiterValue = "").takeUnless { it.isNullOrBlank() } ?: " "
    val mail  = email.trim().lowercase()

    val post = api.createUser(
        com.example.benakmoume_yahi.models.UserCreate(
            firstname = first,
            lastname  = last,
            email     = mail,
            areas_preferred      = null,  // préférences locales → DataStore
            preferred_categories = null,
            photo_profile        = null,
            firebase_uid         = uid
        )
    )
    if (post.isSuccessful) return Result.success(Unit)

    val msg = post.errorBody()?.string().orEmpty()
    // Idempotence et messages clairs
    return if (post.code() == 400) {
        val reCheck = api.getUser(uid)
        if (reCheck.isSuccessful && reCheck.body() != null) {
            Result.success(Unit)
        } else if (msg.contains("email", ignoreCase = true) ||
            msg.contains("existant", ignoreCase = true) ||
            msg.contains("existe", ignoreCase = true)) {
            Result.failure(Exception("Cet e‑mail est déjà utilisé par un autre compte. Connectez‑vous avec cet e‑mail."))
        } else {
            Result.failure(Exception("POST /users -> 400 $msg"))
        }
    } else {
        Result.failure(Exception("POST /users -> ${post.code()} $msg"))
    }
}

data class AuthUiState(
    val loading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val backendReady: Boolean = false
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState(user = repo.currentUser))
    val state: StateFlow<AuthUiState> = _state

    init {
        viewModelScope.launch {
            repo.currentUserFlow.collectLatest { user: FirebaseUser? ->
                _state.update { it.copy(user = user) }
            }
        }
    }

    // Email sign-up → Firebase → Backend Postgres → Profil Firebase → Firestore
    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, backendReady = false) }
            val res = repo.signUp(email, password)
            res.fold(
                onSuccess = { user ->
                    // 1) Créer l'utilisateur en base Postgres
                    val backend = ensureBackendUser(
                        uid = user.uid,
                        email = email,
                        displayName = "$firstName $lastName"
                    )
                    if (backend.isFailure) {
                        _state.update { it.copy(loading = false, user = user, backendReady = false, error = backend.exceptionOrNull()?.message) }
                        return@fold
                    }

                    // 2) Mettre à jour le profil Firebase + Firestore
                    try {
                        val profile = userProfileChangeRequest { displayName = "$firstName $lastName" }
                        user.updateProfile(profile).await()
                        val doc = mapOf(
                            "uid" to user.uid,
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email.trim().lowercase(),
                            "photoUrl" to null,
                            "profileCompleted" to true,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                        db.collection("users").document(user.uid).set(doc, SetOptions.merge()).await()
                        _state.update { it.copy(loading = false, user = user, backendReady = true, error = null) }
                    } catch (e: Exception) {
                        _state.update { it.copy(loading = false, user = user, backendReady = true, error = e.message) }
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(loading = false, backendReady = false, error = e.message ?: "Erreur") }
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

    // Google sign-in → Backend Postgres → Firestore minimal → navigation
    fun onGoogleAuthSuccess(
        navToComplete: () -> Unit,
        navNext: () -> Unit
    ) = viewModelScope.launch {
        val user = repo.currentUser ?: return@launch
        _state.update { it.copy(user = user, loading = true, error = null, backendReady = false) }

        val backend = ensureBackendUser(
            uid = user.uid,
            email = user.email ?: "",
            displayName = user.displayName
        )
        if (backend.isFailure) {
            _state.update { it.copy(loading = false, backendReady = false, error = backend.exceptionOrNull()?.message) }
            // On continue le flux Firestore/navigation si tu le souhaites, mais l'utilisateur backend manquera.
        } else {
            _state.update { it.copy(backendReady = true) }
        }

        val ref = db.collection("users").document(user.uid)
        var snap = ref.get().await()
        if (!snap.exists()) {
            val base = mapOf(
                "uid" to user.uid,
                "email" to (user.email?.trim()?.lowercase() ?: ""),
                "profileCompleted" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
            ref.set(base, SetOptions.merge()).await()
            snap = ref.get().await()
        }
        val completed = snap.getBoolean("profileCompleted") ?: false
        _state.update { it.copy(loading = false) }
        if (completed) navNext() else navToComplete()
    }

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
                "email" to (user.email?.trim()?.lowercase() ?: ""),
                "profileCompleted" to true,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(user.uid).set(doc, SetOptions.merge()).await()
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
