package com.example.myandroidproject.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class ScheduleItem(
    val title: String,
    val startTime: String,
    val status: String
)

private val sampleSchedules = listOf(
    ScheduleItem("Morning workout", "08:00", "Completed"),
    ScheduleItem("Study Kotlin", "10:00", "Planned"),
    ScheduleItem("University assignments", "13:30", "Planned"),
    ScheduleItem("Evening walk", "18:00", "Skipped")
).sortedBy { it.startTime }

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAddScheduleClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val today = SimpleDateFormat(
        "EEEE, d MMMM yyyy",
        Locale.getDefault()
    ).format(Date())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // ── Fejléc sor: köszönés + Logout gomb ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Good day!",
                style = MaterialTheme.typography.headlineMedium
            )

            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = today,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's schedule",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "${sampleSchedules.size} activities",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (sampleSchedules.isEmpty()) {
            Text(
                text = "No activities scheduled for today.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            sampleSchedules.forEach { schedule ->
                ScheduleCard(schedule = schedule)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onAddScheduleClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Schedule")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { /* Placeholder for future functionality */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View all schedules")
        }
    }
}

@Composable
private fun ScheduleCard(schedule: ScheduleItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = schedule.startTime,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = schedule.status,
                style = MaterialTheme.typography.labelMedium,
                color = when (schedule.status) {
                    "Completed" -> MaterialTheme.colorScheme.primary
                    "Skipped" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}