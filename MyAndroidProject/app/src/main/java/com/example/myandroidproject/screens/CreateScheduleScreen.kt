package com.example.myandroidproject.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myandroidproject.utils.DateTimeUtils
import com.example.myandroidproject.viewmodel.CreateScheduleUiState
import com.example.myandroidproject.viewmodel.CreateScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScheduleScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onNavigateToAddHabit: () -> Unit,
    viewModel: CreateScheduleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadHabits()
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            Toast.makeText(context, "Schedule created", Toast.LENGTH_SHORT).show()
            viewModel.consumeSaved()
            onSaved()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Dátum választó beállítása
    val showDatePicker = {
        val calendar = Calendar.getInstance()
        runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(uiState.date)
        }.getOrNull()?.let { calendar.time = it }

        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
                viewModel.onDateChange(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selected.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Idő választó beállítása
    val showTimePicker = {
        val parts = uiState.startTime.split(":").mapNotNull { it.toIntOrNull() }
        val hour = parts.getOrNull(0) ?: 9
        val minute = parts.getOrNull(1) ?: 0

        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                viewModel.onStartTimeChange(String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute))
            },
            hour,
            minute,
            true
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New schedule") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Choose a habit, time, and repeat pattern.", style = MaterialTheme.typography.bodyMedium)

            Text("Habit", style = MaterialTheme.typography.titleMedium)
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.habits, key = { it.id }) { habit ->
                        FilterChip(
                            selected = uiState.selectedHabitId == habit.id,
                            onClick = { viewModel.onHabitSelected(habit.id) },
                            label = { Text(habit.name) }
                        )
                    }
                }
            }

            Text("When", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(DateTimeUtils.formatDisplayDate(uiState.date))
                }
                OutlinedButton(
                    onClick = { showTimePicker() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(uiState.startTime)
                }
            }

            OutlinedTextField(
                value = uiState.durationMinutes,
                onValueChange = viewModel::onDurationChange,
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )

            Text("Repeat", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(
                    listOf(
                        CreateScheduleUiState.REPEAT_NONE,
                        CreateScheduleUiState.REPEAT_DAILY,
                        CreateScheduleUiState.REPEAT_WEEKDAYS,
                        CreateScheduleUiState.REPEAT_WEEKENDS
                    )
                ) { pattern ->
                    val label = when(pattern) {
                        CreateScheduleUiState.REPEAT_NONE -> "Once"
                        CreateScheduleUiState.REPEAT_DAILY -> "Daily"
                        CreateScheduleUiState.REPEAT_WEEKDAYS -> "Weekdays"
                        CreateScheduleUiState.REPEAT_WEEKENDS -> "Weekends"
                        else -> pattern
                    }
                    FilterChip(
                        selected = uiState.repeatPattern == pattern,
                        onClick = { viewModel.onRepeatPatternChange(pattern) },
                        label = { Text(label) }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::createSchedule,
                enabled = !uiState.isSaving && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Create schedule")
                }
            }

            TextButton(
                onClick = onNavigateToAddHabit,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Add another habit")
            }
        }
    }
}