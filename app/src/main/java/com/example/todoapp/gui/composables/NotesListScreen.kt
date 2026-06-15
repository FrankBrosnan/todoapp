package com.example.todoapp.gui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.viewmodel.NotesViewModel
import androidx.navigation.NavController
import com.example.todoapp.viewmodel.events.NotesListEvent
import com.example.todoapp.viewmodel.events.NotesUiEvent

import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Required Imports
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.SwipeToDismissBoxValue.*

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(navController: NavController, viewModel: NotesViewModel) {

    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag("fab_add_note"),
                onClick = { navController.navigate("add_edit_note") }) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("notes_list"),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Notes", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.testTag("notes_list").fillMaxSize()
            ) {
                // Using items(notes) with a key for smooth animations and stability
                items(
                    items = notes,
                    key = { it.id }
                ) { note ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.deleteNote(note)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false, // Swipe Left only
                        backgroundContent = {
                            val color = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { navController.navigate("add_edit_note?noteId=${note.id}") },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(text = note.title, modifier = Modifier.testTag("note_title_${note.id}"), style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }


        }
    }

    LaunchedEffect(Unit) {
        viewModel.showUndoEvent.collect {
            val result = snackbarHostState.showSnackbar(
                message = "Note deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            }
        }
    }
}
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(navController: NavController, viewModel: NotesViewModel) {

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    // Collect one-shot events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NotesUiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        // User pressed Undo
                        viewModel.undoDelete()
                    }
                }
                is NotesUiEvent.Navigate -> {
                    navController.navigate(event.route)
                }

                NotesUiEvent.PopBackStack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag("fab_add_note"),
                onClick = { viewModel.onAddClick() }) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    )
    { padding ->
        // Your main screen content goes here.
        // Make sure to apply the 'paddingValues' to your top-level layout (e.g. Box, Column, or LazyColumn)
        val state by viewModel.state.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            if (state.isLoading) {
                CircularProgressIndicator()
            }

            else if (state.notes.isEmpty()) {
                Text(
                    text = "No notes",
                    modifier = Modifier.testTag("empty_state")
                )
            }

            else if (state.error != null){
                Text("Oops ! Something went wrong")
            }

            else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("notes_list")
                ) {
                    items(
                        items = state.notes,
                        key = { it.id }
                    ) { note ->

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == EndToStart) {
                                    viewModel.onEvent(
                                        NotesListEvent.DeleteClicked(note.id)
                                    )
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false, // Replaces 'directions'
                            backgroundContent = { // Renamed from 'background'

                                // Only show red if the user is swiping toward the start
                                val isSwiping = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(if (isSwiping) Color.Red else Color.Transparent)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text("Delete", color = Color.White)
                                }
                            }
                        ) { // This is the 'content' slot (replaces dismissContent)
                            NoteItem(
                                note = note,
                                onClick = {
                                    viewModel.onEvent(NotesListEvent.NoteClicked(note.id))
                                }
                            )
                        }


                    }
                }



            }
        }


    }
}

