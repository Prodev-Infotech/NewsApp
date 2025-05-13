package org.kotlin.multiplatform.newsapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.launch
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.network.KtorfitServiceCreator
import org.kotlin.multiplatform.newsapp.utils.BookmarksUtil
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.baseUrl

class NewsViewmodel: ViewModel(){
    val bookmarksUtil = BookmarksUtil() // Create the BookmarksUtil instance

    private val _newsState = mutableStateOf<ResultState<List<NewsPost>>>(ResultState.Initial)
    val newsState: State<ResultState<List<NewsPost>>> get() = _newsState

    private val _newsDetailState = mutableStateOf<ResultState<NewsPost>>(ResultState.Initial)
    val newsDetailState: State<ResultState<NewsPost>> get() = _newsDetailState

//    private val _signUpState = mutableStateOf<ResultState<User>>(ResultState.Initial)
//    val signUpState: State<ResultState<User>> get() = _signUpState

    private val _bookmarks = mutableStateOf<Set<String>>(emptySet())
    val bookmarks: State<Set<String>> = _bookmarks

    private val _newsList = mutableStateOf<List<NewsPost>>(emptyList())
    val newsList: State<List<NewsPost>> = _newsList

    val ktorfitService = KtorfitServiceCreator(baseUrl)

    init {
        fetchNews()
        loadBookmarks()
    }

    fun loadBookmarks() {
        _bookmarks.value = bookmarksUtil.getBookmarks().toSet()
    }
    // Function to fetch news
    fun fetchNews() {
        _newsState.value = ResultState.Loading // Start loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getNews() // API call to get news
                if(response.status){
                    _newsState.value = ResultState.Success(response.data.reversed())
                }
                else{
                    _newsState.value = ResultState.Error(response.message.toString())
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
                    _newsDetailState.value = ResultState.Error(response.message.toString())
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
            _bookmarks.value = _bookmarks.value + news.id
        } else {
            _bookmarks.value = _bookmarks.value - news.id
        }
    }
    fun fetchBookMarkNews() {
        viewModelScope.launch {
            try {
                val bookmarks = bookmarksUtil.getBookmarks()
                println(" Bookmarks from SharedPrefs: $bookmarks")

                val response = ktorfitService.api.getNews()

                // Assuming you're using Ktorfit and response is a custom object
                if (response.data != null) {
                    val filtered = response.data.filter { it.id in bookmarks }
                    println(" Filtered Bookmarked News: $filtered")

                    _newsState.value = ResultState.Success(filtered)
                } else {
                    println(" Empty response data")
                    _newsState.value = ResultState.Error("No data found")
                }
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

}