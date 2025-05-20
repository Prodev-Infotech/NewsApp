package org.kotlin.multiplatform.newsapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.io.IOException
import kotlinx.coroutines.launch
import org.kotlin.multiplatform.newsapp.model.Comment
import org.kotlin.multiplatform.newsapp.model.CommentRequest
import org.kotlin.multiplatform.newsapp.model.LikeRequest
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.network.KtorfitServiceCreator
import org.kotlin.multiplatform.newsapp.utils.BookmarksUtil
import org.kotlin.multiplatform.newsapp.utils.baseUrl

class NewsViewmodel: ViewModel(){
    private val bookmarksUtil = BookmarksUtil()

    private val _newsState = mutableStateOf<ResultState<List<NewsPost>>>(ResultState.Initial)
    val newsState: State<ResultState<List<NewsPost>>> get() = _newsState

    private val _newsDetailState = mutableStateOf<ResultState<NewsPost>>(ResultState.Initial)
    val newsDetailState: State<ResultState<NewsPost>> get() = _newsDetailState

    private val _bookmarks = mutableStateOf<Set<String>>(emptySet())
    private val bookmarks: State<Set<String>> = _bookmarks

    private val _newsList = mutableStateOf<List<NewsPost>>(emptyList())
    val newsList: State<List<NewsPost>> = _newsList

    private val ktorfitService = KtorfitServiceCreator(baseUrl)

    init {
        fetchNews()
        loadBookmarks()
    }

    private fun loadBookmarks() {
        _bookmarks.value = bookmarksUtil.getBookmarks().toSet()
    }
    // Function to fetch news
    private fun fetchNews() {
        _newsState.value = ResultState.Loading // Start loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getNews() // API call to get news
                if(response.status){
                    _newsState.value = ResultState.Success(response.data.reversed())
                }
                else{
                    _newsState.value = ResultState.Error(response.message)
                }

            }  catch (e: IOException) {
                _newsState.value = ResultState.Error(e.message.toString()) // Handle network errors
            }
        }
    }

    fun getNewsById(newsId: String) {
        _newsDetailState.value = ResultState.Loading // Start loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getNewsById(newsId) // API call to get news
                if(response.status){
                    val news =response.data
                    if(news !=null){
//                        _newsDetailState.value = ResultState.Success(news)
                        _newsDetailState.value = ResultState.Success(news[0]) // or show list
                        println("News Details:-->${news[0]}")

                    }
                    else{
                        _newsDetailState.value = ResultState.Error("News data is null")
                    }
                }
                else{
                    _newsDetailState.value = ResultState.Error(response.message)
                }

            }  catch (e: IOException) {
                _newsDetailState.value = ResultState.Error(e.message.toString()) // Handle network errors
            }
        }
    }


    fun toggleBookmark(news: NewsPost) {
        val isBookmarked = news.id in bookmarksUtil.getBookmarks()

        bookmarksUtil.toggleBookmark(news.id, isBookmarked)
        news.isBookMark = !isBookmarked

        _newsList.value = _newsList.value.map {
            if (it.id == news.id) it.copy(isBookMark = news.isBookMark) else it
        }

        if (news.isBookMark) {
            _bookmarks.value += news.id
        } else {
            _bookmarks.value -= news.id
        }
    }
    fun fetchBookMarkNews() {
        viewModelScope.launch {
            try {
                val bookmarks = bookmarksUtil.getBookmarks()
                println(" Bookmarks from SharedPrefs: $bookmarks")

                val response = ktorfitService.api.getNews()

                // Assuming you're using Ktorfit and response is a custom object
                val filtered = response.data.filter { it.id in bookmarks }
                println(" Filtered Bookmarked News: $filtered")

                _newsState.value = ResultState.Success(filtered.reversed())
            } catch (e: Exception) {
                println(" Exception: ${e.message}")
                _newsState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun isBookmarked(newsId: String): Boolean {
        println("BookmarkCheck Checking if News ID $newsId is bookmarked")
        val isBookmarked = bookmarks.value.contains(newsId)
        println("BookmarkCheck Is  Bookmarked: $isBookmarked")
        return isBookmarked
    }

    private val _commentsStates = mutableStateMapOf<String, ResultState<List<Comment>>>()
    val commentsStates: Map<String, ResultState<List<Comment>>> get() = _commentsStates

    private val _likeStates = mutableStateMapOf<String, ResultState<Boolean>>()
    val likeStates: Map<String, ResultState<Boolean>> get() = _likeStates

    private val _likeCountStates = mutableStateMapOf<String, ResultState<Int>>()
    val likeCountStates: Map<String, ResultState<Int>> get() = _likeCountStates

    fun getComments(newsId: String) {
        _commentsStates[newsId] = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getComments(newsId)
                if (response.status) {
                    _commentsStates[newsId] = ResultState.Success(response.data)
                } else {
                    _commentsStates[newsId] = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _commentsStates[newsId] = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun postComment(newsId: String, userId: String, content: String,userName:String) {
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.postComment(newsId, CommentRequest(userId, content,userName))
                if (response.status) {
                    getComments(newsId)
                }
            } catch (e: Exception) {
                println("Post comment error: ${e.message}")
            }
        }
    }

    fun toggleLike(newsId: String, userId: String) {
        _likeStates[newsId] = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.toggleLike(newsId, LikeRequest(userId))
                if (response.status) {
                    _likeStates[newsId] = ResultState.Success(response.data.first().liked)
                    getLikeCount(newsId)
                } else {
                    _likeStates[newsId] = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _likeStates[newsId] = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getLikeCount(newsId: String) {
        _likeCountStates[newsId] = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getLikeCount(newsId)
                if (response.status) {
                    _likeCountStates[newsId] = ResultState.Success(response.data.first().count)
                } else {
                    _likeCountStates[newsId] = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _likeCountStates[newsId] = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getUserLikeStatus(newsId: String, userId: String) {
        _likeStates[newsId] = ResultState.Loading
        println("idLiked Loading stat3---> ${ResultState.Loading}")
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getUserLikeStatus(newsId, userId) // Adjust API if needed
                if (response.status) {
                    _likeStates[newsId] = ResultState.Success(response.data.first().liked)
                    println("idLiked---> ${response.data}")
                } else {
                    _likeStates[newsId] = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _likeStates[newsId] = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private val _commentLikeStates = mutableStateMapOf<String, ResultState<Boolean>>()
    val commentLikeStates: Map<String, ResultState<Boolean>> get() = _commentLikeStates

    private val _commentLikeCountStates = mutableStateMapOf<String, ResultState<Int>>()
    val commentLikeCountStates: Map<String, ResultState<Int>> get() = _commentLikeCountStates

    fun getCommentLikeStatus(commentId: String, userId: String) {
        _commentLikeStates[commentId] = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getUserCommentLikeStatus(commentId, userId)
                if (response.status) {
                    val likeStatus = response.data.firstOrNull() // This should give you the first LikeStatusResponse
                    if (likeStatus != null) {
                        _commentLikeStates[commentId] = ResultState.Success(likeStatus.liked)
                    } else {
                        _commentLikeStates[commentId] = ResultState.Error("No like status available")
                    }
                } else {
                    _commentLikeStates[commentId] = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _commentLikeStates[commentId] = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleCommentLike(commentId: String, userId: String) {
        _commentLikeStates[commentId] = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.toggleCommentLike(commentId, LikeRequest(userId))
                if (response.status) {
                    _commentLikeStates[commentId] = ResultState.Success(response.data.first().liked)
                    getCommentLikeCount(commentId)
                } else {
                    _commentLikeStates[commentId] = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _commentLikeStates[commentId] = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getCommentLikeCount(commentId: String) {
        _commentLikeCountStates[commentId] = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getCommentLikeCount(commentId)
                if (response.status) {
                    _commentLikeCountStates[commentId] = ResultState.Success(response.data.first().count)
                } else {
                    _commentLikeCountStates[commentId] = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _commentLikeCountStates[commentId] = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

}