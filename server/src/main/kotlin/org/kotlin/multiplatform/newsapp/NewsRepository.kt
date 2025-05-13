package org.kotlin.multiplatform.newsapp

import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.User

class  NewsRepository {
    private val newsList = mutableListOf<NewsPost>()
    private val users = mutableListOf<User>() // or a map of userId to User

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

}