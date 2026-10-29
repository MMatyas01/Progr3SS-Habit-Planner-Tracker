package com.example.myandroidproject.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HabitCategory(
    val id: Long,
    val name: String,
    val iconUrl: String? = null
)

@Serializable
data class HabitResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val category: HabitCategory,
    val goal: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class CreateHabitRequest(
    val name: String,
    val description: String? = null,
    val categoryId: Long,
    val goal: String
)

@Serializable
data class ParticipantDto(
    val id: Long,
    val name: String? = null,
    val email: String? = null,
    @SerialName("profile_image") val profileImage: String? = null
)

@Serializable
data class ProgressResponseDto(
    val id: Long,
    @SerialName("scheduleId") val scheduleId: Long? = null,
    val date: String? = null,
    @SerialName("logged_time") val loggedTime: Double? = null,
    val notes: String? = null,
    @SerialName("is_completed") val isCompleted: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class ScheduleResponse(
    val id: Long,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    val status: String? = null,
    val date: String? = null,
    @SerialName("is_custom") val isCustom: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val type: String? = null,
    @SerialName("duration_minutes") val durationMinutes: Int? = null,
    val notes: String? = null,
    val participants: List<ParticipantDto> = emptyList(),
    val habit: HabitResponse? = null,
    val progress: List<ProgressResponseDto> = emptyList(),
    @SerialName("is_participant_only") val isParticipantOnly: Boolean = false
)

@Serializable
data class CreateRecurringScheduleRequest(
    val habitId: Long,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String? = null,
    @SerialName("duration_minutes") val durationMinutes: Int? = null,
    val repeatPattern: String,
    val repeatDays: Int = 30,
    @SerialName("is_custom") val isCustom: Boolean = true,
    val participantIds: List<Long> = emptyList(),
    val notes: String? = null
)

@Serializable
data class CreateCustomScheduleRequest(
    val habitId: Long,
    val date: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String? = null,
    @SerialName("duration_minutes") val durationMinutes: Int? = null,
    @SerialName("is_custom") val isCustom: Boolean = true,
    val participantIds: List<Long> = emptyList(),
    val notes: String? = null
)