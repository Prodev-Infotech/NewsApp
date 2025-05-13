package org.kotlin.multiplatform.newsapp.utils

object SessionUtil {

    private const val KEY_USER_ID = "user_id"

    fun saveUserId(userId: String) {
        settings.putString(KEY_USER_ID, userId)
    }

    fun getUserId(): String? = settings.getStringOrNull(KEY_USER_ID)

    fun isUserLoggedIn(): Boolean = getUserId() != null

    fun logout() {
        settings.remove(KEY_USER_ID)
    }


}