package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.gui.NotesScreen
import com.example.todoapp.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val notesViewModel: NotesViewModel = viewModel()
                    NotesScreen(viewModel = notesViewModel)
                }
            }
        }
    }
}