package com.balu.quoteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balu.quoteapp.api.RetroAPIClient
import com.balu.quoteapp.endpoint.Quote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuotesViewModel : ViewModel(){
    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchQuotes() {
        viewModelScope.launch {
            try {
                val response = RetroAPIClient.quotesApiService.getQuotes()
                _quotes.value = response.quotes
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}