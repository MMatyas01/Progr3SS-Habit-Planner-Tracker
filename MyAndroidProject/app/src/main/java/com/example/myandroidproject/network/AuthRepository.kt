package com.example.myandroidproject.network

import com.example.myandroidproject.network.models.ApiError
import com.example.myandroidproject.network.models.LoginRequest
import com.example.myandroidproject.network.models.LoginResponse
import kotlinx.serialization.decodeFromString
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class AuthRepository {
    private val api = RetrofitClient.authApi

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return runCatching {
            val response = api.signIn(LoginRequest(email, password))
            handleResponse(response)
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        profileImage: MultipartBody.Part? = null
    ): Result<LoginResponse> {
        return runCatching {
            val jsonType = "text/plain".toMediaTypeOrNull()
            val usernamePart = username.toRequestBody(jsonType)
            val emailPart = email.toRequestBody(jsonType)
            val passwordPart = password.toRequestBody(jsonType)

            val response = api.signUp(usernamePart, emailPart, passwordPart, profileImage)
            handleResponse(response)
        }
    }

    private fun handleResponse(response: Response<LoginResponse>): LoginResponse {
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            val errorMsg = parseErrorMessage(response, "Login failed")
            throw Exception(errorMsg)
        }
    }

    private fun parseErrorMessage(response: Response<*>, fallback: String): String {
        val raw = response.errorBody()?.string()
        if (!raw.isNullOrBlank()) {
            val parsed = try {
                RetrofitClient.json.decodeFromString<ApiError>(raw)
            } catch (e: Exception) {
                null
            }
            if (parsed?.message != null) {
                return parsed.message
            }
        }
        val message = response.message()
        return if (message.isBlank()) {
            "$fallback (${response.code()})"
        } else {
            message
        }
    }
}

