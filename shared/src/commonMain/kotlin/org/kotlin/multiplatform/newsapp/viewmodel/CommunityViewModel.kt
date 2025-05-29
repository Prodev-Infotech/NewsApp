package org.kotlin.multiplatform.newsapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.launch
import org.kotlin.multiplatform.newsapp.model.BaseResponse
import org.kotlin.multiplatform.newsapp.model.Community
import org.kotlin.multiplatform.newsapp.model.CommunityWithJoinStatus
import org.kotlin.multiplatform.newsapp.model.CreatePostRequest
import org.kotlin.multiplatform.newsapp.model.JoinLeaveRequest
import org.kotlin.multiplatform.newsapp.model.JoinRequest
import org.kotlin.multiplatform.newsapp.model.Post
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.network.KtorfitServiceCreator
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.baseUrl

class CommunityViewModel : ViewModel() {
    private val _communityState = mutableStateOf<ResultState<List<CommunityWithJoinStatus>>>(ResultState.Initial)
    val communityState: State<ResultState<List<CommunityWithJoinStatus>>> get() = _communityState

    private val _communityPostState = mutableStateOf<ResultState<Community>>(ResultState.Initial)
    val communityPostState: State<ResultState<Community>> get() = _communityPostState

    private val _joinLeaveState = mutableStateOf<ResultState<BaseResponse>>(ResultState.Initial)
    val joinLeaveState: State<ResultState<BaseResponse>> get() = _joinLeaveState
    private val _joinCommunityState = mutableStateOf<ResultState<BaseResponse>>(ResultState.Initial)
    val joinCommunityState: State<ResultState<BaseResponse>> get() = _joinCommunityState
    private val _pendingRequestsState = mutableStateOf<ResultState<List<JoinRequest>>>(ResultState.Initial)
    val pendingRequestsState: State<ResultState<List<JoinRequest>>> get() = _pendingRequestsState

    private val ktorfitService: KtorfitServiceCreator by lazy {
        KtorfitServiceCreator(baseUrl)
    }
    init {
        fetchCommunities(SessionUtil.getUserId().toString())
    }

    fun fetchCommunities(userId:String) {
            if (userId == null) {
                _communityState.value = ResultState.Error("Missing user ID")
                return
            }

        _communityState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getAllCommunitiesWithJoinStatus(userId)
                if (response.status) {
                    _communityState.value = ResultState.Success(response.data.reversed())
                } else {
                    _communityState.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _communityState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }
    private val _singleCommunityState = mutableStateOf<ResultState<CommunityWithJoinStatus>>(ResultState.Initial)
    val singleCommunityState: State<ResultState<CommunityWithJoinStatus>> get() = _singleCommunityState
    fun fetchSingleCommunity( communityId: String,userId: String) {
        println("fetchSingleCommunity...userId:-$userId  and communityId:->$communityId")
        if (userId.isBlank() || communityId.isBlank()) {
            _singleCommunityState.value = ResultState.Error("Missing user ID or community ID")
            return
        }

        _singleCommunityState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getSingleCommunityWithJoinStatus(userId, communityId)
                println("fetchSingleCommunity response:->$response")
                if (response.success && response.data != null) {
                    _singleCommunityState.value = ResultState.Success(response.data)
                } else {
                    _singleCommunityState.value = ResultState.Error(response.message)
                    println("fetchSingleCommunity response else:->${response.message}")
                }
            } catch (e: Exception) {
                _singleCommunityState.value = ResultState.Error(e.message ?: "Unknown error")
                println("fetchSingleCommunity response error:->${e.message}")
            }
        }
    }

    fun joinCommunity(user: User, communityId: String) {
        _joinCommunityState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val request = JoinLeaveRequest(user, communityId)
                val response = ktorfitService.api.joinCommunity(request)
                if (response.status) {
                    _joinCommunityState.value = ResultState.Success(response)
                    println("Joined community successfully: ${response.message}")
                    fetchCommunities(SessionUtil.getUserId().toString())
                    val currentState = _singleCommunityState.value
                    if (currentState is ResultState.Success) {
                        val updatedCommunity = currentState.data.copy(joinStatus = "Requested") // or whatever the API returns
                        _singleCommunityState.value = ResultState.Success(updatedCommunity)
                    }

                } else {
                    _joinCommunityState.value = ResultState.Error(response.message)
                    println("Failed to join community: ${response.message}")
                }
            } catch (e: Exception) {
                _joinCommunityState.value = ResultState.Error(e.message ?: "Unknown error")
                println("Error joining community: ${e.message}")
            }
        }
    }

    fun leaveCommunity(user: User, communityId: String) {
        _joinLeaveState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ktorfitService.api.leaveCommunity(JoinLeaveRequest(user, communityId))
                if (response.status) {
                    _joinLeaveState.value = ResultState.Success(response)
                } else {
                    _joinLeaveState.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _joinLeaveState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

//    private val _communityState = mutableStateOf<ResultState<Community>>(ResultState.Initial)
//    val communityState: State<ResultState<Community>> get() = _communityState
//
//
//    fun getCommunityById(id: String) {
//        _communityState.value = ResultState.Loading
//
//        viewModelScope.launch {
//            try {
//                val response = communityApi.getCommunityById(id)
//                if (response.status && response.data.isNotEmpty()) {
//                    _communityState.value = ResultState.Success(response.data.first())
//                } else {
//                    _communityState.value = ResultState.Error(response.message)
//                }
//            } catch (e: Exception) {
//                _communityState.value = ResultState.Error(e.localizedMessage ?: "Unknown error")
//            }
//        }
//    }

    private val _uploadState = mutableStateOf<ResultState<String>>(ResultState.Initial)
    val uploadState: State<ResultState<String>> get() = _uploadState

    fun uploadImage(imageBytes: ByteArray, imageName: String) {
        viewModelScope.launch {
            _uploadState.value = ResultState.Loading

            val multipartData = MultiPartFormDataContent(
                formData {
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"image\"; filename=\"$imageName\"")
                    })
                }
            )

            try {
                val response = ktorfitService.api.uploadImage(multipartData)
                _uploadState.value = ResultState.Success(response.data ?: "")
            } catch (e: Exception) {
                _uploadState.value = ResultState.Error("Upload failed: ${e.message}")
            }
        }
    }
    private val _downloadState = mutableStateOf<ResultState<String>>(ResultState.Initial)
    val downloadState: State<ResultState<String>> get() = _downloadState

    fun downloadImage(filename: String) {
        viewModelScope.launch {
            _downloadState.value = ResultState.Loading
            try {
                val response = ktorfitService.api.getImage(filename)
                _downloadState.value = ResultState.Success(response.data ?: "")
                println("Image URL: ${response.data}")
            } catch (e: Exception) {
                _downloadState.value = ResultState.Error("Download failed: ${e.message}")
                println("Download failed: ${e.message}")
            }
        }
    }

    private val _createPostState = mutableStateOf<ResultState<Post>>(ResultState.Initial)
    val createPostState: State<ResultState<Post>> get() = _createPostState

    fun createPost(request: CreatePostRequest) {
        _createPostState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.createPost(request)
                if (response.success && response.data != null) {
                    _createPostState.value = ResultState.Success(response.data)
                } else {
                    _createPostState.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _createPostState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }
    private val _communityPosts = mutableStateOf<ResultState<List<Post>>>(ResultState.Initial)
    val communityPosts: State<ResultState<List<Post>>> = _communityPosts

    fun fetchCommunityPosts(communityId: String) {
        viewModelScope.launch {
            _communityPosts.value = ResultState.Loading
            try {
                val response = ktorfitService.api.getPostsByCommunity(communityId)
                if (response.success && response.data != null) {
                    _communityPosts.value = ResultState.Success(response.data.reversed())
                } else {
                    _communityPosts.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _communityPosts.value = ResultState.Error(e.message?: "Unknown error")
            }
        }
    }
}