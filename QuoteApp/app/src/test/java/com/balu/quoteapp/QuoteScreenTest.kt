package com.balu.quoteapp

import com.balu.quoteapp.endpoint.Quote
import com.balu.quoteapp.ui.screens.performSearch
import org.junit.Assert.assertEquals
import org.junit.Test

class QuoteScreenKtTest {

    // Sample data for testing
    // file: app/src/test/java/com/balu/quoteapp/QuoteScreenTest.kt

    private val sampleQuotes = listOf(
        Quote(id = 1, author = "Author One", quote = "This is a wise quote."),
        Quote(id = 2, author = "Author Two", quote = "This is another smart saying."),
        Quote(id = 3, author = "Author One", quote = "A second quote from the same person."),
        Quote(id = 4, author = "Unique Author", quote = "Something completely different.")
    )


    @Test
    fun `performSearch with blank query returns all quotes`() {
        // Arrange
        val searchQuery = ""
        var result: List<Quote> = emptyList()

        // Act
        performSearch(
            searchQuery = searchQuery,
            quotes = sampleQuotes,
            onResult = { result = it },
            resetPage = {}
        )

        // Assert
        assertEquals(4, result.size)
        assertEquals(sampleQuotes, result)
    }

    @Test
    fun `performSearch with specific author query returns correct quotes`() {
        // Arrange
        val searchQuery = "Author One"
        var result: List<Quote> = emptyList()

        // Act
        performSearch(
            searchQuery = searchQuery,
            quotes = sampleQuotes,
            onResult = { result = it },
            resetPage = {}
        )

        // Assert
        assertEquals(2, result.size)
        assertEquals("Author One", result[0].author)
        assertEquals("Author One", result[1].author)
    }

    @Test
    fun `performSearch with specific quote content returns correct quote`() {
        // Arrange
        val searchQuery = "smart saying"
        var result: List<Quote> = emptyList()

        // Act
        performSearch(
            searchQuery = searchQuery,
            quotes = sampleQuotes,
            onResult = { result = it },
            resetPage = {}
        )

        // Assert
        assertEquals(1, result.size)
        assertEquals("Author Two", result[0].author)
    }

    @Test
    fun `performSearch with case-insensitive query returns correct quotes`() {
        // Arrange
        val searchQuery = "author one" // Lowercase
        var result: List<Quote> = emptyList()

        // Act
        performSearch(
            searchQuery = searchQuery,
            quotes = sampleQuotes,
            onResult = { result = it },
            resetPage = {}
        )

        // Assert
        assertEquals(2, result.size)
    }

    @Test
    fun `performSearch with no matching query returns empty list`() {
        // Arrange
        val searchQuery = "NonExistent"
        var result: List<Quote> = emptyList()

        // Act
        performSearch(
            searchQuery = searchQuery,
            quotes = sampleQuotes,
            onResult = { result = it },
            resetPage = {}
        )

        // Assert
        assertEquals(0, result.size)
    }

    @Test
    fun `performSearch triggers resetPage callback`() {
        // Arrange
        var wasReset = false

        // Act
        performSearch(
            searchQuery = "test",
            quotes = sampleQuotes,
            onResult = {},
            resetPage = { wasReset = true }
        )

        // Assert
        assertEquals(true, wasReset)
    }
}
