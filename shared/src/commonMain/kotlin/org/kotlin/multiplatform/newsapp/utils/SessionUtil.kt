package org.kotlin.multiplatform.newsapp.utils

import kotlinx.serialization.json.Json
import org.kotlin.multiplatform.newsapp.model.User

object SessionUtil {

    private const val KEY_USER_JSON = "user_json"

    private val json = Json { ignoreUnknownKeys = true }

    fun saveUser(user: User) {
        val userJson = json.encodeToString(user)
        settings.putString(KEY_USER_JSON, userJson)
    }

    fun getUser(): User? {
        val userJson = settings.getStringOrNull(KEY_USER_JSON) ?: return null
        return try {
            json.decodeFromString<User>(userJson)
        } catch (e: Exception) {
            null
        }
    }

    fun getUserId(): String? = getUser()?.id
    fun getUserName(): String? = getUser()?.name

    fun isUserLoggedIn(): Boolean = getUserId() != null
    fun logout() {
        settings.remove(KEY_USER_JSON)
    }


}