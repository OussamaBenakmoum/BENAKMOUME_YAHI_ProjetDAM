package com.example.benakmoume_yahi.remote

import com.example.benakmoume_yahi.models.UserCreate
import retrofit2.Response

suspend fun ensureBackendUser(
    api: RecipeApiService,
    uid: String,
    email: String?,
    displayName: String?
): Result<Unit> {
    // 0) Vérifier si l'utilisateur existe déjà (par uid)
    val get = api.getUser(uid)
    if (get.isSuccessful && get.body() != null) return Result.success(Unit)
    if (get.code() != 404) {
        return Result.failure(Exception("GET /users/$uid -> ${get.code()} ${get.errorBody()?.string()}"))
    }

    // 1) Préparer le payload de création
    val first = displayName?.substringBefore(" ").takeUnless { it.isNullOrBlank() } ?: "User"
    val last  = displayName?.substringAfter(" ", missingDelimiterValue = "").takeUnless { it.isNullOrBlank() } ?: " "
    val mail  = (email ?: "").trim().lowercase()
    if (mail.isEmpty()) {
        // Si le backend exige un email, stoppe ici avec message clair
        return Result.failure(IllegalArgumentException("Aucune adresse e‑mail disponible pour ce compte"))
    }

    // 2) POST de création
    val post: Response<Unit> = api.createUser(
        UserCreate(
            firstname = first,
            lastname  = last,
            email     = mail,
            areas_preferred      = null,   // préférences locales → DataStore
            preferred_categories = null,
            photo_profile        = null,
            firebase_uid         = uid
        )
    )

    if (post.isSuccessful) return Result.success(Unit)

    // 3) Gestion robuste des erreurs
    val msg = post.errorBody()?.string().orEmpty()
    return if (post.code() == 400) {
        // Idempotence: re-GET par uid; si finalement présent → OK
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
