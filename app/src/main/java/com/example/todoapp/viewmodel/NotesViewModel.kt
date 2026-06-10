package com.example.todoapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.domain.usecases.NoteUseCases
import com.example.todoapp.gui.navigation.Routes
import com.example.todoapp.model.Note
import com.example.todoapp.viewmodel.events.AddEditEvent
import com.example.todoapp.viewmodel.events.NotesListEvent
import com.example.todoapp.viewmodel.events.NotesUiEvent
import com.example.todoapp.viewmodel.states.AddEditNoteUiState
import com.example.todoapp.viewmodel.states.NotesListState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update

class NotesViewModel( private val useCases: NoteUseCases) : ViewModel() {

    // UI STATE
    private val _state = MutableStateFlow<NotesListState>(
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
    private var currentNoteId: Long? = null

    init {
        viewModelScope.launch {
            useCases.getAllNotes()
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


    fun undoDelete() {
        viewModelScope.launch {
            recentlyDeletedNote?.let { useCases.addNote(it) }
            recentlyDeletedNote = null
        }
    }

    fun onAddClick() {
        viewModelScope.launch {
            _events.emit(NotesUiEvent.Navigate(Routes.ADD_EDIT_NOTE))
        }
    }



    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            val note = useCases.getNote((noteId))
            note?.let {
                _addEditUiState.value = AddEditNoteUiState(
                    title = it.title,
                    content = it.content,
                    error = ""
                )
            }
        }
    }


    fun saveNote() {
        viewModelScope.launch {
            try {
                val state = _addEditUiState.value
                if (currentNoteId == null) {
                    // New note
                    useCases.addNote(Note(title = state.title, content = state.content))
                } else {
                    useCases.updateNote(
                        Note(
                            id = currentNoteId!!,
                            title = state.title,
                            content = state.content
                        )
                    )
                }
                //_events.emit(NotesUiEvent.Navigate(Routes.NOTES_LIST))
                _events.emit(NotesUiEvent.PopBackStack)
            }
            catch (e: IllegalArgumentException) {
                /*
                _events.emit(
                    NotesUiEvent.ShowSnackbar(
                        message = e.message ?: "Title cannot be blank"
                    )
                )
                 */
                _addEditUiState.update {
                    it.copy(
                        error = e.message
                    )
                }

            }
        }
    }

    fun deleteCurrentNote(noteId: Long) {
        viewModelScope.launch {
            val note = useCases.getNote(noteId) ?: return@launch
            recentlyDeletedNote = note
            useCases.deleteNote(note)
            _events.emit(
                NotesUiEvent.ShowSnackbar("Note deleted", action = "Undo")
            )
        }
    }

    fun deleteAndNavigateBack(noteId:Long) {
        viewModelScope.launch {
            val note = useCases.getNote(noteId) ?: return@launch
            recentlyDeletedNote = note
            useCases.deleteNote(note)

            // 1. Navigate back
            //_events.emit(NotesUiEvent.Navigate(Routes.NOTES_LIST))
            _events.emit(NotesUiEvent.PopBackStack)

            //allow list screen time to start
            //collecting events.
            delay(100)

            // 2. Show snackbar
            _events.emit(
                NotesUiEvent.ShowSnackbar(
                    message = "Note deleted",
                    action = "Undo"
                    )
                )


        }
    }

    fun init(noteId: Long?) {
        currentNoteId = noteId
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

    fun onAddEditEvent(event: AddEditEvent) {
        when (event) {

            is AddEditEvent.TitleChanged -> {
                _addEditUiState.update {
                    it.copy(title = event.value,error = null)
                }
            }

            is AddEditEvent.ContentChanged -> {
                _addEditUiState.update {
                    it.copy(content = event.value)
                }
            }

            is AddEditEvent.SaveClicked -> {
                saveNote()
            }

            is AddEditEvent.DeleteClicked ->{
                currentNoteId?.let { deleteAndNavigateBack(it) }
            }
        }
    }

}
