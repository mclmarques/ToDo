package com.android.puc.mcl.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY isCompleted ASC, date ASC")
    fun getTodos(): Flow<List<Todo>>

    @Upsert
    suspend fun upsert(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}
