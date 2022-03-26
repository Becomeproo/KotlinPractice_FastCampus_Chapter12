package com.example.practicekotlin12.model

import com.google.gson.annotations.SerializedName

data class SearchBookDto (
    @SerializedName("items") val books: List<Book>
)