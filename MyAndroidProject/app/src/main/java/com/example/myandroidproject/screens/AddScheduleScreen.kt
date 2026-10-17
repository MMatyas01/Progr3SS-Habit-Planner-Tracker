package com.example.myandroidproject.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddScheduleScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Planned") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Schedule",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                message = ""
            },
            label = { Text("Activity name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = startTime,
            onValueChange = { startTime = it },
            label = { Text("Start time (HH:MM)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = "Status",
            style = MaterialTheme.typography.titleMedium
        )

        listOf("Planned", "Completed", "Skipped").forEach { option ->
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = status == option,
                    onClick = { status = option }
                )
                Text(text = option)
            }
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = {
                message = if (title.isBlank()) {
                    "Please enter an activity name."
                } else {
                    "Schedule details are ready (not saved)."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Schedule")
        }

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Cancel")
        }
    }
}
