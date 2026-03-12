package com.example.todoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.todoapp.data.NoteDatabase
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.gui.NotesAddEditScreen
import com.example.todoapp.gui.NotesListScreen
import com.example.todoapp.viewmodel.NotesViewModel
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule


import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class NotesAppTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: NoteDatabase
    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NotesViewModel

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
        viewModel = NotesViewModel(repository)

        composeTestRule.setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "note_list"
            ) {

                composable("note_list") {
                    NotesListScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                composable("add_edit_note") { // Matches FAB call
                    NotesAddEditScreen(navController, -1L, viewModel)
                }

                composable("add_edit_note?noteId={noteId}") { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
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

    @Test
    fun add_note_flow_test() {

        // 1️⃣ Verify we are on NotesListScreen
        composeTestRule
            .onNodeWithTag("notes_list")
            .assertIsDisplayed()

        // 2️⃣ Click FAB
        composeTestRule
            .onNodeWithTag("fab_add_note")
            .performClick()

        // 3️⃣ Verify AddEditNoteScreen opened
        composeTestRule
            .onNodeWithTag("title_input")
            .assertIsDisplayed()

        // 4️⃣ Enter text
        composeTestRule
            .onNodeWithTag("title_input")
            .performTextInput("test1")

        composeTestRule
            .onNodeWithTag("content_input")
            .performTextInput("test1")

        // 5️⃣ Press Save
        composeTestRule
            .onNodeWithTag("save_note")
            .performClick()

        // 6️⃣ Verify back to NotesListScreen
        composeTestRule
            .onNodeWithTag("notes_list")
            .assertIsDisplayed()

        // 7️⃣ Verify note added (Wait up to 3 seconds for it to appear)
        composeTestRule.waitUntil(3000) {
            composeTestRule
                .onAllNodesWithText("test1")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 6️⃣ Give the async database operation a moment
        composeTestRule.waitForIdle()

        composeTestRule
                .onNodeWithText("test1")
                .assertIsDisplayed()
    }

    @Test
    fun edit_note_flow_test() {

        // 1️⃣ Verify we are on NotesListScreen
        composeTestRule
            .onNodeWithTag("notes_list")
            .assertIsDisplayed()

        // 2️⃣ Click FAB
        composeTestRule
            .onNodeWithTag("fab_add_note")
            .performClick()

        // 3️⃣ Verify AddEditNoteScreen opened
        composeTestRule
            .onNodeWithTag("title_input")
            .assertIsDisplayed()

        // 4️⃣ Enter text
        composeTestRule
            .onNodeWithTag("title_input")
            .performTextInput("test1")

        composeTestRule
            .onNodeWithTag("content_input")
            .performTextInput("test1")

        // 5️⃣ Press Save
        composeTestRule
            .onNodeWithTag("save_note")
            .performClick()

        // 6️⃣ Verify back to NotesListScreen
        composeTestRule
            .onNodeWithTag("notes_list")
            .assertIsDisplayed()

        // 7️⃣ Verify note added (Wait up to 3 seconds for it to appear)
        composeTestRule.waitUntil(3000) {
            composeTestRule
                .onAllNodesWithText("test1")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 6️⃣ Give the async database operation a moment
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("test1")
            .assertIsDisplayed()

        // --- NEW STEPS ADDED BELOW ---

        // 9️⃣ Click on the newly added note to open it for editing
        composeTestRule
            .onNodeWithText("test1")
            .performClick()

        // 🔟 Verify AddEditNoteScreen is opened again and perform text replacements
        /*
        composeTestRule
            .onNodeWithTag("title_input")
            .assertIsDisplayed()
            .performTextReplacement("test1_edited_title")

        composeTestRule
            .onNodeWithTag("content_input")
            .performTextReplacement("test1_edited_content")

         */

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

        // 1️⃣2️⃣ Verify back to NotesListScreen
        composeTestRule
            .onNodeWithTag("notes_list")
            .assertIsDisplayed()

        // 1️⃣3️⃣ Verify the note is updated with the new text
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithText("test1_edited_title", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Ensure the old title is GONE
        composeTestRule
            .onNodeWithText("test1")
            .assertDoesNotExist()

        // Now assert the new content
        composeTestRule
            .onNodeWithText("test1_edited_title", useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.waitForIdle()

        // Assert the edited title and content are displayed
        composeTestRule
            .onNodeWithText("test1_edited_title",useUnmergedTree=true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("test1_edited_content",useUnmergedTree=true)
            .assertIsDisplayed()


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

}