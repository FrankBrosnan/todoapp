package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.NoteDatabase
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    val notes: StateFlow<List<Note>>

    init {
        val dao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(dao)
        notes = repository.allNotes.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun addNote(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.insert(Note(text = text))
        }
    }

    fun removeNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}
