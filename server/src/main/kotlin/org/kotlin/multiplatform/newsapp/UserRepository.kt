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

}