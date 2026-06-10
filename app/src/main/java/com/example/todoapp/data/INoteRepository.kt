package com.example.todoapp.data

import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.Flow

interface INoteRepository {

    fun getAllNotes(): Flow<List<Note>>

    suspend fun getById(id: Long): Note?

    suspend fun insert(note: Note): Long

    suspend fun update(note: Note)

    suspend fun delete(note: Note)
}