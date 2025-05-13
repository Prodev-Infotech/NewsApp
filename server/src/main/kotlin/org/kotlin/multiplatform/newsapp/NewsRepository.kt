package org.kotlin.multiplatform.newsapp

import org.kotlin.multiplatform.newsapp.model.Comment
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.User
import java.util.concurrent.ConcurrentHashMap

class  NewsRepository {
    private val newsList = mutableListOf<NewsPost>()
    private val users = mutableListOf<User>() // or a map of userId to User
    private val commentLikes: MutableMap<String, MutableSet<String>> = ConcurrentHashMap()

    fun addNews(news: NewsPost): NewsPost {
        newsList.add(news)
        return news
    }

    fun getAllNews(): List<NewsPost> {
        return newsList
    }

    fun deleteNews(postId: String): NewsPost? {
        // Attempt to find the news post by ID
        val post = newsList.find { it.id == postId }

        if (post != null) {
            // If found, remove it from the list (or delete from database)
            newsList.remove(post)
            return post
        }

        return null
    }

    fun getNewsById(id: String): NewsPost? {
        return newsList.find { it.id == id } // Find and return the news post by ID
    }

    private val comments: MutableMap<String, MutableList<Comment>> =
        ConcurrentHashMap()

    fun addComment(newsId: String, comment: Comment): Comment {
        val list = comments.getOrPut(newsId) { mutableListOf() }
        list += comment
        return comment
    }

    fun getComments(newsId: String): List<Comment> =
        comments[newsId]?.toList() ?: emptyList()

    private val likes: MutableMap<String, MutableSet<String>> =
        ConcurrentHashMap()

    fun toggleLike(newsId: String, userId: String): Boolean {
        val set = likes.getOrPut(newsId) { mutableSetOf() }
        return if (set.contains(userId)) {
            set -= userId
            false
        } else {
            set += userId
            true
        }
    }

    fun getLikes(newsId: String): Int = likes[newsId]?.size ?: 0

    fun getUserLikeStatus(newsId: String, userId: String): Boolean {
        return likes[newsId]?.contains(userId) ?: false
    }

    fun toggleCommentLike(commentId: String, userId: String): Boolean {
        val set = commentLikes.getOrPut(commentId) { mutableSetOf() }
        return if (set.contains(userId)) {
            set.remove(userId)
            false
        } else {
            set.add(userId)
            true
        }
    }

    fun getCommentLikeCount(commentId: String): Int {
        return commentLikes[commentId]?.size ?: 0
    }

    fun getUserCommentLikeStatus(commentId: String, userId: String): Boolean {
        return commentLikes[commentId]?.contains(userId) ?: false
    }
}