package com.example.todoapp.viewmodel

import com.example.todoapp.model.Note

data class AddEditNoteUiState(
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false
)