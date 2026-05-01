package com.example.todoapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.gui.navigation.Routes
import com.example.todoapp.model.Note
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    // UI STATE
    private val _state = MutableStateFlow< NotesListState>(
        NotesListState()
    )
    val state: StateFlow<NotesListState> = _state

    // ADD/EDIT STATE
    private val _addEditUiState = MutableStateFlow<AddEditNoteUiState>(AddEditNoteUiState())
    val addEditUiState: StateFlow<AddEditNoteUiState> = _addEditUiState


    // EVENTS
    private val _events = MutableSharedFlow<NotesUiEvent>(
        extraBufferCapacity = 1,
        replay = 0
    )
    val events = _events.asSharedFlow()

    private var recentlyDeletedNote: Note? = null

    init {
        viewModelScope.launch {
            repository.getAllNotes()
                .catch { e ->
                    _state.value = NotesListState(
                        error = e.message ?: "Unknown error"
                    )
                }
                .collect { notes ->
                    _state.value = NotesListState(
                        notes = notes,
                        isLoading = false
                    )
                }
        }
    }



    suspend fun addNote(title:String, content: String) {
        viewModelScope.launch {
            repository.insert(Note(title = title, content = content))
            _events.emit(NotesUiEvent.Navigate("notes_list"))
        }
    }

    suspend fun getNoteById(id: Long): Note? {
        return repository.getById(id)
    }

    suspend fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.update(note)
            _events.emit(NotesUiEvent.Navigate(route = Routes.NOTES_LIST))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
            recentlyDeletedNote = note
            _events.emit(
                NotesUiEvent.ShowSnackbar(
                    message = "Note deleted",
                    action = "Undo"
                )
            )
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            recentlyDeletedNote?.let { repository.insert(it) }
            recentlyDeletedNote = null
        }
    }

    fun onAddClick() {
        viewModelScope.launch {
            _events.emit(NotesUiEvent.Navigate(Routes.ADD_EDIT_NOTE))
        }
    }

    //OK For now but need to use Routes object
    fun onEditClick(noteId: Long) {
        viewModelScope.launch {
            Log.d("NotesViewModel.onEditClick","${noteId}")
            _events.emit(NotesUiEvent.Navigate("${Routes.ADD_EDIT_NOTE}?noteId=${noteId}"))
        }
    }

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            val note = repository.getById(noteId)
            note?.let {
                _addEditUiState.value = AddEditNoteUiState(
                    title = it.title,
                    content = it.content
                )
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _addEditUiState.update { it.copy(title = newTitle) }
    }

    fun updateContent(newContent: String) {
        _addEditUiState.update { it.copy(content = newContent) }
    }

    fun saveNote(noteId: Long? = null) {
        viewModelScope.launch {
            val state = _addEditUiState.value
            if (noteId == null) {
                // New note
                repository.insert(Note(title = state.title, content = state.content))
            } else {
                repository.update(
                    Note(
                        id = noteId,
                        title = state.title,
                        content = state.content
                    )
                )
            }
            _events.emit(NotesUiEvent.Navigate(Routes.NOTES_LIST))
        }
    }

    fun deleteCurrentNote(noteId: Long) {
        viewModelScope.launch {
            val note = repository.getById(noteId) ?: return@launch
            recentlyDeletedNote = note
            repository.delete(note)
            _events.emit(
                NotesUiEvent.ShowSnackbar("Note deleted", action = "Undo")
            )
        }
    }

    fun init(noteId: Long?) {
        if (noteId == null) {
            // New note → reset state
            _addEditUiState.value = AddEditNoteUiState()
        } else {
            // Edit note → load it
            loadNote(noteId)
        }
    }

    fun onEvent(event: NotesListEvent) {
        when (event) {

            is NotesListEvent.DeleteClicked -> {
                deleteCurrentNote(event.noteId)
            }

            is NotesListEvent.NoteClicked -> {
                viewModelScope.launch {
                    _events.emit(
                        NotesUiEvent.Navigate("add_edit_note?noteId=${event.noteId}")
                    )
                }

            }
        }
    }

}
