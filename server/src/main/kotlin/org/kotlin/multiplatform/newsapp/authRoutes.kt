package org.kotlin.multiplatform.newsapp

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.kotlin.multiplatform.newsapp.model.ApiResponse
import org.kotlin.multiplatform.newsapp.model.LoginRequest
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.model.UserResponse

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

}
