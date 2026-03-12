package com.example.todoapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoapp.data.NoteDao
import com.example.todoapp.data.NoteDatabase
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var db: NoteDatabase
    private lateinit var dao: NoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Use in-memory database for testing
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).build()
        dao = db.noteDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun writeAndReadTodo() = runBlocking {
        val item = Note(id = 1, title = "Test KSP", content = "Test KSP")
        dao.insert(item)
        val allItems = dao.getAllNotes().first()
        assert(allItems.contains(item))
    }
}