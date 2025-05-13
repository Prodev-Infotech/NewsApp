package org.kotlin.multiplatform.newsapp.model

import kotlinx.serialization.Serializable
import org.kotlin.multiplatform.newsapp.utils.generateId
import org.kotlin.multiplatform.newsapp.utils.getCurrentFormattedDate

@Serializable
data class NewsPostResponse(
    val status: Boolean,
    val message: String,
    val data: List<NewsPost>
)
@Serializable
data class CreateNewsRequest(
    val title: String,
    val details: String,
    val link: String,
    val channelName: String
)
@Serializable
data class NewsPost(
    val id: String = generateId(),
    val title: String,
    val createdAt: String=getCurrentFormattedDate(),
    val details: String,
    val link: String,
    val channelName: String,
    var isBookMark: Boolean = false
)

@Serializable
data class Bookmark(
    val id: String = generateId(),
    val userId: String,
    val postId: String
)

@Serializable
data class User(
    val id: String = generateId(),
    val name: String,
    val email: String,
    val password: String, // In real-world apps, always hash passwords!
    val isAdmin: Boolean = false, //  flag for admin
    val createdAt: String = getCurrentFormattedDate()
)
@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val isAdmin: Boolean
)


@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class ApiResponse<T>(
    val status: Boolean,
    val message: String,
    val data: T? = null
)

@Serializable
data class CommentResponse<T>(
    val status: Boolean,
    val message: String,
    val data: List<T>
)
@Serializable
data class LikeCount(val count: Int)
@Serializable
data class UserResponse(
    val status: Boolean,
    val message: String,
    val data: User? = null
)
@Serializable
data class UserBookmark(
    val id: String = generateId(),
    val userId: String,
    val newsId: String
)

@Serializable
data class UserWithNews(
    val id: String,
    val name: String,
    val email: String,
    val isAdmin: Boolean,
    val createdAt: String,
    val news: List<NewsPost>
)

@Serializable
data class UserNewsResponse(
    val status: Boolean,
    val message: String,
    val user: List<UserWithNews>
)
@Serializable
data class CommentRequest(val userId: String, val content: String,val userName: String)

@Serializable
data class LikeRequest(val userId: String)
@Serializable
data class LikeStatusResponse(
    val liked: Boolean
)

@Serializable
data class Comment(
    val id: String = generateId(),
    val newsId: String,
    val userId: String,
    val userName:String,
    val content: String,
    val createdAt: String = getCurrentFormattedDate()
)

@Serializable
data class Like(
    val id: String = generateId(),
    val newsId: String,
    val userId: String
)
sealed class ResultState<out T> {
    object Initial : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val exception: String) : ResultState<Nothing>()
}