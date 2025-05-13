package org.kotlin.multiplatform.newsapp.utils

object SessionUtil {

    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"

    fun saveUserId(userId: String) {
        settings.putString(KEY_USER_ID, userId)
    }
    fun saveUserName(userName: String) {
        settings.putString(KEY_USER_NAME, userName)
    }

    fun getUserId(): String? = settings.getStringOrNull(KEY_USER_ID)
    fun getUserName(): String? = settings.getStringOrNull(KEY_USER_NAME)

    fun isUserLoggedIn(): Boolean = getUserId() != null

    fun logout() {
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_USER_NAME)
    }


}