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
                        //val viewModel: NotesViewModel = viewModel(parentEntry)
                        // 2. Pass the factory here
                        val viewModel: NotesViewModel = viewModel(
                            viewModelStoreOwner = backStackEntry,
                            factory = factory
                        )

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
                        //val viewModel: NotesViewModel = viewModel(parentEntry)

                        // 3. And pass the factory here if you want to share the same ViewModel
                        val viewModel: NotesViewModel = viewModel(
                            viewModelStoreOwner = parentEntry,
                            factory = factory
                        )

                        //val noteId = backStackEntry.arguments?.getString("noteId")?.toLong() ?: -1L
                        val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                        NotesAddEditScreen(navController, noteId, viewModel = viewModel)
                    }
                }
            }
        }
    }
}