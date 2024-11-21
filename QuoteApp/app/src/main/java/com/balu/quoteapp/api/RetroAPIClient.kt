package com.balu.quoteapp.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.balu.quoteapp.endpoint.QuotesApiService

object RetroAPIClient{
    private const val BASE_URL = "https://dummyjson.com/"

    val quotesApiService: QuotesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuotesApiService::class.java)
    }
}