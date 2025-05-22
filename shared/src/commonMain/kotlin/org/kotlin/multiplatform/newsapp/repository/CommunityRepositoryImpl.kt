package org.kotlin.multiplatform.newsapp.repository

import org.kotlin.multiplatform.newsapp.model.Community
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
        communities.removeIf { it.id == id }

}