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
import org.kotlin.multiplatform.newsapp.model.CreateNewsRequest
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.NewsPostResponse
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


}