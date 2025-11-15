package com.example.benakmoume_yahi.Auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Etat utilisateur observable pour garder l'UI sync
    private val _currentUserFlow = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUserFlow: StateFlow<FirebaseUser?> = _currentUserFlow

    val currentUser: FirebaseUser? get() = auth.currentUser

    init {
        auth.addAuthStateListener { fa -> _currentUserFlow.value = fa.currentUser }
    }

    // ---------- MAPPING D'ERREURS UX ----------
    private fun mapAuthError(t: Throwable): String {
        val code = (t as? com.google.firebase.auth.FirebaseAuthException)?.errorCode?.uppercase()
            ?: (t.message ?: "").uppercase()

        return when {
            // Mauvais mot de passe
            code.contains("WRONG_PASSWORD") || code.contains("INVALID_PASSWORD") ||
                    code.contains("ERROR_WRONG_PASSWORD") ->
                "Mot de passe incorrect"

            // Email introuvable / invalide
            code.contains("USER_NOT_FOUND") || code.contains("INVALID_EMAIL") ||
                    code.contains("ERROR_USER_NOT_FOUND") ->
                "Adresse e-mail introuvable"

            // Compte désactivé
            code.contains("USER_DISABLED") || code.contains("ERROR_USER_DISABLED") ->
                "Compte désactivé"

            // Trop de tentatives
            code.contains("TOO_MANY_ATTEMPTS_TRY_LATER") ->
                "Trop de tentatives. Réessayez plus tard"

            // Session/credential expirés
            code.contains("CREDENTIAL_TOO_OLD_LOGIN_AGAIN") ||
                    code.contains("EXPIRED_ACTION_CODE") ||
                    code.contains("CREDENTIAL_MISMATCH") ->
                "La session a expiré. Veuillez vous reconnecter"

            // Email déjà utilisé (cas inscription)
            code.contains("EMAIL_ALREADY_IN_USE") || code.contains("ERROR_EMAIL_ALREADY_IN_USE") ->
                "E-mail déjà utilisé"

            else -> "Identifiants invalides"
        }
    }

    // ---------- AUTH EMAIL ----------

    // Inscription simple
    suspend fun signUp(email: String, password: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _currentUserFlow.value = it.user
                    cont.resume(Result.success(it.user!!))
                }
                .addOnFailureListener { err ->
                    cont.resume(Result.failure(Exception(mapAuthError(err))))
                }
        }

    // Inscription + profil Firestore
    suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<FirebaseUser> = try {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user!!
        val profile = userProfileChangeRequest { displayName = "$firstName $lastName" }
        user.updateProfile(profile).await()

        val doc = mapOf(
            "uid" to user.uid,
            "firstName" to firstName,
            "lastName"  to lastName,
            "email"     to email,
            "photoUrl"  to null,
            "profileCompleted" to true,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(user.uid).set(doc, SetOptions.merge()).await()

        _currentUserFlow.value = user
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(Exception(mapAuthError(e)))
    }

    // Connexion email
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _currentUserFlow.value = it.user
                    cont.resume(Result.success(it.user!!))
                }
                .addOnFailureListener { err ->
                    cont.resume(Result.failure(Exception(mapAuthError(err))))
                }
        }

    // ---------- AUTH GOOGLE ----------

    // Connexion Google via idToken
    suspend fun signInWithGoogleIdToken(idToken: String): Result<FirebaseUser> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val res = auth.signInWithCredential(credential).await()
        _currentUserFlow.value = res.user
        Result.success(res.user!!)
    } catch (e: Exception) {
        Result.failure(Exception(mapAuthError(e)))
    }

    // ---------- PROFIL FIRESTORE ----------

    // Sauvegarde/MAJ du profil (merge Firestore)
    suspend fun saveUserProfile(firstName: String, lastName: String, email: String): Result<Unit> = try {
        val user = auth.currentUser ?: return Result.failure(IllegalStateException("Utilisateur non connecté"))
        val profile = userProfileChangeRequest { displayName = "$firstName $lastName" }
        user.updateProfile(profile).await()

        val doc = mapOf(
            "uid"       to user.uid,
            "firstName" to firstName,
            "lastName"  to lastName,
            "email"     to email,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(user.uid)
            .set(doc, SetOptions.merge())
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(mapAuthError(e)))
    }

    // Déconnexion
    fun signOut() {
        auth.signOut()
        _currentUserFlow.value = null
    }
}
