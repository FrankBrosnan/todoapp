package com.example.todoapp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import com.example.todoapp.data.INoteRepository
import com.example.todoapp.model.Note

class FakeNoteRepository : INoteRepository {

    // 1. A StateFlow keeps your list active and reactive
    private val _notesFlow = MutableStateFlow<List<Note>>(emptyList())

    // 2. Expose it as a read-only list for assertions in your tests
    val notes: List<Note> get() = _notesFlow.value

    private var currentId = 0L

    // 3. This will now automatically emit changes to the ViewModel in real-time!
    override fun getAllNotes(): Flow<List<Note>> = _notesFlow.asStateFlow()

    override suspend fun getById(id: Long): Note? {
        return _notesFlow.value.find { it.id == id }
    }

    override suspend fun insert(note: Note): Long {
        currentId++
        val finalId = if (note.id != 0L) note.id else currentId
        val newNote = note.copy(id = finalId)

        // Update the reactive state flow list
        _notesFlow.update { currentList -> currentList + newNote }
        return finalId
    }

    override suspend fun update(note: Note) {
        _notesFlow.update { currentList ->
            currentList.map { existingNote ->
                if (existingNote.id == note.id) note else existingNote
            }
        }
    }

    override suspend fun delete(note: Note) {
        _notesFlow.update { currentList ->
            currentList.filterNot { it.id == note.id }
        }
    }
}
