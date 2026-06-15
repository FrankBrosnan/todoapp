package com.example.todoapp.gui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.viewmodel.NotesViewModel
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.gui.navigation.Routes
import com.example.todoapp.viewmodel.events.AddEditEvent
import com.example.todoapp.viewmodel.events.NotesUiEvent


/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAddEditScreen(
    navController: NavController,
    noteId: Long,
    viewModel: NotesViewModel
) {

    val scope = rememberCoroutineScope()
    val uiState by viewModel.addEditUiState.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    val existingNote by produceState<Note?>(initialValue = null, key1 = noteId) {
        if (noteId != -1L) value = viewModel.getNoteById(noteId)
    }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load note when screen starts
    LaunchedEffect(noteId) {
        noteId?.let { viewModel.loadNote(it) }
    }

    // Collect events for navigation and snackbar
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NotesUiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoDelete()
                    }
                }
                is NotesUiEvent.Navigate -> {
                    println("Navigate to ${event.route}") // Replace with NavController
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "Add Note" else "Edit Note") },
                actions = {
                    if (noteId != null) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.testTag("action_delete")
                        ) {
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
                modifier = Modifier.fillMaxWidth().testTag("title_input")
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("content_input")
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (noteId == null) {
                            viewModel.addNote(title, content)
                        } else {
                            existingNote?.let {
                                viewModel.updateNote(it.copy(title = title, content = content))
                            }
                        }
                        navController.popBackStack()


                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("save_note")
            ) {
                Text(if (noteId == null) "Save" else "Update")
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
*/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAddEditScreen(
    viewModel: NotesViewModel,
    navController: NavController,
    noteId: Long? = null
) {

    val state by viewModel.addEditUiState.collectAsStateWithLifecycle()

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    // Load note when screen starts
    LaunchedEffect(noteId) {
        viewModel.init(noteId)
    }

    // Collect events for navigation and snackbar
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NotesUiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoDelete()
                    }
                }
                is NotesUiEvent.Navigate -> {
                    navController.navigate(event.route) {
                        popUpTo(Routes.NOTES_LIST)
                    }
                }
                NotesUiEvent.PopBackStack -> {
                    navController.popBackStack()
                }

            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId ==null) "Add Note" else "Edit Note") },
                        actions = {
                    if (noteId != null) {
                        IconButton(
                            onClick = {
                                viewModel.onAddEditEvent(
                                    AddEditEvent.DeleteClicked
                                )
                            },
                            modifier = Modifier.testTag("action_delete")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
                )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onAddEditEvent(
                    AddEditEvent.SaveClicked
                )
            },
                modifier = Modifier.fillMaxWidth().testTag("save_note")
            ) {
                Text("Save")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .testTag("notes_add_edit")
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = {
                    viewModel.onAddEditEvent(
                        AddEditEvent.TitleChanged(it)
                    )
                },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth().testTag("title_input")
            )
            if (state.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.content,
                onValueChange = {
                    viewModel.onAddEditEvent(
                        AddEditEvent.ContentChanged(it)
                    )
                },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().testTag(tag = "content_input")
            )


        }
    }
}
