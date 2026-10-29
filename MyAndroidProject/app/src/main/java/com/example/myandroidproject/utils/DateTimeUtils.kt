package com.example.myandroidproject.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    private val isoUtcFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormatter = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun todayDate(): String = dateFormatter.format(Date())
    fun currentTime(): String = timeFormatter.format(Date())

    fun formatDisplayDate(date: String): String {
        val parsed = runCatching { dateFormatter.parse(date) }.getOrNull() ?: return date
        return displayDateFormatter.format(parsed)
    }

    fun formatScheduleTime(scheduleStart: String?, scheduleEnd: String?, durationMinutes: Int?): String {
        val start = parseIso(scheduleStart) ?: return "Time not set"
        val end = parseIso(scheduleEnd) ?: durationMinutes?.let { minutes ->
            Calendar.getInstance().apply {
                time = start
                add(Calendar.MINUTE, minutes)
            }.time
        }
        val startText = timeFormatter.format(start)
        val endText = end?.let { timeFormatter.format(it) }
        return if (endText != null) "$startText - $endText" else startText
    }

    fun formatTime(iso: String?): String {
        val parsed = parseIso(iso) ?: return ""
        return timeFormatter.format(parsed)
    }

    fun buildIsoDateTime(date: String, time: String): String {
        val localFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        val selected = localFormatter.parse("$date $time") ?: Date()
        return isoUtcFormatter.format(selected)
    }

    fun addMinutesToIso(iso: String, minutes: Int): String? {
        val start = parseIso(iso) ?: return null
        val end = Calendar.getInstance().apply {
            time = start
            add(Calendar.MINUTE, minutes)
        }
        return isoUtcFormatter.format(end.time)
    }

    private fun parseIso(value: String?): Date? {
        if (value.isNullOrBlank()) return null
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )
        for (pattern in patterns) {
            val parsed = runCatching {
                SimpleDateFormat(pattern, Locale.US).apply {
                    if (pattern.endsWith("'Z'")) timeZone = TimeZone.getTimeZone("UTC")
                }.parse(value)
            }.getOrNull()
            if (parsed != null) return parsed
        }
        return null
    }
}