package com.example.todoapp.domain.usecases

import com.example.todoapp.data.INoteRepository
import com.example.todoapp.model.Note

class DeleteNoteUseCase(
    private val repository: INoteRepository
) {

    suspend operator fun invoke(note: Note) {
        repository.delete(note)
    }
}