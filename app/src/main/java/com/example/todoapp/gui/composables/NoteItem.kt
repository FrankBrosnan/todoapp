package com.example.todoapp.gui.composables

import androidx.compose.foundation.clickable
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
fun NoteItem(note: Note,
             onClick:()->Unit,
             modifier: Modifier=Modifier
             ) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("note_item_${note.id}").clickable{onClick()},
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp).testTag("note_click_area_${note.id}")
        ) {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).testTag("note_title_${note.id}")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.testTag("note_content_${note.id}")
            )

            Spacer(modifier = Modifier.height(12.dp))


        }




        }



    }
