package com.android.puc.mcl.todo.DI

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.android.puc.mcl.todo.data.TodoDao
import com.android.puc.mcl.todo.data.TodoDatabase
import com.android.puc.mcl.todo.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TodoDatabase {
        return databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_database"
        ).build()
    }

    @Provides
    fun provideTodoDao(database: TodoDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    fun provideRepository(todoDao: TodoDao): TodoRepository {
        return TodoRepository(todoDao)
    }
}
