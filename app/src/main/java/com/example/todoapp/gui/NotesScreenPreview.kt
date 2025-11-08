package com.example.todoapp.gui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(showBackground = true)
@Composable
fun NotesScreenPreview() {
    MaterialTheme {
        NotesScreen(viewModel = viewModel())
    }
}