package org.kotlin.multiplatform.newsapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import org.kotlin.multiplatform.newsapp.model.BaseResponse
import org.kotlin.multiplatform.newsapp.model.Community
import org.kotlin.multiplatform.newsapp.model.CommunityWithJoinStatus
import org.kotlin.multiplatform.newsapp.model.CreateCommunityRequest
import org.kotlin.multiplatform.newsapp.model.JoinLeaveRequest
import org.kotlin.multiplatform.newsapp.model.JoinRequest
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
        println("Creating KtorFitServiceCreator with baseUrl: ${baseUrl}")
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
                println("Calling getAllCommunitiesWithJoinStatus with userId: $userId")
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


    fun joinCommunity(user: User, communityId: String) {
        _joinCommunityState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val request = JoinLeaveRequest(user, communityId)
                val response = ktorfitService.api.joinCommunity(request)
                if (response.success) {
                    _joinCommunityState.value = ResultState.Success(response)
                    println("Joined community successfully: ${response.message}")
                    fetchCommunities(SessionUtil.getUserId().toString())
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
                if (response.success) {
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
}