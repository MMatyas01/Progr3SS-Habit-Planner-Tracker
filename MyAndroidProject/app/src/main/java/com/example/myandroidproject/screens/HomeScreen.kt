package com.example.myandroidproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myandroidproject.network.models.HabitResponse
import com.example.myandroidproject.network.models.ScheduleResponse
import com.example.myandroidproject.utils.DateTimeUtils
import com.example.myandroidproject.viewmodel.HomeViewModel
import java.util.*

@Composable
fun HomeScreen(
    onAddHabit: () -> Unit,
    onCreateSchedule: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadToday()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Today", style = MaterialTheme.typography.headlineLarge)
            TextButton(onClick = onLogout) { Text("Logout", color = MaterialTheme.colorScheme.error) }
        }
        Text(uiState.today, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAddHabit, modifier = Modifier.weight(1f)) { Text("Add habit") }
            Button(onClick = onCreateSchedule, modifier = Modifier.weight(1f)) { Text("New schedule") }
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            uiState.error?.let {
                item { Text("Couldn't load your data: $it", color = MaterialTheme.colorScheme.error) }
            }

            item { Text("Today's schedule", style = MaterialTheme.typography.titleLarge) }
            if (uiState.schedules.isEmpty()) {
                item { Text("Nothing scheduled today", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                // JAVÍTÁS: "schedule_" előtag hozzáadva a kulcshoz
                items(uiState.schedules, key = { "schedule_${it.id}" }) { schedule ->
                    ScheduleCard(schedule)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            item { Text("Habits", style = MaterialTheme.typography.titleLarge) }
            if (uiState.habits.isEmpty()) {
                item { Text("No habits yet", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                // JAVÍTÁS: "habit_" előtag hozzáadva a kulcshoz
                items(uiState.habits, key = { "habit_${it.id}" }) { habit ->
                    HabitCard(habit)
                }
            }
        }
    }
}

@Composable
private fun ScheduleCard(schedule: ScheduleResponse) {
    val categoryName = schedule.habit?.category?.name ?: "Other"
    val startTime = DateTimeUtils.formatTime(schedule.startTime).ifBlank { "---" }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(Modifier.width(60.dp)) {
                Text(startTime, style = MaterialTheme.typography.titleMedium)
                Text("${schedule.durationMinutes ?: 0} min", style = MaterialTheme.typography.bodySmall)
            }
            Column(Modifier.weight(1f)) {
                Text(schedule.habit?.name ?: "Unknown habit", style = MaterialTheme.typography.titleMedium)
                Text(categoryName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                schedule.notes?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun HabitCard(habit: HabitResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(habit.name, style = MaterialTheme.typography.titleMedium)
            Text(habit.category.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text(habit.goal, style = MaterialTheme.typography.bodyMedium)
        }
    }
}