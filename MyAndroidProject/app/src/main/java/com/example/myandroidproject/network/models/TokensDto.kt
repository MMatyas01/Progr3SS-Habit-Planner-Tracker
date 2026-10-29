package com.example.myandroidproject.network.models

import kotlinx.serialization.Serializable

@Serializable
data class TokensDto(
    val accessToken: String? = null,
    val refreshToken: String? = null
)