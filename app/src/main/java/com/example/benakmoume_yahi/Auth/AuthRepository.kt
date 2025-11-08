package com.example.benakmoume_yahi.Auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    // Inscription simple (compatibilité)
    suspend fun signUp(email: String, password: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { cont.resume(Result.success(it.user!!)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    // Inscription avec profil (nom/prénom) + UID stocké
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

        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Connexion email
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { cont.resume(Result.success(it.user!!)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    // Connexion Google via idToken (One Tap / GoogleSignIn)
    suspend fun signInWithGoogleIdToken(idToken: String): Result<FirebaseUser> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val res = auth.signInWithCredential(credential).await()
        Result.success(res.user!!)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Sauvegarde/MAJ du profil (fusionne et garde uid)
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
        Result.failure(e)
    }

    fun signOut() { auth.signOut() }
}

