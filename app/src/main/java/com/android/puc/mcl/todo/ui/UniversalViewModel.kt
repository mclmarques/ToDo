package com.android.puc.mcl.todo.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.puc.mcl.todo.data.Todo
import com.android.puc.mcl.todo.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class UniversalViewModel  @Inject constructor(private val repository: TodoRepository) : ViewModel(){
    val todos: StateFlow<List<Todo>> = repository.todos.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    var selectionMode = mutableStateOf(false)
        private set

    private val _selectedItems = mutableStateListOf<Todo>()
    val selectedItems: List<Todo> get() = _selectedItems

    fun clearSelectedItems() {
        _selectedItems.clear()
    }

    fun toggleSelectionMode() {
        selectionMode.value = !selectionMode.value
        if (!selectionMode.value) clearSelection()
    }

    fun toggleItemSelection(todo: Todo) {
        if (_selectedItems.contains(todo)) {
            _selectedItems.remove(todo)
        } else {
            _selectedItems.add(todo)
        }
    }

    fun clearSelection() {
        _selectedItems.clear()
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            _selectedItems.forEach { repository.delete(it) }
            clearSelection()
            toggleSelectionMode()
        }
    }

    fun addTodo(title: String, description: String, date: Date) {
        viewModelScope.launch {
            repository.upsert(Todo(title = title, description = description, date = date))
        }
    }

    fun toggleCompleted(todo: Todo) {
        viewModelScope.launch {
            repository.upsert(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }
}