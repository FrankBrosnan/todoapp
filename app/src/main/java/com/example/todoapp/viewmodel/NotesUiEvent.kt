package com.example.todoapp.viewmodel

sealed class NotesUiEvent {
    data class ShowSnackbar(val message: String, val action: String? = null) : NotesUiEvent()
    data class Navigate(val route: String) : NotesUiEvent()
}