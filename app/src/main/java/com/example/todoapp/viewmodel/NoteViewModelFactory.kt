package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.domain.usecases.NoteUseCases

class NoteViewModelFactory(
    private val noteUseCases: NoteUseCases
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(noteUseCases) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}