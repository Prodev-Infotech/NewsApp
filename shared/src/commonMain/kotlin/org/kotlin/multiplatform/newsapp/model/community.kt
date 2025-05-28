package org.kotlin.multiplatform.newsapp.model

import kotlinx.serialization.SerialName
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
@Serializable
data class CommunityByIdResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)



@Serializable
data class JoinLeaveRequest(val user:User, val communityId: String)

@Serializable
data class JoinApproveRequest(val userId:String, val communityId: String)
@Serializable
data class CommunityWithJoinStatus(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val authorName: String,
    val createdAt: String,
    val isPrivate: Boolean,
    val joinStatus: String,
)

@Serializable
data class BaseResponse(
    @SerialName("success") val status: Boolean = false,
    val message: String = ""
)

@Serializable
data class JoinRequest(
    val id: String,
    val userId: String,
    val communityId: String,
    val userName: String,
    val communityName: String,
    val joinStatus: String, // "pending", "approved"
    val requestedAt: String,
    val profileImageUrl:String? = null
)

@Serializable
data class JoinRequestResponse(
    val success: Boolean,
    val message: String,
    val requests: List<JoinRequest> = emptyList()
)