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
import org.kotlin.multiplatform.newsapp.model.Comment
import org.kotlin.multiplatform.newsapp.model.CommentRequest
import org.kotlin.multiplatform.newsapp.model.CommentResponse
import org.kotlin.multiplatform.newsapp.model.CreateNewsRequest
import org.kotlin.multiplatform.newsapp.model.LikeCount
import org.kotlin.multiplatform.newsapp.model.LikeRequest
import org.kotlin.multiplatform.newsapp.model.LikeStatusResponse
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.NewsPostResponse
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.model.UserNewsResponse
import org.kotlin.multiplatform.newsapp.model.UserWithNews

fun Route.configureRouting(newsRepository: NewsRepository,
                           userRepo: UserRepository) {

        route("/news") {
            post {
                try {
                    val request  = call.receive<CreateNewsRequest>()

                    val news = NewsPost(
                        title = request.title,
                        details = request.details,
                        link = request.link,
                        channelName = request.channelName
                        // id, createdAt, isBookMark will use default values
                    )
                    val savedNews = newsRepository.addNews(news)
                    call.respond(
                        HttpStatusCode.Created,
                        NewsPostResponse(
                            status = true,
                            message = "News post created successfully!",
                            data = listOf(savedNews)
                        )
                    )
                    println("save New:-->$savedNews")
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        NewsPostResponse(
                            status=false,
                            message="Error: ${e.localizedMessage}",
                            data = emptyList()
                        )
                    )
                }
            }
            get {
                try {
                    val newsList = newsRepository.getAllNews()
                    println("Fetched News: $newsList")

                    call.respond(
                        HttpStatusCode.OK,
                        NewsPostResponse(
                            status = true,
                            message = "News fetched successfully",
                            data = newsList // newsList is a List<NewsPost>
                        )
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf(
                            "status" to false,
                            "message" to "Error: ${e.localizedMessage}"
                        )
                    )
                }
            }


            delete("/{id}") {
                val postId = call.parameters["id"] // Extract the postId from the URL parameters

                if (postId != null) {
                    try {
                        // Try to delete the news post from the repository using the postId
                        val deletedPost = newsRepository.deleteNews(postId)

                        if (deletedPost != null) {
                            // If the post is successfully deleted, respond with a success message
                            call.respond(
                                HttpStatusCode.OK,
                                NewsPostResponse(
                                    status = true,
                                    message = "News post deleted successfully",
                                    data = emptyList() // No data to return, so we send an empty list
                                )
                            )
                        } else {
                            // If the post was not found, respond with an error message
                            call.respond(
                                HttpStatusCode.NotFound,
                                NewsPostResponse(
                                    status = false,
                                    message = "Post not found",
                                    data = emptyList()
                                )
                            )
                        }
                    } catch (e: Exception) {
                        // If an error occurs, respond with an internal server error message
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            NewsPostResponse(
                                status = false,
                                message = "Error: ${e.localizedMessage}",
                                data = emptyList()
                            )
                        )
                    }
                } else {
                    // If the postId is missing or invalid, respond with a bad request error
                    call.respond(
                        HttpStatusCode.BadRequest,
                        NewsPostResponse(
                            status = false,
                            message = "Missing or invalid post ID",
                            data = emptyList()
                        )
                    )
                }
            }

            get("/{id}") {
                val postId = call.parameters["id"] // Extract the postId from the URL parameters

                if (postId != null) {
                    try {
                        // Try to get the specific news post by its ID from the repository
                        val newsPost = newsRepository.getNewsById(postId)

                        if (newsPost != null) {
                            // If the post is found, respond with the news data
                            call.respond(
                                HttpStatusCode.OK,
                                NewsPostResponse(
                                    status = true,
                                    message = "News fetched successfully",
                                    data = listOf(newsPost) // Wrap the single newsPost in a list
                                )
                            )
                        } else {
                            // If the post is not found, respond with a 404 Not Found
                            call.respond(
                                HttpStatusCode.NotFound,
                                NewsPostResponse(
                                    status = false,
                                    message = "News post not found",
                                    data = emptyList()
                                )
                            )
                        }
                    } catch (e: Exception) {
                        // If an error occurs, respond with an internal server error
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            NewsPostResponse(
                                status = false,
                                message = "Error: ${e.localizedMessage}",
                                data = emptyList()
                            )
                        )
                    }
                } else {
                    // If the postId is missing or invalid, respond with a bad request error
                    call.respond(
                        HttpStatusCode.BadRequest,
                        NewsPostResponse(
                            status = false,
                            message = "Missing or invalid post ID",
                            data = emptyList()
                        )
                    )
                }
            }
        }


    // POST /news/{id}/comment
    post("/{id}/comment") {
        val newsId = call.parameters["id"]
        if (newsId == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                CommentResponse<Comment>(
                    status = false,
                    message = "Invalid news ID",
                    data = emptyList()
                )
            )
            return@post
        }

        val request = call.receive<CommentRequest>()
        val comment = Comment(
            newsId = newsId,
            userId = request.userId,
            content = request.content,
            userName = request.userName
        )
        val savedComment = newsRepository.addComment(newsId, comment)

        call.respond(
            HttpStatusCode.Created,
            CommentResponse(
                status = true,
                message = "Comment added successfully",
                data = listOf(savedComment)
            )
        )
    }

// GET /news/{id}/comments
    get("/{id}/comments") {
        val newsId = call.parameters["id"]
        if (newsId == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                CommentResponse<Comment>(
                    status = false,
                    message = "Invalid news ID",
                    data = emptyList()
                )
            )
            return@get
        }

        val comments = newsRepository.getComments(newsId)
        call.respond(
            HttpStatusCode.OK,
            CommentResponse(
                status = true,
                message = "Comments fetched successfully",
                data = comments
            )
        )
    }

// POST /news/{id}/like
    post("/{id}/like") {
        val newsId = call.parameters["id"]
        if (newsId == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                CommentResponse<LikeStatusResponse>(
                    status = false,
                    message = "Invalid news ID",
                    data = emptyList()
                )
            )
            return@post
        }

        val request = call.receive<LikeRequest>()
        val liked = newsRepository.toggleLike(newsId, request.userId)

        call.respond(
            HttpStatusCode.OK,
            CommentResponse(
                status = true,
                message = if (liked) "Liked" else "Unliked",
                data = listOf(LikeStatusResponse(liked))
            )
        )
    }

// GET /news/{id}/likes
    get("/{id}/likes") {
        val newsId = call.parameters["id"]
        if (newsId == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                CommentResponse<LikeCount>(
                    status = false,
                    message = "Invalid news ID",
                    data = emptyList()
                )
            )
            return@get
        }

        val count = newsRepository.getLikes(newsId)
        call.respond(
            HttpStatusCode.OK,
            CommentResponse(
                status = true,
                message = "Like count fetched",
                data = listOf(LikeCount(count))
            )
        )
    }

    get("/{id}/like-status") {
        val newsId = call.parameters["id"]
        val userId = call.request.queryParameters["userId"]

        if (newsId == null || userId.isNullOrEmpty()) {
            call.respond(
                HttpStatusCode.BadRequest,
                CommentResponse<LikeStatusResponse>(
                    status = false,
                    message = "Missing news ID or user ID",
                    data = emptyList()
                )
            )
            return@get
        }

        val liked = newsRepository.getUserLikeStatus(newsId, userId)

        call.respond(
            HttpStatusCode.OK,
            CommentResponse(
                status = true,
                message = "User like status fetched",
                data = listOf(LikeStatusResponse(liked))
            )
        )
    }

    post("/comments/{id}/like") {
        val commentId = call.parameters["id"]
        if (commentId == null) {
            call.respond(HttpStatusCode.BadRequest, CommentResponse<LikeStatusResponse>(
                status = false, message = "Invalid comment ID", data = emptyList()
            ))
            return@post
        }

        val request = call.receive<LikeRequest>()
        val liked = newsRepository.toggleCommentLike(commentId, request.userId)

        call.respond(HttpStatusCode.OK, CommentResponse(
            status = true,
            message = if (liked) "Comment liked" else "Comment unliked",
            data = listOf(LikeStatusResponse(liked))
        ))
    }
    get("/comments/{id}/likes") {
        val commentId = call.parameters["id"]
        if (commentId == null) {
            call.respond(HttpStatusCode.BadRequest, CommentResponse<LikeCount>(
                status = false, message = "Invalid comment ID", data = emptyList()
            ))
            return@get
        }

        val count = newsRepository.getCommentLikeCount(commentId)
        call.respond(HttpStatusCode.OK, CommentResponse(
            status = true,
            message = "Comment like count fetched",
            data = listOf(LikeCount(count))
        ))
    }

    get("/comments/{id}/like-status") {
        val commentId = call.parameters["id"]
        val userId = call.request.queryParameters["userId"]

        if (commentId == null || userId.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, CommentResponse<LikeStatusResponse>(
                status = false, message = "Missing comment ID or user ID", data = emptyList()
            ))
            return@get
        }

        val liked = newsRepository.getUserCommentLikeStatus(commentId, userId)
        call.respond(HttpStatusCode.OK, CommentResponse(
            status = true,
            message = "User like status fetched for comment",
            data = listOf(LikeStatusResponse(liked))
        ))
    }

}