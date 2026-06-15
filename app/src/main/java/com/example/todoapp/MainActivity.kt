package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.data.NoteRepository
import com.example.todoapp.domain.usecases.AddNoteUseCase
import com.example.todoapp.domain.usecases.DeleteNoteUseCase
import com.example.todoapp.domain.usecases.GetAllNotesUseCase
import com.example.todoapp.domain.usecases.GetNoteUseCase
import com.example.todoapp.domain.usecases.NoteUseCases
import com.example.todoapp.domain.usecases.UpdateNoteUseCase
import com.example.todoapp.gui.composables.AppNavHost
import com.example.todoapp.viewmodel.NoteViewModelFactory
import com.example.todoapp.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Get the repository (adjust this to match how your app stores it)
        val repository = NoteRepository.getInstance(applicationContext)

        val useCases = NoteUseCases(
            addNote = AddNoteUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository),
            updateNote = UpdateNoteUseCase(repository),
            getAllNotes = GetAllNotesUseCase(repository),
            getNote = GetNoteUseCase(repository)
        )

        val factory = NoteViewModelFactory(useCases)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                // SINGLE ViewModel instance
                val viewModel: NotesViewModel = viewModel(factory = factory)




                AppNavHost(navController, viewModel)

            }
        }
    }
}