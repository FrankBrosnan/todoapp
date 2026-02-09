package com.example.todoapp.data

import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    val allNotes: Flow<List<Note>> = dao.getAllNotes()

    suspend fun insert(note: Note) = dao.insert(note)
    suspend fun delete(note: Note) = dao.delete(note)
}