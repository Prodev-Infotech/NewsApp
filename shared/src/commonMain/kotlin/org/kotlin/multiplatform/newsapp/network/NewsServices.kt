package org.kotlin.multiplatform.newsapp.network

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import org.kotlin.multiplatform.newsapp.model.ApiResponse
import org.kotlin.multiplatform.newsapp.model.LoginRequest
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.NewsPostResponse
import org.kotlin.multiplatform.newsapp.model.SignUpRequest
import org.kotlin.multiplatform.newsapp.model.User

interface NewsServices {

    @GET("news")
    suspend fun getNews(): NewsPostResponse

    @GET("news/{id}")
    suspend fun getNewsById(@Path("id") id:String): ApiResponse<List<NewsPost>>

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): ApiResponse<User>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<User>
}