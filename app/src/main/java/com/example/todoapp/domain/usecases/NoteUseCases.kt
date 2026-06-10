package com.example.todoapp.domain.usecases

data class NoteUseCases(
    val addNote: AddNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val getAllNotes: GetAllNotesUseCase,
    val getNote: GetNoteUseCase
)