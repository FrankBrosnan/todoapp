package com.example.todoapp.domain.usecases

import com.example.todoapp.data.INoteRepository
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(
    private val repository: INoteRepository
) {

    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}