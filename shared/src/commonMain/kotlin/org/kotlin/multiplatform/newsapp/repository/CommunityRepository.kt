package org.kotlin.multiplatform.newsapp.repository

import org.kotlin.multiplatform.newsapp.model.Community

interface CommunityRepository {
    suspend fun addCommunity(community: Community): Community
    suspend fun getAllCommunities(): List<Community>
    suspend fun getCommunityById(id: String): Community?
    suspend fun updateCommunity(id: String, updated: Community): Boolean
    suspend fun deleteCommunity(id: String): Boolean
}