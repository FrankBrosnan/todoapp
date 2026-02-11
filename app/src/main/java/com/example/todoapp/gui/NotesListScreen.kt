package com.example.todoapp.gui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.model.Note
import com.example.todoapp.gui.composables.NoteItem
import com.example.todoapp.viewmodel.NotesViewModel

@Composable
fun NotesListScreen(viewModel: NotesViewModel = viewModel()) {
    val notes by viewModel.notes.collectAsState()

    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("New note") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                viewModel.addNote(text)
                text = ""
            }) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes, key = { it.id }) { note ->
                NoteItem(note = note, onDelete = { viewModel.removeNote(it) })
            }
        }
    }
}