package org.kotlin.multiplatform.newsapp.network

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import org.kotlin.multiplatform.newsapp.model.ApiResponse
import org.kotlin.multiplatform.newsapp.model.Comment
import org.kotlin.multiplatform.newsapp.model.CommentRequest
import org.kotlin.multiplatform.newsapp.model.CommentResponse
import org.kotlin.multiplatform.newsapp.model.LikeCount
import org.kotlin.multiplatform.newsapp.model.LikeRequest
import org.kotlin.multiplatform.newsapp.model.LikeStatusResponse
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

    @GET("news/{id}/comments")
    suspend fun getComments(@Path("id") newsId: String): CommentResponse<Comment>

    @POST("news/{id}/comment")
    suspend fun postComment(
        @Path("id") newsId: String,
        @Body request: CommentRequest
    ): CommentResponse<Comment>

    @POST("news/{id}/like")
    suspend fun toggleLike(
        @Path("id") newsId: String,
        @Body request: LikeRequest
    ): CommentResponse<LikeStatusResponse>

    @GET("news/{id}/likes")
    suspend fun getLikeCount(@Path("id") newsId: String): CommentResponse<LikeCount>

    @GET("news/{id}/like-status")
    suspend fun getUserLikeStatus(
        @Path("id") newsId: String,
        @Query("userId") userId: String
    ): CommentResponse<LikeStatusResponse>

    // Toggle comment like
    @POST("news/comments/{id}/like")
    suspend fun toggleCommentLike(
        @Path("id") commentId: String,
        @Body request: LikeRequest
    ): CommentResponse<LikeStatusResponse>

    // Get comment like count
    @GET("news/comments/{id}/likes")
    suspend fun getCommentLikeCount(
        @Path("id") commentId: String
    ): CommentResponse<LikeCount>

    // Get user like status for a comment
    @GET("news/comments/{id}/like-status")
    suspend fun getUserCommentLikeStatus(
        @Path("id") commentId: String,
        @Query("userId") userId: String
    ): CommentResponse<LikeStatusResponse>
}