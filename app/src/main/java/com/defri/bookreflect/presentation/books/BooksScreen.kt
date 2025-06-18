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
import com.defri.bookreflect.domain.model.Mood
import com.defri.bookreflect.presentation.books.components.BookCard

@Composable
fun BooksScreen(
    viewModel: BooksViewModel = hiltViewModel(),
    onNavigateToMoods: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(state.books) {
        if(state.books.isEmpty()){
            viewModel.setState { copy(isLoading = true) }
        }
        viewModel.handleEvent(BooksEvent.LoadBooks)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    bookMoods = state.moods.groupBy { it.bookId },
                    onAddMoodClick = { selectedBook ->
                        onNavigateToMoods(selectedBook.id)
                    }
                )
            }
        }
    }
}

@Composable
fun BooksList(
    books: List<Book>,
    bookMoods: Map<String, List<Mood>>,
    onAddMoodClick: (Book) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookCard(
                book = book,
                moods = bookMoods[book.id].orEmpty(),
                onAddMoodClick = onAddMoodClick
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