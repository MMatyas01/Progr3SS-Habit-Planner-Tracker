package com.example.myandroidproject.network

import com.example.myandroidproject.network.models.CreateHabitRequest
import com.example.myandroidproject.network.models.HabitCategory
import com.example.myandroidproject.network.models.HabitResponse

class HabitRepository(private val api: ApiService = RetrofitClient.apiService) {
    suspend fun getCategories(): Result<List<HabitCategory>> = runCatching {
        api.getHabitCategories().bodyOrThrow("Failed to load habit categories")
    }
    suspend fun getHabits(): Result<List<HabitResponse>> = runCatching {
        api.getHabits().bodyOrThrow("Failed to load habits")
    }
    suspend fun createHabit(name: String, description: String, categoryId: Long, goal: String): Result<HabitResponse> = runCatching {
        api.createHabit(CreateHabitRequest(name, description.ifBlank { null }, categoryId, goal))
            .bodyOrThrow("Failed to create habit")
    }
}