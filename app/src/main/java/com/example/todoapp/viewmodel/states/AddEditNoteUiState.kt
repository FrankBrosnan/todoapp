package com.example.todoapp.viewmodel.states

data class AddEditNoteUiState(
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val error:String? = null
)