package com.balu.quoteapp.endpoint

import retrofit2.http.GET

data class Quote(val id: Int, val quote: String, val author: String)
data class QuoteResponse(val quotes: List<Quote>)

interface QuotesApiService{
    @GET("quotes")
    suspend fun getQuotes(): QuoteResponse
}