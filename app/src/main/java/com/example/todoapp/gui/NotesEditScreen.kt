package com.example.todoapp.gui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.model.Note
import com.example.todoapp.viewmodel.NotesViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesEditScreen(
    navController: NavController,
    noteId: Long,
    viewModel: NotesViewModel
) {
    Log.d("VM_CHECK_NotesEditScreen",viewModel.toString())
    val scope = rememberCoroutineScope()

    val existingNote by produceState<Note?>(initialValue = null, key1 = noteId) {
        if (noteId != -1L) value = viewModel.getNoteById(noteId)
    }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(existingNote) {
        existingNote?.let {
            title = it.title
            content = it.content
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == -1L) "Add Note" else "Edit Note") },
                actions = {
                    if (noteId != -1L) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Note"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (noteId == -1L) {
                            viewModel.addNote(title, content)
                        } else {
                            existingNote?.let {
                                viewModel.updateNote(it.copy(title = title, content = content))
                            }
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (noteId == -1L) "Save" else "Update")
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        existingNote?.let { viewModel.deleteNote(it) }
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") }
        )
    }
}
