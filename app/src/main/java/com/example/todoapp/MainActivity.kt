package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.gui.NotesListScreen
import com.example.todoapp.viewmodel.NotesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()
                    val viewModel: NotesViewModel = viewModel()

                    NavHost(navController = navController, startDestination = "notes_list") {
                        composable("notes_list") { NotesListScreen(viewModel = viewModel) }
                        // composable("note_edit/{noteId}") { ... } // future
                    }
                }
            }
        }
    }
}