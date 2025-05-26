package org.kotlin.multiplatform.newsapp.repository

import org.kotlin.multiplatform.newsapp.model.Community
import org.kotlin.multiplatform.newsapp.model.JoinRequest
import org.kotlin.multiplatform.newsapp.model.User

interface CommunityRepository {
    suspend fun addCommunity(community: Community): Community
    suspend fun getAllCommunities(): List<Community>
    suspend fun getCommunityById(id: String): Community?
    suspend fun updateCommunity(id: String, updated: Community): Boolean
    suspend fun deleteCommunity(id: String): Boolean

    suspend fun joinCommunity(user: User, communityId: String): Boolean
    suspend fun leaveCommunity(user: User, communityId: String): Boolean
    suspend fun getJoinedCommunityIdsForUser(userId: String): List<String>
    suspend fun getRequestedCommunityIdsForUser(userId: String): List<String>
    suspend fun getPendingRequests(): List<JoinRequest>
    suspend fun approveRequest(userId: String, communityId: String): Boolean
    suspend fun getPendingRequestsForCommunity(communityId: String): List<JoinRequest>
    suspend fun rejectRequest(user: User, communityId: String): Boolean
}