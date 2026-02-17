package com.example.todoapp.gui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.model.Note
import com.example.todoapp.gui.composables.NoteItem
import com.example.todoapp.viewmodel.NotesViewModel
import androidx.navigation.NavController

@Composable
fun NotesListScreen(navController: NavController, viewModel: NotesViewModel = viewModel()) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_edit_note") }) {
                Text("+")
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Notes", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(notes.size) { index ->
                    val note = notes[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { navController.navigate("add_edit_note?noteId=${note.id}") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}