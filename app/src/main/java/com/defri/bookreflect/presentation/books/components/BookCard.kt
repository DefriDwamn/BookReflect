package com.defri.bookreflect.presentation.books.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.model.Mood

@Composable
fun BookCard(
    book: Book,
    moods: List<Mood>,
    onAddMoodClick: (Book) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Book,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (showDialog) {
        BookDetailDialog(
            book = book,
            moods = moods,
            onDismiss = { showDialog = false },
            onAddMoodClick = onAddMoodClick
        )
    }
}

@Composable
fun BookDetailDialog(
    book: Book,
    moods: List<Mood>,
    onDismiss: () -> Unit,
    onAddMoodClick: (Book) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(book.title) },
        text = {
            Column {
                Text(
                    text = book.description.ifBlank { "No description available" },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Moods:",
                    style = MaterialTheme.typography.titleMedium
                )

                if (moods.isEmpty()) {
                    Text(
                        text = "No moods yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        moods.forEach { mood ->
                            MoodItem(mood)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onAddMoodClick(book) }) {
                Text("Add Mood")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun MoodItem(mood: Mood) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "#${mood.tag}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = mood.note,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}