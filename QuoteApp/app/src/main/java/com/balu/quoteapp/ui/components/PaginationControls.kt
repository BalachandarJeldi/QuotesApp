package com.balu.quoteapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.balu.quoteapp.ui.theme.QuoteAppTheme

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
) {
    // The number of page numbers to display around the current page
    val pageWindow = 2

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Previous" button - only shown if not on the first page
        if (currentPage > 1) {
            TextButton(onClick = { onPageChange(currentPage - 1) }) {
                Text("Previous")
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Generate and display page numbers
        val pageRange = 1..totalPages
        val pagesToShow = mutableSetOf<Int>()

        // Always add the first page
        pagesToShow.add(1)

        // Add pages around the current page
        for (i in (currentPage - pageWindow)..(currentPage + pageWindow)) {
            if (i in pageRange) {
                pagesToShow.add(i)
            }
        }

        // Always add the last page
        pagesToShow.add(totalPages)

        val sortedPages = pagesToShow.toList().sorted()
        var lastPage = 0

        for (page in sortedPages) {
            // If there's a gap between page numbers, show ellipsis "..."
            if (page > lastPage + 1) {
                Text(
                    text = "...",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (page == currentPage) {
                // Current page - not clickable, different style
                Text(
                    text = "$page",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                // Other pages - clickable
                TextButton(onClick = { onPageChange(page) }) {
                    Text("$page")
                }
            }
            lastPage = page
        }

        // "Next" button - only shown if not on the last page
        if (currentPage < totalPages) {
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = { onPageChange(currentPage + 1) }) {
                Text("Next")
            }
        }
    }
}

// --- Previews for the PaginationControls Composable ---
@Preview(showBackground = true, name = "Pagination - First Page")
@Composable
private fun PaginationControlsPreview_FirstPage() {
    QuoteAppTheme {
        Surface {
            PaginationControls(
                currentPage = 1,
                totalPages = 10,
                onPageChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Pagination - Middle Page")
@Composable
private fun PaginationControlsPreview_MiddlePage() {
    QuoteAppTheme {
        Surface {
            PaginationControls(
                currentPage = 5,
                totalPages = 10,
                onPageChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Pagination - Last Page")
@Composable
private fun PaginationControlsPreview_LastPage() {
    QuoteAppTheme {
        Surface {
            PaginationControls(
                currentPage = 10,
                totalPages = 10,
                onPageChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Pagination - With Gaps (...)")
@Composable
private fun PaginationControlsPreview_WithGaps() {
    QuoteAppTheme {
        Surface {
            PaginationControls(
                currentPage = 10,
                totalPages = 20,
                onPageChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Pagination - Single Page")
@Composable
private fun PaginationControlsPreview_SinglePage() {
    QuoteAppTheme {
        Surface {
            PaginationControls(
                currentPage = 1,
                totalPages = 1,
                onPageChange = {}
            )
        }
    }
}