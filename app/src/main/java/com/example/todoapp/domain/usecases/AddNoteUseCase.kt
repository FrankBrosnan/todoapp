package com.example.todoapp.domain.usecases

import com.example.todoapp.data.INoteRepository
import com.example.todoapp.model.Note

class AddNoteUseCase(
    private val repository: INoteRepository
) {

    suspend operator fun invoke(note: Note) {

        if (note.title.isBlank()) {
            throw IllegalArgumentException("Title cannot be empty")
        }

        repository.insert(note)
    }
}
