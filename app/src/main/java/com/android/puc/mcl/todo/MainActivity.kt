package com.android.puc.mcl.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.puc.mcl.todo.ui.AddTodoScreen
import com.android.puc.mcl.todo.ui.TodoListScreen
import com.android.puc.mcl.todo.ui.UniversalViewModel
import com.android.puc.mcl.todo.ui.theme.ToDoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: UniversalViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoTheme {
                TodoApp(
                    viewModel = this.viewModel
                )
            }
        }
    }
}

@Composable
fun TodoApp(navController: NavHostController = rememberNavController(), viewModel: UniversalViewModel) {
    NavHost(navController, startDestination = "todoList") {
        composable("todoList") {
            TodoListScreen(
                todos = viewModel.todos.collectAsState().value,
                onAddTodoClick = { navController.navigate("addTodo") },
                viewModel = viewModel
            )
        }
        composable("addTodo") {
            AddTodoScreen(
                onSave = { title, description, date ->
                    viewModel.addTodo(title, description, date)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
