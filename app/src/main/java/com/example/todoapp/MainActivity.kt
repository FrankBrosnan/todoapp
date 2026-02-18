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
import com.example.todoapp.gui.NotesListScreen
import com.example.todoapp.gui.NotesEditScreen
import com.example.todoapp.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "note_list"
                ) {
                    //composable("note_list") {
                    //    NotesListScreen(navController)

                    //new code
                    composable("note_list") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("note_list")
                        }
                        val viewModel: NotesViewModel = viewModel(parentEntry)

                        NotesListScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }



                    composable(
                        route = "add_edit_note?noteId={noteId}",
                        arguments = listOf(
                            navArgument("noteId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    ) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {navController.getBackStackEntry("note_list")}
                        val viewModel: NotesViewModel = viewModel(parentEntry)
                        //val noteId = backStackEntry.arguments?.getString("noteId")?.toLong() ?: -1L
                        val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                        NotesEditScreen(navController, noteId, viewModel = viewModel)
                    }
                }
            }
        }
    }
}