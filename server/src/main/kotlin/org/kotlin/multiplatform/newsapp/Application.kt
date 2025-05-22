package org.kotlin.multiplatform.newsapp


import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.kotlin.multiplatform.newsapp.repository.CommunityRepository
import org.kotlin.multiplatform.newsapp.repository.CommunityRepositoryImpl

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    install(ContentNegotiation) {
        json(Json {
            encodeDefaults = true //This includes all default values
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true})
    }


    val newsRepository = NewsRepository()
    val userRepository=UserRepository()
    val communityRepository = CommunityRepositoryImpl()
    routing {
    configureRouting(newsRepository,userRepository,communityRepository)
        authRoutes(userRepository)
    }

}