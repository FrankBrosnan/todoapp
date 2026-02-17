package com.example.todoapp.data

import android.content.Context
import androidx.room.*
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note):Long

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)

}