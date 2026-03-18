package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    //private val repository: NoteRepository = NoteRepository.getInstance(app)
    //val allNotes: Flow<List<Note>> = repository.getAllFlow()

    init {
        viewModelScope.launch {

            repository.getAllFlow().collect { notes ->

                _uiState.value =
                    if (notes.isEmpty()) {
                        NotesUiState.Empty
                    } else {
                        NotesUiState.Success(notes)
                    }
            }
        }
    }


    private val _uiState = MutableStateFlow<NotesUiState>(
        NotesUiState.Loading
    )

    val uiState: StateFlow<NotesUiState> = _uiState


    private var recentlyDeletedNote: Note? = null

    private val _showUndoEvent = Channel<Unit>(Channel.BUFFERED)
    val showUndoEvent = _showUndoEvent.receiveAsFlow()

    suspend fun addNote(title:String, content: String) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            repository.insert(Note(title = title, content = content))
        }
    }

    suspend fun getNoteById(id: Long): Note? {
        return repository.getById(id)
    }

    suspend fun updateNote(note: Note) {
            repository.update(note)
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
            recentlyDeletedNote = note
            _showUndoEvent.send(Unit)
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            recentlyDeletedNote?.let {
                repository.insert(it.copy(id = 0)) // insert new copy
                recentlyDeletedNote = null
            }
        }
    }
}
