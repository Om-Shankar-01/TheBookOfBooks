package com.example.thebookofbooks.network

import com.example.thebookofbooks.model.BookDetailsItem
import com.example.thebookofbooks.model.BookResponse
import com.example.thebookofbooks.model.Item
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("api_key") key: String
    ): BookResponse

    @GET("volumes/{id}")
    suspend fun getBook(
        @Path("id") id: String,
        @Query("key") key: String
    ): BookDetailsItem
}