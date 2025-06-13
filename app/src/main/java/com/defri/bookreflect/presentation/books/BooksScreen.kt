package com.defri.bookreflect.presentation.books

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.model.BookStatus
import com.defri.bookreflect.presentation.books.components.BookCard

@Composable
fun BooksScreen(
    viewModel: BooksViewModel = hiltViewModel(),
    onNavigateToBookDetail: (String) -> Unit,
    ) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(BooksEvent.LoadBooks)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isDialogOpen = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Book")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = paddingValues.calculateBottomPadding()
                    )
                )
                .padding(horizontal = 16.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search books...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            val filteredBooks = if (searchQuery.isNotEmpty()) {
                state.books.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.author.contains(searchQuery, ignoreCase = true)
                }
            } else {
                state.books
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    filteredBooks.isEmpty() -> EmptyBooksList()
                    else -> BooksList(
                        books = filteredBooks,
                        onBookClick = onNavigateToBookDetail,
                        onStatusChange = { book, status ->
                            viewModel.handleEvent(BooksEvent.UpdateStatus(book, status))
                        }
                    )
                }
            }
        }
    }

    if (isDialogOpen) {
        CreateBookDialog(
            onDismiss = { isDialogOpen = false },
            onCreateBook = { book ->
                viewModel.handleEvent(BooksEvent.CreateBook(book))
                isDialogOpen = false
            }
        )
    }
}

@Composable
private fun BooksList(
    books: List<Book>,
    onBookClick: (String) -> Unit,
    onStatusChange: (Book, BookStatus) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookCard(
                book = book,
                onBookClick = { onBookClick(book.id) },
                onStatusChange = { status -> onStatusChange(book, status) }
            )
        }
    }
}

@Composable
private fun EmptyBooksList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You dont have any books",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun CreateBookDialog(
    onDismiss: () -> Unit,
    onCreateBook: (Book) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Book") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val book = Book(
                        id = "",
                        title = title,
                        author = author,
                        isLocal = true,
                        description = "",
                        coverUrl = "",
                        status = BookStatus.ADDED
                    )
                    onCreateBook(book)
                },
                enabled = title.isNotBlank() && author.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}