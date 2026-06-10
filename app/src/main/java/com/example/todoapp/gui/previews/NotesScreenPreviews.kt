package com.example.todoapp.gui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.gui.composables.NotesAddEditScreen
import com.example.todoapp.gui.composables.NotesListScreen

@Preview(showBackground = true)
@Composable
fun NotesListScreenPreview() {
    MaterialTheme {
        NotesListScreen(navController = rememberNavController(), viewModel = viewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun NotesEditScreenPreview() {
    MaterialTheme {
        NotesAddEditScreen(
            navController = rememberNavController(),
            noteId = -1,
            viewModel = viewModel()
        )
    }
}