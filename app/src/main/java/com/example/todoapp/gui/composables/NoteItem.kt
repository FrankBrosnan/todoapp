package com.example.todoapp.gui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.Note

@Composable
fun NoteItem(note: Note, onDelete: (Note) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        /*
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(note.title, modifier = Modifier.weight(1f))
            IconButton(onClick = { onDelete(note) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
        */

        Column(Modifier.padding(16.dp)) {
            Text(text = note.title, modifier = Modifier.testTag("note_title_${note.id}"), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
        }


    }
}