package com.example.todoapp.domain.usecases

import com.example.todoapp.data.INoteRepository
import com.example.todoapp.model.Note

class GetNoteUseCase(
    private val repository: INoteRepository
) {

    suspend operator fun invoke(id: Long): Note? {
        return repository.getById(id)
    }
}