package com.example.todoapp.viewmodel.events

sealed class NotesListEvent {
    data class DeleteClicked(val noteId: Long) : NotesListEvent()
    data class NoteClicked(val noteId: Long) : NotesListEvent()
}