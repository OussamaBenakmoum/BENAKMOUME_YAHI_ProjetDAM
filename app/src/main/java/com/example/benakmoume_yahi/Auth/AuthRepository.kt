package com.example.benakmoume_yahi.Auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepository constructor(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
        }
    }

    // ✅ StateFlow qui track l'utilisateur actuel
    private val _currentUserFlow = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUserFlow: StateFlow<FirebaseUser?> = _currentUserFlow

    val currentUser: FirebaseUser? get() = auth.currentUser

    init {
        // ✅ Écoute les changements d'état Firebase Auth
        auth.addAuthStateListener { firebaseAuth ->
            _currentUserFlow.value = firebaseAuth.currentUser
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _currentUserFlow.value = it.user
                    cont.resume(Result.success(it.user!!))
                }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _currentUserFlow.value = it.user
                    cont.resume(Result.success(it.user!!))
                }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    fun signOut() {
        auth.signOut()
        _currentUserFlow.value = null
    }
}
