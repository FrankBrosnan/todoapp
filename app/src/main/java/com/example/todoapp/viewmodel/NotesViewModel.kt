package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotesViewModel : ViewModel() {
    private val _notes =
        MutableStateFlow<List<Note>>(emptyList())
    val notes : StateFlow<List<Note>> =
        _notes.asStateFlow()

    fun addNote(text: String){
        if (text.isBlank()) return
        val note = Note(text = text)
        _notes.value = _notes.value + note
    }

    fun removeNote(id:Long){
        _notes.value = _notes.value.filter { it.id != id }
    }

}