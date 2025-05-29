package org.kotlin.multiplatform.newsapp

import org.kotlin.multiplatform.newsapp.model.CreatePostRequest
import org.kotlin.multiplatform.newsapp.model.Post
import org.kotlin.multiplatform.newsapp.utils.generateId

object PostRepository {
    private val posts = mutableListOf<Post>()

    fun createPost(request: CreatePostRequest): Post {
        val post = Post(
            postId = generateId(), // custom function to generate a unique ID
            communityId = request.communityId,
            userId = request.userId,
            title = request.title,
            authorName = request.authorName,
            userProfileImageUrl = request.userProfileImageUrl,
            media = request.media,
            link = request.link
        )
        posts.add(post)
        return post
    }

    fun getPostsByCommunity(communityId: String): List<Post> {
        return posts.filter { it.communityId == communityId }
    }
}