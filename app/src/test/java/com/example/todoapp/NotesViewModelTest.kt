package com.example.todoapp

import app.cash.turbine.test
import com.example.todoapp.domain.usecases.AddNoteUseCase
import com.example.todoapp.domain.usecases.DeleteNoteUseCase
import com.example.todoapp.domain.usecases.GetAllNotesUseCase
import com.example.todoapp.domain.usecases.GetNoteUseCase
import com.example.todoapp.domain.usecases.NoteUseCases
import com.example.todoapp.domain.usecases.UpdateNoteUseCase
import com.example.todoapp.model.Note
import com.example.todoapp.viewmodel.NotesViewModel
import com.example.todoapp.viewmodel.events.AddEditEvent
import com.example.todoapp.viewmodel.events.NotesUiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("IllegalIdentifier")
class NotesViewModelTest {
    private lateinit var viewModel: NotesViewModel
    private lateinit var repository: FakeNoteRepository

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = FakeNoteRepository()

        val useCases = NoteUseCases(
            addNote = AddNoteUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository),
            updateNote = UpdateNoteUseCase(repository),
            getAllNotes = GetAllNotesUseCase(repository),
            getNote = GetNoteUseCase(repository)
        )

        viewModel = NotesViewModel(useCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `save new note emits navigation event and inserts note`() = runTest {

        // GIVEN
        viewModel.init(null) // new note

        viewModel.onAddEditEvent(AddEditEvent.TitleChanged("Title"))
        viewModel.onAddEditEvent(AddEditEvent.ContentChanged("Content"))

        // WHEN
        viewModel.onAddEditEvent(AddEditEvent.SaveClicked)

        // advance coroutine execution
        advanceUntilIdle()

        // THEN: repository contains note
        assert(repository.notes.size == 1)
        assert(repository.notes.first().title == "Title")

    }

    @Test
    fun `save emits navigation event`() = runTest {

        viewModel.init(null)

        viewModel.onAddEditEvent(AddEditEvent.TitleChanged("Title"))
        viewModel.onAddEditEvent(AddEditEvent.ContentChanged("Content"))

        viewModel.events.test {

            viewModel.onAddEditEvent(AddEditEvent.SaveClicked)

            val event = awaitItem()

            assert(event is NotesUiEvent.PopBackStack)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete note emits snackbar event`() = runTest {

        // GIVEN
        viewModel.init(null) // new note

        viewModel.onAddEditEvent(AddEditEvent.TitleChanged("Title"))
        viewModel.onAddEditEvent(AddEditEvent.ContentChanged("Content"))

        // WHEN
        viewModel.onAddEditEvent(AddEditEvent.SaveClicked)

        // advance coroutine execution
        advanceUntilIdle()

        // THEN: repository contains note
        assert(repository.notes.size == 1)

        viewModel.events.test {

            viewModel.deleteCurrentNote(noteId = 1)

            // advance coroutine execution
            advanceUntilIdle()

            assert(repository.notes.size == 0)
            val event = awaitItem()
            assert(event is NotesUiEvent.ShowSnackbar)
        }
    }

    @Test
    fun `undo delete restores recently deleted note`() = runTest {
        // GIVEN: A note exists and is then deleted
        viewModel.init(null)
        viewModel.onAddEditEvent(AddEditEvent.TitleChanged("Delete Me"))
        viewModel.onAddEditEvent(AddEditEvent.ContentChanged("Content"))
        viewModel.onAddEditEvent(AddEditEvent.SaveClicked)
        advanceUntilIdle()

        // Grab the generated ID from repository to delete it correctly
        val savedNoteId = repository.notes.first().id

        viewModel.events.test {
            viewModel.deleteCurrentNote(noteId = savedNoteId)

            val snackbarEvent = awaitItem()
            assert(snackbarEvent is NotesUiEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
        advanceUntilIdle()
        assert(repository.notes.isEmpty()) // Confirmed deleted

        // WHEN: Undo delete is clicked
        viewModel.undoDelete()
        advanceUntilIdle()

        // THEN: Note is restored back to the repository
        assert(repository.notes.size == 1)
        assert(repository.notes.first().title == "Delete Me")

        //id matches also.
        assert(repository.notes.first().id == savedNoteId)
    }

    @Test
    fun `save blank title updates ui state with error`() = runTest {
        // GIVEN: ViewModel initialized with an empty title string
        viewModel.init(null)
        viewModel.onAddEditEvent(AddEditEvent.TitleChanged("")) // Blank title
        viewModel.onAddEditEvent(AddEditEvent.ContentChanged("Some content"))

        // WHEN: Save is triggered
        viewModel.onAddEditEvent(AddEditEvent.SaveClicked)
        advanceUntilIdle()

        // THEN: UI state reflects the validation error thrown by usecase/entity
        val currentUiState = viewModel.addEditUiState.value
        assert(currentUiState.error != null)
    }

    @Test
    fun `edit existing note updates repository instead of inserting new`() = runTest {
        // GIVEN: A note already exists in the repository with ID 1
        val existingNote = Note(id = 1L, title = "Original Title", content = "Original Content")
        // 💡 FIX: Use the insert method instead of .add()
        repository.insert(existingNote)

        // Initialize ViewModel with the existing note's ID
        viewModel.init(1L)
        advanceUntilIdle()

        // Verify current state loaded the existing data correctly
        assert(viewModel.addEditUiState.value.title == "Original Title")

        // WHEN: User changes the title and hits save
        viewModel.onAddEditEvent(AddEditEvent.TitleChanged("Updated Title"))
        viewModel.onAddEditEvent(AddEditEvent.SaveClicked)
        advanceUntilIdle()

        // THEN: Repository size remains 1 (no duplicate insertion), but the data is updated
        assert(repository.notes.size == 1)
        assert(repository.notes.first().id == 1L)
        assert(repository.notes.first().title == "Updated Title")
    }

    @Test
    fun `deleteAndNavigateBack emits PopBackStack immediately and ShowSnackbar after delay`() = runTest {
        // GIVEN: A note exists in the repository
        val noteId = 1L
        val noteToDelete = Note(id = noteId, title = "To Be Deleted", content = "Content")
        repository.insert(noteToDelete)
        advanceUntilIdle()

        // Initialize ViewModel with the note ID to set currentNoteId
        viewModel.init(noteId)
        advanceUntilIdle()

        // 1. Establish the Turbine event listener stream
        viewModel.events.test {
            // WHEN: Triggering the delete and navigate back action
            viewModel.onAddEditEvent(AddEditEvent.DeleteClicked)

            // THEN: Step 1 - PopBackStack is emitted immediately
            val firstEvent = awaitItem()
            assert(firstEvent is NotesUiEvent.PopBackStack)

            // THEN: Step 2 - ShowSnackbar is emitted next (after the 100ms delay)
            val secondEvent = awaitItem()
            assert(secondEvent is NotesUiEvent.ShowSnackbar)

            val snackbar = secondEvent as NotesUiEvent.ShowSnackbar
            assert(snackbar.message == "Note deleted")
            assert(snackbar.action == "Undo")

            cancelAndIgnoreRemainingEvents()
        }

        // Verify the data was actually removed from the source repository
        assert(repository.notes.isEmpty())
    }



}