package org.kotlin.multiplatform.newsapp

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.kotlin.multiplatform.newsapp.model.ApiResponse
import org.kotlin.multiplatform.newsapp.model.BaseResponse
import org.kotlin.multiplatform.newsapp.model.Comment
import org.kotlin.multiplatform.newsapp.model.CommentRequest
import org.kotlin.multiplatform.newsapp.model.CommentResponse
import org.kotlin.multiplatform.newsapp.model.Community
import org.kotlin.multiplatform.newsapp.model.CommunityByIdResponse
import org.kotlin.multiplatform.newsapp.model.CommunityResponse
import org.kotlin.multiplatform.newsapp.model.CommunityWithJoinStatus
import org.kotlin.multiplatform.newsapp.model.CreateCommunityRequest
import org.kotlin.multiplatform.newsapp.model.CreateNewsRequest
import org.kotlin.multiplatform.newsapp.model.JoinApproveRequest
import org.kotlin.multiplatform.newsapp.model.JoinLeaveRequest
import org.kotlin.multiplatform.newsapp.model.JoinRequestResponse
import org.kotlin.multiplatform.newsapp.model.LikeCount
import org.kotlin.multiplatform.newsapp.model.LikeRequest
import org.kotlin.multiplatform.newsapp.model.LikeStatusResponse
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.NewsPostResponse
import org.kotlin.multiplatform.newsapp.model.UpdateCommunityRequest
import org.kotlin.multiplatform.newsapp.repository.CommunityRepository
import org.kotlin.multiplatform.newsapp.utils.getCurrentFormattedDate
import java.io.File

fun Route.configureRouting(
    newsRepository: NewsRepository,
    userRepo: UserRepository,
    communityRepository: CommunityRepository,
) {

    route("/news") {
        post {
            try {
                val request = call.receive<CreateNewsRequest>()

                val news = NewsPost(
                    title = request.title,
                    details = request.details,
                    link = request.link,
                    channelName = request.channelName,
                    imageUrl = request.imageUrl
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
                        status = false,
                        message = "Error: ${e.localizedMessage}",
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
                call.respond(
                    HttpStatusCode.BadRequest, CommentResponse<LikeStatusResponse>(
                        status = false, message = "Invalid comment ID", data = emptyList()
                    )
                )
                return@post
            }

            val request = call.receive<LikeRequest>()
            val liked = newsRepository.toggleCommentLike(commentId, request.userId)

            call.respond(
                HttpStatusCode.OK, CommentResponse(
                    status = true,
                    message = if (liked) "Comment liked" else "Comment unliked",
                    data = listOf(LikeStatusResponse(liked))
                )
            )
        }
        get("/comments/{id}/likes") {
            val commentId = call.parameters["id"]
            if (commentId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, CommentResponse<LikeCount>(
                        status = false, message = "Invalid comment ID", data = emptyList()
                    )
                )
                return@get
            }

            val count = newsRepository.getCommentLikeCount(commentId)
            call.respond(
                HttpStatusCode.OK, CommentResponse(
                    status = true,
                    message = "Comment like count fetched",
                    data = listOf(LikeCount(count))
                )
            )
        }

        get("/comments/{id}/like-status") {
            val commentId = call.parameters["id"]
            val userId = call.request.queryParameters["userId"]

            if (commentId == null || userId.isNullOrEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest, CommentResponse<LikeStatusResponse>(
                        status = false,
                        message = "Missing comment ID or user ID",
                        data = emptyList()
                    )
                )
                return@get
            }

            val liked = newsRepository.getUserCommentLikeStatus(commentId, userId)
            call.respond(
                HttpStatusCode.OK, CommentResponse(
                    status = true,
                    message = "User like status fetched for comment",
                    data = listOf(LikeStatusResponse(liked))
                )
            )
        }

        post("/upload/image") {
            val multipart = call.receiveMultipart()
            var fileName: String? = null
            multipart.forEachPart { part ->
                if (part is PartData.FileItem && part.name == "image") {
                    fileName = part.originalFileName ?: "unnamed.jpg"
                    val bytes = part.streamProvider().readBytes()
                    File("uploads/$fileName").writeBytes(bytes)
                }
                part.dispose()
            }
            if (fileName != null) {
                call.respond(ApiResponse(true, "File uploaded", fileName))
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(false, "No file received", data = null)
                )
            }
        }
//            post("/upload/image") {
//                val multipart = call.receiveMultipart()
//                var fileName: String? = null
//
//                // Clear the uploads folder before saving new image
//                val uploadsDir = File("uploads")
//                if (uploadsDir.exists()) {
//                    uploadsDir.listFiles()?.forEach { it.delete() } // Delete each file
//                } else {
//                    uploadsDir.mkdirs() // Create directory if not present
//                }
//
//                multipart.forEachPart { part ->
//                    if (part is PartData.FileItem && part.name == "image") {
//                        fileName = part.originalFileName ?: "unnamed.jpg"
//                        val bytes = part.streamProvider().readBytes()
//                        File(uploadsDir, fileName!!).writeBytes(bytes)
//                    }
//                    part.dispose()
//                }
//
//                if (fileName != null) {
//                    call.respond(ApiResponse(true, "File uploaded", fileName))
//                } else {
//                    call.respond(HttpStatusCode.BadRequest, ApiResponse(false, "No file received", data = null))
//                }
//            }

        get("/image/{filename}") {
            val filename = call.parameters["filename"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing filename"
            )
            val file = File("uploads/$filename")

            if (!file.exists()) {
                call.respond(HttpStatusCode.NotFound, "File not found")
                return@get
            }

            // Respond with the image bytes and proper content type
            call.respondFile(file)
        }

    }


    route("/community") {

        post {
            try {
                val request = call.receive<CreateCommunityRequest>()
                val community = Community(
                    name = request.name,
                    description = request.description,
                    authorName = request.authorName,
                    imageUrl = request.imageUrl,
                    isPrivate = request.isPrivate
                )
                val saved = communityRepository.addCommunity(community)
                call.respond(
                    HttpStatusCode.Created,
                    CommunityResponse(true, "Community created", listOf(saved))
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    CommunityResponse<Community>(
                        status = false,
                        message = "Error: ${e.localizedMessage}"
                    )
                )
            }
        }

        get {
            try {
                val all = communityRepository.getAllCommunities()
                call.respond(HttpStatusCode.OK, CommunityResponse(true, "Communities fetched", all))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    CommunityResponse<Community>(false, "Error: ${e.localizedMessage}")
                )
            }
        }


        get("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    CommunityResponse<Community>(false, "Missing ID")
                )
                return@get
            }

            val community = communityRepository.getCommunityById(id)
            if (community != null) {
                call.respond(
                    HttpStatusCode.OK,
                    CommunityResponse(true, "Community found", listOf(community))
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    CommunityResponse<Community>(false, "Community not found")
                )
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    CommunityResponse<Community>(false, "Missing ID")
                )
                return@put
            }

            val request = call.receive<UpdateCommunityRequest>()
            val updated = Community(
                id = id,
                name = request.name,
                description = request.description,
                imageUrl = request.imageUrl,
                authorName = request.authorName,
                createdAt = getCurrentFormattedDate(),
                isPrivate = request.isPrivate

            )

            val success = communityRepository.updateCommunity(id, updated)
            if (success) {
                call.respond(
                    HttpStatusCode.OK,
                    CommunityResponse(true, "Community updated", listOf(updated))
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    CommunityResponse<Community>(false, "Community not found")
                )
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    CommunityResponse<Community>(false, "Missing ID")
                )
                return@delete
            }

            val success = communityRepository.deleteCommunity(id)
            if (success) {
                call.respond(
                    HttpStatusCode.OK,
                    CommunityResponse<Community>(true, "Community deleted")
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    CommunityResponse<Community>(false, "Community not found")
                )
            }
        }

        post("/join") {
            val request = call.receive<JoinLeaveRequest>()
            try {
                val success = communityRepository.joinCommunity(request.user, request.communityId)
                if (success) {
                    call.respond(HttpStatusCode.OK, BaseResponse(true, "Joined community"))
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        BaseResponse(false, "Already joined or error")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    BaseResponse(false, "Error: ${e.localizedMessage}")
                )
            }
        }

        post("/leave") {
            val request = call.receive<JoinLeaveRequest>()
            try {
                val success = communityRepository.leaveCommunity(request.user, request.communityId)
                if (success) {
                    call.respond(HttpStatusCode.OK, BaseResponse(true, "Left community"))
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        BaseResponse(false, "Not part of community")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    BaseResponse(false, "Error: ${e.localizedMessage}")
                )
            }
        }
    }

    get("/communities") {
        val userId = call.request.queryParameters["userId"]
        if (userId == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                CommunityResponse<CommunityWithJoinStatus>(false, "Missing userId")
            )
            return@get
        }

        try {
            val allCommunities = communityRepository.getAllCommunities()
            val joinedIds = communityRepository.getJoinedCommunityIdsForUser(userId)
            val requestedIds = communityRepository.getRequestedCommunityIdsForUser(userId)

            val responseList = allCommunities.map { community ->
                val status = when {
                    community.id in joinedIds -> "Joined"
                    community.id in requestedIds -> "Requested"
                    else -> "Join"
                }

                CommunityWithJoinStatus(
                    id = community.id,
                    name = community.name,
                    description = community.description,
                    imageUrl = community.imageUrl,
                    authorName = community.authorName,
                    createdAt = community.createdAt,
                    isPrivate = community.isPrivate,
                    joinStatus = status
                )
            }

            call.respond(
                HttpStatusCode.OK,
                CommunityResponse(true, "Fetched with join status", responseList)
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                CommunityResponse<CommunityWithJoinStatus>(false, "Error: ${e.localizedMessage}")
            )
        }
    }
    get("/communityById") {
        val userId = call.request.queryParameters["userId"]
        val communityId = call.request.queryParameters["communityId"]

        if (userId == null || communityId == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                CommunityByIdResponse<CommunityWithJoinStatus>(false, "Missing userId or communityId")
            )
            return@get
        }
        val allCommunities = communityRepository.getAllCommunities()
        val matchedCommunity = allCommunities.find { it.id == communityId }

        if (matchedCommunity == null) {
            call.respond(
                HttpStatusCode.NotFound,
                CommunityByIdResponse<CommunityWithJoinStatus>(false, "Community not found")
            )
            return@get
        }

        try {
            val joinedIds = communityRepository.getJoinedCommunityIdsForUser(userId)
            val requestedIds = communityRepository.getRequestedCommunityIdsForUser(userId)

            val joinStatus = when {
                communityId in joinedIds -> "Joined"
                communityId in requestedIds -> "Requested"
                else -> "Join"
            }

            val response = CommunityWithJoinStatus(
                id = matchedCommunity.id,
                name = matchedCommunity.name,
                description = matchedCommunity.description,
                imageUrl = matchedCommunity.imageUrl,
                authorName = matchedCommunity.authorName,
                createdAt = matchedCommunity.createdAt,
                isPrivate = matchedCommunity.isPrivate,
                joinStatus = joinStatus
            )

            call.respond(
                HttpStatusCode.OK,
                CommunityByIdResponse(true, "Community fetched", response)
            )

        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                CommunityByIdResponse<CommunityWithJoinStatus>(false, "Error: ${e.localizedMessage}")
            )
        }
    }

    post("/admin/community/approve") {
        val approval = call.receive<JoinApproveRequest>()
        val success = communityRepository.approveRequest(approval.userId, approval.communityId)

        if (success) {
            call.respond(
                status = HttpStatusCode.OK,
                message = BaseResponse(true, "Request approved")
            )
        } else {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = BaseResponse(false, "Approval failed")
            )
        }
    }

    post("/admin/community/reject") {
        val rejection = call.receive<JoinApproveRequest>()
        try {
            val success = communityRepository.rejectRequest(rejection.userId, rejection.communityId)
            if (success) {
                call.respond(BaseResponse(status = true, message = "Request rejected"))
            } else {
                call.respond(BaseResponse(status = false, message = "Rejection failed"))
            }
        } catch (e: Exception) {
            call.respond(BaseResponse(status = false, message = "Error: ${e.localizedMessage}"))
        }
    }

    get("/admin/community/all-requests") {
        val communityId = call.request.queryParameters["communityId"]?.trim()

        if (communityId.isNullOrEmpty()) {
            call.respond(
                HttpStatusCode.BadRequest,
                JoinRequestResponse(
                    success = false,
                    message = "Missing or empty communityId",
                    requests = emptyList()
                )
            )
            return@get
        }

        try {
            val requests = communityRepository.getAllRequestsForCommunity(communityId)
            call.respond(
                HttpStatusCode.OK,
                JoinRequestResponse(
                    success = true,
                    message = if (requests.isNotEmpty()) "All requests fetched" else "No requests found",
                    requests = requests
                )
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                JoinRequestResponse(
                    success = false,
                    message = "Error: ${e.localizedMessage}",
                    requests = emptyList()
                )
            )
        }
    }

}