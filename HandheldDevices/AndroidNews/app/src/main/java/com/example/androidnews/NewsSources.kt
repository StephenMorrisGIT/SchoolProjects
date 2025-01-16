package com.example.androidnews

import java.io.Serializable

data class NewsSources(
    val websiteID: String,
    val websiteName: String,
    val websiteDescription: String
) : Serializable