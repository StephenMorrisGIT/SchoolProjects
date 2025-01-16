package com.example.androidnews

import okhttp3.Address
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class NewsManager {

    val okHttpClient: OkHttpClient

    init{
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }

    fun getNews(urlContent: String): List<NewsArticles>{
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/$urlContent&apiKey=ad597c08de3347f9b00107d349dc0dbe")
            // .url("https://newsapi.org/v2/everything?q=boston&apiKey=ad597c08de3347f9b00107d349dc0dbe")
            .get()
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val newsArticles = mutableListOf<NewsArticles>()
        val responseString = response.body?.string()

        if (response.isSuccessful && !responseString.isNullOrEmpty()) {
            // Parse our JSON string

            // Represents the JSON from the root level
            val json = JSONObject(responseString)

            val articles = json.getJSONArray("articles")

            for (i in 0 until articles.length()) {

                val curr = articles.getJSONObject(i)

                val articleTitle = curr.getString("title")
                val articleDescription = curr.getString("description")
                val articlePicture = curr.getString("urlToImage")
                val articleUrl = curr.getString("url")

                val source = curr.getJSONObject("source")
                val website = source.getString("name")


                val article = NewsArticles(
                    articleTitle = articleTitle,
                    websiteName = website,
                    iconUrl = articlePicture,
                    content = articleDescription,
                    url = articleUrl
                )
                newsArticles.add(article)
            }
        }
        return newsArticles
    }
}