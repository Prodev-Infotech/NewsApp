package org.kotlin.multiplatform.newsapp.model

import kotlinx.serialization.Serializable
import org.kotlin.multiplatform.newsapp.utils.generateId
import org.kotlin.multiplatform.newsapp.utils.getCurrentFormattedDate

@Serializable
data class Community(
    val id: String = generateId(),
    val name: String,
    val description: String,
    val authorName:String,
    val imageUrl: String?,
    val isPrivate: Boolean, //  Add this field
    val createdAt: String = getCurrentFormattedDate()
)
@Serializable
data class CreateCommunityRequest(
    val name: String,
    val description: String,
    val authorName:String,
    val imageUrl: String? = null,
    val isPrivate: Boolean
)
@Serializable
data class UpdateCommunityRequest(
    val name: String,
    val description: String,
    val authorName:String,
    val imageUrl: String?,
    val isPrivate: Boolean
)
@Serializable
data class CommunityResponse<T>(
    val status: Boolean,
    val message: String,
    val data: List<T> = emptyList()
)