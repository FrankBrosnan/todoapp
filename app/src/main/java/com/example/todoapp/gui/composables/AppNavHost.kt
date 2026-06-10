package com.example.todoapp.gui.composables

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todoapp.gui.navigation.Routes
import com.example.todoapp.viewmodel.NotesViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: NotesViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Routes.NOTES_LIST
    ) {

        composable(Routes.NOTES_LIST) {
            NotesListScreen(navController = navController, viewModel)
        }


        composable(
            route = "${Routes.ADD_EDIT_NOTE}?noteId={noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        )
        { backStackEntry ->

            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            NotesAddEditScreen(
                noteId = if (noteId == -1L) null else noteId,
                viewModel = viewModel,
                navController = navController
            )
        }


    }
}

