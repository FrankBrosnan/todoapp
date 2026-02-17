package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotesViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: NoteRepository = NoteRepository.getInstance(app)
    val allNotes: Flow<List<Note>> = repository.getAllFlow()

    fun addNote(title:String, content: String) {
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            repository.insert(Note(title = title, content = content))
        }
    }

    suspend fun getNoteById(id: Long): Note? {
        return repository.getById(id)
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.delete(note) }
    }
}
