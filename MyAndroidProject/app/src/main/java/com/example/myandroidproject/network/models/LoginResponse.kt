package com.example.myandroidproject.network.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val message: String? = null,
    val user: UserDto? = null,
    val tokens: TokensDto? = null
) {
    val accessToken: String? get() = tokens?.accessToken
    val refreshToken: String? get() = tokens?.refreshToken
}