package com.example.myandroidproject.network

import com.example.myandroidproject.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("habit/categories")
    suspend fun getHabitCategories(): Response<List<HabitCategory>>

    @GET("habit")
    suspend fun getHabits(): Response<List<HabitResponse>>

    @POST("habit")
    suspend fun createHabit(@Body body: CreateHabitRequest): Response<HabitResponse>

    @GET("schedule/day")
    suspend fun getScheduleByDay(@Query("date") date: String): Response<List<ScheduleResponse>>

    @POST("schedule/recurring")
    suspend fun createRecurringSchedule(@Body body: CreateRecurringScheduleRequest): Response<List<ScheduleResponse>>

    @POST("schedule/custom")
    suspend fun createCustomSchedule(@Body body: CreateCustomScheduleRequest): Response<ScheduleResponse>
}