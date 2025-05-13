package com.balu.quoteapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.balu.quoteapp.endpoint.Quote
import com.balu.quoteapp.viewmodel.QuotesViewModel
import retrofit2.http.Query

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteScreen(quotesViewModel: QuotesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val quotes by quotesViewModel.quotes.collectAsState()
    val error by quotesViewModel.error.collectAsState()

    var displayedQuotes by remember { mutableStateOf<List<Quote>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    // Pagination State
    val quotesPerPage = 5
    var currentPage by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        quotesViewModel.fetchQuotes()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Quotes App") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (error != null) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )
            } else if (quotes.isEmpty()) {
                CircularProgressIndicator()
            } else {
                // Row for Search Bar and Search Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search for a quote or author") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        modifier = Modifier.weight(1f),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                performSearch(
                                    searchQuery = searchQuery,
                                    quotes = quotes,
                                    onResult = {displayedQuotes = it},
                                    resetPage = {currentPage = 1})
                                }
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = {
                        performSearch(
                            searchQuery = searchQuery,
                            quotes = quotes,
                            onResult = {displayedQuotes = it},
                            resetPage = {currentPage = 1})
                    }) {
                        Text("Search")
                    }
                }

                // Row for Display All Quotes Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        displayedQuotes = quotes
                        currentPage = 1 // Reset to first page on "Display All"
                    }) {
                        Text("Display All Quotes")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Paginated Quotes Display
                val totalPages = (displayedQuotes.size + quotesPerPage - 1) / quotesPerPage // Ceiling division
                val startIndex = (currentPage - 1) * quotesPerPage
                val endIndex = (startIndex + quotesPerPage).coerceAtMost(displayedQuotes.size)
                val paginatedQuotes = if (displayedQuotes.isNotEmpty()) {
                    displayedQuotes.subList(startIndex, endIndex)
                } else {
                    emptyList()
                }

                LazyColumn(
                    modifier = Modifier.weight(1f), // Fill available space
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(paginatedQuotes.size) { index ->
                        val quote = paginatedQuotes[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "\"${quote.quote}\"",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "- ${quote.author}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pagination Links
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (currentPage > 1) {
                        TextButton(onClick = { currentPage-- }) {
                            Text("Previous")
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Page Numbers
                    (1..totalPages).forEach { page ->
                        TextButton(onClick = { currentPage = page }) {
                            Text(
                                text = page.toString(),
                                color = if (page == currentPage) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (currentPage < totalPages) {
                        TextButton(onClick = { currentPage++ }) {
                            Text("Next")
                        }
                    }
                }
            }
        }
    }
}

fun performSearch(searchQuery: String,
                  quotes: List<Quote>,
                  onResult: (List<Quote>) -> Unit,
                  resetPage: ()-> Unit){
    val filtered = quotes.filter {
        it.quote.contains(searchQuery, ignoreCase = true) ||
                it.author.contains(searchQuery, ignoreCase = true)
    }
    onResult(filtered)
    resetPage()
}
