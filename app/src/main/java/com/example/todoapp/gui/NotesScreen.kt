package com.example.todoapp.gui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.Note
import com.example.todoapp.viewmodel.NotesViewModel




    @Composable
    fun NotesScreen(viewModel: NotesViewModel){
        val notes by viewModel.notes.collectAsState()
        var text by remember { mutableStateOf("")}

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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

    @Composable

    //Design Decision.
    //Had 2 options here.
    // Option 1. Pass entire note to function as opposed to just id
    // Usually, it's better for a "Delete" action in a list to pass
    // the entire object back.
    // This makes the UI more flexible and matches your current
    // ViewModel logic.
    // Option 2. Not done. pass only the note id a long.
    // If you want to keep the Composable passing a Long id
    // (perhaps you only want to work with IDs in the UI layer),
    // you must change how you call the ViewModel.
    fun NoteItem(note: Note,
                 onDelete:(Note) -> Unit){
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(note.text, modifier = Modifier.weight(1f))
                IconButton(onClick = { onDelete(note) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }

    }

