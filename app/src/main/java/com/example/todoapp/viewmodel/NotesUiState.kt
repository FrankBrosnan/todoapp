package com.example.todoapp.viewmodel

import com.example.todoapp.model.Note

sealed class NotesUiState {

    open val notes: List<Note> = emptyList()
    object Loading : NotesUiState()

    object Empty : NotesUiState()

    data class Success(
        override val notes: List<Note>
    ) : NotesUiState()

    data class Error(
        val message: String
    ) : NotesUiState()

    val isLoading: Boolean
        get() = this is Loading
}