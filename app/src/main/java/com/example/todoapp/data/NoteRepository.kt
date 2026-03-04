package com.example.todoapp.data

import android.content.Context
import androidx.room.Room
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.Flow

//class NoteRepository private constructor(context: Context) {
class NoteRepository(private val dao: NoteDao) {


    fun getAllFlow(): Flow<List<Note>> = dao.getAllNotes()

    suspend fun insert(note: Note) = dao.insert(note)
    suspend fun delete(note: Note) = dao.delete(note)

    suspend fun getById(id: Long) = dao.getById(id)

    suspend fun update(note: Note) = dao.update(note)


    companion object {
        @Volatile private var instance: NoteRepository? = null

        fun getInstance(context: Context): NoteRepository {
            return instance ?: synchronized(this) {
                instance ?: buildRepository(context).also { instance = it }
            }
        }

        private fun buildRepository(context: Context): NoteRepository {
            val db = Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java, "notes.db"
            ).build()
            return NoteRepository(db.noteDao())
        }
    }


}