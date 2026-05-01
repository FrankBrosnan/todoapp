package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.gui.NotesListScreen
import com.example.todoapp.gui.NotesAddEditScreen
import com.example.todoapp.gui.composables.AppNavHost
import com.example.todoapp.viewmodel.NoteViewModelFactory
import com.example.todoapp.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Get the repository (adjust this to match how your app stores it)
        val repository = NoteRepository.getInstance(applicationContext)
        val factory = NoteViewModelFactory(repository)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                // SINGLE ViewModel instance
                val viewModel: NotesViewModel = viewModel(factory = factory)
                AppNavHost(navController, viewModel)

            }
        }
    }
}