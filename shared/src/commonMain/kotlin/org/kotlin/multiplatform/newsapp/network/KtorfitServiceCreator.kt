package org.kotlin.multiplatform.newsapp.network

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorfitServiceCreator (val baseUrl: String){

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        // Add this to set default Content-Type header
        defaultRequest {
            headers.append("Content-Type", "application/json")
        }
    }


    private val ktorfit = Ktorfit.Builder()
        .baseUrl(baseUrl)
        .httpClient(httpClient)
        .build()

    val api = ktorfit.create<NewsServices>()

}