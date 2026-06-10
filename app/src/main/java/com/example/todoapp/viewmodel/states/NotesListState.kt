package com.example.todoapp.viewmodel.states

import com.example.todoapp.model.Note

data class NotesListState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)