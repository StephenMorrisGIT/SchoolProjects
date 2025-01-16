package com.example.androidnews

import java.io.Serializable

data class NewsArticles(
    val articleTitle: String,
    val iconUrl: String,
    val websiteName: String,
    val content: String,
    val url: String
) : Serializable