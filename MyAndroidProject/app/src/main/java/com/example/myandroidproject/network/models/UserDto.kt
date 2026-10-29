package com.example.myandroidproject.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long? = null,
    val email: String? = null,
    @SerialName("auth_provider") val authProvider: String? = null,
    val profile: ProfileDto? = null
)