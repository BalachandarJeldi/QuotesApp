package com.balu.quoteapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.balu.quoteapp.endpoint.Quote
import com.balu.quoteapp.viewmodel.QuotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteScreen(quotesViewModel: QuotesViewModel = viewModel()) {
    val quotes by quotesViewModel.quotes.collectAsState()
    val error by quotesViewModel.error.collectAsState()

    var displayedQuotes by remember { mutableStateOf<List<Quote>>(emptyList()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        quotesViewModel.fetchQuotes()
    }
    Scaffold(topBar = {
        TopAppBar(title = {Text("Quotes")})
    }) { padding ->
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
                // Display Search Bar
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(Modifier.padding(8.dp)) {
                            if (searchQuery.text.isEmpty()) {
                                Text("Search for a quote or author")
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons for "Display All" and "Search"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { displayedQuotes = quotes }) {
                        Text("Display All Quotes")
                    }
                    Button(onClick = {
                        displayedQuotes = quotes.filter {
                            it.quote.contains(searchQuery.text, ignoreCase = true) ||
                                    it.author.contains(searchQuery.text, ignoreCase = true)
                        }
                    }) {
                        Text("Search")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Quotes
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedQuotes.size) { index ->
                        val quote = displayedQuotes[index]
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
            }
        }
    }
}