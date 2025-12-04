package com.balu.quoteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balu.quoteapp.api.RetroAPIClient
import com.balu.quoteapp.endpoint.Quote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- MVI: STATE ---
// A single, immutable object representing the entire screen state.
data class QuoteScreenState(
    val isLoading: Boolean = true,
    val allQuotes: List<Quote> = emptyList(), // Master list from the API
    val displayedQuotes: List<Quote> = emptyList(), // Filtered list for display
    val paginatedQuotes: List<Quote> = emptyList(), // Sublist for the current page
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val error: String? = null,
)

// --- MVI: INTENT ---
// A sealed class representing all possible user actions.
sealed class QuoteScreenIntent {
    data class SearchQueryChanged(val query: String) : QuoteScreenIntent()
    data class PageChanged(val newPage: Int) : QuoteScreenIntent()
    data object SearchClicked : QuoteScreenIntent()
    data object DisplayAllClicked : QuoteScreenIntent()
    data object RetryNetworkRequest : QuoteScreenIntent()
}

class QuotesViewModel : ViewModel() {

    companion object {
        private const val QUOTES_PER_PAGE = 5
    }

    private val _state = MutableStateFlow(QuoteScreenState())
    val state: StateFlow<QuoteScreenState> = _state.asStateFlow()

    init {
        // Fetch initial data when ViewModel is created.
        fetchQuotes()
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * MVI: This is the single entry point for all user actions (Intents).
     */
    fun processIntent(intent: QuoteScreenIntent) {
        when (intent) {
            is QuoteScreenIntent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
            is QuoteScreenIntent.PageChanged -> {
                _state.update { it.copy(currentPage = intent.newPage) }
                updatePagination()
            }
            is QuoteScreenIntent.SearchClicked -> {
                performSearch()
            }
            is QuoteScreenIntent.DisplayAllClicked -> {
                _state.update {
                    it.copy(
                        displayedQuotes = it.allQuotes,
                        currentPage = 1,
                        searchQuery = "",
                    )
                }
                updatePagination()
            }
            is QuoteScreenIntent.RetryNetworkRequest -> {
                fetchQuotes()
            }
        }
    }

    fun fetchQuotes() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val response = RetroAPIClient.quotesApiService.getQuotes()
                _state.update {
                    it.copy(
                        allQuotes = response.quotes,
                        displayedQuotes = response.quotes,
                        isLoading = false,
                    )
                }
                updatePagination() // Update pagination with the new data
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "An unknown error occurred",
                        isLoading = false,
                    )
                }
            }
        }
    }

    /**
     * Centralized pagination logic. Called whenever the displayed data or page changes.
     */
    private fun updatePagination() {
        val currentState = _state.value
        val totalPages = (currentState.displayedQuotes.size + QUOTES_PER_PAGE -1)/QUOTES_PER_PAGE.coerceAtLeast(1)
        val startIndex = (currentState.currentPage - 1) * QUOTES_PER_PAGE
        val endIndex = (startIndex + QUOTES_PER_PAGE).coerceAtMost(currentState.displayedQuotes.size)

        val paginated = if(startIndex < endIndex){
            currentState.displayedQuotes.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        _state.update {
            it.copy(
                totalPages = totalPages,
                paginatedQuotes = paginated
            )
        }
    }

    private fun performSearch(){
        _state.update { it.copy(currentPage = 1) } // Reset to the first page when searching.
        val currentState = _state.value
        val filteredQuotes = if(currentState.searchQuery.isBlank()){
            currentState.allQuotes
        } else {
            currentState.allQuotes.filter { quote ->
                quote.quote.contains(currentState.searchQuery, ignoreCase = true) ||
                        (quote.author.contains(currentState.searchQuery, ignoreCase = true))
            }
        }
        _state.update { it.copy(displayedQuotes =  filteredQuotes) }
        updatePagination()
    }
}