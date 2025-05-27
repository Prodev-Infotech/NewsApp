package org.kotlin.multiplatform.newsapp.repository

import org.kotlin.multiplatform.newsapp.model.Community
import org.kotlin.multiplatform.newsapp.model.JoinRequest
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.utils.generateId
import org.kotlin.multiplatform.newsapp.utils.getCurrentFormattedDate

class CommunityRepositoryImpl : CommunityRepository {
    private val communities = mutableListOf<Community>()

    override suspend fun addCommunity(community: Community): Community {
        val newCommunity = community.copy(id = generateId(), createdAt = getCurrentFormattedDate())
        communities.add(newCommunity)
        return newCommunity
    }

    override suspend fun getAllCommunities(): List<Community> = communities

    override suspend fun getCommunityById(id: String): Community? =
        communities.find { it.id == id }

    override suspend fun updateCommunity(id: String, updated: Community): Boolean {
        val index = communities.indexOfFirst { it.id == id }
        return if (index != -1) {
            communities[index] = updated
            true
        } else false
    }

    override suspend fun deleteCommunity(id: String): Boolean =
        communities.removeAll  { it.id == id }



    private val joinRequests = mutableListOf<JoinRequest>()
    private val communityMembers = mutableSetOf<Pair<String, String>>() // userId to communityId
override suspend fun joinCommunity(user: User, communityId: String): Boolean {
    val community = communities.find { it.id == communityId } ?: return false

    val alreadyJoined = communityMembers.contains(user.id to communityId)
    val alreadyRequested = joinRequests.any {
        it.userId == user.id && it.communityId == communityId && it.joinStatus == "pending"
    }

    if (!alreadyRequested && !alreadyJoined) {
        joinRequests.add(
            JoinRequest(
                id = generateId(),
                userId = user.id,
                communityId = communityId,
                userName = user.name,
                communityName = community.name,
                joinStatus = "pending",
                requestedAt = getCurrentFormattedDate(),
                profileImageUrl = user.profileImageUrl
            )
        )
        return true
    }

    return false
}

    override suspend fun leaveCommunity(user: User, communityId: String): Boolean {
        return communityMembers.remove(user.id to communityId)
    }

    override suspend fun getJoinedCommunityIdsForUser(userId: String): List<String> {
        return communityMembers.filter { it.first == userId }.map { it.second }
    }

    override suspend fun getRequestedCommunityIdsForUser(userId: String): List<String> {
        return joinRequests
            .filter { it.userId == userId && it.joinStatus == "pending" }
            .map { it.communityId }
    }

    override suspend fun rejectRequest(userId: String, communityId: String): Boolean {
        val request = joinRequests.find {
            it.userId == userId && it.communityId == communityId && it.joinStatus == "pending"
        } ?: return false
        joinRequests.remove(request)
        return true
    }


    override suspend fun approveRequest(userId: String, communityId: String): Boolean {
        val request = joinRequests.find {
            it.userId == userId && it.communityId == communityId && it.joinStatus == "pending"
        } ?: return false

        joinRequests.remove(request)
        joinRequests.add(request.copy(joinStatus = "approved"))
        communityMembers.add(userId to communityId)
        return true
    }
    override suspend fun getAllRequestsForCommunity(communityId: String): List<JoinRequest> {
        return joinRequests.filter { it.communityId == communityId }
    }

}