package com.defri.bookreflect.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.defri.bookreflect.R
import com.defri.bookreflect.presentation.books.BooksScreen
import com.defri.bookreflect.presentation.home.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        TabItem("Home", Icons.Default.Home),
        TabItem("Books", Icons.Default.Book),
        TabItem("Mood", Icons.Default.EmojiEmotions)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 56.dp)
                    ) {
                        Text(
                            text = tabs[selectedTab].title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_background),
                        contentDescription = "BookReflect Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(start = 16.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeContent()
                1 -> BooksScreen(
                    onNavigateToMoods = {}
                )

                2 -> MoodContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (state.globalBooks.isEmpty() && !state.isLoading) {
            viewModel.handleEvent(HomeEvent.LoadData)
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.handleEvent(HomeEvent.LoadData)
            isRefreshing = false
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val totalItemsCount = state.globalBooks.size
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItemsCount - 3 &&
                    !state.isLoadingMore && !state.isEndReached && !state.isLoading
                ) {
                    viewModel.handleEvent(HomeEvent.LoadMoreGlobalBooks)
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading && !isRefreshing) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@HomeContent
        }
        if (state.error != null) {
            Text(
                text = state.error ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
            return@HomeContent
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome to BookReflect",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            val recommendedBooks by remember(state.globalBooks, state.userBooks) {
                derivedStateOf {
                    state.globalBooks.filter { gb ->
                        state.userBooks.none { ub -> ub.id == gb.id }
                    }
                }
            }
            if (recommendedBooks.isNotEmpty()) {
                Text(
                    text = "Recommended Books",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(recommendedBooks, key = { it.id }) { book ->
                        SquareBookCard(
                            book = book,
                            onAddBook = { viewModel.handleEvent(HomeEvent.AddBook(book)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            Text(
                text = "All Books",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = state.searchQuery,
                onValueChange = { q ->
                    viewModel.handleEvent(HomeEvent.UpdateSearchQuery(q))
                },
                placeholder = { Text("Search books...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            val filteredAll by remember(state.globalBooks, state.searchQuery) {
                derivedStateOf {
                    if (state.searchQuery.isBlank()) {
                        state.globalBooks
                    } else {
                        val q = state.searchQuery.lowercase()
                        state.globalBooks.filter { book ->
                            book.title.lowercase().contains(q) ||
                                    book.author.lowercase().contains(q) ||
                                    book.description.lowercase().contains(q)
                        }
                    }
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    state = listState
                ) {
                    items(filteredAll, key = { it.id }) { book ->
                        val alreadyAdded = state.userBooks.any { ub -> ub.id == book.id }
                        RectangularBookCard(
                            book = book,
                            showAddButton = !alreadyAdded,
                            onAddBook = { viewModel.handleEvent(HomeEvent.AddBook(book)) }
                        )
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Reading Mood",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Placeholder for mood notes
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "No mood notes yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private data class TabItem(
    val title: String,
    val icon: ImageVector
) 