package com.android.puc.mcl.todo.repository

import com.android.puc.mcl.todo.data.Todo
import com.android.puc.mcl.todo.data.TodoDao
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    val todos: Flow<List<Todo>> = todoDao.getTodos()

    suspend fun upsert(todo: Todo) = todoDao.upsert(todo)
    suspend fun delete(todo: Todo) = todoDao.delete(todo)

}
