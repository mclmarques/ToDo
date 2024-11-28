package com.android.puc.mcl.todo.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.puc.mcl.todo.data.Todo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onAddTodoClick: () -> Unit,
    viewModel: UniversalViewModel,
) {
    val activity = LocalContext.current as? Activity
    val todos = viewModel.todos.collectAsState().value
    val selectionMode = viewModel.selectionMode.value
    val selectedItems = viewModel.selectedItems
    // Rotation Animation
    val rotationAngle by animateFloatAsState(
        targetValue = if (selectionMode) 0f else 180f, // Rotate to 0 for Delete, 180 for Add
        label = "Rotation Animation"
    )

    BackHandler {
        if (selectionMode) {
            viewModel.toggleSelectionMode()
        }
        else {
            activity?.finish()
        }
    }



    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectionMode) {
                        viewModel.deleteSelectedItems()
                    } else {
                        onAddTodoClick()
                    }
                },
                containerColor = if (selectionMode) Color.Red else MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (selectionMode) Icons.Default.Delete else Icons.Default.Add,
                    contentDescription = if (selectionMode) "Delete Selected" else "Add Todo",
                    modifier = Modifier.graphicsLayer(
                        rotationZ = rotationAngle
                    ),
                    tint = Color.Black
                )
            }
        },
        modifier = Modifier.clickable {
            viewModel.selectionMode.value = false
            viewModel.clearSelection()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "My Todo List",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 56.dp) // Extra padding to prevent overlap with FAB
            ) {
                items(todos) { todo ->
                    TodoItem(
                        todo = todo,
                        isSelected = selectedItems.contains(todo),
                        inSelectionMode = selectionMode,
                        onLongPress = { viewModel.toggleSelectionMode() },
                        onToggleSelected = { viewModel.toggleItemSelection(todo) },
                        onToggleCompleted = { viewModel.toggleCompleted(todo) },
                        cancelDelete = {
                            viewModel.selectionMode.value = false
                            viewModel.clearSelection()}
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItem(
    todo: Todo,
    isSelected: Boolean,
    inSelectionMode: Boolean,
    onLongPress: () -> Unit,
    onToggleSelected: () -> Unit,
    onToggleCompleted: () -> Unit,
    cancelDelete: () -> Unit
) {
    val cardColor = if (todo.isCompleted) {
        Color.Gray.copy(alpha = 0.2f)  // Dimmed color when completed
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    if (inSelectionMode) {
                        onToggleSelected()
                    }
                },
                onLongClick = {
                    if (!inSelectionMode) {
                        onLongPress()
                        onToggleSelected()
                    } else {
                        cancelDelete()
                    }


                }
            ),
        colors = CardDefaults.cardColors(
            if (isSelected) Color.LightGray else cardColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Checkbox(
                        checked = todo.isCompleted,
                        onCheckedChange = { if (!inSelectionMode) onToggleCompleted() }
                    )
                    Text(
                        text = todo.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (todo.description.isNotEmpty()) {
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

            }

            Text(
                text = "Due: ${convertMillisToDate(todo.date.time)}",
                style = MaterialTheme.typography.bodySmall,
                color = if (todo.date.time < System.currentTimeMillis()) Color.Red else Color.Black,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}



fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}