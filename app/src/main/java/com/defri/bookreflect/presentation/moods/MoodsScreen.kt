package com.defri.bookreflect.presentation.moods

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.defri.bookreflect.presentation.moods.components.*

@Composable
fun MoodsScreen(
    bookId: String? = null,
    bookTitle: String? = null,
    viewModel: MoodsViewModel = hiltViewModel(),
    onBackFromCreateMood: () -> Unit
) {
    if (bookId != null && bookTitle != null) {
        CreateMoodScreen(bookId, bookTitle, viewModel, {
            onBackFromCreateMood()
        })
    } else {
        ViewMoodsScreen(viewModel)
    }
}

@Composable
private fun CreateMoodScreen(
    bookId: String,
    bookTitle: String,
    viewModel: MoodsViewModel,
    onBack: () -> Unit,
    moodToEdit: MoodUi? = null
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    var showAddQuoteDialog by remember { mutableStateOf(false) }
    var newQuote by remember { mutableStateOf("") }
    var isEditingQuote by remember { mutableStateOf(false) }
    val allTags = remember {
        listOf("Sad", "Happy", "Angry", "Relaxed", "Motivated", "Nostalgic")
    }

    LaunchedEffect(moodToEdit) {
        moodToEdit?.let {
            viewModel.setState {
                copy(
                    currentNote = it.note,
                    currentQuotes = it.quotes,
                    selectedTags = it.tags.toSet()
                )
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxHeight = maxHeight
        val noteMaxHeight = maxHeight - 400.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = bookTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = "Select tags:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                allTags.forEach { tag ->
                    val isSelected = state.selectedTags.contains(tag)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable {
                                viewModel.handleEvent(MoodsEvent.ToggleTag(tag))
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tag,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            OutlinedTextField(
                value = state.currentNote,
                onValueChange = { viewModel.setState { copy(currentNote = it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = noteMaxHeight),
                label = { Text("Your reflection note") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Quotes:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                state.currentQuotes.forEachIndexed { index, quote ->
                    QuoteChip(
                        quote = quote,
                        onEditDelete = {
                            newQuote = quote
                            viewModel.handleEvent(MoodsEvent.RemoveQuote(index))
                            showAddQuoteDialog = true
                            isEditingQuote = true
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showAddQuoteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Quote")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (state.selectedTags.isNotEmpty() && state.currentNote.isNotBlank()) {
                        viewModel.handleEvent(
                            MoodsEvent.SaveMood(
                                moodId = moodToEdit?.id,
                                bookId = bookId,
                                tags = state.selectedTags.toList(),
                                note = state.currentNote,
                                quotes = state.currentQuotes
                            )
                        )
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.selectedTags.isNotEmpty() && state.currentNote.isNotBlank()
            ) {
                Text(if (moodToEdit != null) "Update Reflection" else "Save Reflection")
            }
            if (showAddQuoteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showAddQuoteDialog = false
                        isEditingQuote = false
                        newQuote = ""
                    },
                    title =  { Text(if (isEditingQuote) "Edit Quote" else "Add Quote") },
                    text = {
                        OutlinedTextField(
                            value = newQuote,
                            onValueChange = { newQuote = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Quote text") }
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newQuote.isNotBlank()) {
                                    viewModel.handleEvent(MoodsEvent.AddQuote(newQuote))
                                    newQuote = ""
                                    showAddQuoteDialog = false
                                    isEditingQuote = false
                                }
                            }
                        ) {
                            Text(if (isEditingQuote) "Confirm" else "Add")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showAddQuoteDialog = false
                                newQuote = ""
                                isEditingQuote = false
                            }
                        ) {
                            Text(if (isEditingQuote) "Delete" else "Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuoteChip(quote: String, onEditDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = quote.take(20).plus(if (quote.length > 20) "…" else ""),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            modifier = Modifier.padding(end = 4.dp)
        )
        IconButton(onClick = onEditDelete, modifier = Modifier.size(16.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit or Delete", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ViewMoodsScreen(viewModel: MoodsViewModel) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<MoodUi?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(state.moods) {
        if (state.moods.isEmpty() && !state.isLoading) {
            viewModel.setState { copy(isLoading = true) }
        }
        viewModel.handleEvent(MoodsEvent.LoadMoods)
    }

    when {
        isEditing && selectedMood != null -> {
            CreateMoodScreen(
                bookId = selectedMood!!.bookId,
                bookTitle = selectedMood!!.bookTitle,
                viewModel = viewModel,
                moodToEdit = selectedMood,
                onBack = {
                    isEditing = false
                    selectedMood = null
                }
            )
        }

        selectedMood != null -> {
            MoodDetailScreen(
                mood = selectedMood!!,
                onBack = { selectedMood = null },
                onEdit = {
                    isEditing = true
                }
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search moods...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                val filteredMoods by remember(searchQuery, state.moods) {
                    derivedStateOf {
                        if (searchQuery.isNotEmpty()) {
                            state.moods.filter {
                                it.note.contains(searchQuery, ignoreCase = true) ||
                                        it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) } ||
                                        it.quotes.any { quote -> quote.contains(searchQuery, ignoreCase = true) }
                            }
                        } else {
                            state.moods
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        state.isLoading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }

                        filteredMoods.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "No matching moods found"
                                    else "No moods yet",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredMoods, key = { it.id }) { mood ->
                                    MoodCard(
                                        mood = mood,
                                        onClick = { selectedMood = mood },
                                        onDelete = { viewModel.handleEvent(MoodsEvent.DeleteMood(mood.id)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoodDetailScreen(
    mood: MoodUi,
    onBack: () -> Unit,
    onEdit: (MoodUi) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = mood.bookTitle,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                mood.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Text(
                    text = mood.note,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (mood.quotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Quotes:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    mood.quotes.forEach { quote ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "“$quote”",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onEdit(mood) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Mood")
        }
    }
}
