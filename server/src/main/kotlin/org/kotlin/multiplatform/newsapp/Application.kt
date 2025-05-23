package org.kotlin.multiplatform.newsapp

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.kotlin.multiplatform.newsapp.model.Bookmark
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.NewsPostResponse
import org.kotlin.multiplatform.newsapp.model.User

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    install(ContentNegotiation) {
        json(Json {
            encodeDefaults = true //This includes all default values
            prettyPrint = true;
            ignoreUnknownKeys = true
            isLenient = true})
    }


    val newsRepository = NewsRepository()
    val userRepository=UserRepository()


    routing {
    configureRouting(newsRepository,userRepository)
        authRoutes(userRepository)
    }

}