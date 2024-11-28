package com.android.puc.mcl.todo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.puc.mcl.todo.ui.theme.ToDoTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    onSave: (String, String, Date) -> Unit,
    onCancel: () -> Unit
) {
    ToDoTheme {
        Surface {
            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            var isTextFieldError by remember { mutableStateOf(false) }

            val currentTime = Calendar.getInstance()
            val timePickerState = rememberTimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = true,
            )
            var showTimePicker by remember { mutableStateOf(false) }
            var selectedTime by remember { mutableStateOf(System.currentTimeMillis()) }

            val calendar = Calendar.getInstance().apply { timeInMillis = selectedTime }
            var showDatePicker by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)

            //Time dialog logic
            if (showTimePicker) {
                TimePickerDialog(
                    onDismiss = { showTimePicker = false },
                    onConfirm = {
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        selectedTime = calendar.timeInMillis
                        showTimePicker = false
                    }
                ) {
                    TimePicker(state = timePickerState)
                }
            }

            //Date dialog
            if (showDatePicker) {
                // DatePickerDialog component with custom colors and button behaviors
                DatePickerDialog(
                    onDismissRequest = {
                        // Action when the dialog is dismissed without selecting a date
                        showDatePicker = false
                    },
                    confirmButton = {
                        // Confirm button with custom action and styling
                        TextButton(
                            onClick = {
                                showDatePicker = false
                                datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                    // Use the new function to update the calendar
                                    selectedDateMillis.updateCalendarDate(calendar)
                                    selectedTime = calendar.timeInMillis // Update selectedTime
                                }
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        // Dismiss button to close the dialog without selecting a date
                        TextButton(
                            onClick = {
                                showDatePicker = false
                            }
                        ) {
                            Text("CANCEL")
                        }
                    }
                ) {
                    // The actual DatePicker component within the dialog
                    DatePicker(
                        state = datePickerState,
                    )
                }
            }


            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add To-Do",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                // Title input
                TextField(
                    value = title,
                    onValueChange = {
                        title = it},
                    label = { Text("Title") },
                    isError = isTextFieldError,
                    supportingText = {
                        // Show error message below the TextField if it's empty
                        if (isTextFieldError) {
                            Text(
                                text = "This field is mandatory",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description input
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date and Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Date Picker Field
                    OutlinedIconButton(
                        onClick = { showDatePicker = true },
                        shape = OutlinedTextFieldDefaults.shape,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            SimpleDateFormat(
                                "MMM dd, yyyy",
                                Locale.getDefault()
                            ).format(Date(selectedTime))
                        )
                    }
                    OutlinedIconButton(
                        onClick = { showTimePicker = true },
                        shape = OutlinedTextFieldDefaults.shape,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            SimpleDateFormat(
                                "hh:mm a",
                                Locale.getDefault()
                            ).format(Date(selectedTime))
                        )
                    }
                }

                // Save and Cancel Buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onCancel) { Text("Cancel") }
                    Button(onClick = {
                        if(title.isNotEmpty() ) onSave(title, description, Date(selectedTime))
                        else isTextFieldError = true


                    }) { Text("Save") }
                }
            }
        }
    }
}

//Time picker
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}

fun Long.updateCalendarDate(calendar: Calendar): Calendar {
    // Create a new calendar instance for the selected date
    val selectedCalendar = Calendar.getInstance().apply {
        timeInMillis = this@updateCalendarDate
        // Adjust for time zone offset to get the correct local date
        val zoneOffset = get(Calendar.ZONE_OFFSET)
        val dstOffset = get(Calendar.DST_OFFSET)
        add(Calendar.MILLISECOND, -(zoneOffset + dstOffset))
    }

    // Update only the date part of the provided calendar
    calendar.set(Calendar.YEAR, selectedCalendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, selectedCalendar.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, selectedCalendar.get(Calendar.DAY_OF_MONTH))

    return calendar
}

