package com.defri.bookreflect.presentation.books.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.defri.bookreflect.R
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.model.BookStatus

@Composable
fun BookCard(
    book: Book,
    onBookClick: () -> Unit,
    onStatusChange: (BookStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onBookClick)
                .padding(16.dp),
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

            Column(
                modifier = Modifier.weight(1f)
            ) {
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

            IconButton(
                onClick = {
                    val newStatus = when (book.status) {
                        BookStatus.ADDED -> BookStatus.COMPLETED
                        null, BookStatus.COMPLETED -> BookStatus.ADDED
                    }
                    onStatusChange(newStatus)
                }
            ) {
                Icon(
                    imageVector = when (book.status) {
                        null, BookStatus.ADDED -> Icons.Default.BookmarkAdd
                        BookStatus.COMPLETED -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = when (book.status) {
                        BookStatus.ADDED -> MaterialTheme.colorScheme.primary
                        BookStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                        null -> MaterialTheme.colorScheme.secondary
                    }
                )
            }
        }
    }
}