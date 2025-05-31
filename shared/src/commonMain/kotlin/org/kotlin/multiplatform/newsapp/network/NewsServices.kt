package org.kotlin.multiplatform.newsapp.network

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpResponse
import org.kotlin.multiplatform.newsapp.model.ApiResponse
import org.kotlin.multiplatform.newsapp.model.BaseResponse
import org.kotlin.multiplatform.newsapp.model.ChangePasswordRequest
import org.kotlin.multiplatform.newsapp.model.Comment
import org.kotlin.multiplatform.newsapp.model.CommentRequest
import org.kotlin.multiplatform.newsapp.model.CommentResponse
import org.kotlin.multiplatform.newsapp.model.Community
import org.kotlin.multiplatform.newsapp.model.CommunityByIdResponse
import org.kotlin.multiplatform.newsapp.model.CommunityResponse
import org.kotlin.multiplatform.newsapp.model.CommunityWithJoinStatus
import org.kotlin.multiplatform.newsapp.model.CreatePostRequest
import org.kotlin.multiplatform.newsapp.model.EditProfileRequest
import org.kotlin.multiplatform.newsapp.model.JoinLeaveRequest
import org.kotlin.multiplatform.newsapp.model.LikeCount
import org.kotlin.multiplatform.newsapp.model.LikeRequest
import org.kotlin.multiplatform.newsapp.model.LikeStatusResponse
import org.kotlin.multiplatform.newsapp.model.LoginRequest
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.NewsPostResponse
import org.kotlin.multiplatform.newsapp.model.Post
import org.kotlin.multiplatform.newsapp.model.PostResponse
import org.kotlin.multiplatform.newsapp.model.SignUpRequest
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.model.UserResponseData

interface NewsServices {

    @GET("news")
    suspend fun getNews(): NewsPostResponse

    @GET("news/{id}")
    suspend fun getNewsById(@Path("id") id:String): ApiResponse<List<NewsPost>>

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): ApiResponse<User>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<User>

    @GET("user/{userId}")
    suspend fun getUserById(@Path("userId") id: String): ApiResponse<UserResponseData>

    @PUT("auth/edit-profile")
    suspend fun updateUser(
        @Body user: EditProfileRequest,
        @Query("userId") userId: String
    ): ApiResponse<User>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit>

    @DELETE("auth/delete/{userId}")
    suspend fun deleteUser(@Path("userId") id: String): ApiResponse<Unit>

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

    @GET("community")
    suspend fun getAllCommunities(): CommunityResponse<CommunityWithJoinStatus>

    @GET("communities")
    suspend fun getAllCommunitiesWithJoinStatus(
        @Query("userId") userId: String
    ): CommunityResponse<CommunityWithJoinStatus>

    @GET("communityById")
    suspend fun getSingleCommunityWithJoinStatus(
        @Query("userId") userId: String,
        @Query("communityId") communityId: String
    ): CommunityByIdResponse<CommunityWithJoinStatus>

    @POST("community/join")
    suspend fun joinCommunity(@Body request: JoinLeaveRequest): BaseResponse

    @POST("community/leave")
    suspend fun leaveCommunity(@Body request: JoinLeaveRequest): BaseResponse

    @POST("news/upload/image")
    suspend fun uploadImage(
        @Body body: MultiPartFormDataContent
    ): ApiResponse<String>

    // Get image from server as a byte stream
    @GET("news/image/{filename}")
    suspend fun getImage(
        @Path("filename") filename: String
    ): ApiResponse<String>  // ✅ Change from HttpResponse to ApiResponse<String>
    @POST("createPost")
    suspend fun createPost(@Body request: CreatePostRequest): PostResponse<Post>

    @GET("communities/{communityId}/posts")
    suspend fun getPostsByCommunity(
        @Path("communityId") communityId: String
    ): PostResponse<List<Post>>
}