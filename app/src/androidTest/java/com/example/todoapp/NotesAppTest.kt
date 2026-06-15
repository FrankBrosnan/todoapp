package com.example.todoapp

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.todoapp.data.NoteDatabase
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.domain.usecases.AddNoteUseCase
import com.example.todoapp.domain.usecases.DeleteNoteUseCase
import com.example.todoapp.domain.usecases.GetAllNotesUseCase
import com.example.todoapp.domain.usecases.GetNoteUseCase
import com.example.todoapp.domain.usecases.NoteUseCases
import com.example.todoapp.domain.usecases.UpdateNoteUseCase
import com.example.todoapp.gui.composables.NotesAddEditScreen
import com.example.todoapp.gui.composables.NotesListScreen
import com.example.todoapp.gui.navigation.Routes
import com.example.todoapp.viewmodel.NotesViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule


import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class NotesAppTest {

    //globals
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var database: NoteDatabase
    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NotesViewModel

    //Utilities/Helper Functions.
    fun printDatabaseRecords() = runBlocking {
        // 1. Fetch the records from your DAO
        val notes = database.noteDao().getAllRecords()

        // 2. Print them to Logcat
        notes.forEach { note ->
            Log.d("DB_RECORDS", "Note: ${note.title}, Content: ${note.content}")
        }
    }

    private fun addNote(title: String, content: String) {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("fab_add_note").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("notes_add_edit").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("title_input").performTextInput(title)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("content_input").performTextInput(content)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("save_note").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForNoteToAppear(title: String) {
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithText(title)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun waitForNoteToDisappear(title: String) {
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithText(title)
                .fetchSemanticsNodes()
                .isEmpty()
        }
    }



    @Before
    fun setup() {
        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = NoteRepository(database.noteDao())

        val useCases = NoteUseCases(
            addNote = AddNoteUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository),
            updateNote = UpdateNoteUseCase(repository),
            getAllNotes = GetAllNotesUseCase(repository),
            getNote = GetNoteUseCase(repository)
        )

        viewModel = NotesViewModel(useCases)

        composeTestRule.setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Routes.NOTES_LIST
            ) {

                composable(Routes.NOTES_LIST) {
                    NotesListScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                composable(Routes.ADD_EDIT_NOTE) { // Matches FAB call
                    NotesAddEditScreen(
                        viewModel = viewModel,
                        navController = navController,
                        noteId = null)
                }

                // In your setup() method's NavHost:
                composable(
                    route = "add_edit_note?noteId={noteId}",
                    arguments = listOf(
                        navArgument("noteId") {
                            type = NavType.LongType
                            defaultValue = -1L
                        }
                    )
                ) { backStackEntry ->
                    // Now this will actually contain the ID from the URL
                    val noteId = backStackEntry.arguments?.getLong("noteId").takeIf { it != -1L }

                    NotesAddEditScreen(
                        navController = navController,
                        noteId = noteId,
                        viewModel = viewModel
                    )
                }

            }
        }
    }

    @After
    fun teardown() {
        database.close()
    }


    //Tests

    @Test
    fun empty_list_shows_empty_state() {
        composeTestRule
            .onNodeWithTag("empty_state")
            .assertIsDisplayed()
    }

    @Test
    fun fab_navigates_to_add_screen() {
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithTag("fab_add_note")
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("notes_add_edit")
        composeTestRule
            .onNodeWithTag("title_input")
            .assertIsDisplayed()
    }

    @Test
    fun back_from_add_screen_returns_to_list() {
        fail("Not Yet Implemented")
    }


    @Test
    fun add_note_flow_test() {

        // 1️⃣ Verify we are on empty NotesListScreen
        composeTestRule
            .onNodeWithTag("empty_state")
            .assertIsDisplayed()

        addNote("test1_title","test1_content")

        // 6️⃣ Verify back to NotesListScreen
        composeTestRule
            .onNodeWithTag("notes_list")
            .assertIsDisplayed()
        composeTestRule.waitForIdle()

        // 7️⃣ Verify note added (Wait up to 3 seconds for it to appear)
        composeTestRule
            .onNodeWithTag("note_item_1")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText("test1_title")
            .fetchSemanticsNodes().isNotEmpty()

        composeTestRule
            .onAllNodesWithText("test1_content")
            .fetchSemanticsNodes().isNotEmpty()


    }

    @Test
    fun multiple_notes_are_displayed() {
        addNote("note1", "c1")
        composeTestRule.waitForIdle()
        addNote("note2", "c2")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("note_item_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("note_item_2").assertIsDisplayed()

        composeTestRule.onNodeWithText("note1").assertIsDisplayed()
        composeTestRule.onNodeWithText("note2").assertIsDisplayed()

        composeTestRule.onNodeWithText("c1").assertIsDisplayed()
        composeTestRule.onNodeWithText("c2").assertIsDisplayed()
    }

    @Test
    fun edit_note_flow_test() {

        // Verify we are on NotesListScreen
        composeTestRule
            .onNodeWithTag("empty_state")
            .assertIsDisplayed()

        addNote("test1_title","test1_content")

        composeTestRule.waitForIdle()
        // 6️⃣ Verify back to NotesListScreen
        composeTestRule
            .onAllNodesWithTag(("notes_list"))
            .fetchSemanticsNodes().isNotEmpty()

        //printDatabaseRecords()

        // 7️⃣ Verify note added (Wait up to 3 seconds for it to appear)
        composeTestRule
            .onNodeWithTag("note_item_1")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText("test1_title")
            .fetchSemanticsNodes().isNotEmpty()

        composeTestRule
            .onAllNodesWithText("test1_content")
            .fetchSemanticsNodes().isNotEmpty()


        // 6️⃣ Give the async database operation a moment
        composeTestRule.waitForIdle()


        // --- NEW STEPS ADDED BELOW ---

        // 9️⃣ Click on the newly added note to open it for editing
        composeTestRule
            .onNodeWithTag("note_item_1")
            .performClick()

        // Robust way to replace text: Clear then Type
        composeTestRule.onNodeWithTag("title_input").performTextClearance()
        composeTestRule.onNodeWithTag("title_input").performTextInput("test1_edited_title")

        composeTestRule.onNodeWithTag("content_input").performTextClearance()
        composeTestRule.onNodeWithTag("content_input").performTextInput("test1_edited_content")

        composeTestRule.waitForIdle()

        // 1️⃣1️⃣ Press Save again
        composeTestRule
            .onNodeWithTag("save_note")
            .performClick()

        composeTestRule.waitForIdle()

        // 1️⃣2️⃣ Verify back to NotesListScreen
        composeTestRule
            .onNodeWithTag("notes_list")
            .assertIsDisplayed()

        //DEBUG whats in db ?
        //printDatabaseRecords()

        // 7️⃣ Verify note added (Wait up to 3 seconds for it to appear)
        composeTestRule
            .onNodeWithTag("note_item_1")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText("test1_edited_title")
            .fetchSemanticsNodes().isNotEmpty()

        composeTestRule
            .onAllNodesWithText("test1_edited_content")
            .fetchSemanticsNodes().isNotEmpty()

    }


    @Test
    fun swipe_to_delete_test() {
        // 1️⃣ Add a note first so there is something to delete
        composeTestRule
            .onNodeWithTag("fab_add_note")
            .performClick()
        composeTestRule
            .onNodeWithTag("title_input")
            .performTextInput("Delete Me")
        composeTestRule
            .onNodeWithTag("save_note")
            .performClick()

        composeTestRule.waitUntil(3000) {
            composeTestRule
                .onAllNodesWithText("Delete Me")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 2️⃣ Verify the note appeared
        composeTestRule
            .onNodeWithText("Delete Me")
            .assertIsDisplayed()

        // 3️⃣ Perform Swipe (Right to Left)
        // Note: Use the tag of the parent container or the text itself
        composeTestRule
            .onNodeWithText("Delete Me")
            .performTouchInput {
                swipeLeft(
                    startX = right,
                    endX = left,
                    durationMillis = 5000
                )
            }

        // 4️⃣ Wait for the deletion and UI update
        composeTestRule.waitForIdle()

        // 5️⃣ Verify "Delete Me" is no longer on screen
        composeTestRule
            .onNodeWithText("Delete Me")
            .assertDoesNotExist()
    }

    @Test
    fun deleteNoteFromAddEditScreen_removesNoteAndNavigatesBack() {
        val testTitle = "Note to be deleted"
        val testContent = "This is a temporary note."

        // 1. Add a new note using the floating action button
        composeTestRule.onNodeWithTag("fab_add_note").performClick()

        // Fill in the title and content
        // (Make sure to assign these test tags in your NotesAddEditScreen composable)
        composeTestRule.onNodeWithTag("title_input").performTextInput(testTitle)
        composeTestRule.onNodeWithTag("content_input").performTextInput(testContent)

        // Save the note and navigate back
        composeTestRule.onNodeWithTag("save_note").performClick()

        // 2. Verify the note was successfully added to the list
        composeTestRule.onNodeWithText(testTitle).assertIsDisplayed()

        // 3. Click on the newly created note to navigate to NotesAddEditScreen
        composeTestRule.onNodeWithText(testTitle).performClick()

        // 4. Click the delete icon in the Add/Edit screen
        composeTestRule.onNodeWithTag("action_delete").performClick()

        // 5. Verify the app navigates back to the list and the note is removed
        composeTestRule.onNodeWithText(testTitle).assertDoesNotExist()

        // Optional: If you want to verify the snackbar shows up
        //composeTestRule.onNodeWithText("Note deleted", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun undo_delete_restores_note() {

        // Add note
        composeTestRule
            .onNodeWithTag("fab_add_note")
            .performClick()

        composeTestRule
            .onNodeWithTag("title_input")
            .performTextInput("Undo Test")

        composeTestRule
            .onNodeWithTag("save_note")
            .performClick()

        composeTestRule.waitUntil(3000) {
            composeTestRule
                .onAllNodesWithText("Undo Test")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Verify note exists
        composeTestRule
            .onNodeWithText("Undo Test")
            .assertIsDisplayed()

        // Swipe delete
        composeTestRule
            .onNodeWithText("Undo Test")
            .performTouchInput {
                swipeLeft(
                    startX = right,
                    endX = left,
                    durationMillis = 5000
                )
            }


        // Verify note removed

        waitForNoteToDisappear("Undo Test")

        // Wait for snackbar
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithText("Undo", ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Click Undo
        composeTestRule
            .onNodeWithText("Undo", ignoreCase = true)
            .performClick()

        composeTestRule.waitForIdle()

        // Verify note restored
        waitForNoteToAppear("Undo Test")

        composeTestRule
            .onNodeWithText("Undo Test")
            .assertIsDisplayed()
    }

}