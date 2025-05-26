package org.kotlin.multiplatform.newsapp

import org.kotlin.multiplatform.newsapp.model.User

class UserRepository {
    private val users = mutableListOf<User>() // This should be a database in production
    private val userList = mutableListOf<User>() // This should be a database in production

    fun registerUser(user: User): Boolean {
        if (users.any { it.email == user.email }) return false
        users.add(user)
        return true
    }

    fun login(email: String, passcode: String): User? {
        return users.find { it.email == email && it.password == passcode }
    }

    fun getAllUsers(): List<User> = users

    fun getUserById(userId: String): User? {
        return users.find { it.id == userId }
    }
    fun isClient(userId: String): Boolean {
        return userList.any { it.id == userId && !it.isAdmin }
    }
    fun userExists(userId: String): Boolean {
        return userList.any { it.id == userId }
    }
    fun updatePassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        val index = users.indexOfFirst { it.id == userId }
        if (index != -1) {
            val user = users[index]
            return if (user.password == oldPassword) {
                users[index] = user.copy(password = newPassword)
                true
            } else {
                false // old password mismatch
            }
        }
        return false
    }

    fun deleteUser(email: String): Boolean {
        return users.removeIf { it.email == email }
    }

    fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    fun updateUser(updatedUser: User): Boolean {
        val index = users.indexOfFirst { it.email == updatedUser.email }
        return if (index != -1) {
            users[index] = updatedUser
            true
        } else {
            false
        }
    }

}