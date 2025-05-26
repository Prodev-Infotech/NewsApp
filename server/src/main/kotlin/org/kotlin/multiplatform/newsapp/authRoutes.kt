package org.kotlin.multiplatform.newsapp

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.kotlin.multiplatform.newsapp.model.ApiResponse
import org.kotlin.multiplatform.newsapp.model.ChangePasswordRequest
import org.kotlin.multiplatform.newsapp.model.EditProfileRequest
import org.kotlin.multiplatform.newsapp.model.LoginRequest
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.model.UserResponse
import org.kotlin.multiplatform.newsapp.model.UserResponseData

fun Route.authRoutes(userRepo: UserRepository) {
    route("/auth") {
        post("/signup") {
            try {
                val user = call.receive<User>()
                val isRegistered = userRepo.registerUser(user)

                if (isRegistered) {
                    call.respond(
                        HttpStatusCode.Created,
                        UserResponse(
                            status = true,
                            message = "Signup successful",
                            data = user
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        UserResponse(
                            status = false,
                            message = "Email already registered",
                            data = null
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    UserResponse(
                        status = false,
                        message = "Signup failed: ${e.localizedMessage}",
                        data = null
                    )
                )
            }
        }

    }

    post("/login") {
        try {
            val loginRequest = call.receive<LoginRequest>()
            val user = userRepo.login(loginRequest.email, loginRequest.password)

            if (user != null) {
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        status = true,
                        message = "Login successful",
                        data = user
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiResponse<User>(
                        status = false,
                        message = "Invalid email or passcode",
                        data = null
                    )
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<User>(
                    status = false,
                    message = "Login failed: ${e.localizedMessage}",
                    data = null
                )
            )
        }
    }
    get("/user/{userId}") {
        val userId = call.parameters["userId"]

        if (userId == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse<Unit>(false, "Missing userId", null)
            )
            return@get
        }

        val user = userRepo.getUserById(userId)

        if (user != null) {
            val responseData = UserResponseData(
                id = user.id,
                name = user.name,
                email = user.email,
                profileImage = user.profileImageUrl
            )

            call.respond(
                HttpStatusCode.OK,
                ApiResponse(true, "User found", responseData)
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                ApiResponse<Unit>(false, "User not found", null)
            )
        }
    }
    delete("/auth/delete-account") {
        try {
            val userId = call.parameters["userId"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Missing userId"
            )

            val deleted = userRepo.deleteUser(userId)

            if (deleted) {
                call.respond(HttpStatusCode.OK, ApiResponse<Unit>(true, "Account deleted", null))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "User not found", null))
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<Unit>(
                    status = false,
                    message = "Error: ${e.localizedMessage}",
                    data = null
                )
            )
        }
    }

    post("/auth/change-password") {
        try {
            val request = call.receive<ChangePasswordRequest>()
            val user = userRepo.getUserById(request.userId)

            if (user == null) {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "User not found", null))
                return@post
            }

            if (user.password != request.oldPassword) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(false, "Old password is incorrect", null))
                return@post
            }

            val success = userRepo.updatePassword(request.userId, request.oldPassword, request.newPassword)

            if (success) {
                call.respond(HttpStatusCode.OK, ApiResponse<Unit>(true, "Password updated successfully", null))
            } else {
                call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(false, "Failed to update password", null))
            }

        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<Unit>(
                    status = false,
                    message = "Error: ${e.localizedMessage}",
                    data = null
                )
            )
        }
    }
    put("/auth/edit-profile") {
        try {
            val userId = call.parameters["userId"] ?: return@put call.respond(
                HttpStatusCode.BadRequest, "Missing userId"
            )

            val request = call.receive<EditProfileRequest>()
            val user = userRepo.getUserById(userId)

            if (user != null) {
                val updatedUser = user.copy(
                    name = request.name ?: user.name,
                    profileImageUrl = request.profileImageUrl ?: user.profileImageUrl
                )

                if (userRepo.updateUser(updatedUser)) {
                    call.respond(HttpStatusCode.OK, ApiResponse(true, "Profile updated", updatedUser))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(false, "Failed to update", null))
                }
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, "User not found", null))
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<Unit>(
                    status = false,
                    message = "Error: ${e.localizedMessage}",
                    data = null
                )
            )
        }
    }

}
