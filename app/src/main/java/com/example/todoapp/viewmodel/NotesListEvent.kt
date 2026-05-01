package com.example.todoapp.viewmodel

sealed class NotesListEvent {
    data class DeleteClicked(val noteId: Long) : NotesListEvent()
    data class NoteClicked(val noteId: Long) : NotesListEvent()
}