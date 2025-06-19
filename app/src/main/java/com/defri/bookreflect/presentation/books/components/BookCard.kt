package com.defri.bookreflect.presentation.books.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.defri.bookreflect.domain.model.Book
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BookCard(
    book: Book,
    onAddMoodClick: (Book) -> Unit,
    onDeleteBookWithMoods: (Book) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val placeholderImage = book.coverUrl.ifBlank { "https://placehold.co/200x300?text=No+Cover" }
    val context = LocalContext.current

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
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                GlideImage(model = book.coverUrl,
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    requestBuilderTransform = { builder ->
                        builder.error(
                            Glide.with(context).load(placeholderImage)
                        )
                    })
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
            onDismiss = { showDialog = false },
            onAddMoodClick = onAddMoodClick,
            onDeleteBookWithMoods = { onDeleteBookWithMoods(book) })
    }
}

@Composable
fun BookDetailDialog(
    book: Book,
    onDismiss: () -> Unit,
    onAddMoodClick: (Book) -> Unit,
    onDeleteBookWithMoods: () -> Unit,
) {
    var showHoldHint by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var holdJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()
    AlertDialog(onDismissRequest = onDismiss, title = { Text(book.title) }, text = {
        Column {
            Box(
                modifier = Modifier
                    .heightIn(min = 100.dp, max = 250.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                val description = remember(book.description) {
                    book.description.ifBlank { "No description available" }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (holdProgress > 0f) {
                LinearProgressIndicator(
                    progress = { holdProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }, confirmButton = {}, dismissButton = {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    onAddMoodClick(book)
                    onDismiss()
                }) {
                    Text("Add Mood")
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                var isPressed by remember { mutableStateOf(false) }
                val infiniteTransition = rememberInfiniteTransition()
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = if (isPressed) 1.05f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInteropFilter {
                            when (it.action) {
                                android.view.MotionEvent.ACTION_DOWN -> {
                                    showHoldHint = true
                                    isPressed = true
                                    val totalTime = 5000L
                                    holdJob?.cancel()
                                    holdJob = scope.launch {
                                        val startTime = System.currentTimeMillis()
                                        while (isActive) {
                                            val elapsed = System.currentTimeMillis() - startTime
                                            holdProgress =
                                                (elapsed.toFloat() / totalTime).coerceAtMost(1f)
                                            if (elapsed >= totalTime) {
                                                onDeleteBookWithMoods()
                                                onDismiss()
                                                break
                                            }
                                            delay(16)
                                        }
                                    }
                                    true
                                }

                                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                                    isPressed = false
                                    showHoldHint = false
                                    holdJob?.cancel()
                                    holdProgress = 0f
                                    true
                                }

                                else -> false
                            }
                        }
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .clip(MaterialTheme.shapes.medium),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Remove book with moods", style = MaterialTheme.typography.labelLarge)
                        if (showHoldHint) {
                            Text(
                                "Hold for 5 seconds to delete",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    })
}
